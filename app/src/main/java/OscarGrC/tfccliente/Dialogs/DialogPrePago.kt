package OscarGrC.tfccliente.Dialogs

import OscarGrC.tfccliente.databinding.DialogPrePagoBinding
import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.redsys.tpvvinapplibrary.ErrorResponse
import com.redsys.tpvvinapplibrary.IPaymentResult
import com.redsys.tpvvinapplibrary.ResultResponse
import com.redsys.tpvvinapplibrary.TPVV
import com.redsys.tpvvinapplibrary.TPVVConfiguration
import com.redsys.tpvvinapplibrary.TPVVConstants
import java.util.Calendar
import java.util.UUID


class DialogPrePago(
    private val totalCarrito:String,
    private val context: Context
) : DialogFragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var binding: DialogPrePagoBinding
    private val sharedPrefsKey = "com.example.app.PREFERENCE_FILE_KEY"
    private val selectedPaymentMethodKey = "MetodoDePago"

    val calendar = Calendar.getInstance()
    private var selectedDate: String = "01/01/2030"
    private var selectedTime: String = "12:00"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPrePagoBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
    // fecha y hora actuales para los text
        // Inicializa fecha y hora actuales
        val year = calendar.get(Calendar.YEAR).toString()
        var month = (calendar.get(Calendar.MONTH)+1).toString()
        var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        var minute = (calendar.get(Calendar.MINUTE) +5).toString()
        if(month.toInt()<10){
            month = "0"+month
        }
        if(day.toInt()<10){
            day= "0"+day
        }
        if(hour.toInt()<10){
            hour="0"+hour
        }
        if(minute.toInt()<10){
            minute="0"+minute
        }
        if(minute.toInt()>59){
           minute = (minute.toInt()-60).toString()
            hour = (hour.toInt()+1).toString()
        }

        selectedDate = "$day/${month}/$year"
        selectedTime = hour+":"+minute
    // Inicializa el Spinner
        val metodosDePagoSpinner: Spinner = binding.spinerPago
        val paymentMethods = arrayOf("Efectivo", "Tarjeta", "Bizum","Paypal")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        metodosDePagoSpinner.adapter = adapter

        // Obtener SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        val savedPaymentMethod = sharedPreferences.getString(selectedPaymentMethodKey, null)

        // Restaurar la selección del usuario desde SharedPreferences
        savedPaymentMethod?.let {
            val spinnerPosition = adapter.getPosition(it)
            metodosDePagoSpinner.setSelection(spinnerPosition)
        }
        // No es necesario agregar onItemSelectedListener si no necesitas guardar la selección en este fragmento

    // BINDING BOTONES
        binding.calendario.setOnClickListener {
            showDatePickerDialog()
        }

        binding.hora.setOnClickListener {
            showTimePickerDialog()
        }

        binding.pagobt.setOnClickListener {
            goGoPagoRanger(metodosDePagoSpinner.selectedItem.toString())
        }

    //Binding text
        binding.textFecha.text ="Fecha Entrega: "+selectedDate
        binding.textHora.text ="Hora Entrega: "+selectedTime



        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.textFecha.text ="Fecha Entrega: "+selectedDate
        }, year, month, day)
        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            selectedTime = "$selectedHour:$selectedMinute"
            binding.textHora.text ="Hora Entrega: "+selectedTime
        }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun goGoPagoRanger(metodoPago: String) {
        //antes de nada el pago vemos que selecciono el usuario
        var pagado: Boolean = false
        val idCarrito = UUID.randomUUID()
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        var carritoContent: List<String>
        var pedidosCliente: MutableList<String>
        //cargamos el contenido del carrito y los pedidos
        db.collection("Carrito").document(user).get().addOnSuccessListener { carritoSnapshot ->
            carritoContent = (carritoSnapshot?.get("LineasProducto") as? List<String>) ?: listOf()

            db.collection("PedidosCliente").document(user).get().addOnSuccessListener { pedidosSnapshot ->
                pedidosCliente = (pedidosSnapshot?.get("PedidosUsers") as? List<String>)?.toMutableList() ?: mutableListOf()

                // Aquí ya se han recuperado los datos, ahora podemos continuar con el procesamiento
                when (metodoPago) {
                    "Tarjeta" -> pagado = iniciarPasarelaDePago()
                    "Bizum" -> pagado = iniciarBizum()
                    "Paypal" -> pagado = iniciarPaypal()
                    else -> {
                        //esto seria efectivo dejamos pagado a false
                    }
                }

                // cargamos en pedidos el contenido del carro la uid metodoPago fecha hora y variable entregado a false y userId
                // Crear un mapa con los datos del pedido
                val horaActual = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"

                val pedidoData = hashMapOf(
                    "userId" to user,
                    "carrito" to carritoContent,
                    "horaPedido" to horaActual,
                    "horaEntrega" to selectedTime,
                    "fecha" to selectedDate,
                    "metodoPago" to metodoPago,
                    "precio" to totalCarrito,
                    "pagado" to pagado,
                    "entregado" to false
                )

                // Añadir el pedido a la colección "Pedido"
                db.collection("Pedido").document(idCarrito.toString()).set(pedidoData).addOnSuccessListener {
                    // Añadir a pedidosUser la referencia del último pedido
                    pedidosCliente.add(idCarrito.toString())
                    db.collection("PedidosCliente").document(user).set(hashMapOf("PedidosUsers" to pedidosCliente)).addOnSuccessListener {
                        // Borrar el contenido del carrito
                        db.collection("Carrito").document(user).delete().addOnSuccessListener {
                            Toast.makeText(context, "Pedido realizado correctamente", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al borrar el carrito: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText( context, "Error al actualizar pedidos del usuario: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Error al añadir el pedido: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Error al obtener pedidos del usuario: ${e.message}", Toast.LENGTH_LONG).show()
            }
                dismiss()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Error al obtener el carrito: ${e.message}", Toast.LENGTH_LONG).show()
            dismiss()
        }

    }




    private fun iniciarBizum():Boolean{

        return true
    }
    private fun iniciarPasarelaDePago(): Boolean {
        if (!isAdded) {
            // El fragmento no está adjunto a ninguna actividad, no se puede acceder al contexto
            return false
        }

        // El fragmento está adjunto a una actividad, se puede acceder al contexto
        TPVVConfiguration.setLicense("999008881")
        TPVVConfiguration.setEnvironment(TPVVConstants.ENVIRONMENT_TEST)
        TPVVConfiguration.setFuc("sq7HjrUOBfKmC576ILgskD5srU870gJ7")
        TPVVConfiguration.setTerminal("001")
        TPVVConfiguration.setCurrency("978")
        TPVVConfiguration.setPaymentMethods(TPVVConstants.PAYMENT_METHOD_T)

        TPVV.doDirectPayment(
            context,
            "numeroDePedido1",
            10.50,
            TPVVConstants.PAYMENT_METHOD_T,
            "sq7HjrUOBfKmC576ILgskD5srU870gJ7",
            "Descripcion",
            hashMapOf("clave" to "valor"), // Aquí puedes agregar pares clave-valor adicionales según sea necesario
            object : IPaymentResult {
                override fun paymentResultOK(p0: ResultResponse?) {
                    Toast.makeText(context,"R "+p0.toString(),Toast.LENGTH_LONG).show()
                }

                override fun paymentResultKO(p0: ErrorResponse?) {
                Toast.makeText(context,"E "+p0.toString(),Toast.LENGTH_LONG).show()
                }
            }
        )
        return true
    }

    private fun iniciarPaypal():Boolean{
        return true
    }

}


