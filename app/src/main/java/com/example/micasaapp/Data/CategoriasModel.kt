package com.example.micasaapp.Data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoriasModel(
    @Json(name = "idCategoria") val idCategoria: Int,
    @Json(name = "nombreCategoria") val nombreCategoria: String,
    @Json(name = "imagenCategoria") val imagenCategoria: String,
    @Json(name = "cantidadServicios") val cantidadServicios: Int? = 0,
    @Json(name = "disponible") val disponible: Boolean? = false
)
