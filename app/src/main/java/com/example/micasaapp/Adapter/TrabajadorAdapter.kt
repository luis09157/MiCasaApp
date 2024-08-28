package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.TrabajadorModel
import com.ninodev.micasaapp.R

class TrabajadorAdapter(private val context: Context, private val listTrabajadores: List<TrabajadorModel>) : BaseAdapter() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = listTrabajadores.size

    override fun getItem(position: Int): TrabajadorModel = listTrabajadores[position]

    override fun getItemId(position: Int): Long = listTrabajadores[position].idProveedor.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val rowView: View

        if (convertView == null) {
            rowView = layoutInflater.inflate(R.layout.recyclerview_trabajador, parent, false)
            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as ViewHolder
        }

        val trabajador = getItem(position)
        viewHolder.bind(trabajador)

        return rowView
    }

    private inner class ViewHolder(view: View) {
        private val imagenPerfil: ImageView = view.findViewById(R.id.imagenPerfil)
        private val nombreCompleto: TextView = view.findViewById(R.id.nombreCompleto)
        private val descripcion: TextView = view.findViewById(R.id.descripcion)

        fun bind(trabajador: TrabajadorModel) {
            nombreCompleto.text = trabajador.nombreCompleto
            descripcion.text = trabajador.descripcion
            Glide.with(context)
                .load(trabajador.imagenPerfil)
                .placeholder(R.drawable.placeholder_image) // Placeholder mientras se carga la imagen
                .error(R.drawable.error_image) // Imagen de error si falla la carga
                .into(imagenPerfil)
        }
    }
}
