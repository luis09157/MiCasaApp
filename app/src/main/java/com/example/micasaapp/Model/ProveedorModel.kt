package com.example.micasaapp.Model
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class ProveedorResponse(
    val datosProveedor: ProveedorModel,
    val listaFotosTrabajo: List<FotoTrabajoModel>,
    val listaCategorias: List<CategoriaProveedorModel>
)

@JsonClass(generateAdapter = true)
data class ProveedorModel(
    val idProveedor: Int,
    val email: String,
    val telefono: String,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val calle: String,
    val numeroExt: String,
    val colonia: String,
    val codigoPostal: Int,
    val estado: String,
    val municipio: String,
    val pais: String,
    val descripcion: String
)

@JsonClass(generateAdapter = true)
data class FotoTrabajoModel(
    val idFotoTrabajo: Int,
    val url: String
)
@JsonClass(generateAdapter = true)
data class CategoriaProveedorModel(
    val categoria: String,
    val subcategoria: String
)
