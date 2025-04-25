package com.example.micasaapp.Model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrabajadorModel(
    @Json(name = "idProveedor") val idProveedor: Int = 0,
    @Json(name = "nombreCompleto") val nombreCompleto: String = "",
    @Json(name = "categorias") val categorias: String = "",
    @Json(name = "direccion") val direccion: String = "",
    @Json(name = "imagenTrabajo") val imagenTrabajo: String = "",
    @Json(name = "descripcion") val descripcion: String = "",
    @Json(name = "imagenPerfil") val imagenPerfil: String = "",
    @Json(name = "calificacion") val calificacion: Float = 0f
)
