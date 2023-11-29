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
import mx.tecnm.cdhidalgo.recuperame.dataClases.Objetos

class AdaptadorArt(val listaPublicacion:ArrayList<Objetos>)
    : RecyclerView.Adapter<AdaptadorArt.ProductoViewHolder>() {

    var onProductoClick:((Objetos)->Unit)? = null

    class ProductoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val foto: ImageView = itemView.findViewById(R.id.fotoProductp)
        val nombre: TextView = itemView.findViewById(R.id.nombreProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_publicacion, parent, false)
        return ProductoViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaPublicacion.size
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaPublicacion[position]
        val linkDeDescarga = producto.foto
        Glide.with(holder.foto.context).load(linkDeDescarga).into(holder.foto)

        //holder.foto.setImageResource(producto.foto)
        holder.nombre.text = producto.nombre

        holder.itemView.setOnClickListener{
            onProductoClick?.invoke(producto)
        }

    }
}