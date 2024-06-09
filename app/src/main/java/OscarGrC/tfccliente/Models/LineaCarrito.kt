package OscarGrC.tfccliente.Models

data class LineaCarrito(var producto:Producto, var cantidad:Int){
    var precio = producto.precio * cantidad
}