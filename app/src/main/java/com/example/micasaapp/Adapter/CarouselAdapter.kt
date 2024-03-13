package com.example.micasaapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.CarouselItemBinding

class CarouselAdapter : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    private val imageResources = listOf(
        R.drawable.carrucel_1,
        R.drawable.carrucel_2,
        R.drawable.carrucel_3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val currentImageResource = imageResources[position]
        holder.bind(currentImageResource)
    }

    override fun getItemCount(): Int {
        return imageResources.size
    }

    inner class CarouselViewHolder(private val binding: CarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageResource: Int) {
            binding.imageView.setImageResource(imageResource)
        }
    }
}
