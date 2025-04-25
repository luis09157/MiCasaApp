package com.example.micasaapp.Model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BannerModel(
    @Json(name = "idBanner") val idBanner: Int = 0,
    @Json(name = "titulo") val titulo: String? = null,
    @Json(name = "descripcion") val descripcion: String? = null,
    @Json(name = "imagenTrabajo") val imagenTrabajo: String? = null,
    @Json(name = "idTrabajador") val idTrabajador: Int = 0,
    @Json(name = "idCategoria") val idCategoria: Int = 0,
    @Json(name = "enlace") val enlace: String? = null,
    @Json(name = "fechaRegistro") val fechaRegistro: String? = null
)
