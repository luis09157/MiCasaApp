package com.example.micasaapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.micasaapp.Adapter.CategoriaAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.DialogUtil
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showLoadingAnimation()
        fetchCategorias()

        return root
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.avionsito_loading) // Reemplaza con tu archivo JSON
            playAnimation()
        }
        binding.contCategorias.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contCategorias.visibility = View.VISIBLE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun showNoData() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contCategorias.visibility = View.GONE
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
                    UtilHelper.replaceFragment(requireContext(), HomeFragment())
                    true
                } else {
                    false
                }
            }
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
            listCategoriasMoshi.addAll(result)
            val categoriaAdapter = CategoriaAdapter(requireContext(), listCategoriasMoshi)
            binding.listCategoria.adapter = categoriaAdapter
            binding.listCategoria.setOnItemClickListener { _, _, i, _ ->
                DataConfig.ID_CATEGORIA = listCategoriasMoshi[i].idCategoria
                UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
            }
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
}
