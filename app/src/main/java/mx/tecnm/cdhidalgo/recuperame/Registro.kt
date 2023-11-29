package mx.tecnm.cdhidalgo.recuperame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore

import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.recuperame.dataClases.Usuario

class Registro : AppCompatActivity() {

    private lateinit var nombre: TextInputLayout
    private lateinit var apaterno: TextInputLayout
    private lateinit var amaterno: TextInputLayout
    private lateinit var correo: TextInputLayout
    private lateinit var pass: TextInputLayout

    private lateinit var btnRegistrar: Button
    private lateinit var btnEstoyRegistrado: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val baseDatos = Firebase.firestore

        nombre = findViewById(R.id.nombre)
        apaterno = findViewById(R.id.apaterno)
        amaterno = findViewById(R.id.amaterno)
        correo = findViewById(R.id.email_registro)
        pass = findViewById(R.id.password_registro)

        btnRegistrar = findViewById(R.id.btnRegistrarDatos)
        btnEstoyRegistrado = findViewById(R.id.btnYaEstoyRegistrado)

        btnRegistrar.setOnClickListener {
            val confirmaDialog = AlertDialog.Builder(it.context)
            confirmaDialog.setTitle("Confirmar Datos")
            confirmaDialog.setMessage("""
                Usuario: ${nombre.editText?.text} ${apaterno.editText?.text} ${amaterno.editText?.text}
                Correo: ${correo.editText?.text}
                Contraseña: ${pass.editText?.text}
            """.trimMargin())
            confirmaDialog.setPositiveButton("Confirmar") {confirmaDialog,which ->

                val email=correo.editText?.text
                val psw=pass.editText?.text

                val usuario = Usuario(
                    email.toString(),
                    nombre.editText?.text.toString(),
                    apaterno.editText?.text.toString(),
                    amaterno.editText?.text.toString())

                if(email.toString().isNotEmpty() && psw.toString().isNotEmpty()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        email.toString(),psw.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            val intent = Intent(this,Login::class.java).apply {
                                baseDatos.collection("usuarios")
                                    .document(email.toString()).set(usuario)
                            }
                            startActivity(intent)
                        } else {
                            showAlert()
                        }
                    }
                }
            }
            confirmaDialog.setNegativeButton("Cancelar"){confirmaDialog,which ->
            }
            confirmaDialog.show()
        }
        btnEstoyRegistrado.setOnClickListener {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

    }

    private fun showAlert() {
        val alerta = AlertDialog.Builder(this)
        alerta.setTitle("Error")
        alerta.setMessage("Se ha producido un Error en la Autenticación!!")
        alerta.setPositiveButton("Aceptar", null)
        alerta.show()

    }
}