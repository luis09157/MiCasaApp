package com.example.micasa.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.micasa.Adapter.CategoriaAdapter
import com.example.micasa.Model.CategoriaModel
import com.example.micasa.R
import com.example.micasa.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var listCategorias : MutableList<CategoriaModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        llenarCategorias()
        getCategoriaAdapter()

        return root
    }

    fun getCategoriaAdapter(){
        val categoriaAdapter = CategoriaAdapter(requireContext(), listCategorias)
        binding.listCategoria.adapter = categoriaAdapter
    }

    fun llenarCategorias(){
        listCategorias.add(CategoriaModel("Carpinteria","Lorem Ipsum es simplemente el texto de relleno de las imprentas", R.drawable.carpinteria))
        listCategorias.add(CategoriaModel("Electricista","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.electricista))
        listCategorias.add(CategoriaModel("Construccion","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.albanil))
        listCategorias.add(CategoriaModel("Plomeria","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.plomeria))
        listCategorias.add(CategoriaModel("Pintura","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.pinto))
        listCategorias.add(CategoriaModel("Soldadores","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.soldador))
        listCategorias.add(CategoriaModel("Gas","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.gas))
        listCategorias.add(CategoriaModel("Fumigadores","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.fumigador))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}