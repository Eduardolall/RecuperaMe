package mx.tecnm.cdhidalgo.recuperame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RecuperarPass : AppCompatActivity() {
    private lateinit var correoRecuperar: TextInputLayout
    private lateinit var btnRecuperar: Button
    private lateinit var btnRegresar: Button

    var email = ""

    private var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_pass)
        correoRecuperar = findViewById(R.id.email_recuperar)
        btnRecuperar = findViewById(R.id.btnRecuperarPass)
        btnRegresar = findViewById(R.id.btnRegresar_recuperar)


        btnRecuperar.setOnClickListener {
            email = correoRecuperar.editText?.text.toString()
            if (email.isNotEmpty()){
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    correoRecuperar.error=null
                    enviarCorreoRecuperar(email)
                }else{
                    correoRecuperar.error = "Ingresar Correo Electronco!!"
                }

            }else{
                correoRecuperar.error = "Ingresar Correo Electronico!!"
            }
        }
        btnRegresar.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun enviarCorreoRecuperar(email: String) {
        auth.sendPasswordResetEmail(correoRecuperar.editText?.text.toString())
            .addOnCompleteListener {
                Toast.makeText(this,
                    "Correo enviado a ${this.email} para restablecer su contrasena",
                    Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
    }
}


