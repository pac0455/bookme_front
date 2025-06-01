package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.valoracion.ValoracionCreateDTO
import com.example.frontendapp.data.model.valoracion.ValoracionResponseDTO
import com.example.frontendapp.data.remote.api.ValoracionApi
import com.example.frontendapp.data.remote.reponses.Resource
import retrofit2.Response

class ValoracionRepo(
    private val valoracionApi: ValoracionApi
) {
    suspend fun addValoracion(valoracion: ValoracionCreateDTO): Resource<ValoracionResponseDTO> {
        return try {
            val response = valoracionApi.crearValoracion(valoracion)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    suspend fun getValoracionesPorNegocio(negocioId: Int): Resource<List<ValoracionResponseDTO>> {
        return try {
            val response = valoracionApi.getValoracionesPorNegocio(negocioId)
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
                @Suppress("UNCHECKED_CAST")
                Resource.Success(Unit as T)
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
        }
    }
}
