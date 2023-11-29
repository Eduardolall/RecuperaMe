package mx.tecnm.cdhidalgo.recuperame.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import mx.tecnm.cdhidalgo.recuperame.R
import mx.tecnm.cdhidalgo.recuperame.dataClases.Comentario
import mx.tecnm.cdhidalgo.recuperame.dataClases.Objetos

class AdaptadorPubli(val listaComentario:ArrayList<Comentario>)
    : RecyclerView.Adapter<AdaptadorPubli.ProductoViewHolder>() {

    var onProductoClick:((Comentario)->Unit)? = null

    class ProductoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val usuario: TextView = itemView.findViewById(R.id.txtNombreComentarista)
        val comenario: TextView = itemView.findViewById(R.id.txtContenidoComentario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_comentarios, parent, false)
        return ProductoViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaComentario.size
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaComentario[position]
        holder.usuario.text = producto.nombre
        holder.comenario.text = producto.comentario

        holder.itemView.setOnClickListener{
            onProductoClick?.invoke(producto)
        }

    }
}