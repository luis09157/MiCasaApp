package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.UtilHelper.Companion.obtenerImagenPorCategoria
import com.ninodev.micasaapp.R

class CategoriaAdapter(private val context: Context, private var categorias: List<CategoriasModel>) : BaseAdapter() {
    private var filteredCategorias: List<CategoriasModel> = categorias

    fun filter(query: String) {
        filteredCategorias = if (query.isBlank()) {
            categorias
        } else {
            categorias.filter { it.nombreCategoria.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return filteredCategorias.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.recyclerview_categoria, viewGroup, false)
            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }

        with(filteredCategorias[position]) {
            viewHolder.txtTitulo.text = nombreCategoria
            viewHolder.imgPortada.setImageDrawable(ContextCompat.getDrawable(context, obtenerImagenPorCategoria(idCategoria)!!))
        }

        return rowView!!
    }

    override fun getItem(position: Int): Any {
        return filteredCategorias[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ViewHolder(view: View) {
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val imgPortada: ImageView = view.findViewById(R.id.imgPortada)
    }
}
