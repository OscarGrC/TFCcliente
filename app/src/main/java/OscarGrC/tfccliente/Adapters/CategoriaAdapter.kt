package OscarGrC.tfccliente.Adapters

import OscarGrC.tfccliente.Models.Categoria
import OscarGrC.tfccliente.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
class CategoriaAdapter(
    private val context: Context,
    private val categorias: List<Categoria>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(categoria: Categoria, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoriaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria, itemClickListener, position)
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(categoria: Categoria, clickListener: OnItemClickListener, position: Int) {
            Glide.with(itemView)
                .load(categoria.fotoId) // URL de la imagen
                .into(itemView.findViewById(R.id.imageView)) // ImageView en el que cargar la imagen
            itemView.findViewById<TextView>(R.id.NombreContacto).text = categoria.nombre

            itemView.setOnClickListener {
                clickListener.onItemClick(categoria, position)
            }
        }
    }
}
