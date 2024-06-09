package OscarGrC.tfccliente.Models

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class Pedido(var carrito:List<String>, var horaPedido:String, var horaRecepcion:String,var fecha:String, var metodoDepago:String,
             var numeroPedido:String ,var precio:String,
             var IsPagado:String = "",
             var IsEntregado:String =""
) {

var QR:Bitmap = GenerateQr()

fun GenerateQr():Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(numeroPedido.toString(), BarcodeFormat.QR_CODE,512,512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565)
        for (x in 0 until width){
            for(y in 0 until height){
                bmp.setPixel(x,y,if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
}

}