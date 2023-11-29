package mx.tecnm.cdhidalgo.recuperame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.cdhidalgo.recuperame.adaptadores.AdaptadorArt
import mx.tecnm.cdhidalgo.recuperame.adaptadores.AdaptadorPubli
import mx.tecnm.cdhidalgo.recuperame.dataClases.Comentario
import mx.tecnm.cdhidalgo.recuperame.dataClases.Objetos

class Comentarios : AppCompatActivity() {
    private lateinit var llComentarios:LinearLayout
    private lateinit var btnEnviar: ImageView
    private lateinit var btnRegresar: ImageView
    private lateinit var txtComenatrio: TextView


    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerViewComentarios: RecyclerView
    private lateinit var listaComentario: ArrayList<Comentario>
    private lateinit var adapatadorComentario: AdaptadorPubli


    private val based = FirebaseFirestore.getInstance()
    val detalleComentariosCollection = based.collection("comentarios")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentarios)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        llComentarios = findViewById(R.id.llComentarios)
        btnEnviar = findViewById(R.id.btnEnviarComentario)
        btnRegresar = findViewById(R.id.btnRegresarR)
        txtComenatrio = findViewById(R.id.edtComentario)
        recyclerViewComentarios= findViewById(R.id.rvComenatrios)

        //RecyclerView Artesanias Horizontal
        recyclerViewComentarios.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,true)
        listaComentario = ArrayList()

        obtenerComentarios()

        /** Obtener comentarios desde Firestore
        db.collection("listaComentarios").document(idpublicacion).collection("listaComentarios")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val comentario = document.toObject(Comentario::class.java)
                    agregarComentarioView(comentario)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }*/

        // Supongamos que recibes el ID de la publicación como extra
        val idPublicacion = intent.getStringExtra("idPublicacion")

        btnEnviar.setOnClickListener {

                val comentarioTexto = txtComenatrio.text.toString()

                if (comentarioTexto.isNotEmpty() && idPublicacion != null) {
                    // Crear un nuevo comentario
                    val nuevoComentario = hashMapOf(
                        "usuarioId" to auth.currentUser?.uid,
                        "comentario" to comentarioTexto,
                        "timestamp" to System.currentTimeMillis()
                    )

                    // Agregar el comentario a la colección de comentarios de la publicación
                    db.collection("publicaciones").document(idPublicacion)
                        .collection("comentarios")
                        .add(nuevoComentario)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(
                                this, "Comentario exitosamente", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this, "Error al agregar el comentario: ${e.message}", Toast.LENGTH_SHORT).show()

                        }
                }
            startActivity(intent)
            }

        btnRegresar.setOnClickListener {
            val intent = Intent(this, DetallePublicacion::class.java)
            startActivity(intent)
        }

    }
    private fun obtenerComentarios() {
        val idPublicacionn = intent.getStringExtra("idPublicacion")

        db.collection("publicaciones")
            .document(idPublicacionn.toString())
            .collection("comentarios")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Acceder a los datos de cada comentario (document.data)
                    // Obtener los campos de cada documento
                    val nombre = document.getString("usuarioId")
                    val comentario = document.getString("comentario")

                    // Crear un objeto Producto con los datos obtenidos
                    val producto = Comentario("${nombre}",
                        "${comentario}"
                    )
                    // Agregar el producto a la lista
                    listaComentario.add(producto)
                    //Adaptador Artesanias
                    adapatadorComentario= AdaptadorPubli(listaComentario)
                    recyclerViewComentarios.adapter = adapatadorComentario
                    adapatadorComentario.onProductoClick = {
                    }
                }
                // Notificar al adaptador que los datos han cambiado
                adapatadorComentario.notifyDataSetChanged()

            }.addOnFailureListener { e ->
                // Manejar cualquier error en la obtención de datos

            }
                }

}






