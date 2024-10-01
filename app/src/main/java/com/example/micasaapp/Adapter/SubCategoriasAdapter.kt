package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.SubCategoriasModel
import com.example.micasaapp.Util.UtilHelper.Companion.obtenerImagenPorCategoria
import com.ninodev.micasaapp.R

class SubCategoriaAdapter(private val context: Context, private var subCategorias: List<SubCategoriasModel>) : BaseAdapter() {
    private var filteredSubCategorias: List<SubCategoriasModel> = subCategorias

    fun filter(query: String) {
        filteredSubCategorias = if (query.isBlank()) {
            subCategorias
        } else {
            subCategorias.filter { it.nombreSubcategoria.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int = filteredSubCategorias.size

    override fun getItem(position: Int): SubCategoriasModel = filteredSubCategorias[position]

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

        with(viewHolder) {
            val subCategoria = getItem(position)
            txtTitulo.text = subCategoria.nombreSubcategoria
            Glide.with(imgPortada)
                .load(subCategoria.imagenSubcategoria)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imgPortada)
            imgPortada.setImageDrawable(ContextCompat.getDrawable(context, obtenerImagenPorCategoria(subCategoria.idCategoria)!!))
        }

        return view
    }

    private class ViewHolder(view: View) {
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val imgPortada: ImageView = view.findViewById(R.id.imgPortada)
    }
}
