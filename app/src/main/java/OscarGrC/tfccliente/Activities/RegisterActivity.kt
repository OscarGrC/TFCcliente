package OscarGrC.tfccliente.Activities

import OscarGrC.tfccliente.databinding.ActivityRegisterBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.registrar.setOnClickListener {
            //comprobamos los valores. Primero capturamos las variables
            var nombre = binding.nombreint.text.toString()
            var correo = binding.emailInt.text.toString()
            var password = binding.passwordint.text.toString()
            var repasword = binding.wordint.text.toString()
            var regexCorreo = Regex("^(([^<>()\\[\\]\\\\.,;:\\s@”]+(\\.[^<>()\\[\\]\\\\.,;:\\s@”]+)*)|(“.+”))@((\\[[0–9]{1,3}\\.[0–9]{1,3}\\.[0–9]{1,3}\\.[0–9]{1,3}])|(([a-zA-Z\\-0–9]+\\.)+[a-zA-Z]{2,}))\$")
            //var regexCorreo = Regex("\\S+@\\S+\\.\\S+")
            //primero campos en blanco
            if(nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || repasword.isEmpty()){
                Toast.makeText(this,"Campo vacio", Toast.LENGTH_SHORT).show()
            }
            //si las contraseñas son diferentes
            if(password != repasword){
                Toast.makeText(this,"Contraseñas diferentes", Toast.LENGTH_SHORT).show()
            }
            //si no cumple la distancia minima
            if(password.length<6){
                Toast.makeText(this,"Contraseña demasiado corta", Toast.LENGTH_SHORT).show()
            }
            //si el correo no cumple el patron de un correo
            if(!regexCorreo.matches(correo)){
                Toast.makeText(this,"Correo erroneo", Toast.LENGTH_SHORT).show()
            }else{
                val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
                firebaseAuth.createUserWithEmailAndPassword(correo,password)
                firebaseAuth.signInWithEmailAndPassword(correo,password)
                var intent = Intent(this, MainActivity::class.java)
                //aqui podemos registrar el nombre del usuario para la toolbar
                startActivity(intent)
            }
        }

    }
}