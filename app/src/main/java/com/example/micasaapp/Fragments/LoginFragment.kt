package com.example.micasaapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.micasaapp.Util.UtilHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.BuildConfig
import com.ninodev.micasaapp.R
import com.ninodev.micasaapp.databinding.FragmentLoginBinding
import com.example.micasaapp.Api.ApiClient
import com.example.micasaapp.Api.Config
import com.example.micasaapp.Model.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.micasaapp.ui.home.HomeFragment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val currentVersion = BuildConfig.VERSION_NAME

    private val binding get() = _binding!!

    companion object{
        var _FLAG_IS_REGISTRO = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //requireActivity().title = getString(R.string.menu_home)

        Toast.makeText(requireContext(),"hola",Toast.LENGTH_LONG).show()
        listeners()


        return root
    }


    private fun listeners() {
        binding.btnOlvidasteContraseA.setOnClickListener {
            showPasswordResetDialog()
        }
        binding.btnRegistro.setOnClickListener {
            _FLAG_IS_REGISTRO  = false
            //UtilFragment.changeFragment(requireActivity().supportFragmentManager, RegistroFragment(), TAG)
        }
        binding.btnLogin.setOnClickListener {
            UtilHelper.hideKeyboard(requireView())
            showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val email = binding.txtCorreo.editText?.text.toString().trim()
                val password = binding.txtContraseA.editText?.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        hideLoading()
                        UtilHelper.showAlert(requireContext(), "Por favor ingrese correo y contraseña")
                    }
                    return@launch
                }

                try {
                    val apiClient = ApiClient(Config._URLApi)
                    val response: LoginResponse = apiClient.login(email, password)
                    withContext(Dispatchers.Main) {
                        hideLoading()
                        if (response.token.isNotEmpty()) {
                            // Save user data and token here
                            UtilHelper.replaceFragment(requireContext(), HomeFragment())
                        } else {
                            UtilHelper.showAlert(requireContext(), "Credenciales incorrectas")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        hideLoading()
                        val errorMessage = when (e) {
                            is java.net.ConnectException -> "No se pudo conectar al servidor"
                            is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
                            else -> "Error: ${e.localizedMessage}"
                        }
                        UtilHelper.showAlert(requireContext(), errorMessage)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.lottieLoading.visibility = View.VISIBLE
        binding.contenedor.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.lottieLoading.visibility = View.GONE
        binding.contenedor.visibility = View.VISIBLE
    }

    private fun showPasswordResetDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
        builder.setTitle(getString(R.string.dialog_reset_password_title))
        builder.setMessage(getString(R.string.dialog_reset_password_message))

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = getString(R.string.input_hint_email)
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.btn_send)) { dialog, _ ->
            val email = input.text.toString().trim()
            if (email.isEmpty()) {
                Snackbar.make(requireView(), getString(R.string.error_empty_email), Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                //sendPasswordResetEmail(email)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onResume() {
        super.onResume()
        // checkVersionAndLogin()
        if(_FLAG_IS_REGISTRO){
            _FLAG_IS_REGISTRO = false
            Snackbar.make(requireView(), getString(R.string.thank_you_for_registering), Snackbar.LENGTH_LONG)
                .show()
        }

        var doubleBackToExitPressedOnce = false

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (doubleBackToExitPressedOnce) {
                        requireActivity().finish()
                        return
                    }

                    doubleBackToExitPressedOnce = true
                    Snackbar.make(requireView(), getString(R.string.snackbar_exit_prompt), Snackbar.LENGTH_LONG)
                        .show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseLoginResponse(responseBody: String?): LoginResponse {
        val adapter: JsonAdapter<LoginResponse> = moshi.adapter(LoginResponse::class.java)
        return adapter.fromJson(responseBody.orEmpty()) ?: LoginResponse(
            idUsuario = 0,
            nombre = "",
            apPaterno = "",
            apMaterno = "",
            token = ""
        )
    }

    private val moshi = Moshi.Builder().build()
}
