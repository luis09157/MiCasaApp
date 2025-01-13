package com.example.micasaapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.micasaapp.Adapter.CarrucelCategoriaAdapter
import com.example.micasaapp.Adapter.TrabajadorAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Model.TrabajadorModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.ninodev.micasaapp.R
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.databinding.FragmentTrabajadoresBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrabajadoresFragment : Fragment() {
    private val TAG: String = "TrabajadoresFragment"
    private var _binding: FragmentTrabajadoresBinding? = null
    private val binding get() = _binding!!
    private val trabajadorList: MutableList<TrabajadorModel> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrabajadoresBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showLoadingAnimation()
        fetchTrabajadores()

        return root
    }

    private fun setupTrabajadoresAdapter() {
        hideLoadingAnimation()
        val trabajadorAdapter = TrabajadorAdapter(requireContext(), trabajadorList)
        binding.listTrabajadores.adapter = trabajadorAdapter
        binding.listTrabajadores.divider = null
        binding.listTrabajadores.setOnItemClickListener { adapterView, view, i, l ->
            TrabajadorDetalleFragment._FLAG_HOME = false
            TrabajadorDetalleFragment._TRABAJADOR_GLOBAL = trabajadorList[i]
            UtilHelper.replaceFragment(requireContext(), TrabajadorDetalleFragment())
        }
    }

    private fun fetchTrabajadores() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val categorias = withContext(Dispatchers.IO) { apiClient.getTrabajadores() }
                if (!categorias.isNullOrEmpty()) {
                    trabajadorList.addAll(categorias)
                    setupTrabajadoresAdapter()
                } else {
                    showNoData()
                }
            } catch (e: Exception) {
                handleNetworkError(e, "Error al cargar las categorías")
            } finally {
                hideLoadingAnimation()
            }
        }
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
                UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
                true
            } else false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.avionsito_loading) // Reemplaza con tu archivo JSON
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

    private fun handleNetworkError(e: Exception, message: String) {
        val errorMessage = NetworkErrorUtil.handleNetworkError(e)
        Log.e(TAG, errorMessage, e)
        MessageUtil.showErrorMessage(requireContext(), requireView(), message)
    }
}
