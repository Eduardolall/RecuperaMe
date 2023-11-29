package mx.tecnm.cdhidalgo.recuperame

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.recuperame.dataClases.Objetos
import mx.tecnm.cdhidalgo.recuperame.dataClases.Usuario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

private var  ImageUri : Uri? = null
private var  imageUrll : String=""
val firestore = FirebaseFirestore.getInstance()
val storage = FirebaseStorage.getInstance()
val storageRef = storage.reference

class SubirObjeto : AppCompatActivity() {

    private lateinit var btnfecha: EditText
    private  val calendar = Calendar.getInstance()
    private lateinit var btnsubirImage : Button
    private lateinit var firebaseImage: ImageView
    private lateinit var txtUbicacion: TextView
    private lateinit var txtUbicacionImg: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnPublicar: Button
    private lateinit var nombre: TextInputLayout
    private lateinit var categoria: Spinner

    var _categorias= ""
    var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_objeto)

        val baseDatos = Firebase.firestore

        btnfecha = findViewById(R.id.btnFecha)
        btnsubirImage = findViewById(R.id.btnSubirImg)
        btnPublicar = findViewById(R.id.btnPublicar)
        firebaseImage = findViewById(R.id.firebaseImage)
        txtUbicacion = findViewById(R.id.txtubicacion)
        txtUbicacionImg = findViewById(R.id.txtubicacionImg)
        nombre = findViewById(R.id.nombre)
        categoria = findViewById(R.id.liscaterias)

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val seleccionarImagenLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // La Uri seleccionada se pasa aquí
                if (uri != null) {
                    // Actualizar la variable imageUri
                    ImageUri = uri
                    // Realizar acciones adicionales según tus necesidades
                    uploadImageToFirebase()
                }
            }

        // Verificar y solicitar permisos si es necesario
        if (checkLocationPermission()) {
            // Obtener la última ubicación conocida
            getLastLocation()
        } else {
            // Si no se tienen permisos, solicitarlos al usuario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }


        btnsubirImage.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
            getLastLocation()
        }

        //spiner tipo de salas
        val array_salas = arrayListOf<String>("Telefono", "Tableta", "Computadora", "ObjetoPersonal")
        val adaptadorSalas = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            array_salas
        )
        categoria.adapter = adaptadorSalas

        categoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                _categorias = categoria.getItemAtPosition(p2) as String

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}


        }

        btnfecha.setOnClickListener{
            showDatePickerDialog()
        }

        btnPublicar.setOnClickListener {
            val confirmaDialog = AlertDialog.Builder(it.context)
            confirmaDialog.setTitle("Confirmar Datos")
            confirmaDialog.setMessage("""
                Foto: ${txtUbicacionImg.text} 
                Ubicacion: ${txtUbicacion.text} 
                Categoria: ${_categorias}
                Nombre: ${nombre.editText?.text}
                Fecha: ${selectedDate}
            """.trimMargin())
            confirmaDialog.setPositiveButton("Confirmar") { confirmaDialog, which ->

                val email = "${UUID.randomUUID()}"

                val Publicacion = Objetos(
                    email,
                    txtUbicacionImg.text.toString(),
                    txtUbicacion.text.toString(),
                    _categorias,
                    nombre.editText?.text.toString(),
                    selectedDate
                )

                val intent = Intent(this, RecuperaOb::class.java).apply {
                    baseDatos.collection("publicaciones")
                        .document(email).set(Publicacion)

                }
                startActivity(intent)
            }
            confirmaDialog.setNegativeButton("Cancelar"){confirmaDialog,which ->
            }
            confirmaDialog.show()
        }
    }

    private fun uploadImageToFirebase() {
        // Subir la imagen a Storage
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
        imagesRef.putFile(ImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // La imagen se ha subido exitosamente
                // Obten la URL de descarga de la imagen
                imagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Guarda la URL de descarga en Firestore u otras acciones según tus necesidades
                    val imageUrl = downloadUri.toString()

                    // Haz algo con la ubicación, por ejemplo, mostrarla en un TextView
                    txtUbicacionImg.text = "$imageUrl"

                    // Por ejemplo, puedes guardar la URL en Firestore
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        firestore.collection("images").document(userId).update("profileImageUrl", imageUrl)
                            .addOnSuccessListener {
                                // Éxito al actualizar la URL en Firestore
                            }
                            .addOnFailureListener { e ->
                                // Manejar el error al actualizar la URL en Firestore
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Manejar el error al subir la imagen a Storage
            }

    }



    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Actualizar el campo de texto con la fecha seleccionada
                calendar.set(selectedYear, selectedMonth, selectedDay)
                updateEditTextDate()
            },
            year, month, day)
            datePickerDialog.show()

    }

    private fun updateEditTextDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)
        btnfecha.setText(selectedDate)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode== RESULT_OK)
            ImageUri = data?.data!!
        firebaseImage.setImageURI(ImageUri)

    }
    ///////// Funciones para obtener la ubicacion

    private fun getLastLocation() {
        // Obtener la última ubicación conocida
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // La última ubicación conocida está disponible en el objeto Location
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Haz algo con la ubicación, por ejemplo, mostrarla en un TextView
                    txtUbicacion.text = "Latitud: $latitude, Longitud: $longitude"
                }
            }
            .addOnFailureListener { e ->
                // Manejar el error al intentar obtener la última ubicación
            }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private fun showAlert() {
        val alerta = AlertDialog.Builder(this)
        alerta.setTitle("Error")
        alerta.setMessage("Se ha producido un Error en la Autenticación!!")
        alerta.setPositiveButton("Aceptar", null)
        alerta.show()

    }


}