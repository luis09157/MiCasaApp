package com.example.micasaapp.Data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoriasModel(
    @Json(name = "idCategoria") val idCategoria: Int,
    @Json(name = "nombreCategoria") val nombreCategoria: String,
    @Json(name = "imagenCategoria") val imagenCategoria: String
)
