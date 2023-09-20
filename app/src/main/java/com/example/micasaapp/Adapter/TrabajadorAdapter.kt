package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.micasaapp.Model.CategoriaModel
import com.example.micasaapp.R

class TrabajadorAdapter(context: Context, val listCategorias: List<CategoriaModel>) : BaseAdapter() {
    private val layoutInflater = LayoutInflater.from(context)
    private val context = context

    override fun getCount(): Int {
        return listCategorias.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = layoutInflater.inflate(R.layout.recyclerview_trabajador, viewGroup, false)

            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder

        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }


        return rowView
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    private class ViewHolder(view: View?) {
    }
}