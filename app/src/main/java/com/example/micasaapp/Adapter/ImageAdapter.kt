package com.example.micasaapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.FotoTrabajoModel
import com.ninodev.micasaapp.R

class ImageAdapter(private val images: List<FotoTrabajoModel>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val fotoTrabajo = images[position]
        holder.bind(fotoTrabajo)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(fotoTrabajo: FotoTrabajoModel) {
            Glide.with(imageView.context)
                .load(fotoTrabajo.url)
                .placeholder(R.drawable.placeholder_image) // Opcional: Añade un drawable de placeholder si deseas
                .error(R.drawable.error_image) // Opcional: Añade un drawable de error si deseas
                .into(imageView)
        }
    }
}
