package OscarGrC.tfccliente.Fragments

import OscarGrC.tfccliente.Adapters.PedidosAdapter
import OscarGrC.tfccliente.Models.Pedido
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import OscarGrC.tfccliente.R
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OldOrderFragment : Fragment() {
    private lateinit var adapter:  PedidosAdapter
    private var listaDePedidos: MutableList<Pedido> = mutableListOf()
    private lateinit var listViewPedidos: ListView
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_old_order, container, false)

        // Obtener el ListView desde el layout
        listViewPedidos = view.findViewById(R.id.UltimosPedidos)

        // Inicializar el adaptador con una lista vacía
        adapter = PedidosAdapter(requireContext(), listaDePedidos,childFragmentManager)

        // Configurar el adaptador en el ListView
        listViewPedidos.adapter = adapter

        // Obtener los pedidos del usuario desde Firestore y actualizar el adaptador
        db.collection("PedidosCliente").document(user).get().addOnSuccessListener { pedido ->
            val listaDePedidosID = (pedido?.get("PedidosUsers") as? List<String>) ?: listOf()
            listaDePedidosID.forEach {
                db.collection("Pedido").document(it).get().addOnSuccessListener { ped->
                    val pedido = Pedido(
                        carrito = (ped.get("carrito") as? List<String>) ?: listOf(),
                        horaPedido = ped.get("horaPedido").toString(),
                        horaRecepcion = ped.get("horaEntrega").toString(),
                        fecha = ped.get("fecha").toString(),
                        metodoDepago = ped.get("metodoPago").toString(),
                        numeroPedido = ped.toString(),
                        precio = ped.get("precio").toString(),
                        IsPagado = ped.get("pagado").toString(),
                        IsEntregado = ped.get("entregado").toString()
                    )
                    listaDePedidos.add(pedido)
                    listaDePedidos.sortByDescending { it.fecha }
                    adapter.notifyDataSetChanged()
                }
            }

        }




        return view
    }

}
