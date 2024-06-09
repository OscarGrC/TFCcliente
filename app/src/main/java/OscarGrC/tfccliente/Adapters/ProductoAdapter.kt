package OscarGrC.tfccliente.Adapters
import OscarGrC.tfccliente.Models.Producto
import OscarGrC.tfccliente.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class ProductoAdapter(context: Context, private var productos: MutableList<Producto>) :
    ArrayAdapter<Producto>(context, 0, productos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val viewHolder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_list_producto, parent, false)
            viewHolder = ViewHolder()
            viewHolder.imageViewProducto = itemView.findViewById(R.id.imagen)
            viewHolder.textViewNombreProducto = itemView.findViewById(R.id.NombreProducto)
            viewHolder.textViewPrecio = itemView.findViewById(R.id.precio)
            viewHolder.textViewDisponible = itemView.findViewById(R.id.disponible)
            itemView.tag = viewHolder
        } else {
            viewHolder = itemView.tag as ViewHolder
        }

        val producto = getItem(position)

        producto?.let {
            viewHolder.textViewNombreProducto.text = it.nombre
            val localprecio = "PRECIO: " + formatPrice(it.precio) + " €"
            viewHolder.textViewPrecio.text = localprecio
            viewHolder.textViewDisponible.text ="Disponibles: "+ it.stock.toString()

            Glide.with(context)
                .load(it.foto)
                .into(viewHolder.imageViewProducto)
        }

        return itemView!!
    }

    // Método para actualizar los productos
    fun updateProductos(newProductos: List<Producto>) {
        productos.clear()
        productos.addAll(newProductos)
        notifyDataSetChanged()
    }

    // Método para actualizar los productos
    fun sortByPrice() {
        productos.sortBy { it.precio }
        notifyDataSetChanged()
    }
    fun sortByAz() {
        productos.sortBy { it.nombre }
        notifyDataSetChanged()
    }
    fun sortByPopular() {
        productos.sortBy { it.id }
        notifyDataSetChanged()
    }

    private class ViewHolder {
        lateinit var imageViewProducto: ImageView
        lateinit var textViewNombreProducto: TextView
        lateinit var textViewPrecio: TextView
        lateinit var textViewDisponible: TextView
    }

    fun formatPrice(price: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        return formatter.format(price / 100.0)
    }
}
