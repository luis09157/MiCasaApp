package com.example.micasaapp.Util

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.micasaapp.R


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
    }
}