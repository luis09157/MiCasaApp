package com.example.micasaapp.ui.home

import CarouselAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.micasaapp.Adapter.CarrucelCategoriaAdapter
import com.example.micasaapp.Adapter.TrabajosHomeAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Model.BannerModel
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Fragments.CategoriasDetalleFragment
import com.example.micasaapp.Fragments.TrabajadorDetalleFragment
import com.example.micasaapp.Model.TrabajadorModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.*
import kotlin.math.abs

class HomeFragment : Fragment() {
    private val TAG: String = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val DOUBLE_BACK_PRESS_INTERVAL = 5000L // Intervalo en milisegundos para presionar dos veces BACK
    private var doubleBackToExitPressedOnce = false
    private var listCategoriasMoshi: MutableList<CategoriasModel> = mutableListOf()
    private var trabajosList: List<TrabajadorModel> = listOf()
    private var bannersList: List<BannerModel> = listOf()
    private var carouselAdapter: CarouselAdapter? = null
    private var isDataLoading = false
    private var networkJob: Job? = null
    private var retryCount = 0
    private val MAX_RETRIES = 3
    private val RETRY_DELAY = 3000L
    private val API_TIMEOUT = 15000L // 15 segundos de timeout para las llamadas API

    companion object {
        private const val AUTO_SCROLL_INTERVAL = 4000L
        private const val AUTO_SCROLL_START_DELAY = 5000L
    }

    private var autoScrollRunnable: Runnable? = null
    private val autoScrollHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingAnimation()
        setupRefreshLayout()
        setupViewPager()
        initListeners()
        loadAllData()
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            retryCount = 0
            loadAllData(isRefresh = true)
        }
        
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorAccent
        )
    }

    private fun setupViewPager() {
        // Configurar transformación para que las páginas tengan efecto de profundidad
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        
        binding.viewPager.apply {
            offscreenPageLimit = 3
            setPageTransformer(compositePageTransformer)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // Reiniciar auto-scroll cuando el usuario interactúa manualmente
                    restartAutoScroll()
                }
            })
        }
    }

    private fun loadAllData(isRefresh: Boolean = false) {
        if (isDataLoading) return
        
        if (!isRefresh) {
            showLoadingAnimation()
        }
        
        if (!isNetworkAvailable()) {
            hideLoadingAnimation()
            showNetworkError()
            binding.swipeRefreshLayout.isRefreshing = false
            return
        }
        
        isDataLoading = true
        networkJob?.cancel()
        
        networkJob = lifecycleScope.launch {
            try {
                withTimeout(API_TIMEOUT) {
                    // Cargar datos en paralelo
                    val apiClient = ApiClient(Config._URLApi)
                    
                    val bannersDeferred = async(Dispatchers.IO) { apiClient.getBanners() }
                    val categoriasDeferred = async(Dispatchers.IO) { apiClient.getCategorias() }
                    val trabajosDeferred = async(Dispatchers.IO) { apiClient.getTrabajosHome() }
                    
                    try {
                        bannersList = bannersDeferred.await()
                        updateCarouselWithBanners()
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            Log.e(TAG, "Error cargando banners", e)
                        }
                    }
                    
                    try {
                        val categorias = categoriasDeferred.await()
                        if (!categorias.isNullOrEmpty()) {
                            listCategoriasMoshi.clear()
                            listCategoriasMoshi.addAll(categorias)
                            initRecyclerView()
                        }
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            Log.e(TAG, "Error cargando categorías", e)
                            showCategoriaError()
                        }
                    }
                    
                    try {
                        val trabajos = trabajosDeferred.await()
                        if (trabajos.isNotEmpty()) {
                            trabajosList = trabajos
                            initGridView()
                        } else {
                            showEmptyTrabajosMessage()
                        }
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            Log.e(TAG, "Error cargando trabajos", e)
                            showTrabajosError()
                        }
                    }
                    
                    // Solo muestra sin datos si todas las listas están vacías
                    if (bannersList.isEmpty() && listCategoriasMoshi.isEmpty() && trabajosList.isEmpty()) {
                        showNoData()
                    } else {
                        hideLoadingAnimation()
                        binding.contHome.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    handleNetworkError(e, "Error al cargar datos")
                    if (retryCount < MAX_RETRIES) {
                        retryCount++
                        delay(RETRY_DELAY)
                        if (isActive) loadAllData(isRefresh)
                    }
                }
            } finally {
                isDataLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                       capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                       capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        }
        return false
    }

    private fun showNetworkError() {
        hideLoadingAnimation()
        with(binding.fragmentNoData) {
            contNoData.visibility = View.VISIBLE
            textNoData.text = "Sin conexión a internet"
            imageNoData.setImageResource(R.drawable.ic_no_wifi)
            buttonRetry.visibility = View.VISIBLE
            buttonRetry.setOnClickListener {
                retryCount = 0
                loadAllData()
            }
        }
        binding.contHome.visibility = View.GONE
    }

    private fun showCategoriaError() {
        binding.textErrorCategorias.visibility = View.VISIBLE
        binding.recyclerViewCategorias.visibility = View.GONE
    }

    private fun showTrabajosError() {
        binding.textErrorTrabajos.visibility = View.VISIBLE
        binding.gridListaTrabajos.visibility = View.GONE
    }

    private fun showEmptyTrabajosMessage() {
        binding.textErrorTrabajos.text = "No hay trabajos disponibles en este momento"
        binding.textErrorTrabajos.visibility = View.VISIBLE
        binding.gridListaTrabajos.visibility = View.GONE
    }

    private fun updateCarouselWithBanners() {
        if (bannersList.isEmpty()) {
            binding.carouselContainer.visibility = View.GONE
            return
        }
        
        binding.carouselContainer.visibility = View.VISIBLE
        
        if (carouselAdapter == null) {
            carouselAdapter = CarouselAdapter(bannersList)
            binding.viewPager.adapter = carouselAdapter
            binding.indicator.setViewPager(binding.viewPager)
            
            // Configurar el listener para los clics en banners
            carouselAdapter?.setOnBannerClickListener(object : CarouselAdapter.OnBannerClickListener {
                override fun onBannerClick(banner: BannerModel, position: Int) {
                    handleBannerClick(banner)
                }
            })
        } else {
            carouselAdapter?.updateBanners(bannersList)
            binding.indicator.setViewPager(binding.viewPager)
        }

        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.setCurrentItem(0, false)

        startAutoScroll()
    }
    
    private fun handleBannerClick(banner: BannerModel) {
        // Implementar navegación basada en el banner (puede ser a un trabajador específico o categoría)
        if (banner.idTrabajador > 0) {
            // Navegar al detalle del trabajador
            lifecycleScope.launch {
                try {
                    showLoadingAnimation()
                    val apiClient = ApiClient(Config._URLApi)
                    val trabajador = withContext(Dispatchers.IO) {
                        apiClient.getTrabajadorById(banner.idTrabajador)
                    }
                    
                    if (trabajador != null) {
                        TrabajadorDetalleFragment._FLAG_HOME = true
                        TrabajadorDetalleFragment._TRABAJADOR_GLOBAL = trabajador
                        UtilHelper.replaceFragment(requireContext(), TrabajadorDetalleFragment())
                    } else {
                        MessageUtil.showErrorMessage(requireContext(), requireView(), "No se encontró el trabajador")
                    }
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        Log.e(TAG, "Error al obtener trabajador: ${e.message}", e)
                        MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar detalles")
                    }
                } finally {
                    hideLoadingAnimation()
                }
            }
        } else if (banner.idCategoria > 0) {
            // Navegar a la categoría
            DataConfig.ID_CATEGORIA = banner.idCategoria
            UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
        } else if (!banner.enlace.isNullOrEmpty()) {
            // Abrir enlace externo si existe
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(banner.enlace))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error al abrir enlace: ${e.message}", e)
                MessageUtil.showErrorMessage(requireContext(), requireView(), "No se pudo abrir el enlace")
            }
        }
    }

    private fun startAutoScroll() {
        if (_binding == null) return
        stopAutoScroll()

        autoScrollRunnable = object : Runnable {
            override fun run() {
                try {
                    _binding?.let { safeBinding ->
                        val totalPages = safeBinding.viewPager.adapter?.itemCount ?: 0
                        if (totalPages > 0) {
                            val currentPage = safeBinding.viewPager.currentItem
                            val nextPage = (currentPage + 1) % totalPages
                            safeBinding.viewPager.setCurrentItem(nextPage, true)
                            if (isAdded && !isDetached) {
                                autoScrollHandler.postDelayed(this, AUTO_SCROLL_INTERVAL)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error en auto-scroll: ${e.message}")
                    stopAutoScroll()
                }
            }
        }

        if (isAdded && !isDetached) {
            autoScrollHandler.postDelayed(autoScrollRunnable!!, AUTO_SCROLL_START_DELAY)
        }
    }

    private fun stopAutoScroll() {
        try {
            autoScrollRunnable?.let {
                autoScrollHandler.removeCallbacks(it)
                autoScrollRunnable = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al detener auto-scroll: ${e.message}")
        }
    }

    private fun restartAutoScroll() {
        stopAutoScroll()
        startAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        setupBackPressListener()
        if (!isDataLoading && (bannersList.isEmpty() || listCategoriasMoshi.isEmpty() || trabajosList.isEmpty())) {
            loadAllData()
        } else {
            startAutoScroll()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    private fun setupBackPressListener() {
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
        binding.fragmentNoData.buttonRetry?.setOnClickListener {
            retryCount = 0
            loadAllData()
        }

        // Configuración del botón FAB para búsqueda
        binding.fabNuevaBusqueda.setOnClickListener {
            // Desplazar al principio para mostrar el campo de búsqueda
            binding.contHome.smoothScrollTo(0, 0)
            
            // Enfocar el campo de búsqueda y mostrar teclado
            binding.searchView.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT)
        }

        // Configuración del campo de búsqueda
        binding.searchView.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                
                val searchQuery = textView.text.toString().trim()
                if (searchQuery.isNotEmpty()) {
                    performSearch(searchQuery)
                }
                
                // Ocultar teclado
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)
                
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun performSearch(query: String) {
        // Mostrar animación de carga
        showLoadingAnimation()
        
        lifecycleScope.launch {
            try {
                // Buscar trabajadores que coincidan con la consulta
                val apiClient = ApiClient(Config._URLApi)
                val resultados = withContext(Dispatchers.IO) { 
                    apiClient.buscarTrabajadores(query) 
                }
                
                if (resultados.isNotEmpty()) {
                    // Actualizar lista y mostrar resultados
                    trabajosList = resultados
                    initGridView()
                    
                    // Desplazarse a la sección de resultados
                    val tituloTrabajos = binding.contHome.findViewById<TextView>(R.id.tituloTrabajos)
                    binding.contHome.post {
                        binding.contHome.smoothScrollTo(0, tituloTrabajos.top)
                    }
                    
                    // Actualizar título de sección
                    tituloTrabajos.text = "Resultados para \"$query\""
                } else {
                    // Mostrar mensaje si no hay resultados
                    binding.textErrorTrabajos.text = "No se encontraron resultados para \"$query\""
                    binding.textErrorTrabajos.visibility = View.VISIBLE
                    binding.gridListaTrabajos.visibility = View.GONE
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e(TAG, "Error en búsqueda: ${e.message}", e)
                    binding.textErrorTrabajos.text = "Error al realizar la búsqueda"
                    binding.textErrorTrabajos.visibility = View.VISIBLE
                    binding.gridListaTrabajos.visibility = View.GONE
                }
            } finally {
                hideLoadingAnimation()
                binding.contHome.visibility = View.VISIBLE
            }
        }
    }

    private fun initRecyclerView() {
        binding.textErrorCategorias.visibility = View.GONE
        binding.recyclerViewCategorias.visibility = View.VISIBLE
        
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
        binding.textErrorTrabajos.visibility = View.GONE
        binding.gridListaTrabajos.visibility = View.VISIBLE
        
        val adapter = TrabajosHomeAdapter(
            context = requireContext(),
            trabajos = trabajosList
        ) { trabajador ->
            TrabajadorDetalleFragment._FLAG_HOME = true
            TrabajadorDetalleFragment._TRABAJADOR_GLOBAL = trabajador
            UtilHelper.replaceFragment(requireContext(), TrabajadorDetalleFragment())
        }
        binding.gridListaTrabajos.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
        networkJob?.cancel()
        carouselAdapter = null
        _binding = null
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.casa_loading)
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
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun showNoData() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contHome.visibility = View.GONE
        with(binding.fragmentNoData) {
            contNoData.visibility = View.VISIBLE
            textNoData.text = "No hay datos disponibles"
            imageNoData.setImageResource(R.drawable.ic_no_data)
            buttonRetry.visibility = View.VISIBLE
        }
    }

    private fun handleNetworkError(e: Exception, message: String) {
        val errorMessage = NetworkErrorUtil.handleNetworkError(e)
        Log.e(TAG, errorMessage, e)
        MessageUtil.showErrorMessage(requireContext(), requireView(), message)
    }
}
