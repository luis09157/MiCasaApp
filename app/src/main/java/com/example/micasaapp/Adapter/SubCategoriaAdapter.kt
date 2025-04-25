package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.SubCategoriasModel
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.ItemSubcategoriaBinding

class SubCategoriaAdapter(
    private val context: Context,
    private var subcategorias: List<SubCategoriasModel>,
    private val onSubcategoriaClick: (SubCategoriasModel) -> Unit
) : RecyclerView.Adapter<SubCategoriaAdapter.ViewHolder>() {

    private var subcategoriasFiltradas = subcategorias.toMutableList()

    inner class ViewHolder(private val binding: ItemSubcategoriaBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(subcategoria: SubCategoriasModel) {
            with(binding) {
                // Configurar nombre
                txtNombreSubcategoria.text = subcategoria.nombreSubcategoria
                
                // Cargar imagen con Glide
                Glide.with(context)
                    .load(subcategoria.imagenSubcategoria)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .centerCrop()
                    .into(imgSubcategoria)

                // Configurar clic
                root.setOnClickListener {
                    onSubcategoriaClick(subcategoria)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSubcategoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subcategoriasFiltradas[position])
    }

    override fun getItemCount(): Int = subcategoriasFiltradas.size

    fun filter(query: String) {
        subcategoriasFiltradas.clear()
        if (query.isEmpty()) {
            subcategoriasFiltradas.addAll(subcategorias)
        } else {
            val busqueda = query.lowercase().trim()
            subcategoriasFiltradas.addAll(
                subcategorias.filter { subcategoria ->
                    subcategoria.nombreSubcategoria.lowercase().contains(busqueda)
                }
            )
        }
        notifyDataSetChanged()
    }

    fun getFilteredItemCount(): Int = subcategoriasFiltradas.size
} 