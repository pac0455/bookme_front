package com.example.frontendapp.data.remote.api



import ReservaResponseDTO
import com.example.frontendapp.data.model.Reserva.ReservaCreateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


private const val reservaApi ="api/reservas"
interface ReservaApi {
    @POST(reservaApi)
    suspend fun addReserva(@Body reserva: ReservaCreateDto): Response<ReservaResponseDTO>

    @GET("$reservaApi/Usuario/{userId}/Todas")
    suspend fun getReservasByUserId(@Path("userId") userId: String): Response<List<ReservaResponseDTO>>
}