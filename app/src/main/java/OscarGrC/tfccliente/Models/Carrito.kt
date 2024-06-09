package OscarGrC.tfccliente.Models

import java.util.UUID


class Carrito(var idCliente:String ="") {
    var lista:MutableList<LineaCarrito> = mutableListOf()
    var precioTotal:Int = 0
    var id = UUID.randomUUID()
    fun Modificar(newLine: LineaCarrito): Int {
        var salida: Int = 404
        lista.forEach { line ->
            if (line.producto.id == newLine.producto.id) {
                line.cantidad = newLine.cantidad
                salida = 500
                calcularPrecioTotal()
            }
        }
        if(salida != 500){
            lista.add(newLine)
            salida = 501
        }
        return salida
    }

    fun Eliminar(delete: Producto): Int {
        val listaLength = lista.size
        val salida = 404
        lista.removeAll { it.producto.id == delete.id }
        if (lista.size == listaLength) {
            return salida// No se eliminó ningún elemento
        } else {
            calcularPrecioTotal()
            500 // Se eliminó al menos un elemento
        }
        return salida
    }
    fun calcularPrecioTotal(){
        var calculo = 0
        lista.forEach { line ->
            calculo += (line.producto.precio * line.cantidad)
        }
        precioTotal = calculo
    }

}