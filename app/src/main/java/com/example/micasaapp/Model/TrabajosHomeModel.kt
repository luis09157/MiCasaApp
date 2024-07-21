package com.example.micasaapp.Model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrabajosHomeModel(
    @Json(name = "idProveedor") val idProveedor: Int,
    @Json(name = "nombreCompleto") val nombreCompleto: String,
    @Json(name = "categorias") val categorias: String,
    @Json(name = "direccion") val direccion: String,
    @Json(name = "imagenTrabajo") val imagenTrabajo: String
)