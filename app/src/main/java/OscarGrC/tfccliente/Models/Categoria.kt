package OscarGrC.tfccliente.Models

 class Categoria (var fotoId:String="", var nombre:String="") {
     var ListaProductos: MutableList<Producto> = mutableListOf()

     fun addProducto(newProducto: Producto): Producto? {
         ListaProductos.forEach { producto: Producto ->
             if (producto.nombre == newProducto.nombre) { return null }
         }
         ListaProductos += newProducto
         return newProducto
     }

     fun removeProducto(newProducto: Producto): Producto? {
         var check: Int = ListaProductos.size
         ListaProductos.removeIf { it.nombre == newProducto.nombre }
         if (ListaProductos.size == check) { return null }
         return newProducto
     }
 }
