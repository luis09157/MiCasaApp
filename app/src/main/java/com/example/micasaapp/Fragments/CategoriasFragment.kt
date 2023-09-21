package com.example.micasaapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.micasaapp.Adapter.CategoriaAdapter
import com.example.micasaapp.Model.CategoriaModel
import com.example.micasaapp.R
import com.example.micasaapp.Util.UtilHelper
import com.example.micasaapp.databinding.FragmentCategoriasBinding

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!
    private var listCategorias : MutableList<CategoriaModel> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        llenarCategorias()
        getCategoriaAdapter()

        return root
    }

    fun getCategoriaAdapter(){
        val categoriaAdapter = CategoriaAdapter(requireContext(), listCategorias)
        binding.listCategoria.adapter = categoriaAdapter
        binding.listCategoria.setOnItemClickListener { adapterView, view, i, l ->

            UtilHelper.replaceFragment(requireContext(),TrabajadoresFragment())
            Log.e("holaquetal","vamos perros")

        }
    }

    fun llenarCategorias(){
        listCategorias.add(CategoriaModel("ALBAÑILERIA","Lorem Ipsum es simplemente el texto de relleno de las imprentas", R.drawable.albanil))
        listCategorias.add(CategoriaModel("PLOMERIA","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.plomero))
        listCategorias.add(CategoriaModel("TECNICO EN A/C Y REFRIGERACION","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.climas))
        listCategorias.add(CategoriaModel("SOLDADOR","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.soldador))
        listCategorias.add(CategoriaModel("TABLAROCA","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.tablaroca))
        listCategorias.add(CategoriaModel("SISTEMAS DE SEGURIDAD","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.seguridad))
        listCategorias.add(CategoriaModel("ARQUITECTOS ","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.arquitecto))
        listCategorias.add(CategoriaModel("DECORADORES DEL HOGAR","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.decoradores))
        listCategorias.add(CategoriaModel("FUGAS DE GAS","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.gas))
        listCategorias.add(CategoriaModel("VENTANAS Y MARCOS","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.ventanas))
        listCategorias.add(CategoriaModel("TRABAJADORAS DEL HOGAR","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.limpieza_hogar))
        listCategorias.add(CategoriaModel("PINTURA","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.pintura))
        listCategorias.add(CategoriaModel("ELECTRICISTA","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.electricista))
        listCategorias.add(CategoriaModel("CARPINTERO","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.carpintero))
        listCategorias.add(CategoriaModel("JARDINEROS","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.jardinero))
        listCategorias.add(CategoriaModel("MECANICOS","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.vulcanizadora))
        listCategorias.add(CategoriaModel("CERRAJERIA","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.cerrajeria))
        listCategorias.add(CategoriaModel("HERREROS","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.herreros))
        listCategorias.add(CategoriaModel("TAPICERIA DE SALAS Y SILLAS","Lorem Ipsum es simplemente el texto de relleno de las imprentas",R.drawable.tapiceria))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}