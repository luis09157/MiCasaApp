package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Data.CategoriasModel
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
                txtTitulo.text = categoria.nombreCategoria
                
                // Cargar imagen con Glide
                Glide.with(context)
                    .load(categoria.imagenCategoria)
                    .into(imgPortada)

                // Configurar clic
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
