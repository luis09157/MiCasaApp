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
import com.example.micasaapp.Data.CategoriasModel
import com.ninodev.micasaapp.R
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
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
        binding.listCategoria.visibility = View.GONE
        binding.lottieAnimationView.setAnimation(R.raw.avionsito_loading) // Reemplaza con tu archivo JSON
        binding.lottieAnimationView.playAnimation()
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.visibility = View.GONE
        binding.listCategoria.visibility = View.VISIBLE
        binding.lottieAnimationView.cancelAnimation()
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
                // Actualiza la interfaz de usuario con las categorías obtenidas
                listCategoriasMoshi.addAll(result)
                val categoriaAdapter = CategoriaAdapter(requireContext(), listCategoriasMoshi)
                binding.listCategoria.adapter = categoriaAdapter
                //MessageUtil.showSuccessMessage(requireContext() ,requireView(), "¡Operación exitosa!")
               // DialogUtil.showSuccessDialog(requireContext(), "Error", "Error al cargar las categorías")
            } else {
                // Handle error
                val exception = Exception("Error obteniendo categorías")
                val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
                Log.e("FetchCategoriasTask", "Error fetching categorias: $errorMessage", exception)
                MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar las categorías")

                // Puedes mostrar el mensaje de error al usuario, por ejemplo, mediante un Toast o un AlertDialog
            }
        }
    }
}
