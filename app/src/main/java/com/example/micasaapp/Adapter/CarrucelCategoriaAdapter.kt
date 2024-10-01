package com.example.micasaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import de.hdodenhof.circleimageview.CircleImageView

class CarrucelCategoriaAdapter(
    private val mContext: Context,
    private var categorias: List<CategoriasModel>,
    private val listener: (CategoriasModel) -> Unit
) : RecyclerView.Adapter<CarrucelCategoriaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_categoria_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria)
    }

    override fun getItemCount(): Int = categorias.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: CircleImageView = itemView.findViewById(R.id.image_view)
        private val name: TextView = itemView.findViewById(R.id.name)

        fun bind(categoria: CategoriasModel) {
            Glide.with(image)
                .load(categoria.imagenCategoria)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(image)
            name.text = categoria.nombreCategoria
            itemView.setOnClickListener { listener(categoria) }
        }
    }
}
