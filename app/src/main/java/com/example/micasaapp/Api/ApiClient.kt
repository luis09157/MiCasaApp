package com.example.micasaapp.Api

import android.util.Log
import com.example.micasaapp.Data.CategoriasModel
import com.example.micasaapp.Model.SubCategoriasModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request


class ApiClient(private val baseUrl: String) {


    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun getCategorias(): List<CategoriasModel> {
        val request = Request.Builder()
            .url("$baseUrl/categorias")
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch data from API: ${response.code}")
            }

            val responseBody = response.body?.string()
            return parseCategorias(responseBody)
        }
    }
    fun getSubCategorias(): List<SubCategoriasModel> {
        val url = "$baseUrl/subcategorias/${DataConfig.IDCATEGORIA}" // Verifica la URL aquí
        Log.d("ApiClient", "URL de la solicitud: $url")

        val request = Request.Builder()
            .url(url)
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch data from API: ${response.code}")
            }

            val responseBody = response.body?.string()
            return parseSubCategorias(responseBody)
        }
    }

    private fun parseCategorias(responseBody: String?): List<CategoriasModel> {
        val adapter: JsonAdapter<List<CategoriasModel>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, CategoriasModel::class.java)
        )
        return adapter.fromJson(responseBody.orEmpty()) ?: emptyList()
    }
    private fun parseSubCategorias(responseBody: String?): List<SubCategoriasModel> {
        val adapter: JsonAdapter<List<SubCategoriasModel>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, SubCategoriasModel::class.java)
        )
        return adapter.fromJson(responseBody.orEmpty()) ?: emptyList()
    }
}
