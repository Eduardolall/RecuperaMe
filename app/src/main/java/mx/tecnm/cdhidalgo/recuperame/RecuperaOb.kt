package mx.tecnm.cdhidalgo.recuperame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.cdhidalgo.recuperame.adaptadores.AdaptadorArt
import mx.tecnm.cdhidalgo.recuperame.dataClases.Objetos

class RecuperaOb : AppCompatActivity() {

    private lateinit var btnPublicar: Button
    private lateinit var btnRegresar: ImageView
    private lateinit var btnPerfil : Button

    private lateinit var recyclerViewPublicacion: RecyclerView
    private lateinit var listaPublicacion: ArrayList<Objetos>
    private lateinit var adapatadorPublicacion: AdaptadorArt

    // Crear una referencia a la colección "detalle_producto_db"
    private val db = FirebaseFirestore.getInstance()
    val detalleProductoCollection = db.collection("publicaciones")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recupera_ob)

        btnPublicar = findViewById(R.id.btnPublicar)
        btnRegresar = findViewById(R.id.btnRegresarR)
        btnPerfil = findViewById(R.id.btnPerfil)
        recyclerViewPublicacion= findViewById(R.id.rvPublicaciones)

        btnPublicar.setOnClickListener {
            val intent = Intent(this, SubirObjeto::class.java)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilUsuario::class.java)
            startActivity(intent)
        }

        //RecyclerView Artesanias Horizontal
        recyclerViewPublicacion.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
            listaPublicacion = ArrayList()

        //lista artesania
        // Obtener los documentos de la colección [][]
        detalleProductoCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val documenId = document.getString("idcoleccion")
                    // Obtener los campos de cada documento
                    val imagenfo = document.getString("foto")
                    val categoria = document.getString("categoria")
                    val nombre = document.getString("nombre")
                    val fecha = document.getString("fecha")
                    val ubicacion = document.getString("ubicacion")

                    // Crear un objeto Producto con los datos obtenidos
                    val producto = Objetos("${documenId}",
                        "${imagenfo}", "${ubicacion}", "${categoria}",
                        "${nombre}", "${fecha}"
                    )

                            // Agregar el producto a la lista
                            listaPublicacion.add(producto)
                            //Adaptador Artesanias
                            adapatadorPublicacion = AdaptadorArt(listaPublicacion)
                            recyclerViewPublicacion.adapter = adapatadorPublicacion
                            adapatadorPublicacion.onProductoClick = {
                                val intent = Intent(this, DetallePublicacion::class.java)
                                intent.putExtra("publicaciones", it)
                                startActivity(intent)
                            

                    }
                }
                // Notificar al adaptador que los datos han cambiado
                adapatadorPublicacion.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                // Manejar cualquier error en la obtención de datos
                notificacion()
            }

        btnRegresar.setOnClickListener {
            var intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
    private fun notificacion() {
        val alerta = AlertDialog.Builder(this)
        alerta.setTitle("Error")
        alerta.setMessage("Se ha producido un Error en la Autenticacion del Usuario")
        alerta.setPositiveButton("Aceptar", null)
        alerta.show()
    }
}