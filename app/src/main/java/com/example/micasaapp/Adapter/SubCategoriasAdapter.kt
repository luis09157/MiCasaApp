package com.example.micasaapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.SubCategoriasModel
import com.ninodev.micasaapp.R
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView

class SubCategoriasAdapter(
    private var subcategorias: List<SubCategoriasModel>,
    private val onItemClick: (SubCategoriasModel) -> Unit
) : RecyclerView.Adapter<SubCategoriasAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imgSubcategoria)
        private val textNombre: TextView = itemView.findViewById(R.id.txtNombreSubcategoria)
        private val chipDisponibilidad: Chip = itemView.findViewById(R.id.chipDisponible)

        fun bind(subcategoria: SubCategoriasModel) {
            textNombre.text = subcategoria.nombreSubcategoria

            Glide.with(itemView.context)
                .load(subcategoria.imagenSubcategoria)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(imageView)

            chipDisponibilidad.visibility = if (subcategoria.disponible == true) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onItemClick(subcategoria)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subcategoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subcategorias[position])
    }

    override fun getItemCount() = subcategorias.size

    fun updateData(newSubcategorias: List<SubCategoriasModel>) {
        subcategorias = newSubcategorias
        notifyDataSetChanged()
    }
} 