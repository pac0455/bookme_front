package com.example.frontendapp.data.remote.source

import ReservaResponseDTO
import com.example.frontendapp.data.model.Reserva.ReservaCreateDto
import com.example.frontendapp.data.model.Reserva.ReservaResponseNegocioDTO
import com.example.frontendapp.data.model.pago.EstadoPago
import com.example.frontendapp.data.remote.api.ReservaApi
import com.example.frontendapp.data.remote.reponses.Resource
import retrofit2.Response

class ReservaRepo(
    private val reservaApi: ReservaApi
){
    suspend fun addReserva(reserva: ReservaCreateDto): Resource<ReservaResponseDTO> {
        return try {
            val response = reservaApi.addReserva(reserva)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun actualizarEstadoPago(reservaId: Int, estadoPago: EstadoPago): Resource<Unit> {
        return try {
            val response = reservaApi.actualizarEstadoPago(reservaId, estadoPago.name)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }


    suspend fun cancelarReserva(reservaId: Int): Resource<ReservaResponseDTO> {
        return try {
            val response = reservaApi.cancelarReserva(reservaId)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }


    suspend fun getReservasByUserId(userId:String): Resource<List<ReservaResponseDTO>> {
        return try {
            val response = reservaApi.getReservasByUserId(userId)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
    suspend fun getReservasByNegocioId(negocioId:Int): Resource<List<ReservaResponseNegocioDTO>> {
        return try {
            val response = reservaApi.getReservasByNegocioId(negocioId)
            handleResponse(response)
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