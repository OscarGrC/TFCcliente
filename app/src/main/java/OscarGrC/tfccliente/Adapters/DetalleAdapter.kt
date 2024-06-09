package OscarGrC.tfccliente.Adapters

import OscarGrC.tfccliente.Models.LineaDetalle
import OscarGrC.tfccliente.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DetalleAdapter(context: Context, private val detalles: List<LineaDetalle>)
    : ArrayAdapter<LineaDetalle>(context, 0, detalles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pdetalle_lineaproducto, parent, false)

        val detalle = getItem(position)

        val nombreTextView: TextView = view.findViewById(R.id.nombre)
        val cantidadTextView: TextView = view.findViewById(R.id.Cantidad)
        val imageView: ImageView = view.findViewById(R.id.fproducto)

        nombreTextView.text = detalle?.producto
        cantidadTextView.text = detalle?.cantidad

        // Usar Glide para cargar la imagen desde la URL
        Glide.with(context)
            .load(detalle?.foto)
            .placeholder(R.drawable.logo) // Placeholder mientras se carga la imagen
            .into(imageView)

        return view
    }
}
