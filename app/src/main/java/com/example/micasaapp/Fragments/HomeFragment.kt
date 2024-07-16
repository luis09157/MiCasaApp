package com.example.micasaapp.ui.home

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.Util
import com.example.micasaapp.Adapter.CarouselAdapter
import com.example.micasaapp.Adapter.CarrucelCategoriaAdapter
import com.example.micasaapp.Adapter.TrabajosHomeAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Fragments.CategoriasDetalleFragment
import com.example.micasaapp.Fragments.CategoriasFragment
import com.example.micasaapp.Model.TrabajosHomeModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentHomeBinding
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {
    private val TAG: String = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val DOUBLE_BACK_PRESS_INTERVAL = 5000 // Intervalo en milisegundos para presionar dos veces BACK
    private var doubleBackToExitPressedOnce = false
    private val handler = Handler()
    private var listCategoriasMoshi: MutableList<CategoriasModel> = mutableListOf()
    private var trabajosList: List<TrabajosHomeModel> = listOf()
    companion object {
        private const val AUTO_SCROLL_INTERVAL = 3000L
        private const val AUTO_SCROLL_START_DELAY = 10000L
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showLoadingAnimation()

        initCarousel()
        initListeners()
        FetchTrabajosTask().execute() // Nueva tarea para cargar los trabajos
        FetchCategoriasTask().execute()


        return root
    }
    private fun initCarousel() {
        val carouselAdapter = CarouselAdapter()
        binding.viewPager.adapter = carouselAdapter

        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.viewPager)

        binding.viewPager.postDelayed({
            if (isAdded && _binding != null) {
                startAutoScroll()
            }
        }, AUTO_SCROLL_START_DELAY)
    }
    private fun startAutoScroll() {
        val carouselAdapter = binding.viewPager.adapter as CarouselAdapter
        val totalPages = carouselAdapter.itemCount
        val currentPage = binding.viewPager.currentItem

        val nextPage = (currentPage + 1) % totalPages
        binding.viewPager.setCurrentItem(nextPage, true)

        binding.viewPager.postDelayed({
            if (isAdded && _binding != null) {
                startAutoScroll()
            }
        }, AUTO_SCROLL_INTERVAL)
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
                if (doubleBackToExitPressedOnce) {
                    // Si ya se ha presionado dos veces BACK, salir de la aplicación
                    activity?.finish()
                } else {
                    // Mostrar mensaje o realizar alguna acción al primer press
                    doubleBackToExitPressedOnce = true
                    MessageUtil.showInfoMessage(requireContext(), requireView(),"Presiona nuevamente para salir")

                    // Restablecer la bandera después del intervalo definido
                    handler.postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, DOUBLE_BACK_PRESS_INTERVAL.toLong())
                }
                true
            } else false
        }
    }

    private fun initListeners(){
        binding.btnCategorias.setOnClickListener {
            UtilHelper.replaceFragment(requireContext(), CategoriasFragment())
        }
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

            hideLoadingAnimation()
        }
    }
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategorias.layoutManager = layoutManager
        val adapter = CarrucelCategoriaAdapter(requireContext(), listCategoriasMoshi) { categoria ->
            onCategoriaClicked(categoria)
        }
        binding.recyclerViewCategorias.adapter = adapter
    }
    private fun onCategoriaClicked(categoria: CategoriasModel) {
        DataConfig.IDCATEGORIA = categoria.idCategoria
        UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
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
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
    private fun showLoadingAnimation() {
        binding.lottieAnimationView.visibility = View.VISIBLE
        binding.contHome.visibility = View.GONE
        binding.lottieAnimationView.setAnimation(R.raw.avionsito_loading) // Reemplaza con tu archivo JSON
        binding.lottieAnimationView.playAnimation()
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.visibility = View.GONE
        binding.contHome.visibility = View.VISIBLE
        binding.lottieAnimationView.cancelAnimation()
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }
    private fun showNodata(){
        binding.lottieAnimationView.visibility = View.GONE
        binding.contHome.visibility = View.GONE
        binding.lottieAnimationView.cancelAnimation()
        binding.fragmentNoData.contNoData.visibility = View.VISIBLE
    }

}
