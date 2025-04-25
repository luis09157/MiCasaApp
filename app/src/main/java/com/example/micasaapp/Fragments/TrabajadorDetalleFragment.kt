package com.example.micasaapp.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.micasaapp.Adapter.ImageAdapter
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Model.FotoTrabajoModel
import com.example.micasaapp.Model.OpinionModel
import com.example.micasaapp.Model.ProveedorResponse
import com.example.micasaapp.Model.TrabajadorModel
import com.example.micasaapp.Util.MessageUtil
import com.example.micasaapp.Util.NetworkErrorUtil
import com.example.micasaapp.Util.UtilHelper
import com.example.micasaapp.ui.home.HomeFragment
import com.google.android.material.imageview.ShapeableImageView
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentTrabajadorDetalleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrabajadorDetalleFragment : Fragment() {

    private var _binding: FragmentTrabajadorDetalleBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: ImageAdapter
    private var currentImageIndex = 0
    private val handler = Handler()
    private lateinit var autoScrollRunnable: Runnable

    companion object {
        var _TRABAJADOR_GLOBAL: TrabajadorModel = TrabajadorModel()
        var _FLAG_HOME: Boolean = false
        private const val AUTO_SCROLL_DELAY = 3000L // 3 segundos
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrabajadorDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadProveedorData()
    }

    private fun setupUI() {
        // Configurar RecyclerView para galería con PagerSnapHelper para efecto de carrusel
        binding.recyclerView.layoutManager = 
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        // Configurar RecyclerView para opiniones
        binding.recyclerOpiniones.layoutManager = LinearLayoutManager(context)
        // Agregar opiniones de ejemplo
        setupOpinionesEjemplo()

        // Configurar botón de WhatsApp
        binding.fabWhatsapp.setOnClickListener {
            val telefono = binding.txtTelefono.text.toString()
            if (telefono.isNotEmpty()) {
                abrirWhatsApp(telefono)
            }
        }

        // Configurar clicks en teléfono y correo
        binding.txtTelefono.setOnClickListener {
            val telefono = binding.txtTelefono.text.toString()
            if (telefono.isNotEmpty()) {
                marcarTelefono(telefono)
            }
        }

        binding.txtCorreo.setOnClickListener {
            val correo = binding.txtCorreo.text.toString()
            if (correo.isNotEmpty()) {
                enviarCorreo(correo)
            }
        }
    }

    private fun setupOpinionesEjemplo() {
        val opinionesEjemplo = listOf(
            OpinionModel(
                nombreUsuario = "María González",
                fecha = "Hace 2 días",
                rating = 5f,
                comentario = "Excelente servicio, muy profesional y puntual. El trabajo quedó perfecto y el precio fue justo. Lo recomiendo ampliamente.",
                imagenUsuario = "https://randomuser.me/api/portraits/women/1.jpg"
            ),
            OpinionModel(
                nombreUsuario = "Juan Pérez",
                fecha = "Hace 1 semana",
                rating = 4.5f,
                comentario = "Muy buen trabajo, cumplió con todo lo acordado. Solo un pequeño detalle con el tiempo de entrega, pero nada grave.",
                imagenUsuario = "https://randomuser.me/api/portraits/men/1.jpg"
            ),
            OpinionModel(
                nombreUsuario = "Ana Martínez",
                fecha = "Hace 2 semanas",
                rating = 5f,
                comentario = "Increíble atención y profesionalismo. Resolvió todos los problemas que teníamos y dejó todo impecable.",
                imagenUsuario = "https://randomuser.me/api/portraits/women/2.jpg"
            )
        )

        binding.recyclerOpiniones.adapter = OpinionesAdapter(opinionesEjemplo)
    }

    inner class OpinionesAdapter(private val opiniones: List<OpinionModel>) :
        RecyclerView.Adapter<OpinionesAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgUsuario: ShapeableImageView = itemView.findViewById(R.id.imgUsuario)
            val txtNombreUsuario: TextView = itemView.findViewById(R.id.txtNombreUsuario)
            val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
            val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
            val txtComentario: TextView = itemView.findViewById(R.id.txtComentario)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_opinion, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val opinion = opiniones[position]
            holder.apply {
                txtNombreUsuario.text = opinion.nombreUsuario
                txtFecha.text = opinion.fecha
                ratingBar.rating = opinion.rating
                txtComentario.text = opinion.comentario

                Glide.with(imgUsuario)
                    .load(opinion.imagenUsuario)
                    .placeholder(R.drawable.placeholder_profile)
                    .error(R.drawable.error_profile)
                    .circleCrop()
                    .into(imgUsuario)
            }
        }

        override fun getItemCount() = opiniones.size
    }

    private fun loadProveedorData() {
        showLoading()
        lifecycleScope.launch {
            try {
                val apiClient = ApiClient(Config._URLApi)
                val response = withContext(Dispatchers.IO) {
                    apiClient.getProveedor(_TRABAJADOR_GLOBAL.idProveedor)
                }
                handleProveedorResponse(response)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleProveedorResponse(response: ProveedorResponse) {
        hideLoading()
        with(binding) {
            // Cargar imagen de perfil
            Glide.with(requireContext())
                .load(_TRABAJADOR_GLOBAL.imagenPerfil)
                .placeholder(R.drawable.placeholder_profile)
                .error(R.drawable.error_profile)
                .circleCrop()
                .into(imgPerfil)

            // Mostrar información del proveedor
            txtNombre.text = "${response.datosProveedor.nombre} ${response.datosProveedor.apellidoPaterno} ${response.datosProveedor.apellidoMaterno}"
            txtOficio.text = response.datosProveedor.descripcion
            txtTelefono.text = response.datosProveedor.telefono
            txtCorreo.text = response.datosProveedor.email

            // Configurar galería de trabajos
            if (response.listaFotosTrabajo.isNotEmpty()) {
                setupGaleria(response.listaFotosTrabajo)
            }
        }
    }

    private fun setupGaleria(fotos: List<FotoTrabajoModel>) {
        imageAdapter = ImageAdapter(fotos)
        binding.recyclerView.adapter = imageAdapter

        // Configurar auto-scroll
        autoScrollRunnable = Runnable {
            if (fotos.isNotEmpty()) {
                currentImageIndex = (currentImageIndex + 1) % fotos.size
                binding.recyclerView.smoothScrollToPosition(currentImageIndex)
                handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
            }
        }
        handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
    }

    private fun abrirWhatsApp(telefono: String) {
        try {
            val mensaje = "Hola, te contacto desde MiCasaApp"
            val intent = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=52$telefono&text=${Uri.encode(mensaje)}"
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            MessageUtil.showErrorMessage(
                requireContext(),
                requireView(),
                "No se pudo abrir WhatsApp"
            )
        }
    }

    private fun marcarTelefono(telefono: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$telefono")
            startActivity(intent)
        } catch (e: Exception) {
            MessageUtil.showErrorMessage(
                requireContext(),
                requireView(),
                "No se pudo realizar la llamada"
            )
        }
    }

    private fun enviarCorreo(correo: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$correo")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contacto desde MiCasaApp")
            startActivity(intent)
        } catch (e: Exception) {
            MessageUtil.showErrorMessage(
                requireContext(),
                requireView(),
                "No se pudo abrir el correo"
            )
        }
    }

    private fun showLoading() {
        binding.lottieAnimationView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.lottieAnimationView.visibility = View.GONE
    }

    private fun handleError(error: Exception) {
        hideLoading()
        val message = NetworkErrorUtil.handleNetworkError(error)
        MessageUtil.showErrorMessage(requireContext(), requireView(), message)
        Log.e("TrabajadorDetalle", "Error: ${error.message}", error)
    }

    override fun onResume() {
        super.onResume()
        setupBackNavigation()
    }

    private fun setupBackNavigation() {
        view?.apply {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (_FLAG_HOME) {
                        UtilHelper.replaceFragment(requireContext(), HomeFragment())
                    } else {
                        UtilHelper.replaceFragment(requireContext(), TrabajadoresFragment())
                    }
                    true
                } else false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
