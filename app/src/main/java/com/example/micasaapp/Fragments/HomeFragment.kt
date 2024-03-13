package com.example.micasaapp.Fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.micasaapp.Adapter.CarouselAdapter
import com.example.micasaapp.Adapter.CustomAdapter
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.databinding.FragmentHomeBinding
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var listCategoriasMoshi: MutableList<CategoriasModel> = mutableListOf()
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initCarousel()
        categoriasCarrucel()

        return root
    }

    private fun initCarousel() {
        val carouselAdapter = CarouselAdapter()
        binding.viewPager.adapter = carouselAdapter

        // Configurar los indicadores circulares
        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.viewPager)

        // Puedes personalizar el aspecto de los indicadores si es necesario
        // indicator.setFillColor(R.color.indicator_fill_color)
        // indicator.setUnFillColor(R.color.indicator_unfill_color)
        // ...

        // Iniciar el auto-scroll del carrusel
        binding.viewPager.postDelayed({
            startAutoScroll()
        }, AUTO_SCROLL_START_DELAY)
    }

    private fun startAutoScroll() {
        val carouselAdapter = binding.viewPager.adapter as CarouselAdapter
        val totalPages = carouselAdapter.itemCount
        val currentPage = binding.viewPager.currentItem

        // Calcular la siguiente página para el auto-scroll
        val nextPage = (currentPage + 1) % totalPages

        // Realizar un desplazamiento suave a la siguiente página
        binding.viewPager.setCurrentItem(nextPage, true)

        // Programar el próximo auto-scroll
        binding.viewPager.postDelayed({
            startAutoScroll()
        }, AUTO_SCROLL_INTERVAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun categoriasCarrucel(){

        val dataList = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")

        val layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.isHorizontalScrollBarEnabled = true


        val adapter = CustomAdapter(requireContext(), dataList)
        binding.recyclerView.adapter = adapter


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
                UtilHelper.replaceFragment(requireContext(), CategoriasFragment())
                true
            } else false
        }
    }

    companion object {
        private const val AUTO_SCROLL_INTERVAL = 3000L // 3 segundos
        private const val AUTO_SCROLL_START_DELAY = 10000L // 10 segundos
    }

}
