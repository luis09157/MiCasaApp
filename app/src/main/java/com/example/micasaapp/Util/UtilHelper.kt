package com.example.micasaapp.Util

import android.content.Context
import android.location.LocationManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.micasaapp.Api.DataConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ninodev.micasaapp.R


class UtilHelper{

    companion object{
        fun replaceFragment(context: Context, fragment: Fragment) {
            val ft: FragmentTransaction = (context as FragmentActivity)
                .supportFragmentManager.beginTransaction()
            val fm: FragmentManager = (context as FragmentActivity)
                .supportFragmentManager
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.replace(R.id.content_main, fragment)
            ft.commit()
        }

        fun hideKeyboard(view: View) {
            val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
        fun showAlert(context: Context,message: String) {
            MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.btn_ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        fun mostrarSnackbar(view: View, mensaje: String) {
            Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT)
                .setAction("Aceptar") { /* Acciones si se requiere */ }
                .show()
        }
        fun isLocationEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                    activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)
        }
    }
}