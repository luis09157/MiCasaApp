package com.example.micasaapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.micasaapp.Adapter.SubCategoriaAdapter
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
    private lateinit var subCategoriaAdapter: SubCategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showLoadingAnimation()
        fetchSubCategorias()

        return root
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.casa_loading) // Reemplaza con tu archivo JSON
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
        binding.contSubCategorias.visibility = View.VISIBLE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun showNoData() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contSubCategorias.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.isFocusableInTouchMode = true
            it.requestFocus()
            it.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    UtilHelper.replaceFragment(requireContext(), CategoriasFragment())
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun fetchSubCategorias() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val result = withContext(Dispatchers.IO) { apiClient.getSubCategorias() }
                handleSubCategoriasResult(result)
            } catch (e: Exception) {
                handleSubCategoriasError(e)
            }
        }
    }

    private fun handleSubCategoriasResult(result: List<SubCategoriasModel>) {
        hideLoadingAnimation()

        if (result.isNotEmpty()) {
            listSubCategoriasMoshi.addAll(result)
            setupSubCategoriaAdapter(result)
            setupSearchView()
        } else {
            showNoData()
        }
    }

    private fun handleSubCategoriasError(exception: Exception) {
        hideLoadingAnimation()
        val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
        Log.e("CategoriasDetalleFragment", "Error fetching subcategorias: $errorMessage", exception)
        MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar las subcategorías")
        showNoData()
    }

    private fun setupSubCategoriaAdapter(subCategorias: List<SubCategoriasModel>) {
        subCategoriaAdapter = SubCategoriaAdapter(requireContext(), subCategorias)
        binding.listSubCategoriaDetalle.adapter = subCategoriaAdapter
        binding.listSubCategoriaDetalle.setOnItemClickListener { _, _, i, _ ->
            DataConfig.ID_SUBCATEGORIA = subCategorias[i].idSubCategoria
            UtilHelper.replaceFragment(requireContext(), TrabajadoresFragment())
        }
    }

    private fun setupSearchView() {
        binding.searchSubCategoria.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                subCategoriaAdapter.filter(newText.orEmpty())
                return true
            }
        })
    }
}
