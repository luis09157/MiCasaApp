package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.micasaapp.Model.TrabajosHomeModel
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R

class TrabajosHomeAdapter(private val context: Context, private var trabajosHome: List<TrabajosHomeModel>) : BaseAdapter() {
    private var filteredTrabajosHome: List<TrabajosHomeModel> = trabajosHome

    fun filter(query: String) {
        filteredTrabajosHome = if (query.isBlank()) {
            trabajosHome
        } else {
            trabajosHome.filter { it.nombreTrabajador.contains(query, ignoreCase = true) }
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
            viewHolder.txtNombreTrabajador.text = nombreTrabajador
            viewHolder.txtProfecion.text = nombreProfecion
            viewHolder.txtDireccion.text =  direccion

            viewHolder.imagen.setImageDrawable(
                ContextCompat.getDrawable(context, UtilHelper.obtenerImagenPorCategoria(
                    idTrabajo
                )!!))
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
