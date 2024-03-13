package com.example.micasaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.micasaapp.Fragments.CategoriasFragment
import com.example.micasaapp.Fragments.HomeFragment
import com.example.micasaapp.Util.UtilHelper
import com.ninodev.micasaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UtilHelper.replaceFragment(this,HomeFragment())
    }
}