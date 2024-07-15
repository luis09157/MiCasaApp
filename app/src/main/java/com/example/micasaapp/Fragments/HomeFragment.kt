package com.example.micasaapp.ui.home

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
import com.example.micasaapp.Adapter.TrabajosHomeAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Fragments.CategoriasFragment
import com.example.micasaapp.Model.TrabajosHomeModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.databinding.FragmentHomeBinding
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {
    private val TAG: String = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var listCategoriasMoshi: MutableList<CategoriasModel> = mutableListOf()
    private var trabajosList: List<TrabajosHomeModel> = listOf()

    private val mNames: ArrayList<String> = ArrayList()
    private val mImageUrls: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initCarousel()
        FetchCategoriasTask().execute()
        FetchTrabajosTask().execute() // Nueva tarea para cargar los trabajos

        return root
    }

    private fun initCarousel() {
        val carouselAdapter = CarouselAdapter()
        binding.viewPager.adapter = carouselAdapter

        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.viewPager)

        binding.viewPager.postDelayed({
            startAutoScroll()
        }, AUTO_SCROLL_START_DELAY)
    }

    private fun startAutoScroll() {
        val carouselAdapter = binding.viewPager.adapter as CarouselAdapter
        val totalPages = carouselAdapter.itemCount
        val currentPage = binding.viewPager.currentItem

        val nextPage = (currentPage + 1) % totalPages
        binding.viewPager.setCurrentItem(nextPage, true)

        binding.viewPager.postDelayed({
            if (isAdded) {
                startAutoScroll()
            }
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
        private const val AUTO_SCROLL_INTERVAL = 3000L
        private const val AUTO_SCROLL_START_DELAY = 10000L
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

            if (result != null) {
                listCategoriasMoshi.addAll(result)
                if (listCategoriasMoshi.isNotEmpty()) {
                    initRecyclerView()
                }
            } else {
                val exception = Exception("Error obteniendo categorías")
                val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
                Log.e("FetchCategoriasTask", "Error fetching categorias: $errorMessage", exception)
                MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar las categorías")
            }
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = CarrucelCategoriaAdapter(requireContext(), listCategoriasMoshi)
        binding.recyclerView.adapter = adapter
    }

    private inner class FetchTrabajosTask : AsyncTask<Void, Void, List<TrabajosHomeModel>>() {
        override fun doInBackground(vararg params: Void?): List<TrabajosHomeModel>? {
            return try {
                // Simulación de obtener datos de trabajos
                listOf(
                    TrabajosHomeModel(1, "Juan Pérez", "Electricista", "Calle 123", "url_de_imagen_1"),
                    TrabajosHomeModel(2, "María López", "Plomero", "Avenida 456", "url_de_imagen_2"),
                    TrabajosHomeModel(3, "María López", "Plomero", "Avenida 456", "url_de_imagen_2"),
                    TrabajosHomeModel(4, "María López", "Plomero", "Avenida 456", "url_de_imagen_2"),
                    TrabajosHomeModel(5, "María López", "Plomero", "Avenida 456", "url_de_imagen_2"),
                    TrabajosHomeModel(6, "María López", "Plomero", "Avenida 456", "url_de_imagen_2")
                )
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: List<TrabajosHomeModel>?) {
            super.onPostExecute(result)

            if (result != null) {
                trabajosList = result
                initGridView()
            } else {
                val exception = Exception("Error obteniendo trabajos")
                val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
                Log.e("FetchTrabajosTask", "Error fetching trabajos: $errorMessage", exception)
                MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar los trabajos")
            }
        }
    }

    private fun initGridView() {
        val adapter = TrabajosHomeAdapter(requireContext(), trabajosList)
        binding.gridListaTrabajos.adapter = adapter
    }
}
