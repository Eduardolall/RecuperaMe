package mx.tecnm.cdhidalgo.recuperame

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.cdhidalgo.recuperame.dataClases.Usuario

class PerfilUsuario : AppCompatActivity() {


    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtener el ID del usuario actual
        userId = auth.currentUser?.uid ?: ""

        // Cargar datos del usuario
        cargarDatosUsuario()


    }

    private fun cargarDatosUsuario() {
        // Referencia al documento del usuario en la colección "usuarios"
        val usuarioRef = db.collection("usuarios").document(userId)

        usuarioRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Mapear el documento a la clase Usuario
                    val usuario = document.toObject(Usuario::class.java)

                    // Actualizar la interfaz de usuario con los datos del usuario
                    actualizarInterfazUsuario(usuario)
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores al obtener datos del usuario
                Log.e(TAG, "Error al obtener datos del usuario", exception)
            }
    }

    private fun actualizarInterfazUsuario(usuario: Usuario?) {
        if (usuario != null) {
                val nombre: TextView = findViewById(R.id.txtNombreUsuario)
                val apaterno: TextView = findViewById(R.id.apaterno)
                val aMaterno: TextView = findViewById(R.id.aMaterno)
                val Correo: TextView = findViewById(R.id.edtCorreoUsuario)

                nombre.text = usuario.nombre
                apaterno.text = usuario.apaterno
                aMaterno.text = usuario.amaterno
                Correo.text = usuario.correo
            }

        }
    }

