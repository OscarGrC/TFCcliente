package OscarGrC.tfccliente.Fragments

import OscarGrC.tfccliente.Adapters.CartAdapter
import OscarGrC.tfccliente.Dialogs.DialogPrePago
import OscarGrC.tfccliente.Models.Carrito
import OscarGrC.tfccliente.Models.LineaCarrito
import OscarGrC.tfccliente.Models.Producto
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import OscarGrC.tfccliente.R
import android.annotation.SuppressLint
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
class CartFragment : Fragment() {
    private lateinit var listViewProductos: ListView
    private lateinit var adapterProductos: CartAdapter
    private var listaDeProductos: MutableList<LineaCarrito> = mutableListOf()
    private lateinit var pago: Button
    private val user = FirebaseAuth.getInstance().currentUser!!.uid
    private var carrito: Carrito = Carrito(user)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var totalCarritoTextView: TextView

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        listViewProductos = view.findViewById(R.id.listProductos)
        totalCarritoTextView = view.findViewById(R.id.totalCart)
        pago = view.findViewById(R.id.pagoCart)
        //borrar esto


        cargarCarrito()

        pago.setOnClickListener {
            // Crea una instancia del diálogo pasando el producto seleccionado
            val dialog = DialogPrePago(
                totalCarrito = totalCarritoTextView.text.toString().replace("€","").replace("TOTAL: ",""),
                context = requireContext()
            )
            // Muestra el diálogo
            dialog.show(parentFragmentManager, "DialogPrePago")
            changeView()
        }

        // Configura el OnTouchListener en la vista raíz
        view.setOnTouchListener { _, _ ->
            // Actualiza el total del carrito al tocar cualquier parte de la pantalla
            totalCarritoTextView.text = formatPrice(adapterProductos.dineros)
            true
        }

        return view
    }

    private fun cargarCarrito() {
        db.collection("Carrito").document(user)
            .get()
            .addOnSuccessListener { result ->
                val lines = result?.get("LineasProducto") as? List<String>
                if (lines != null) {
                    procesarLineas(lines)
                } else {
                    actualizarUI()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al cargar el carrito", Toast.LENGTH_SHORT).show()
            }
    }

    private fun procesarLineas(lines: List<String>) {
        val productosTasks = lines.map { linea ->
            val nombre = convertirNombre(linea.split("$$$")[0])
            val cantidad = linea.split("$$$")[1].toInt()
            db.collection("Productos").document(nombre).get().continueWith { task ->
                val document = task.result
                if (document != null && document.exists()) {
                    val producto = Producto(
                        document.getString("nombre").orEmpty(),
                        document.getString("id").orEmpty(),
                        document.getString("fotoId").orEmpty(),
                        document.getLong("precio")?.toInt() ?: 0,
                        document.getLong("stock")?.toInt() ?: 0
                    )
                    LineaCarrito(producto, cantidad)
                } else {
                    null
                }
            }
        }

        Tasks.whenAllSuccess<LineaCarrito>(productosTasks).addOnSuccessListener { lineasCarrito ->
            listaDeProductos.clear()
            listaDeProductos.addAll(lineasCarrito.filterNotNull())
            carrito.lista = listaDeProductos
            carrito.calcularPrecioTotal()
            actualizarUI()
        }
    }

    private fun actualizarUI() {
        adapterProductos = CartAdapter(requireContext(), listaDeProductos)
        listViewProductos.adapter = adapterProductos
        totalCarritoTextView.text = formatPrice(carrito.precioTotal)

        // Establecer listener para cambios en el carrito
        adapterProductos.setOnCarritoChangeListener(object : CartAdapter.OnCarritoChangeListener {
            override fun onCarritoChange(total: Int) {
                totalCarritoTextView.text = formatPrice(total)
            }
        })
    }

    private fun formatPrice(price: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        return "TOTAL: "+ formatter.format(price / 100.0)+"€"
    }

    private fun convertirNombre(nombre: String): String {
        val partes = nombre.split(" ")
        val primeraParte = partes.firstOrNull()?.lowercase() ?: ""
        val segundaParte = partes.drop(1).joinToString("")
        return primeraParte + segundaParte
    }
    private fun changeView(){
        findNavController().navigate(R.id.action_cartFragment2_to_oldOrderFragment)
    }
}
