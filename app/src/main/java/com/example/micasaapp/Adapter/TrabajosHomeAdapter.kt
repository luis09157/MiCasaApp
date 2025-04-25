package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.micasaapp.Model.TrabajadorModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ninodev.micasaapp.R
import kotlin.random.Random

class TrabajosHomeAdapter(private val context: Context, private var trabajosHome: List<TrabajadorModel>) : BaseAdapter() {
    private var filteredTrabajosHome: List<TrabajadorModel> = trabajosHome
    private val favoritos = mutableSetOf<Int>() // Simular favoritos

    fun filter(query: String) {
        filteredTrabajosHome = if (query.isBlank()) {
            trabajosHome
        } else {
            trabajosHome.filter { it.nombreCompleto.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return filteredTrabajosHome.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.recyclerview_trabajos_home, viewGroup, false)
            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }

        val trabajador = filteredTrabajosHome[position]
        
        // Configurar datos del trabajador
        viewHolder.txtNombreTrabajador.text = trabajador.nombreCompleto
        viewHolder.txtProfecion.text = trabajador.categorias
        viewHolder.txtDireccion.text = trabajador.direccion

        // Añadir calificación (simulada)
        val rating = 4.0 + (Random.nextDouble() * 1.0)
        viewHolder.chipRating.text = String.format("%.1f", rating)

        // Usar Glide para cargar la imagen
        Glide.with(context)
            .load(trabajador.imagenTrabajo)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(viewHolder.imagen)

        // Configurar botón favorito
        val isFavorito = favoritos.contains(position)
        configureHeartButton(viewHolder.btnMeGusta, isFavorito)
        
        viewHolder.btnMeGustaContainer.setOnClickListener {
            // Alterna estado de favorito
            if (isFavorito) {
                favoritos.remove(position)
            } else {
                favoritos.add(position)
            }
            configureHeartButton(viewHolder.btnMeGusta, !isFavorito)
            Toast.makeText(context, if (!isFavorito) "Añadido a favoritos" else "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
        }

        // Configurar el botón de ver perfil
        viewHolder.viewProfile.setOnClickListener {
            // Implementar navegación al perfil
            Toast.makeText(context, "Ver perfil de ${trabajador.nombreCompleto}", Toast.LENGTH_SHORT).show()
        }

        return rowView!!
    }

    private fun configureHeartButton(imageView: ImageView, isFavorite: Boolean) {
        if (isFavorite) {
            imageView.setImageResource(R.drawable.ic_corazon_filled)
            imageView.setColorFilter(context.getColor(R.color.md_theme_primary))
        } else {
            imageView.setImageResource(R.drawable.ic_corazon)
            imageView.clearColorFilter()
        }
    }

    override fun getItem(position: Int): Any {
        return filteredTrabajosHome[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ViewHolder(view: View) {
        val txtNombreTrabajador: TextView = view.findViewById(R.id.txtNombreTrabajador)
        val txtProfecion: Chip = view.findViewById(R.id.txtProfecion)
        val txtDireccion: TextView = view.findViewById(R.id.txtDireccion)
        val imagen: ImageView = view.findViewById(R.id.imagen)
        val btnMeGusta: ImageView = view.findViewById(R.id.btnMeGusta)
        val btnMeGustaContainer: View = view.findViewById(R.id.btnMeGustaContainer)
        val chipRating: Chip = view.findViewById(R.id.chipRating)
        val viewProfile: MaterialButton = view.findViewById(R.id.viewProfile)
        val chipGroupServicios: ChipGroup = view.findViewById(R.id.chipGroupServicios)
    }
}
