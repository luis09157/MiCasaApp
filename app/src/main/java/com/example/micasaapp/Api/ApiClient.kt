package com.example.micasaapp.Api

import android.util.Log
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiClient(private val baseUrl: String) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Usa add en lugar de addLast
        .build()

    private val client = OkHttpClient()

    fun getCategorias(): List<CategoriasModel> {
        val request = Request.Builder()
            .url("$baseUrl/categorias")
            .build()

        return executeRequest(request, ::parseCategorias)
    }

    fun getSubCategorias(): List<SubCategoriasModel> {
        val url = "$baseUrl/subcategorias/${DataConfig.ID_CATEGORIA}" // Verifica la URL aquí
        Log.d("ApiClient", "URL de la solicitud: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        return executeRequest(request, ::parseSubCategorias)
    }

    fun getTrabajosHome(): List<TrabajosHomeModel> {
        val request = Request.Builder()
            .url("$baseUrl/proveedor")
            .build()

        return executeRequest(request, ::parseTrabajosHome)
    }

    fun getBanners(): List<BannerModel> {
        val request = Request.Builder()
            .url("$baseUrl/proveedor/banner")
            .build()

        return executeRequest(request, ::parseBanners)
    }

    fun getTrabajadores(): List<TrabajadorModel> {
        val url = "$baseUrl/proveedor/categoria?idCategoria=${DataConfig.ID_CATEGORIA}&idSubcategoria=${DataConfig.ID_SUBCATEGORIA}"

        val request = Request.Builder()
            .url(url)
            .get() // Indicar explícitamente que es una solicitud GET
            .build()

        return executeRequest(request, ::parseTrabajadores)
    }

    // Nuevo método para obtener el proveedor
    fun getProveedor(idProveedor: Int): ProveedorResponse {
        val url = "$baseUrl/proveedor/$idProveedor"
        Log.d("ApiClient", "URL de la solicitud: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        return executeRequest(request, ::parseProveedorResponse)
    }

    private fun <T> executeRequest(request: Request, parseFunction: (String?) -> T): T {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch data from API: ${response.code}")
            }
            val responseBody = response.body?.string()
            return parseFunction(responseBody)
        }
    }

    private fun parseCategorias(responseBody: String?): List<CategoriasModel> {
        val adapter: JsonAdapter<List<CategoriasModel>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, CategoriasModel::class.java)
        )
        return adapter.fromJson(responseBody.orEmpty()) ?: emptyList()
    }

    private fun parseTrabajosHome(responseBody: String?): List<TrabajosHomeModel> {
        val adapter: JsonAdapter<List<TrabajosHomeModel>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, TrabajosHomeModel::class.java)
        )
        return adapter.fromJson(responseBody.orEmpty()) ?: emptyList()
    }

    private fun parseSubCategorias(responseBody: String?): List<SubCategoriasModel> {
        val adapter: JsonAdapter<List<SubCategoriasModel>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, SubCategoriasModel::class.java)
        )
        return adapter.fromJson(responseBody.orEmpty()) ?: emptyList()
    }

    private fun parseBanners(responseBody: String?): List<BannerModel> {
        val adapter: JsonAdapter<List<BannerModel>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, BannerModel::class.java)
        )
        return adapter.fromJson(responseBody.orEmpty()) ?: emptyList()
    }

    private fun parseTrabajadores(responseBody: String?): List<TrabajadorModel> {
        val adapter: JsonAdapter<List<TrabajadorModel>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, TrabajadorModel::class.java)
        )
        return adapter.fromJson(responseBody.orEmpty()) ?: emptyList()
    }

    private fun parseProveedorResponse(responseBody: String?): ProveedorResponse {
        val adapter: JsonAdapter<ProveedorResponse> = moshi.adapter(ProveedorResponse::class.java)
        return adapter.fromJson(responseBody.orEmpty()) ?: ProveedorResponse(
            datosProveedor = ProveedorModel(
                idProveedor = 0,
                email = "",
                telefono = "",
                nombre = "",
                apellidoPaterno = "",
                apellidoMaterno = "",
                calle = "",
                numeroExt = "",
                colonia = "",
                codigoPostal = 0,
                estado = "",
                municipio = "",
                pais = "",
                descripcion = ""
            ),
            listaFotosTrabajo = emptyList(),
            listaCategorias = emptyList()
        )
    }
}
