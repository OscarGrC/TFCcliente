package OscarGrC.tfccliente.Models

import java.io.Serializable

data class Ingrediente (var nombre:String,var id:String ="",var foto:String,var precio:Int,var stock:Int,var UnidadesPedido:Int,):
    Serializable {
    //Comento esto por que puede ser lioso...
    //Stock representa la cantidad de unidades disponibles de un producto o ingrendiente.
    //Unidades de Pedido representa las unidades de stock que vienen en un pedido Ejemplo Tomates:1 caja RedBull:1 caja
    // una caja puede contener 5 kg de tomantes o 12 RedBull
    // cantidadPedidoUnidad es la cantidad de unidades de stock que se consume en un pedido Ejemplo stock 5000g de tomates en un pedido se restara 100 g de stock
    // Redbull stock 30 cantidadPedidoUnidad = 1  tambien es la unidad que fija el precio 1 redbull  100g de tomate ...
    var cantidadPedidoUnidad = 1

}