package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.micasaapp.Data.CategoriasModel
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

    override fun getCount(): Int = filteredCategorias.size

    override fun getItem(position: Int): CategoriasModel = filteredCategorias[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.recyclerview_categoria, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val categoria = getItem(position)
        viewHolder.apply {
            txtTitulo.text = categoria.nombreCategoria
            Glide.with(imgPortada)
                .load(categoria.imagenCategoria)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imgPortada)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val imgPortada: ImageView = view.findViewById(R.id.imgPortada)
    }
}
