package mx.tecnm.cdhidalgo.recuperame.dataClases

import android.os.Parcel
import android.os.Parcelable


data class Objetos(
    val idcoleccion: String?,
    val foto: String?,
    val ubicacion: String?,
    val categoria: String?,
    val nombre:String?,
    val fecha:String?,

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idcoleccion)
        parcel.writeString(foto)
        parcel.writeString(ubicacion)
        parcel.writeString(categoria)
        parcel.writeString(nombre)
        parcel.writeString(fecha)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Objetos> {
        override fun createFromParcel(parcel: Parcel): Objetos {
            return Objetos(parcel)
        }

        override fun newArray(size: Int): Array<Objetos?> {
            return arrayOfNulls(size)
        }
    }
}
