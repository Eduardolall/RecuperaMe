package mx.tecnm.cdhidalgo.recuperame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import mx.tecnm.cdhidalgo.recuperame.dataClases.Objetos
class DetallePublicacion : AppCompatActivity() {
    private lateinit var btnRegresar: ImageButton
    private lateinit var btnComentario: Button
    private lateinit var btnGenerarComentario: Button
    private lateinit var btnEliminar : ImageButton

    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_publicacion)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        btnRegresar = findViewById(R.id.btnRegresar_detalle)
        btnComentario = findViewById(R.id.btnComentario)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnGenerarComentario = findViewById(R.id.btnGenerarComentario)


        val producto = intent.getParcelableExtra<Objetos>("publicaciones")

        if (producto != null) {
            val foto: ImageView = findViewById(R.id.foto_detalle)
            val nombre: TextView = findViewById(R.id.nombre_detalle)
            val categoria: TextView = findViewById(R.id.categoria_detalle)
            val ubicacion: TextView = findViewById(R.id.ubicacion_detalle)
            val fecha: TextView = findViewById(R.id.fecha_detalle)

            val linkDeDescarga = producto.foto
            //val linkDeDescarga = "https://firebasestorage.googleapis.com/v0/b/recuperame-669ba.appspot.com/o/images%2Fbf3adeab-7ab9-497b-a23d-7c322067b1e7?alt=media&token=c69cd93f-2a06-4641-9bb2-4af82e9d2de5"
            Glide.with(foto.context).load(linkDeDescarga).into(foto)
            nombre.text = producto.nombre
            categoria.text = producto.categoria
            fecha.text = producto.fecha
            ubicacion.text = producto.ubicacion
        }


        val documentIdToDelete = producto?.idcoleccion.toString()

        // Inicializa el botón

        btnEliminar.setOnClickListener {
            deleteDocument(documentIdToDelete)
        }

        btnGenerarComentario.setOnClickListener {
            val comentarioTexto = "."

            if (comentarioTexto.isNotEmpty() && documentIdToDelete != null) {
                // Crear un nuevo comentario
                val nuevoComentario = hashMapOf(
                    "usuarioId" to auth.currentUser?.uid,
                    "comentario" to comentarioTexto,
                    "timestamp" to System.currentTimeMillis()
                )

                // Agregar el comentario a la colección de comentarios de la publicación
                db.collection("publicaciones").document(documentIdToDelete)
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


        btnComentario.setOnClickListener {
            val intent = Intent(this, Comentarios::class.java)
            intent.putExtra("idPublicacion", documentIdToDelete)
            startActivity(intent)

        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this, RecuperaOb::class.java)
            startActivity(intent)
        }

    }

    private fun deleteDocument(documentId: String) {
        // Elimina el documento de la colección
        firestore.collection("publicaciones")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                this, "Documento eliminado exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RecuperaOb::class.java)
                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                            this, "Error al eliminar el documento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
//Tengo que sacra el id de este publicacion y mandarla a los comentarios para que jale