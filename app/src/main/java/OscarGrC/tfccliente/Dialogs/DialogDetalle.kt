package OscarGrC.tfccliente.Dialogs

import OscarGrC.tfccliente.Adapters.DetalleAdapter
import OscarGrC.tfccliente.Models.LineaDetalle
import OscarGrC.tfccliente.Models.Pedido
import OscarGrC.tfccliente.databinding.ItemPedidoDetalleBinding
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore


class DialogDetalle(
    private val pedido: Pedido,
) : DialogFragment() {
    private lateinit var binding: ItemPedidoDetalleBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = ItemPedidoDetalleBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        binding.FPedido.text ="Fecha Pedido: "+ pedido.fecha
        binding.FEntrega.text ="Hora Entrega: "+ pedido.horaRecepcion
        binding.Hora.text ="Hora Pedido: "+ pedido.horaPedido
        binding.NumPedido.text ="NºPedido: "+ pedido.numeroPedido.split("/")[1].split("-")[0]
        binding.MetodoPago.text ="M.Pago "+ pedido.metodoDepago
        binding.Entregado.text = if (pedido.IsPagado.toBoolean()) "Estado: Pagado" else "Estado: Pendiente"

        //para la lista tendremos que hacer varias cosas
        //primero definimos el adaptador y la lista
        var lineasCarritoDetalle:MutableList<LineaDetalle> = mutableListOf()
        var adapter = DetalleAdapter(requireContext(),lineasCarritoDetalle)
        binding.listCarrito.adapter = adapter
        // segundo recorrer la lista del carrito

        val db = FirebaseFirestore.getInstance()
        pedido.carrito.forEach {linea ->
            val nombre = linea.split("$$$")[0]
            val cantidad = linea.split("$$$")[1]
            val nombreConsulta = dbName(nombre)

            db.collection("Productos").document(nombreConsulta).get().addOnSuccessListener {
                val newLinea = LineaDetalle(nombre,cantidad,it.get("fotoId").toString())
                lineasCarritoDetalle.add(newLinea)
                adapter.notifyDataSetChanged()
            }
        }

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    fun dbName(nombre:String):String{
        var lista = nombre.split(" ")
        if(lista.size ==1){
            return lista[0]
        }
        return lista[0].lowercase() + lista.drop(1).joinToString("")
    }

}
