package com.example.micasaapp.Util

import android.content.Context
import com.ninodev.micasaapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtil {

    fun showSuccessDialog(context: Context, title: String, message: String) {
        showDialog(context, title, message, R.color.white)
    }

    fun showInfoDialog(context: Context, title: String, message: String) {
        showDialog(context, title, message, android.R.color.holo_blue_light)
    }

    fun showErrorDialog(context: Context, title: String, message: String) {
        showDialog(context, title, message, android.R.color.holo_red_light)
    }

    private fun showDialog(context: Context, title: String, message: String, backgroundColor: Int) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setBackground(context.getDrawable(backgroundColor))
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
