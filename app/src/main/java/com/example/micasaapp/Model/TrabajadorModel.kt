package com.example.micasaapp.Model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrabajadorModel(
    @Json(name = "idProveedor") val idProveedor: Int = 0,
    @Json(name = "nombreCompleto") val nombreCompleto: String = "",
    @Json(name = "descripcion") val descripcion: String = "",
    @Json(name = "imagenPerfil") val imagenPerfil: String = ""

)
