package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Servicio
import com.example.frontendapp.data.model.ServicioDetalleDto
import com.example.frontendapp.data.remote.api.ServicioApi
import com.example.frontendapp.data.remote.reponses.Resource
import retrofit2.Response

class ServicioRemoteSource(
    private val servicioApi: ServicioApi
) {

    private fun validateServicio(servicio: Servicio): String? {
        return when {
            servicio.nombre.isNullOrBlank() -> "El nombre del servicio no puede estar vacío."
            servicio.descripcion.isNullOrBlank() -> "La descripción no puede estar vacía."
            servicio.precio == null || servicio.precio <= 0 -> "El precio debe ser mayor que cero."
            else -> null
        }
    }

    suspend fun addServicio(servicio: Servicio): Resource<Servicio> {
        val validationError = validateServicio(servicio)
        if (validationError != null) return Resource.Error(validationError)

        return try {
            val response = servicioApi.add(servicio)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun getServicio(id: Int): Resource<Servicio> {
        return try {
            val response = servicioApi.get(id)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun getAllServicios(): Resource<List<Servicio>> {
        return try {
            val response = servicioApi.getAll()
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun updateServicio(id: Int, servicio: Servicio): Resource<Unit> {
        val validationError = validateServicio(servicio)
        if (validationError != null) return Resource.Error(validationError)

        return try {
            val response = servicioApi.update(id, servicio)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun deleteServicio(id: Int): Resource<Unit> {
        return try {
            val response = servicioApi.delete(id)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun getServiciosByNegocioId(negocioId: Int): Resource<List<Servicio>> {
        return try {
            val response = servicioApi.getServiciosByNegocioId(negocioId)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error al obtener servicios: ${e.message}")
        }
    }

    // Función para obtener servicios con detalle por negocioId
    suspend fun getServiciosDetalleByNegocioId(negocioId: Int): Resource<List<ServicioDetalleDto>> {
        return try {
            val response = servicioApi.getServiciosDetalleByNegocioId(negocioId)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error al obtener servicios detallados: ${e.message}")
        }
    }

    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                Resource.Success(result)
            } else {
                // Para respuestas con no content (204), devolver Success con un valor Unit (si es Unit)
                @Suppress("UNCHECKED_CAST")
                if (response.code() == 204) {
                    Resource.Success(Unit as T)
                } else {
                    Resource.Error("Respuesta vacía del servidor.")
                }
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
        }
    }
}
