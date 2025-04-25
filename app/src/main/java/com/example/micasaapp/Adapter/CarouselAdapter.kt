import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.micasaapp.Model.BannerModel
import com.google.android.material.chip.Chip
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.CarouselItemBinding

class CarouselAdapter(private var banners: List<BannerModel>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    // Interfaz para manejar clics en elementos del carrusel
    interface OnBannerClickListener {
        fun onBannerClick(banner: BannerModel, position: Int)
    }
    
    private var listener: OnBannerClickListener? = null
    
    fun setOnBannerClickListener(listener: OnBannerClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val currentBanner = banners[position % banners.size]
        holder.bind(currentBanner, position)
    }

    override fun getItemCount(): Int {
        return if (banners.isEmpty()) 0 else banners.size
    }

    fun updateBanners(newBanners: List<BannerModel>) {
        banners = newBanners
        notifyDataSetChanged()
    }

    inner class CarouselViewHolder(private val binding: CarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: BannerModel, position: Int) {
            // Aplicar efecto de imagen con bordes redondeados
            Glide.with(binding.imageView.context)
                .load(banner.imagenTrabajo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.imageView)
            
            // Configurar título
            binding.bannerTitle.text = banner.titulo ?: "Servicios profesionales"
            
            // Añadir subtítulo (generado dinámicamente si no existe)
            binding.bannerSubtitle.text = banner.descripcion ?: 
                "Encuentra los mejores servicios para tu hogar"
            
            // Indicador de página actual
            binding.pageIndicator.text = "${position + 1}/${banners.size}"
            
            // Configurar clic en el banner
            binding.root.setOnClickListener {
                listener?.onBannerClick(banner, position)
            }
        }
    }
}
