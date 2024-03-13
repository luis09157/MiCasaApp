package com.example.micasaapp.Adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

    override fun getCount(): Int {
        return filteredSubCategorias.size
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

        with(filteredSubCategorias[position]) {
            viewHolder.txtTitulo.text = nombreSubcategoria
            viewHolder.imgPortada.setImageDrawable(ContextCompat.getDrawable(context, obtenerImagenPorCategoria(idCategoria)!!))
        }

        return rowView!!
    }

    override fun getItem(position: Int): Any {
        return filteredSubCategorias[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ViewHolder(view: View) {
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val imgPortada: ImageView = view.findViewById(R.id.imgPortada)
    }
}
