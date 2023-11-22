package com.example.micasaapp.Fragments

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.micasaapp.Adapter.ImageAdapter
import com.example.micasaapp.R
import com.example.micasaapp.Util.UtilHelper
import com.example.micasaapp.databinding.FragmentTrabajadorDetalleDetalleBinding

class TrabajadorDetalleFragment : Fragment() {
    private var _binding: FragmentTrabajadorDetalleDetalleBinding? = null
    private val binding get() = _binding!!

    val images = listOf(
        R.drawable.img_perfil,
        R.drawable.limpieza_hogar,
        R.drawable.climas,
        // Agrega más imágenes según sea necesario
    )
    private lateinit var adapter: ImageAdapter
    private var currentIndex = 0
    private val handler = Handler()
    private lateinit var runnable: Runnable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrabajadorDetalleDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initCarrucel()

        return root
    }

    fun initCarrucel(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapter = ImageAdapter(images)
        binding.recyclerView.adapter = adapter

        // Agrega el efecto de pager al RecyclerView
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        // Programa la actualización automática cada 5 segundos
        runnable = object : Runnable {
            override fun run() {
                currentIndex = (currentIndex + 1) % images.size
                binding.recyclerView.smoothScrollToPosition(currentIndex)
                handler.postDelayed(this, 5000) // 5000 milisegundos = 5 segundos
            }
        }
        handler.postDelayed(runnable, 5000)
    }
    override fun onDestroy() {
        super.onDestroy()
        // Detiene la actualización automática al destruir la actividad
        handler.removeCallbacks(runnable)
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
                UtilHelper.replaceFragment(requireContext(),TrabajadoresFragment())
                true
            } else false
        }
    }
}