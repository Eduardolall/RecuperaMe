package mx.tecnm.cdhidalgo.recuperame

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Manejar la recepción de la notificación aquí
        Log.d(TAG, "Mensaje recibido: ${remoteMessage.data}")

        // Obtener datos de la notificación
        val titulo = remoteMessage.data["RecuperaMe"]
        val cuerpo = remoteMessage.data["Hay una nueva publicacion"]

        // Mostrar la notificación
        mostrarNotificacion(titulo, cuerpo)
    }

    private fun mostrarNotificacion(titulo: String?, cuerpo: String?) {
        // Puedes utilizar una clase como NotificationManager para mostrar la notificación
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Construir la notificación
        val notificationBuilder = NotificationCompat.Builder(this, "canal_id")
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setSmallIcon(R.drawable.recuperame) // Cambia por el icono deseado
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Mostrar la notificación
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}