package com.example.micasaapp.Model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrabajosHomeModel(
    @Json(name = "idTrabajo") val idTrabajo: Int,
    @Json(name = "nombreTrabajador") val nombreTrabajador: String,
    @Json(name = "nombreProfecion") val nombreProfecion: String,
    @Json(name = "direccion") val direccion: String,
    @Json(name = "imagen") val imagen: String
)