package OscarGrC.tfccliente.Adapters

import OscarGrC.tfccliente.Dialogs.DialogDetalle
import OscarGrC.tfccliente.Dialogs.DialogPrePago
import OscarGrC.tfccliente.Dialogs.DialogQr
import OscarGrC.tfccliente.Models.Pedido
import OscarGrC.tfccliente.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PedidosAdapter(context: Context, private var pedidos: List<Pedido>, private val fragmentManager: FragmentManager) : ArrayAdapter<Pedido>(context, 0, pedidos) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false)
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val pedido = getItem(position)

        val fechaPedidoTextView: TextView = view.findViewById(R.id.FPedido)
        val fechaEntregaTextView: TextView = view.findViewById(R.id.FEntrega)
        val horaTextView: TextView = view.findViewById(R.id.Hora)
        val repetirButton: Button = view.findViewById(R.id.BRepetir)
        val qrButton: Button = view.findViewById(R.id.button3)
        val detalle:Button = view.findViewById(R.id.detallebt)

        fechaPedidoTextView.text = "Fecha: ${pedido!!.fecha}"
        fechaEntregaTextView.text = "Hora Pedido: ${pedido!!.horaPedido}"
        horaTextView.text = "Hora Entrega: ${pedido!!.horaRecepcion}"

        repetirButton.setOnClickListener {
            // primero vaciamos el carrito
            db.collection("Carrito").document(user).delete()
            // cargamos los productos del pedido actual
            db.collection("Carrito").document(user).set(mapOf("LineasProducto" to pedido.carrito))
            //lanzamos dialogoPrePago
            var dialog = DialogPrePago(pedido.precio,context)
            dialog.show(fragmentManager,"DialogPrePago")
        }

        detalle.setOnClickListener {
            var dialog = DialogDetalle(pedido)
            dialog.show(fragmentManager, "DialogDetalle")
        }

        qrButton.setOnClickListener {
            var dialog = DialogQr(pedido.numeroPedido)
            dialog.show(fragmentManager, "DialogQr")
        }

        view.setOnClickListener {
            // Lógica para manejar el clic en  el elemento del pedido
        }

        return view
    }

}
