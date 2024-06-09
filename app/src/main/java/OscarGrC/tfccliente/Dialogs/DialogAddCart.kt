package OscarGrC.tfccliente.Dialogs

import OscarGrC.tfccliente.Models.Carrito
import OscarGrC.tfccliente.databinding.DialogAddcartBinding
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DialogAddCart(
    private val productName: String,
    private val productImage: String,
    private val stock: Int,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
): DialogFragment() {
    var cantidad = 1
    private lateinit var binding : DialogAddcartBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddcartBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

///bindings
        context?.let {
            Glide.with(it)
                .load(productImage) // URL de la imagen
                .into(binding.imagenAdd)
        }

        binding.NombreAdd.text=productName

        binding.masAdd.setOnClickListener {
            if ((cantidad + 1) <= stock) {
                cantidad++
                binding.cantidadAdd.text = cantidad.toString()
            } else {
                Toast.makeText(requireContext(), "Cantidad Máxima, no queda stock", Toast.LENGTH_LONG).show()
            }
        }
        binding.menosAdd.setOnClickListener {
            if (cantidad > 0) {
                cantidad--
                binding.cantidadAdd.text = cantidad.toString()
            } else {
                Toast.makeText(requireContext(), "Cantidad no puede ser inferior a 0", Toast.LENGTH_LONG).show()
            }
        }

        binding.sendAdd.setOnClickListener {
            //consultamos quien es el usuario actual
            val user = FirebaseAuth.getInstance().currentUser?.uid
            val carrito = Carrito(user!!)


            //cargamos el carrito del usuario y recuperamos las lineas
            db.collection("Carrito").document(user)
                .get()
                .addOnSuccessListener { result ->
                    var lineas = mutableListOf<String>()
                    var exist = false
                    val lineasExito = result?.get("LineasProducto") as? List<String>
                    if (lineasExito != null) {
                        lineas = lineasExito.toMutableList()
                        val updatedLineas = lineas.map {
                            val name = it.split("$$$")[0]
                            val cantidad = it.split("$$$")[1].toInt()
                            if (name == productName) {
                                exist = true
                                "$name$$$${cantidad + 1}"
                            } else {
                                it
                            }
                        }.toMutableList()
                        if (!exist) {
                            updatedLineas.add("$productName$$$${cantidad}")
                        }
                        lineas = updatedLineas
                    } else {
                        lineas.add("$productName$$$${cantidad}")
                    }

                    // Actualizar las líneas en Firestore
                    db.collection("Carrito").document(user)
                        .set(mapOf("LineasProducto" to lineas))
                        .addOnSuccessListener {
                            // Subida exitosa
                            dismiss()
                        }
                        .addOnFailureListener { e ->
                            // Manejar errores
                            dismiss()
                        }
                }
                .addOnFailureListener { e ->
                    // Manejar errores
                    dismiss()
                }
        }
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

}