package OscarGrC.tfccliente.Fragments

import OscarGrC.tfccliente.Adapters.CategoriaAdapter
import OscarGrC.tfccliente.Adapters.ProductoAdapter
import OscarGrC.tfccliente.Dialogs.DialogAddCart
import OscarGrC.tfccliente.Models.Categoria
import OscarGrC.tfccliente.Models.Producto
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import OscarGrC.tfccliente.R
import android.text.Editable
import android.text.TextWatcher
import android.widget.ListView
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class MenuFragment : Fragment(), CategoriaAdapter.OnItemClickListener {

    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var adapter: CategoriaAdapter
    private var listaDeCategorias: MutableList<Categoria> = mutableListOf()
    private lateinit var listViewProductos: ListView
    private lateinit var adapterProductos: ProductoAdapter
    private var listaDeProductos: MutableList<Producto> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private var listaDeProductosCompleta: MutableList<Producto> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        adapter = CategoriaAdapter(requireContext(), listaDeCategorias, this)
        adapterProductos = ProductoAdapter(requireContext(), listaDeProductos)

        if(listaDeCategorias.isEmpty()){
            // Carga las categorías desde Firestore
            db.collection("CategoriasProductos")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val categoria = Categoria(document.get("fotoId").toString(), document.get("nombre").toString())
                        listaDeCategorias.add(categoria)
                    }
                    adapter.notifyDataSetChanged()
                }
        }
        if(listaDeProductos.isEmpty()){
            // Carga los productos desde Firestore
            db.collection("Productos")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val producto = Producto(
                            document.get("nombre").toString(),
                            document.get("id").toString(),
                            document.get("fotoId").toString(),
                            document.get("precio").toString().toInt(),
                            document.get("stock").toString().toInt()
                        )
                        listaDeProductos.add(producto)
                        listaDeProductosCompleta.add(producto)
                        listaDeCategorias.forEach { categoria ->
                            if (categoria.nombre == document.get("categoria")) {
                                categoria.ListaProductos.add(producto)
                            }
                        }
                    }
                    adapterProductos.notifyDataSetChanged()
                }
        }


        // Configura el RecyclerView
        recyclerViewCategory = view.findViewById(R.id.categorias)
        recyclerViewCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategory.adapter = adapter

        // Configura el ListView de productos
        listViewProductos = view.findViewById(R.id.productos)
        listViewProductos.adapter = adapterProductos

        // Listener para productos
        listViewProductos.setOnItemClickListener { parent, view, position, id ->
            // Obtén el producto seleccionado
            val selectedProduct = listaDeProductos[position]

            // Crea una instancia del diálogo pasando el producto seleccionado
            val dialog = DialogAddCart(
                productName = selectedProduct.nombre,
                productImage = selectedProduct.foto,
                stock = selectedProduct.stock,
            )
            // Muestra el diálogo
            dialog.show(parentFragmentManager, "DialogAddCart")
        }


        // Configurar el RadioGroup y añadir el listener
        val radioGroup = view.findViewById<RadioGroup>(R.id.ordenarBy)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton -> {
                    adapterProductos.sortByPopular()
                }
                R.id.radioButton2 -> {
                    adapterProductos.sortByAz()
                }
                R.id.radioButton3 -> {
                    adapterProductos.sortByPrice()
                }
            }
        }
        // Añadir TextWatcher al TextInputEditText para la búsqueda
        val buscador = view.findViewById<TextInputEditText>(R.id.buscador)
        buscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarProductos(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        return view
    }


    override fun onItemClick(categoria: Categoria, position: Int) {
        // Maneja el clic en el ítem de categoria
        listaDeProductos= mutableListOf()
        listaDeCategorias.forEach { cat ->
            if(cat.nombre == categoria.nombre){
                listaDeProductos = cat.ListaProductos
                adapterProductos.updateProductos(cat.ListaProductos)
            }
        }
    }
    private fun filtrarProductos(query: String) {
        var filteredList = listOf<Producto>()
        if(query== ""){
             filteredList = listaDeProductos.filter { it.nombre.contains(query, ignoreCase = true) }
        }else{
            filteredList = listaDeProductos.filter { it.nombre.contains(query, ignoreCase = true) }
        }

        adapterProductos.updateProductos(filteredList)
        adapterProductos.notifyDataSetChanged()
    }
}

