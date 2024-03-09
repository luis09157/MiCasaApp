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
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R


class SubCategoriaAdapter(context: Context, val listSubCategorias: MutableList<SubCategoriasModel>) : BaseAdapter() {
    private val layoutInflater = LayoutInflater.from(context)
    private val context = context

    override fun getCount(): Int {
        return listSubCategorias.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = layoutInflater.inflate(R.layout.recyclerview_categoria, viewGroup, false)

            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder

        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }


        viewHolder.txtTitulo.text = listSubCategorias.get(position).nombreSubcategoria
        viewHolder.imgPortada.setImageDrawable(
            ContextCompat.getDrawable(
            context, UtilHelper.obtenerImagenPorCategoria(listSubCategorias.get(position).idCategoria)!!))


        return rowView
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    private class ViewHolder(view: View?) {
        val txtTitulo = view?.findViewById(R.id.txtTitulo) as TextView
        val imgPortada = view?.findViewById(R.id.imgPortada) as ImageView
    }
}