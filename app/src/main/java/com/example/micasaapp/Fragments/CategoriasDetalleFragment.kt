package com.example.micasaapp.Fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.micasaapp.Adapter.SubCategoriaAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Model.SubCategoriasModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentCategoriasDetalleBinding

class CategoriasDetalleFragment : Fragment() {

    private var _binding: FragmentCategoriasDetalleBinding? = null
    private val binding get() = _binding!!
    private var listSubCategoriasMoshi: MutableList<SubCategoriasModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showLoadingAnimation()

        FetchSubCategoriasTask().execute()

        return root
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.visibility = View.VISIBLE
        binding.contSubCategorias.visibility = View.GONE
        binding.lottieAnimationView.setAnimation(R.raw.avionsito_loading) // Reemplaza con tu archivo JSON
        binding.lottieAnimationView.playAnimation()
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.visibility = View.GONE
        binding.contSubCategorias.visibility = View.VISIBLE
        binding.lottieAnimationView.cancelAnimation()
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun showNodata(){
        binding.lottieAnimationView.visibility = View.GONE
        binding.contSubCategorias.visibility = View.GONE
        binding.lottieAnimationView.cancelAnimation()
        binding.fragmentNoData.contNoData.visibility = View.VISIBLE
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
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                UtilHelper.replaceFragment(requireContext(), CategoriasFragment())
                true
            } else false
        }
    }

    private inner class FetchSubCategoriasTask : AsyncTask<Void, Void, List<SubCategoriasModel>>() {

        override fun doInBackground(vararg params: Void?): List<SubCategoriasModel>? {
            try {
                val apiClient = ApiClient(Config._URLApi)
                return apiClient.getSubCategorias()
            } catch (e: Exception) {
                Log.e("Error","Error: ${e.message}")
                showNodata()
                return null
            }

        }

        override fun onPostExecute(result: List<SubCategoriasModel>?) {
            super.onPostExecute(result)

            hideLoadingAnimation()

            if (result != null) {
                listSubCategoriasMoshi.addAll(result)
                if(listSubCategoriasMoshi.size > 0){
                    val subCategoriaAdapter = SubCategoriaAdapter(requireContext(), listSubCategoriasMoshi)
                    binding.listSubCategoriaDetalle.adapter = subCategoriaAdapter
                }else{
                    showNodata()
                }
            } else {
                // Handle error
                val exception = Exception("Error obteniendo categorías")
                val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
                Log.e("FetchCategoriasTask", "Error fetching categorias: $errorMessage", exception)
                MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar las categorías")

                showNodata()
            }
        }
    }
}