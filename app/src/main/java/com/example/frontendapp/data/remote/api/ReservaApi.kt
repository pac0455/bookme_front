package com.example.frontendapp.data.remote.api



import ReservaResponseDTO
import com.example.frontendapp.data.model.Reserva.ReservaCreateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


private const val reservaApi ="api/reservas"
interface ReservaApi {
    @POST(reservaApi)
    suspend fun addReserva(@Body reserva: ReservaCreateDto): Response<ReservaResponseDTO>
}