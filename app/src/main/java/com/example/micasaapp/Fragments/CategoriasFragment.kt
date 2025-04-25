package com.example.micasaapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.micasaapp.Adapter.CategoriaAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.example.micasaapp.ui.home.HomeFragment
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentCategoriasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!
    private var listCategoriasMoshi: MutableList<CategoriasModel> = mutableListOf()
    private var categoriasFiltradas: MutableList<CategoriasModel> = mutableListOf()
    private lateinit var categoriaAdapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        showLoadingAnimation()
        fetchCategorias()
    }

    private fun setupRecyclerView() {
        categoriaAdapter = CategoriaAdapter(requireContext(), categoriasFiltradas) { categoria ->
            DataConfig.ID_CATEGORIA = categoria.idCategoria
            UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
        }
        
        binding.listCategoria.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = categoriaAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filtrarCategorias(newText)
                    return true
                }
            })
            
            // Configurar hints y apariencia
            queryHint = "Buscar categoría..."
            clearFocus()
        }
    }

    private fun filtrarCategorias(query: String?) {
        categoriasFiltradas.clear()
        
        if (query.isNullOrBlank()) {
            categoriasFiltradas.addAll(listCategoriasMoshi)
        } else {
            val busqueda = query.lowercase().trim()
            val resultados = listCategoriasMoshi.filter { categoria ->
                categoria.nombreCategoria.lowercase().contains(busqueda)
            }
            categoriasFiltradas.addAll(resultados)
        }
        
        categoriaAdapter.notifyDataSetChanged()
        
        // Mostrar mensaje cuando no hay resultados
        if (categoriasFiltradas.isEmpty() && !query.isNullOrBlank()) {
            binding.fragmentNoData.apply {
                contNoData.visibility = View.VISIBLE
                textNoData.text = "No se encontraron categorías para \"$query\""
                buttonRetry.visibility = View.GONE
            }
            binding.listCategoria.visibility = View.GONE
        } else {
            binding.fragmentNoData.contNoData.visibility = View.GONE
            binding.listCategoria.visibility = View.VISIBLE
        }
    }

    private fun fetchCategorias() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val result = withContext(Dispatchers.IO) { apiClient.getCategorias() }
                handleCategoriasResult(result)
            } catch (e: Exception) {
                handleCategoriasError(e)
            }
        }
    }

    private fun handleCategoriasResult(result: List<CategoriasModel>) {
        hideLoadingAnimation()

        if (result.isNotEmpty()) {
            listCategoriasMoshi.clear()
            listCategoriasMoshi.addAll(result)
            categoriasFiltradas.clear()
            categoriasFiltradas.addAll(result)
            categoriaAdapter.notifyDataSetChanged()
            binding.listCategoria.visibility = View.VISIBLE
            binding.fragmentNoData.contNoData.visibility = View.GONE
        } else {
            showNoData()
        }
    }

    private fun handleCategoriasError(exception: Exception) {
        hideLoadingAnimation()
        val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
        Log.e("CategoriasFragment", "Error fetching categorias: $errorMessage", exception)
        MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar las categorías")
        showNoData()
    }

    override fun onResume() {
        super.onResume()
        setupBackNavigation()
    }

    private fun setupBackNavigation() {
        view?.let {
            it.isFocusableInTouchMode = true
            it.requestFocus()
            it.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!binding.searchView.query.isNullOrEmpty()) {
                        binding.searchView.setQuery("", false)
                        return@setOnKeyListener true
                    }
                    UtilHelper.replaceFragment(requireContext(), HomeFragment())
                    true
                } else false
            }
        }
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.casa_loading)
            playAnimation()
        }
        binding.listCategoria.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
    }

    private fun showNoData() {
        binding.listCategoria.visibility = View.GONE
        binding.fragmentNoData.apply {
            contNoData.visibility = View.VISIBLE
            textNoData.text = "No hay categorías disponibles"
            buttonRetry.visibility = View.VISIBLE
            buttonRetry.setOnClickListener {
                showLoadingAnimation()
                fetchCategorias()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
