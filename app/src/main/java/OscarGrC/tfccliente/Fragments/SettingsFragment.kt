package OscarGrC.tfccliente.Fragments

import OscarGrC.tfccliente.Activities.LoggingActivity
import OscarGrC.tfccliente.Activities.MainActivity
import OscarGrC.tfccliente.Adapters.CartAdapter
import OscarGrC.tfccliente.Models.Carrito
import OscarGrC.tfccliente.Models.LineaCarrito
import OscarGrC.tfccliente.Models.Producto
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import OscarGrC.tfccliente.R
import OscarGrC.tfccliente.databinding.DialogAddcartBinding
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.text.NumberFormat
import java.util.Locale

class SettingsFragment : Fragment() {
private lateinit var metodosDePago: Spinner
private val selectedPaymentMethodKey = "MetodoDePago"
private val sharedPrefsKey = "com.example.app.PREFERENCE_FILE_KEY"
private lateinit var salir : Button


@SuppressLint("MissingInflatedId")
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    // Inflate the layout
    val view = inflater.inflate(R.layout.fragment_settings, container, false)

    metodosDePago = view.findViewById(R.id.spinerPago2)
    val paymentMethods = arrayOf("Efectivo", "Tarjeta", "Bizum","Paypal")
    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentMethods)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    metodosDePago.adapter = adapter

    // Restaurar la selección del usuario desde SharedPreferences
    val sharedPreferences = requireActivity().getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
    val savedPaymentMethod = sharedPreferences.getString(selectedPaymentMethodKey, null)

    savedPaymentMethod?.let {
        val spinnerPosition = adapter.getPosition(it)
        metodosDePago.setSelection(spinnerPosition)
    }

    metodosDePago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val selectedMethod = parent.getItemAtPosition(position).toString()

            // Guardar la selección del usuario en SharedPreferences
            with(sharedPreferences.edit()) {
                putString(selectedPaymentMethodKey, selectedMethod)
                apply()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }


    // bindings

    salir = view.findViewById(R.id.salir)
    salir.setOnClickListener {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireActivity(), LoggingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }
    return view
}

}