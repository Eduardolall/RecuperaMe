package mx.tecnm.cdhidalgo.recuperame

import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.tecnm.cdhidalgo.recuperame.dataClases.Usuario

lateinit var usuarioApp: Usuario

class Login : AppCompatActivity() {

    private lateinit var bntRegistrar: Button
    private lateinit var btnIngresar: Button
    private lateinit var btnGoogle: SignInButton
    private lateinit var btnOlvidar: MaterialButton

    private lateinit var correo: TextInputLayout
    private lateinit var password: TextInputLayout

    private lateinit var auth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    val REQ_ONE_TAP = 10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //acceso a la base de datos
        val baseDatos = Firebase.firestore
        auth = Firebase.auth

        bntRegistrar = findViewById(R.id.btnRegistrar)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnGoogle =  findViewById(R.id.btnGoogle)
        btnOlvidar = findViewById(R.id.btnOlvidar)

        correo = findViewById(R.id.email)
        password = findViewById(R.id.password)


        btnIngresar.setOnClickListener {
            if (correo.editText?.text.toString().isNotEmpty() &&
                password.editText?.text.toString().isNotEmpty()
            ) {
                auth.signInWithEmailAndPassword(
                    correo.editText?.text.toString(),
                    password.editText?.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        baseDatos.collection("usuarios")
                            .whereEqualTo("correo", correo.editText?.text.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (documents in documents) {
                                    usuarioApp = Usuario(
                                        "${documents.data.get("correo")}",
                                        "${documents.data.get("nombre")}",
                                        "${documents.data.get("apaterno")}",
                                        "${documents.data.get("amaterno")}"
                                    )
                                }
                                val intent = Intent(this, RecuperaOb::class.java)
                                startActivity(intent)
                            }
                    } else {
                        notificacion()
                    }
                }
            }
        }

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.idCliente))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(false)
            .build()
        // ...
        auth = Firebase.auth

        btnOlvidar.setOnClickListener {
            val intent = Intent(this, RecuperarPass::class.java )
            startActivity(intent)
        }
    }


    override fun onStart() {
        super.onStart()
        bntRegistrar.setOnClickListener {
            var intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
        btnGoogle.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(ContentValues.TAG, e.localizedMessage, e)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                                        val user = auth.currentUser

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                                    }
                                }
                            Log.d(ContentValues.TAG, "Got ID token.")

                            val intent = Intent(this, RecuperaOb::class.java)
                            startActivity(intent)


                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(ContentValues.TAG, "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                    Log.d(ContentValues.TAG, "API", e)
                }
            }
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