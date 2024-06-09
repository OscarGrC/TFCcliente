package OscarGrC.tfccliente.Activities

import OscarGrC.tfccliente.databinding.ActivityLoginBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoggingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.button2.setOnClickListener {
            if(binding.emailInsertado.text.toString() != "" && binding.contrainsertada.text.toString() !=""){
                Logarse(binding.emailInsertado.text.toString(),binding.contrainsertada.text.toString())
            }
        }
        binding.textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun Logarse (email:String, password:String){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                onStart()
            }else{
                Toast.makeText(this,"Error de logado", Toast.LENGTH_LONG).show()
            }
        }

    }
    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser !=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}