package com.example.micasaapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.micasaapp.Adapter.CategoriaAdapter
import com.example.micasaapp.Adapter.TrabajadorAdapter
import com.example.micasaapp.Model.CategoriaModel
import com.example.micasaapp.R
import com.example.micasaapp.Util.UtilHelper
import com.example.micasaapp.databinding.FragmentTrabajadoresBinding

class TrabajadoresFragment  : Fragment() {
    private var _binding: FragmentTrabajadoresBinding? = null
    private val binding get() = _binding!!
    private var listCategorias : MutableList<CategoriaModel> = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrabajadoresBinding.inflate(inflater, container, false)
        val root: View = binding.root

        llenarCategorias()
        getTrabajadoresAdapter()

        return root
    }
    fun getTrabajadoresAdapter(){
        val trabajadorAdapter = TrabajadorAdapter(requireContext(), listCategorias)
        binding.listTrabajadores.adapter = trabajadorAdapter
        binding.listTrabajadores.divider = null
        binding.listTrabajadores.setOnItemClickListener { adapterView, view, i, l ->

            UtilHelper.replaceFragment(requireContext(),TrabajadoresFragment())
            Log.e("holaquetal","vamos perros")

        }
    }

    fun llenarCategorias(){
        listCategorias.add(CategoriaModel("Carpinteria","Lorem Ipsum es simplemente el texto de relleno de las imprentas", R.drawable.carpinteria))
        listCategorias.add(
            CategoriaModel("Electricista","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.electricista)
        )
        listCategorias.add(
            CategoriaModel("Construccion","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.albanil)
        )
        listCategorias.add(
            CategoriaModel("Plomeria","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.plomeria)
        )
        listCategorias.add(
            CategoriaModel("Pintura","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.pinto)
        )
        listCategorias.add(
            CategoriaModel("Soldadores","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.soldador)
        )
        listCategorias.add(
            CategoriaModel("Gas","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.gas)
        )
        listCategorias.add(
            CategoriaModel("Fumigadores","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.fumigador)
        )
    }
}