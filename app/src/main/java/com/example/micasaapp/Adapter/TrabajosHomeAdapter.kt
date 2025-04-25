package com.example.micasaapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.micasaapp.Model.TrabajadorModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ninodev.micasaapp.R

class TrabajosHomeAdapter(
    private val context: Context,
    private val trabajos: List<TrabajadorModel>,
    private val onItemClick: (TrabajadorModel) -> Unit
) : BaseAdapter() {
    private val TAG = "TrabajosHomeAdapter"
    private var filteredTrabajosHome: List<TrabajadorModel> = trabajos
    private val favoritos = mutableSetOf<Int>()

    override fun getCount(): Int = filteredTrabajosHome.size

    override fun getItem(position: Int): Any = filteredTrabajosHome[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.recyclerview_trabajos_home, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val trabajador = filteredTrabajosHome[position]
        
        try {
            // Configurar datos básicos
            viewHolder.apply {
                txtNombreTrabajador.text = trabajador.nombreCompleto
                txtProfecion.text = trabajador.categorias
                txtDireccion.text = trabajador.direccion
                chipRating.text = String.format("%.1f★", trabajador.calificacion)
            }

            // Configurar imagen
            loadImage(trabajador, viewHolder.imagen)

            // Configurar botón favorito
            val isFavorito = favoritos.contains(position)
            configureHeartButton(viewHolder.btnMeGusta, isFavorito)
            
            // Configurar listeners
            viewHolder.btnMeGustaContainer.setOnClickListener {
                toggleFavorito(position, viewHolder.btnMeGusta)
            }

            // Configurar clic en el item completo
            view.setOnClickListener {
                onItemClick(trabajador)
            }

            // El botón de ver perfil también lleva al detalle
            viewHolder.viewProfile.setOnClickListener {
                onItemClick(trabajador)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error configurando vista para trabajador: ${trabajador.nombreCompleto}", e)
            handleError(viewHolder, trabajador)
        }

        return view
    }

    private fun loadImage(trabajador: TrabajadorModel, imageView: ImageView) {
        val imageUrl = trabajador.imagenPerfil.takeIf { it.isNotEmpty() } 
            ?: trabajador.imagenTrabajo

        Log.d(TAG, "Cargando imagen para ${trabajador.nombreCompleto}: $imageUrl")

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()

        try {
            Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(imageView)
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando imagen para ${trabajador.nombreCompleto}: ${e.message}")
            imageView.setImageResource(R.drawable.error_image)
        }
    }

    private fun handleError(viewHolder: ViewHolder, trabajador: TrabajadorModel) {
        viewHolder.apply {
            imagen.setImageResource(R.drawable.error_image)
            txtNombreTrabajador.text = trabajador.nombreCompleto.takeIf { it.isNotEmpty() } 
                ?: "Nombre no disponible"
            txtProfecion.text = trabajador.categorias.takeIf { it.isNotEmpty() } 
                ?: "Profesión no disponible"
            txtDireccion.text = trabajador.direccion.takeIf { it.isNotEmpty() } 
                ?: "Dirección no disponible"
            chipRating.text = "0.0★"
        }
    }

    private fun toggleFavorito(position: Int, imageView: ImageView) {
        val isFavorito = favoritos.contains(position)
        if (isFavorito) {
            favoritos.remove(position)
            configureHeartButton(imageView, false)
            Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
        } else {
            favoritos.add(position)
            configureHeartButton(imageView, true)
            Toast.makeText(context, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureHeartButton(imageView: ImageView, isFavorite: Boolean) {
        imageView.setImageResource(
            if (isFavorite) R.drawable.ic_corazon_filled else R.drawable.ic_corazon
        )
        if (isFavorite) {
            imageView.setColorFilter(context.getColor(R.color.md_theme_primary))
        } else {
            imageView.clearColorFilter()
        }
    }

    fun filter(query: String) {
        filteredTrabajosHome = if (query.isBlank()) {
            trabajos
        } else {
            trabajos.filter { it.nombreCompleto.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
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
