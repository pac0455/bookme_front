package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.api.NegocioApi
import com.example.frontendapp.data.remote.reponses.Resource
import retrofit2.Response

class NegocioRemoteSource(
    private val negocioApi: NegocioApi
) {

    private fun validateNegocio(negocio: Negocio): String? {
        return when {
            negocio.nombre.isNullOrBlank() -> "El nombre del negocio no puede estar vacío."
            negocio.descripcion.isNullOrBlank() -> "La descripción no puede estar vacía."
            negocio.direccion.isNullOrBlank() -> "La dirección no puede estar vacía."
            negocio.latitud == null -> "La latitud no puede estar vacía."
            negocio.longitud == null -> "La longitud no puede estar vacía."
            else -> null
        }
    }

    suspend fun addNegocio(negocio: Negocio): Resource<Negocio> {
        val validationError = validateNegocio(negocio)
        if (validationError != null) return Resource.Error(validationError)

        return try {
            val response = negocioApi.add(negocio)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun getNegocio(id: Int): Resource<Negocio> {
        return try {
            val response = negocioApi.get(id)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun getAllNegocios(): Resource<List<Negocio>> {
        return try {
            val response = negocioApi.getAll()
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun updateNegocio(id: Int, negocio: Negocio): Resource<Unit> {
        val validationError = validateNegocio(negocio)
        if (validationError != null) return Resource.Error(validationError)

        return try {
            val response = negocioApi.update(id, negocio)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun deleteNegocio(id: Int): Resource<Unit> {
        return try {
            val response = negocioApi.delete(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("Respuesta vacía del servidor")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
        }
    }
}