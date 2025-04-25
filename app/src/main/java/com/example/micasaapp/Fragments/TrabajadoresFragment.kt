package com.example.micasaapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.micasaapp.Adapter.TrabajadorAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Model.TrabajadorModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.ninodev.micasaapp.R
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.databinding.FragmentTrabajadoresBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrabajadoresFragment : Fragment() {
    private val TAG: String = "TrabajadoresFragment"
    private var _binding: FragmentTrabajadoresBinding? = null
    private val binding get() = _binding!!
    private val trabajadorList: MutableList<TrabajadorModel> = mutableListOf()
    private val trabajadoresFiltrados: MutableList<TrabajadorModel> = mutableListOf()
    private lateinit var adapter: TrabajadorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrabajadoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupBackPressHandler()
        showLoadingAnimation()
        fetchTrabajadores()
    }

    private fun setupRecyclerView() {
        adapter = TrabajadorAdapter(requireContext(), trabajadoresFiltrados) { trabajador ->
            navigateToDetail(trabajador)
        }
        
        binding.listTrabajadores.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TrabajadoresFragment.adapter
        }
    }

    private fun setupSearchView() {
        binding.searchSubCategoria.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filtrarTrabajadores(newText)
                    return true
                }
            })

            // Configurar hints y apariencia
            queryHint = "Buscar por nombre o descripción..."
            
            // Expandir completamente el SearchView
            isIconified = false
            clearFocus()
        }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Simplemente navegar hacia atrás a CategoriasDetalleFragment
                    UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
                }
            }
        )
    }

    private fun filtrarTrabajadores(query: String?) {
        trabajadoresFiltrados.clear()
        
        if (query.isNullOrBlank()) {
            trabajadoresFiltrados.addAll(trabajadorList)
            binding.textNoResults.visibility = View.GONE
        } else {
            val busqueda = query.lowercase().trim()
            val resultados = trabajadorList.filter { trabajador ->
                trabajador.nombreCompleto.lowercase().contains(busqueda) ||
                trabajador.descripcion.lowercase().contains(busqueda) ||
                trabajador.categorias.lowercase().contains(busqueda)
            }
            trabajadoresFiltrados.addAll(resultados)
            
            binding.textNoResults.visibility = if (resultados.isEmpty()) View.VISIBLE else View.GONE
            binding.textNoResults.text = "No se encontraron resultados para '$query'"
        }

        updateStats()
        adapter.notifyDataSetChanged()
    }

    private fun navigateToDetail(trabajador: TrabajadorModel) {
        try {
            TrabajadorDetalleFragment._FLAG_HOME = false
            TrabajadorDetalleFragment._TRABAJADOR_GLOBAL = trabajador
            UtilHelper.replaceFragment(requireContext(), TrabajadorDetalleFragment())
        } catch (e: Exception) {
            Log.e(TAG, "Error al navegar al detalle del trabajador", e)
            MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al mostrar el detalle del trabajador")
        }
    }

    private fun updateStats() {
        binding.txtTotalTrabajadores.text = trabajadoresFiltrados.size.toString()
        
        val promedioCalificacion = if (trabajadoresFiltrados.isNotEmpty()) {
            trabajadoresFiltrados.map { it.calificacion }.average()
        } else 0.0
        
        binding.txtCalificacionPromedio.text = String.format("%.1f★", promedioCalificacion)
    }

    private fun fetchTrabajadores() {
        Log.d(TAG, "Iniciando fetchTrabajadores con ID_CATEGORIA: ${DataConfig.ID_CATEGORIA} y ID_SUBCATEGORIA: ${DataConfig.ID_SUBCATEGORIA}")
        
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                Log.d(TAG, "Realizando llamada a getTrabajadores()")
                val trabajadores = withContext(Dispatchers.IO) { apiClient.getTrabajadores() }
                Log.d(TAG, "Respuesta recibida: ${trabajadores.size} trabajadores")
                
                if (trabajadores.isNotEmpty()) {
                    trabajadorList.clear()
                    trabajadorList.addAll(trabajadores)
                    trabajadoresFiltrados.clear()
                    trabajadoresFiltrados.addAll(trabajadores)
                    updateStats()
                    adapter.notifyDataSetChanged()
                    binding.contTrabajadores.visibility = View.VISIBLE
                } else {
                    Log.d(TAG, "No se encontraron trabajadores")
                    showNoData()
                    binding.fragmentNoData.textNoData.text = "No hay trabajadores disponibles para esta subcategoría"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar trabajadores", e)
                handleNetworkError(e, "Error al cargar los trabajadores")
                showNoData()
                binding.fragmentNoData.textNoData.text = "Error al cargar los trabajadores"
            } finally {
                hideLoadingAnimation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.casa_loading)
            playAnimation()
        }
        binding.contTrabajadores.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
    }

    private fun showNoData() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contTrabajadores.visibility = View.GONE
        binding.fragmentNoData.apply {
            contNoData.visibility = View.VISIBLE
            buttonRetry.visibility = View.VISIBLE
            buttonRetry.setOnClickListener {
                showLoadingAnimation()
                fetchTrabajadores()
            }
        }
    }

    private fun handleNetworkError(e: Exception, message: String) {
        val errorMessage = NetworkErrorUtil.handleNetworkError(e)
        Log.e(TAG, errorMessage, e)
        MessageUtil.showErrorMessage(requireContext(), requireView(), message)
    }
}
