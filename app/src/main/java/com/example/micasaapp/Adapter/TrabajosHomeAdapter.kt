package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.micasaapp.Model.TrabajosHomeModel
import com.ninodev.micasaapp.R
import com.squareup.picasso.Picasso

class TrabajosHomeAdapter(private val context: Context, private var trabajosHome: List<TrabajosHomeModel>) : BaseAdapter() {
    private var filteredTrabajosHome: List<TrabajosHomeModel> = trabajosHome

    fun filter(query: String) {
        filteredTrabajosHome = if (query.isBlank()) {
            trabajosHome
        } else {
            trabajosHome.filter { it.nombreCompleto.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return filteredTrabajosHome.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.recyclerview_trabajos_home, viewGroup, false)
            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }

        with(filteredTrabajosHome[position]) {
            viewHolder.txtNombreTrabajador.text = nombreCompleto
            viewHolder.txtProfecion.text = categorias
            viewHolder.txtDireccion.text = direccion

            // Usar Picasso para cargar la imagen
            Picasso.get()
                .load(imagenTrabajo) // Suponiendo que 'imagenTrabajo' es la URL de la imagen
                .placeholder(R.drawable.placeholder_image) // Imagen de marcador de posición mientras se carga la imagen
                .error(R.drawable.error_image) // Imagen de error si la carga falla
                .into(viewHolder.imagen)
        }

        return rowView!!
    }

    override fun getItem(position: Int): Any {
        return filteredTrabajosHome[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ViewHolder(view: View) {
        val txtNombreTrabajador: TextView = view.findViewById(R.id.txtNombreTrabajador)
        val txtProfecion: TextView = view.findViewById(R.id.txtProfecion)
        val txtDireccion: TextView = view.findViewById(R.id.txtDireccion)
        val imagen: ImageView = view.findViewById(R.id.imagen)
    }
}
