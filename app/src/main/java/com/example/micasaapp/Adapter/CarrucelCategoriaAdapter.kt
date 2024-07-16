package com.example.micasaapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import de.hdodenhof.circleimageview.CircleImageView

class CarrucelCategoriaAdapter(
    private val mContext: Context,
    private var categorias: List<CategoriasModel>,
    private val listener: (CategoriasModel) -> Unit // Añadir el listener aquí
) : RecyclerView.Adapter<CarrucelCategoriaAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "RecyclerViewAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_categoria_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called.")

        holder.image.setImageDrawable(
            ContextCompat.getDrawable(mContext, UtilHelper.obtenerImagenPorCategoria(
                categorias[position].idCategoria
            )!!)
        )

        holder.name.text = categorias[position].nombreCategoria

        holder.itemView.setOnClickListener {
            listener(categorias[position]) // Llamar al listener cuando se haga clic en el elemento
        }
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: CircleImageView = itemView.findViewById(R.id.image_view)
        var name: TextView = itemView.findViewById(R.id.name)
    }
}
