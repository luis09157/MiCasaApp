package com.example.micasaapp.Util

import android.view.View
import com.google.android.material.snackbar.Snackbar

object MessageUtil {
    fun showSuccessMessage(view: View, message: String) {
        showMessage(view, message, Snackbar.LENGTH_SHORT, android.R.color.holo_green_dark)
    }

    fun showInfoMessage(view: View, message: String) {
        showMessage(view, message, Snackbar.LENGTH_SHORT, android.R.color.holo_blue_light)
    }

    fun showErrorMessage(view: View, message: String) {
        showMessage(view, message, Snackbar.LENGTH_LONG, android.R.color.holo_red_light)
    }

    private fun showMessage(view: View, message: String, duration: Int, backgroundColor: Int) {
        val snackbar = Snackbar.make(view, message, duration)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundResource(backgroundColor)
        snackbar.show()
    }
}
