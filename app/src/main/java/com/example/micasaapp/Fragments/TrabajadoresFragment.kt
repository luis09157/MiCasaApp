package com.example.micasaapp.Fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.micasaapp.Adapter.TrabajadorAdapter
import com.example.micasaapp.Model.CategoriaModel
import com.ninodev.micasaapp.R
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.databinding.FragmentTrabajadoresBinding

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

            UtilHelper.replaceFragment(requireContext(),TrabajadorDetalleFragment())
            Toast.makeText(requireContext(),"vamos perros",Toast.LENGTH_SHORT).show()

        }
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
                UtilHelper.replaceFragment(requireContext(),CategoriasFragment())
                true
            } else false
        }
    }

    fun llenarCategorias(){
        listCategorias.add(CategoriaModel("Carpinteria","Lorem Ipsum es simplemente el texto de relleno de las imprentas", R.drawable.categoria_carpintero))
        listCategorias.add(
            CategoriaModel("Electricista","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.categoria_electricista)
        )
        listCategorias.add(
            CategoriaModel("Construccion","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.categoria_albanil)
        )
        listCategorias.add(
            CategoriaModel("Plomeria","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.categoria_plomero)
        )
        listCategorias.add(
            CategoriaModel("Pintura","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.categoria_pintura)
        )
        listCategorias.add(
            CategoriaModel("Soldadores","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.cateogira_soldador)
        )
        listCategorias.add(
            CategoriaModel("Gas","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.categoria_gas)
        )
        listCategorias.add(
            CategoriaModel("Fumigadores","Lorem Ipsum es simplemente el texto de relleno de las imprentas",
                R.drawable.categoria_albanil)
        )
    }
}