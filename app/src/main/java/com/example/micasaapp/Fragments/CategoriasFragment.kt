package com.example.micasaapp.Fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.micasaapp.Adapter.CategoriaAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.DialogUtil
import com.ninodev.micasaapp.R
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.example.micasaapp.ui.home.HomeFragment
import com.ninodev.micasaapp.databinding.FragmentCategoriasBinding

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

        FetchCategoriasTask().execute()

        return root
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.visibility = View.VISIBLE
        binding.contCategorias.visibility = View.GONE
        binding.lottieAnimationView.setAnimation(R.raw.avionsito_loading) // Reemplaza con tu archivo JSON
        binding.lottieAnimationView.playAnimation()
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.visibility = View.GONE
        binding.contCategorias.visibility = View.VISIBLE
        binding.lottieAnimationView.cancelAnimation()
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }
    private fun showNodata(){
        binding.lottieAnimationView.visibility = View.GONE
        binding.contCategorias.visibility = View.GONE
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
                UtilHelper.replaceFragment(requireContext(), HomeFragment())
                true
            } else false
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

            hideLoadingAnimation()

            if (result != null) {
                listCategoriasMoshi.addAll(result)
                if(listCategoriasMoshi.size > 0 ){
                    val categoriaAdapter = CategoriaAdapter(requireContext(), listCategoriasMoshi)
                    binding.listCategoria.adapter = categoriaAdapter
                    binding.listCategoria.setOnItemClickListener { adapterView, view, i, l ->
                        DataConfig.IDCATEGORIA = listCategoriasMoshi.get(i).idCategoria
                        UtilHelper.replaceFragment(requireContext(), CategoriasDetalleFragment())
                    }
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

                // Puedes mostrar el mensaje de error al usuario, por ejemplo, mediante un Toast o un AlertDialog
            }
        }
    }
}
