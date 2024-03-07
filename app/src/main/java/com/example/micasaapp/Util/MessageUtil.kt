package com.example.micasaapp.Util

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.ninodev.micasaapp.R
import com.google.android.material.snackbar.Snackbar

object MessageUtil {
    fun showSuccessMessage(context: Context, view: View, message: String) {
        showMessage(context, view, message, Snackbar.LENGTH_SHORT, R.color.successColor)
    }

    fun showInfoMessage(context: Context, view: View, message: String) {
        showMessage(context, view, message, Snackbar.LENGTH_SHORT, R.color.infoColor)
    }

    fun showErrorMessage(context: Context, view: View, message: String) {
        showMessage(context, view, message, Snackbar.LENGTH_LONG, R.color.errorColor)
    }

    private fun showMessage(
        context: Context,
        view: View,
        message: String,
        duration: Int,
        backgroundColor: Int
    ) {
        val snackbar = Snackbar.make(view, message, duration)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
        snackbar.show()
    }
}
