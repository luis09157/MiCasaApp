package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.UtilHelper
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.ninodev.micasaapp.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random

class CarrucelCategoriaAdapter(
    private val mContext: Context,
    private var categorias: List<CategoriasModel>,
    private val listener: (CategoriasModel) -> Unit
) : RecyclerView.Adapter<CarrucelCategoriaAdapter.ViewHolder>() {

    // Lista de categorías populares (en una aplicación real, esto vendría del backend)
    private val popularCategorias = mutableSetOf<Int>()
    
    init {
        // Simular categorías populares para demostración
        if (categorias.size > 3) {
            // Marcar aleatoriamente 2 categorías como populares
            val indices = categorias.indices.shuffled().take(2)
            popularCategorias.addAll(indices)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_categoria_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria, position in popularCategorias)
    }

    override fun getItemCount(): Int = categorias.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ShapeableImageView = itemView.findViewById(R.id.image_view)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val categoryCount: MaterialCardView = itemView.findViewById(R.id.categoryCount)
        private val profesionalCount: TextView = itemView.findViewById(R.id.profesionalCount)
        private val cardRecommended: MaterialCardView = itemView.findViewById(R.id.cardRecommended)

        fun bind(categoria: CategoriasModel, isPopular: Boolean) {
            // Aplicar esquinas redondeadas a la imagen
            val cornerRadius = itemView.context.resources.getDimensionPixelSize(R.dimen.carousel_corner_radius)
            val requestOptions = RequestOptions().transform(RoundedCorners(cornerRadius))
            
            Glide.with(image)
                .load(categoria.imagenCategoria)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .centerCrop()
                .into(image)
                
            name.text = categoria.nombreCategoria
            
            // Mostrar contador de profesionales (simulación)
            val count = Random.nextInt(5, 30)
            profesionalCount.text = count.toString()
            
            // Mostrar etiqueta "Popular" si es una categoría popular
            if (isPopular) {
                cardRecommended.visibility = View.VISIBLE
            } else {
                cardRecommended.visibility = View.GONE
            }
            
            // Configurar clic en toda la tarjeta
            itemView.setOnClickListener { listener(categoria) }
        }
    }
    
    // Actualizar la lista de categorías
    fun updateCategorias(newCategorias: List<CategoriasModel>) {
        categorias = newCategorias
        notifyDataSetChanged()
    }
}
