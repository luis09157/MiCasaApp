package com.example.micasaapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.SubCategoriasModel
import com.google.android.material.imageview.ShapeableImageView
import com.ninodev.micasaapp.R

class SubCategoriaAdapter(
    private val context: Context,
    private var subCategorias: List<SubCategoriasModel>,
    private val onItemClick: (SubCategoriasModel) -> Unit
) : RecyclerView.Adapter<SubCategoriaAdapter.ViewHolder>() {

    private var filteredSubCategorias: List<SubCategoriasModel> = subCategorias

    fun filter(query: String) {
        filteredSubCategorias = if (query.isBlank()) {
            subCategorias
        } else {
            subCategorias.filter { it.nombreSubcategoria.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_subcategoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subCategoria = filteredSubCategorias[position]
        
        with(holder) {
            txtNombreSubcategoria.text = subCategoria.nombreSubcategoria
            Glide.with(imgSubcategoria)
                .load(subCategoria.imagenSubcategoria)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imgSubcategoria)

            itemView.setOnClickListener {
                onItemClick(subCategoria)
            }
        }
    }

    override fun getItemCount(): Int = filteredSubCategorias.size

    fun getFilteredItemCount(): Int = filteredSubCategorias.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombreSubcategoria: TextView = view.findViewById(R.id.txtNombreSubcategoria)
        val imgSubcategoria: ShapeableImageView = view.findViewById(R.id.imgSubcategoria)
    }
}
