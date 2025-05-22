package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.model.Reserva
import com.example.frontendapp.data.model.ReservaDetallada
import com.example.frontendapp.data.model.Servicio
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
    suspend fun getNegociosByUserId(): Resource<List<Negocio>> {
        return try {
            val response = negocioApi.getByUserId()
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Excepción: ${e.localizedMessage}")
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
    suspend fun updateNegocioByNombre(negocio: Negocio): Resource<Unit> {
        return try {
            val response = negocioApi.updateByNombre(negocio.nombre, negocio)
           handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Excepción: ${e.localizedMessage}")
        }
    }


    suspend fun updateNegocio(id: Int, negocio: Negocio): Resource<Unit> {
        val validationError = validateNegocio(negocio)
        if (validationError != null) return Resource.Error(validationError)

        return try {
            val response = negocioApi.update(id, negocio)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
    suspend fun getReservasDetalladasByNegocioId(id: Int): Resource<List<ReservaDetallada>> {
        return try {
            val response = negocioApi.getReservasDetalladas(id)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error al obtener reservas detalladas: ${e.message}")
        }
    }

    suspend fun deleteNegocio(id: Int): Resource<Unit> {
        return try {
            val response = negocioApi.delete(id)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
    suspend fun getServiciosByNegocioId(id: Int): Resource<List<Servicio>> {
        return try {
            val response = negocioApi.getServiciosByNegocioId(id)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error al obtener servicios: ${e.message}")
        }
    }
    suspend fun getReservasByNegocioId(id: Int): Resource<List<Reserva>> {
        return try {
            val response = negocioApi.getReservasByNegocioId(id)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error al obtener reservas: ${e.message}")
        }
    }


    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                Resource.Success(result)
            } else {
                // Manejo especial para 204 No Content
                @Suppress("UNCHECKED_CAST")
                Resource.Success(Unit as T) // Solo válido si T es Unit
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
        }
    }
}