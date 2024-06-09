package OscarGrC.tfccliente.Models

import java.io.Serializable

class Producto(var nombre:String, val id:String="", var foto:String, var precio:Int, var stock:Int): Serializable {
     var categoria:String ="Undefined"
     var ingredientes:MutableList<Ingrediente> = mutableListOf()
     fun vender(){ stock-- }
     fun addStock(cantidad:Int){ stock+=cantidad }
     fun calularPrecioIngredientes():Int{
         return 0
     }
 }