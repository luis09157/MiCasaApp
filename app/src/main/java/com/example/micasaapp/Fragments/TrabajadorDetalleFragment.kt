package com.example.micasaapp.Fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.example.micasaapp.Adapter.ImageAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Api.DataConfig
import com.example.micasaapp.Model.FotoTrabajoModel
import com.example.micasaapp.Model.ProveedorResponse
import com.example.micasaapp.Model.TrabajadorModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.example.micasaapp.ui.home.HomeFragment
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentTrabajadorDetalleDetalleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrabajadorDetalleFragment : Fragment() {

    private var _binding: FragmentTrabajadorDetalleDetalleBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ImageAdapter
    private var currentIndex = 0
    private val handler = Handler()
    private lateinit var runnable: Runnable
    companion object{
        var _TRABAJADOR_GLOBAL : TrabajadorModel = TrabajadorModel()
        var _FLAG_HOME : Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrabajadorDetalleDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (_TRABAJADOR_GLOBAL.idProveedor != 0){
          //  DataConfig.ID_SUBCATEGORIA = _TRABAJADOR_GLOBAL.
        }

        showLoadingAnimation()
        fetchProveedorData()

        return root
    }

    private fun showLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(R.raw.avionsito_loading)
            playAnimation()
        }
        binding.contProveedor.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contProveedor.visibility = View.VISIBLE
        binding.fragmentNoData.contNoData.visibility = View.GONE
    }

    private fun showNoData() {
        binding.lottieAnimationView.apply {
            visibility = View.GONE
            cancelAnimation()
        }
        binding.contProveedor.visibility = View.GONE
        binding.fragmentNoData.contNoData.visibility = View.VISIBLE
    }

    private fun fetchProveedorData() {
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val result = withContext(Dispatchers.IO) { apiClient.getProveedor(_TRABAJADOR_GLOBAL.idProveedor) }
                handleProveedorResult(result)
            } catch (e: Exception) {
                handleProveedorError(e)
            }
        }
    }

    private fun handleProveedorResult(result: ProveedorResponse) {
        hideLoadingAnimation()

       /* Glide.with(binding.imagenPerfil)
            .load(result.datosProveedor.)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(binding.imagenPerfil)*/

        binding.txtTelefono.text = result.datosProveedor.telefono
        binding.txtNombre.text = "${result.datosProveedor.nombre} ${result.datosProveedor.apellidoPaterno} ${result.datosProveedor.apellidoMaterno}"
        binding.txtCorreo.text = result.datosProveedor.email
        binding.txtOficio.text = result.datosProveedor.descripcion

        if (result.listaFotosTrabajo.isNotEmpty()) {
            setupRecyclerView(result.listaFotosTrabajo)
        } else {
            showNoData()
        }
    }

    private fun setupRecyclerView(images: List<FotoTrabajoModel>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = ImageAdapter(images)
        binding.recyclerView.adapter = adapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        runnable = object : Runnable {
            override fun run() {
                currentIndex = (currentIndex + 1) % images.size
                binding.recyclerView.smoothScrollToPosition(currentIndex)
                handler.postDelayed(this, 5000)
            }
        }
        handler.postDelayed(runnable, 5000)
    }

    private fun handleProveedorError(exception: Exception) {
        hideLoadingAnimation()
        val errorMessage = NetworkErrorUtil.handleNetworkError(exception)
        Log.e("TrabajadorDetalleFragment", "Error fetching proveedor: $errorMessage", exception)
        MessageUtil.showErrorMessage(requireContext(), requireView(), "Error al cargar los detalles del proveedor")
        showNoData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.isFocusableInTouchMode = true
            it.requestFocus()
            it.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if(_FLAG_HOME){
                        UtilHelper.replaceFragment(requireContext(), HomeFragment())
                    }else{
                        UtilHelper.replaceFragment(requireContext(), TrabajadoresFragment())
                    }

                    true
                } else {
                    false
                }
            }
        }
    }
}
