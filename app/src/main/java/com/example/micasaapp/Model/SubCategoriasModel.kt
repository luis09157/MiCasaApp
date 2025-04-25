package com.example.micasaapp.Model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SubCategoriasModel(
    @Json(name = "idSubcategoria") val idSubCategoria: Int,
    @Json(name = "nombreSubcategoria") val nombreSubcategoria: String,
    @Json(name = "idCategoria") val idCategoria: Int,
    @Json(name = "imagenSubcategoria") val imagenSubcategoria: String,
    @Json(name = "disponible") val disponible: Boolean? = true
)