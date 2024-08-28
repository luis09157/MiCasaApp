import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Model.BannerModel
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.CarouselItemBinding

class CarouselAdapter(private val banners: List<BannerModel>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val currentBanner = banners[position % banners.size]
        holder.bind(currentBanner)
    }

    override fun getItemCount(): Int {
        return banners.size
    }

    inner class CarouselViewHolder(private val binding: CarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: BannerModel) {
            Glide.with(binding.imageView.context)
                .load(banner.imagenTrabajo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.imageView)
        }
    }
}
