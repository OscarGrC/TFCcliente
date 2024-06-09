package OscarGrC.tfccliente.Adapters

import OscarGrC.tfccliente.Models.Carrito
import OscarGrC.tfccliente.Models.LineaCarrito
import OscarGrC.tfccliente.Models.Producto
import OscarGrC.tfccliente.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(context: Context, productos: List<LineaCarrito>) :
    ArrayAdapter<LineaCarrito>(context, 0, productos) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser!!.uid
    private var carrito = Carrito(user)
    var dineros: Int = carrito.precioTotal
        private set
    private var listener: OnCarritoChangeListener? = null

    init {
        cargarCarrito() // Cargar el carrito una vez al inicializar el adaptador
    }

    fun setOnCarritoChangeListener(listener: OnCarritoChangeListener) {
        this.listener = listener
    }

    private fun notifyCarritoChange() {
        dineros = carrito.precioTotal
        listener?.onCarritoChange(dineros)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val viewHolder: ViewHolder
        if (itemView == null) {
            // Inflar el diseño de item_producto si aún no se ha creado una vista
            itemView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
            // Crear un ViewHolder y almacenar referencias a las vistas
            viewHolder = ViewHolder()
            viewHolder.imageViewProducto = itemView.findViewById(R.id.imagenCart)
            viewHolder.textViewNombreProducto = itemView.findViewById(R.id.Nombrecart)
            viewHolder.textViewPrecio = itemView.findViewById(R.id.precioCart)
            viewHolder.textViewCantidad = itemView.findViewById(R.id.cantidadCart)
            viewHolder.buttonAdd = itemView.findViewById(R.id.masCart)
            viewHolder.buttonRest = itemView.findViewById(R.id.menosCart)
            viewHolder.delete = itemView.findViewById(R.id.deleteCart)

            // Establecer el ViewHolder como una etiqueta en la vista
            itemView.tag = viewHolder
        } else {
            // Si la vista ya existe, obtener el ViewHolder de la etiqueta
            viewHolder = itemView.tag as ViewHolder
        }

        // Obtener el producto en la posición actual
        val producto = getItem(position)

        // Asignar los valores del producto a las vistas correspondientes
        producto?.let {
            // NOMBRE
            viewHolder.textViewNombreProducto.text = it.producto.nombre
            // PRECIO
            viewHolder.textViewPrecio.text = formatPrice(it.producto.precio * it.cantidad) + " €"


            Glide.with(context)
                .load(it.producto.foto)
                .placeholder(R.drawable.logo)  // Imagen de placeholder mientras se carga la imagen real
                .into(viewHolder.imageViewProducto)

            viewHolder.textViewCantidad.text = it.cantidad.toString()

            // Configurar listeners de clics para los botones
            viewHolder.buttonAdd.setOnClickListener {
                // Incrementar la cantidad
                producto.cantidad += 1
                carrito.lista[position].cantidad = producto.cantidad // Actualizar la cantidad en el carrito
                actualizarCarritoEnFirestore()
                carrito.calcularPrecioTotal()
                notifyCarritoChange()
                notifyDataSetChanged() // Notificar al ListView que los datos han cambiado
            }

            viewHolder.buttonRest.setOnClickListener {
                // Decrementar la cantidad
                if (producto.cantidad > 0) {
                    producto.cantidad -= 1
                    carrito.lista[position].cantidad = producto.cantidad // Actualizar la cantidad en el carrito
                    actualizarCarritoEnFirestore()
                    carrito.calcularPrecioTotal()
                    notifyCarritoChange()
                    notifyDataSetChanged() // Notificar al ListView que los datos han cambiado
                }
            }

            viewHolder.delete.setOnClickListener {
                // Eliminar la línea del carrito
                carrito.lista.removeAt(position)
                remove(producto)
                actualizarCarritoEnFirestore()
                carrito.calcularPrecioTotal()
                notifyCarritoChange()
                notifyDataSetChanged() // Notificar al ListView que los datos han cambiado
            }
        }

        return itemView!!
    }

    // ViewHolder para almacenar referencias a las vistas de cada elemento de la lista
    private class ViewHolder {
        lateinit var imageViewProducto: ImageView
        lateinit var textViewNombreProducto: TextView
        lateinit var textViewPrecio: TextView
        lateinit var textViewCantidad: TextView
        lateinit var buttonAdd: Button
        lateinit var buttonRest: Button
        lateinit var delete: ImageView
    }

    private fun convertirNombre(nombre: String): String {
        val partes = nombre.split(" ") // Dividir el nombre en partes por los espacios
        val primeraParte = partes.firstOrNull()?.lowercase() ?: "" // Convertir la primera parte a minúsculas
        val segundaParte = partes.drop(1).joinToString("")

        return primeraParte + segundaParte // Combinar las partes y devolver el resultado
    }

    private fun cargarCarrito() {
        db.collection("Carrito").document(user)
            .get()
            .addOnSuccessListener { result ->
                val lines = result?.get("LineasProducto") as? List<String>
                if (lines != null) {
                    procesarLineas(lines)
                }
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
            carrito.lista.clear()
            carrito.lista.addAll(lineasCarrito.filterNotNull())
            carrito.calcularPrecioTotal()
            notifyCarritoChange()
            notifyDataSetChanged()
        }
    }

    private fun formatPrice(price: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        return formatter.format(price / 100.0)
    }


    private fun actualizarCarritoEnFirestore() {
        val lineasProducto = carrito.lista.map { "${it.producto.nombre}$$$${it.cantidad}" }
        db.collection("Carrito").document(user)
            .set(mapOf("LineasProducto" to lineasProducto))
            .addOnSuccessListener {
                // Carrito actualizado correctamente
            }
            .addOnFailureListener { e ->
                // Manejar el error
            }
    }

    interface OnCarritoChangeListener {
        fun onCarritoChange(total: Int)
    }
}
