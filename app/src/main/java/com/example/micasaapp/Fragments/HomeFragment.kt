package com.example.micasaapp.ui.home

import CarouselAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.micasaapp.Adapter.CarrucelCategoriaAdapter
import com.example.micasaapp.Adapter.TrabajosHomeAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Model.BannerModel
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Fragments.CategoriasDetalleFragment
import com.example.micasaapp.Fragments.CategoriasFragment
import com.example.micasaapp.Model.TrabajosHomeModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {
    private val TAG: String = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val DOUBLE_BACK_PRESS_INTERVAL = 5000L // Intervalo en milisegundos para presionar dos veces BACK
    private var doubleBackToExitPressedOnce = false
    private var listCategoriasMoshi: MutableList<CategoriasModel> = mutableListOf()
    private var trabajosList: List<TrabajosHomeModel> = listOf()
    private var bannersList: List<BannerModel> = listOf()

    companion object {
        private const val AUTO_SCROLL_INTERVAL = 4000L
        private const val AUTO_SCROLL_START_DELAY = 10000L
    }

    private var autoScrollRunnable: Runnable? = null
    private val autoScrollHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showLoadingAnimation()
        initCarousel()
        initListeners()
        fetchBanners() // Agregar esta línea para cargar los banners
        fetchCategorias()
        fetchTrabajosHome()

        return root
    }

    private fun initCarousel() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val banners = withContext(Dispatchers.IO) { apiClient.getBanners() }
                if (banners.isNotEmpty()) {
                    val carouselAdapter = CarouselAdapter(banners)
                    binding.viewPager.adapter = carouselAdapter
                    binding.indicator.setViewPager(binding.viewPager)

                    // Configura el ViewPager2
                    binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    binding.viewPager.setCurrentItem(0, false)

                    // Inicia el auto-scroll
                    startAutoScroll()
                } else {
                    showNoData()
                }
            } catch (e: Exception) {
                hideLoadingAnimation()
                handleNetworkError(e, "Error al cargar los banners")
            } finally {

            }
        }
    }

    private fun startAutoScroll() {
        autoScrollRunnable?.let {
            autoScrollHandler.removeCallbacks(it)
        }

        autoScrollRunnable = object : Runnable {
            override fun run() {
                val totalPages = binding.viewPager.adapter?.itemCount ?: 0
                if (totalPages > 0) {
                    val currentPage = binding.viewPager.currentItem
                    val nextPage = (currentPage + 1) % totalPages
                    binding.viewPager.setCurrentItem(nextPage, true)
                    autoScrollHandler.postDelayed(this, AUTO_SCROLL_INTERVAL)
                }
            }
        }

        autoScrollHandler.postDelayed(autoScrollRunnable!!, AUTO_SCROLL_START_DELAY)
    }

    private fun updateCarouselWithBanners() {
        val carouselAdapter = CarouselAdapter(bannersList)
        binding.viewPager.adapter = carouselAdapter
        binding.indicator.setViewPager(binding.viewPager)

        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.setCurrentItem(0, false)

        startAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        view?.apply {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (doubleBackToExitPressedOnce) {
                        activity?.finish()
                    } else {
                        doubleBackToExitPressedOnce = true
                        MessageUtil.showInfoMessage(requireContext(), this, "Presiona nuevamente para salir")
                        postDelayed({ doubleBackToExitPressedOnce = false }, DOUBLE_BACK_PRESS_INTERVAL)
                    }
                    true
                } else false
            }
        }
    }

    private fun initListeners() {
        binding.btnCategorias.setOnClickListener {
            UtilHelper.replaceFragment(requireContext(), CategoriasFragment())
        }
    }

    private fun fetchCategorias() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val categorias = withContext(Dispatchers.IO) { apiClient.getCategorias() }
                if (!categorias.isNullOrEmpty()) {
                    listCategoriasMoshi.addAll(categorias)
                    initRecyclerView()
                } else {
                    showNoData()
                }
            } catch (e: Exception) {
                hideLoadingAnimation()
                handleNetworkError(e, "Error al cargar las categorías")
            } finally {
               // hideLoadingAnimation()
            }
        }
    }

    private fun fetchTrabajosHome() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val trabajos = withContext(Dispatchers.IO) { apiClient.getTrabajosHome() }
                if (trabajos.isNotEmpty()) {
                    trabajosList = trabajos
                    initGridView()
                } else {
                    showNoData()
                }
            } catch (e: Exception) {
                handleNetworkError(e, "Error al cargar los trabajos")
            } finally {
                hideLoadingAnimation()
            }
        }
    }

    private fun fetchBanners() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val banners = withContext(Dispatchers.IO) { apiClient.getBanners() }
                if (banners.isNotEmpty()) {
                    bannersList = banners
                    updateCarouselWithBanners()
                } else {
                    showNoData()
                }
            } catch (e: Exception) {
                handleNetworkError(e, "Error al cargar los banners")
            } finally {
                hideLoadingAnimation()
            }
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
        DataConfig.ID_CATEGORIA = categoria.idCategoria
        UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
    }

    private fun initGridView() {
        hideLoadingAnimation()
        val adapter = TrabajosHomeAdapter(requireContext(), trabajosList)
        binding.gridListaTrabajos.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollRunnable?.let {
            autoScrollHandler.removeCallbacks(it)
        }
        _binding = null
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.avionsito_loading) // Reemplaza con tu archivo JSON
            playAnimation()
        }
        binding.contHome.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contHome.visibility = View.VISIBLE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun showNoData() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contHome.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.VISIBLE
    }

    private fun handleNetworkError(e: Exception, message: String) {
        val errorMessage = NetworkErrorUtil.handleNetworkError(e)
        Log.e(TAG, errorMessage, e)
        MessageUtil.showErrorMessage(requireContext(), requireView(), message)
    }
}
