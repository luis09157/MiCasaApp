package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Data.CategoriasModel
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.RecyclerviewCategoriaBinding

class CategoriaAdapter(
    private val context: Context,
    private val categorias: List<CategoriasModel>,
    private val onCategoriaClick: (CategoriasModel) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(private val binding: RecyclerviewCategoriaBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(categoria: CategoriasModel) {
            with(binding) {
                // Configurar título y descripción
                txtTitulo.text = categoria.nombreCategoria
                txtDescripcion.text = "Encuentra expertos en ${categoria.nombreCategoria.lowercase()}"
                
                // Configurar contador de servicios
                val servicios = categoria.cantidadServicios ?: 0
                chipCount.text = "$servicios servicios"
                
                // Mostrar/ocultar chip de disponibilidad
                chipDisponible.visibility = if (categoria.disponible == true) View.VISIBLE else View.GONE
                
                // Cargar imagen con Glide y efectos
                Glide.with(context)
                    .load(categoria.imagenCategoria)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .centerCrop()
                    .into(imgPortada)

                // Configurar clic con efecto ripple
                root.setOnClickListener {
                    onCategoriaClick(categoria)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = RecyclerviewCategoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    override fun getItemCount(): Int = categorias.size
}
