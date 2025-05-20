package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.api.NegocioApi
import com.example.frontendapp.data.remote.api.UserApi

class NegocioRemoteSource(private val negocioApi: NegocioApi) {
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
        if (validationError != null) {
            return Resource.Error(validationError)
        }
        return try {
            val response = negocioApi.add(negocio)
            if (response.isSuccessful) {
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
            // Tu implementación de Retrofit devuelve Resource, por lo que esto ya es correcto
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
}