package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.remote.api.CategoriaApi
import com.example.frontendapp.data.remote.reponses.Resource
import org.json.JSONObject
import retrofit2.Response

class CategoriaRemoteDataSource(private val categoriaApi: CategoriaApi) {

    suspend fun getCategorias(): Resource<List<Categoria>> = try {
        val response = categoriaApi.getAll()
        handleResponse(response)
    } catch (ex: Exception) {
        Resource.Error(ex.message ?: "Error al cargar las categorías")
    }

    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        if (response.isSuccessful) {
            val result = response.body()
            return if (result != null) Resource.Success(result)
            else if (response.code() == 204) Resource.Success(Unit as T)
            else Resource.Error("Respuesta vacía del servidor.")
        }

        val rawError = response.errorBody()?.string()
        val fallbackMessage = try {
            JSONObject(rawError ?: "").optString("message", "Error desconocido del servidor.")
        } catch (e: Exception) {
            rawError ?: "Error desconocido del servidor."
        }
        return Resource.Error("Error del servidor: ${response.code()} - $fallbackMessage")
    }

}