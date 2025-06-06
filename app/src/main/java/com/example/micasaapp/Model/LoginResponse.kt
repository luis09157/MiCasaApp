package com.example.micasaapp.Model

data class LoginResponse(
    val idUsuario: Int,
    val nombre: String,
    val apPaterno: String,
    val apMaterno: String,
    val token: String
)