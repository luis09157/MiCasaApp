package com.example.micasaapp.Util
import java.io.IOException

class NetworkErrorUtil {

    companion object {
        fun handleNetworkError(error: Throwable): String {
            return when {
                isNetworkError(error) -> "Error de conexión a Internet"
                else -> "Error desconocido"
            }
        }

        private fun isNetworkError(error: Throwable): Boolean {
            return error is IOException
        }
    }
}
