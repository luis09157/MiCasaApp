package com.example.micasaapp.Fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.micasaapp.Adapter.CarouselAdapter
import com.example.micasaapp.Adapter.CarrucelCategoriaAdapter
import com.example.micasaapp.Adapter.CategoriaAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.databinding.FragmentHomeBinding
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {
    val TAG: String = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null

    private var listCategoriasMoshi: MutableList<CategoriasModel> = mutableListOf()
    private val binding get() = _binding!!

    private val mNames: ArrayList<String> = ArrayList()
    private val mImageUrls: ArrayList<String> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initCarousel()
        //getImages()
        FetchCategoriasTask().execute()

        return root
    }

    private fun initCarousel() {
        val carouselAdapter = CarouselAdapter()
        binding.viewPager.adapter = carouselAdapter

        // Configurar los indicadores circulares
        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.viewPager)

        // Puedes personalizar el aspecto de los indicadores si es necesario
        // indicator.setFillColor(R.color.indicator_fill_color)
        // indicator.setUnFillColor(R.color.indicator_unfill_color)
        // ...

        // Iniciar el auto-scroll del carrusel
        binding.viewPager.postDelayed({
            startAutoScroll()
        }, AUTO_SCROLL_START_DELAY)
    }

    private fun startAutoScroll() {
        val carouselAdapter = binding.viewPager.adapter as CarouselAdapter
        val totalPages = carouselAdapter.itemCount
        val currentPage = binding.viewPager.currentItem

        // Calcular la siguiente página para el auto-scroll
        val nextPage = (currentPage + 1) % totalPages

        // Realizar un desplazamiento suave a la siguiente página
        binding.viewPager.setCurrentItem(nextPage, true)

        // Programar el próximo auto-scroll
        binding.viewPager.postDelayed({
            startAutoScroll()
        }, AUTO_SCROLL_INTERVAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        if (view == null) {
            return
        }
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                UtilHelper.replaceFragment(requireContext(), CategoriasFragment())
                true
            } else false
        }
    }

    companion object {
        private const val AUTO_SCROLL_INTERVAL = 3000L // 3 segundos
        private const val AUTO_SCROLL_START_DELAY = 10000L // 10 segundos
    }



    private fun getImages() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.")

        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg")
        mNames.add("Havasu Falls")

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg")
        mNames.add("Trondheim")

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg")
        mNames.add("Portugal")

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg")
        mNames.add("Rocky Mountain National Park")

        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg")
        mNames.add("Mahahual")

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg")
        mNames.add("Frozen Lake")

        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg")
        mNames.add("White Sands Desert")

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg")
        mNames.add("Australia")

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg")
        mNames.add("Washington")

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = CarrucelCategoriaAdapter(requireContext(), listCategoriasMoshi)
        binding.recyclerView.adapter = adapter
    }

    private inner class FetchCategoriasTask : AsyncTask<Void, Void, List<CategoriasModel>>() {

        override fun doInBackground(vararg params: Void?): List<CategoriasModel>? {
            return try {
                val apiClient = ApiClient(Config._URLApi)
                apiClient.getCategorias()
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: List<CategoriasModel>?) {
            super.onPostExecute(result)

           // hideLoadingAnimation()

            if (result != null) {
                listCategoriasMoshi.addAll(result)
                if(listCategoriasMoshi.size > 0 ){

                   initRecyclerView()
                }else{
                   // showNodata()
                }
            } else {
                // Handle error
                val exception = Exception("Error obteniendo categorías")
                val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
                Log.e("FetchCategoriasTask", "Error fetching categorias: $errorMessage", exception)
                MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar las categorías")
                //showNodata()

                // Puedes mostrar el mensaje de error al usuario, por ejemplo, mediante un Toast o un AlertDialog
            }
        }
    }

}
