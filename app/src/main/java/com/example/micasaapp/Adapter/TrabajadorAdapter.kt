package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.TrabajadorModel
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView
import com.ninodev.micasaapp.R

class TrabajadorAdapter(
    private val context: Context,
    private val trabajadores: List<TrabajadorModel>,
    private val onItemClick: (TrabajadorModel) -> Unit
) : RecyclerView.Adapter<TrabajadorAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPerfil: ShapeableImageView = view.findViewById(R.id.imgPerfil)
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtOficio: TextView = view.findViewById(R.id.txtOficio)
        val chipRating: Chip = view.findViewById(R.id.chipRating)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(trabajadores[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_trabajador, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trabajador = trabajadores[position]

        // Cargar imagen de perfil
        Glide.with(context)
            .load(trabajador.imagenPerfil)
            .placeholder(R.drawable.placeholder_profile)
            .error(R.drawable.error_profile)
            .circleCrop()
            .into(holder.imgPerfil)

        // Configurar textos
        holder.txtNombre.text = trabajador.nombreCompleto
        holder.txtOficio.text = trabajador.descripcion

        // Configurar rating
        holder.chipRating.text = String.format("%.1f", 4.5) // Por ahora hardcodeado hasta que tengamos el rating real
    }

    override fun getItemCount() = trabajadores.size
}
