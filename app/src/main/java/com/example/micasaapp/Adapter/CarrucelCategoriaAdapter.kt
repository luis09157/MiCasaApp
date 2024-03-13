package com.example.micasaapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import de.hdodenhof.circleimageview.CircleImageView

class CarrucelCategoriaAdapter(private val mContext: Context, private var categorias: List<CategoriasModel>) :
    RecyclerView.Adapter<CarrucelCategoriaAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "RecyclerViewAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called.")

        holder.image.setImageDrawable(
            ContextCompat.getDrawable(mContext, UtilHelper.obtenerImagenPorCategoria(
                categorias[position].idCategoria
            )!!))

        holder.name.text = categorias[position].nombreCategoria

        holder.image.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: CircleImageView = itemView.findViewById(R.id.image_view)
        var name: TextView = itemView.findViewById(R.id.name)
    }
}
