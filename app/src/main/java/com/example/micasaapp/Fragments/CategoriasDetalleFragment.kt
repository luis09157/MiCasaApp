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
import com.example.micasaapp.Adapter.SubCategoriasAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Model.SubCategoriasModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentCategoriasDetalleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriasDetalleFragment : Fragment() {

    private var _binding: FragmentCategoriasDetalleBinding? = null
    private val binding get() = _binding!!
    private var listSubCategoriasMoshi: MutableList<SubCategoriasModel> = mutableListOf()
    private var subcategoriasFiltradas: MutableList<SubCategoriasModel> = mutableListOf()
    private lateinit var subCategoriaAdapter: SubCategoriasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        showLoadingAnimation()
        fetchSubCategorias()
    }

    private fun setupRecyclerView() {
        subCategoriaAdapter = SubCategoriasAdapter(subcategoriasFiltradas) { subcategoria ->
            try {
                DataConfig.ID_SUBCATEGORIA = subcategoria.idSubCategoria
                UtilHelper.replaceFragment(requireContext(), TrabajadoresFragment())
            } catch (e: Exception) {
                Log.e("CategoriasDetalleFragment", "Error al navegar", e)
                MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar los trabajadores")
            }
        }
        
        binding.listSubCategoriaDetalle.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = subCategoriaAdapter
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
                    filtrarSubcategorias(newText)
                    return true
                }
            })
            
            queryHint = "Buscar subcategoría..."
            clearFocus()
        }
    }

    private fun filtrarSubcategorias(query: String?) {
        subcategoriasFiltradas.clear()
        
        if (query.isNullOrBlank()) {
            subcategoriasFiltradas.addAll(listSubCategoriasMoshi)
            binding.txtNoResults.visibility = View.GONE
        } else {
            val busqueda = query.lowercase().trim()
            val resultados = listSubCategoriasMoshi.filter { subcategoria ->
                subcategoria.nombreSubcategoria.lowercase().contains(busqueda)
            }
            subcategoriasFiltradas.addAll(resultados)
            
            binding.txtNoResults.apply {
                visibility = if (resultados.isEmpty()) View.VISIBLE else View.GONE
                text = "No se encontraron resultados para \"$query\""
            }
        }
        
        subCategoriaAdapter.updateData(subcategoriasFiltradas)
    }

    private fun fetchSubCategorias() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val result = withContext(Dispatchers.IO) { 
                    apiClient.getSubCategoriasByCategoria(DataConfig.ID_CATEGORIA) 
                }
                handleSubCategoriasResult(result)
            } catch (e: Exception) {
                handleSubCategoriasError(e)
            }
        }
    }

    private fun handleSubCategoriasResult(result: List<SubCategoriasModel>) {
        hideLoadingAnimation()

        if (result.isNotEmpty()) {
            listSubCategoriasMoshi.clear()
            listSubCategoriasMoshi.addAll(result)
            subcategoriasFiltradas.clear()
            subcategoriasFiltradas.addAll(result)
            subCategoriaAdapter.updateData(subcategoriasFiltradas)
            binding.contSubCategorias.visibility = View.VISIBLE
            binding.fragmentNoData.contNoData.visibility = View.GONE
        } else {
            showNoData()
        }
    }

    private fun handleSubCategoriasError(exception: Exception) {
        hideLoadingAnimation()
        val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
        Log.e("CategoriasDetalleFragment", "Error: $errorMessage", exception)
        MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar las subcategorías")
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
                    UtilHelper.replaceFragment(requireContext(), CategoriasFragment())
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
        binding.contSubCategorias.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
    }

    private fun showNoData() {
        binding.contSubCategorias.visibility = View.GONE
        binding.fragmentNoData.apply {
            contNoData.visibility = View.VISIBLE
            textNoData.text = "No hay subcategorías disponibles"
            buttonRetry.visibility = View.VISIBLE
            buttonRetry.setOnClickListener {
                showLoadingAnimation()
                fetchSubCategorias()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
