package com.example.frontendapp.data.remote.api



import ReservaResponseDTO
import com.example.frontendapp.data.model.Reserva.ReservaCreateDto
import com.example.frontendapp.data.model.Reserva.ReservaResponseNegocioDTO
import com.example.frontendapp.data.model.Reserva.ReservaPorDiaDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


private const val reservaApi ="api/reservas"
interface ReservaApi {
    @POST(reservaApi)
    suspend fun addReserva(@Body reserva: ReservaCreateDto): Response<ReservaResponseDTO>

    @GET("$reservaApi/Usuario/{userId}/Todas")
    suspend fun getReservasByUserId(@Path("userId") userId: String): Response<List<ReservaResponseDTO>>
    @PUT("$reservaApi/Cancelar/{id}")
    suspend fun cancelarReserva(@Path("id") reservaId: Int): Response<ReservaResponseDTO>

    @GET("$reservaApi/Negocio/{negocioId}/reservas")
    suspend fun getReservasByNegocioId(@Path("negocioId") negocioId: Int): Response<List<ReservaResponseNegocioDTO>>

    @GET("$reservaApi/Estadisticas/PorDiaSemana")
    suspend fun getReservasPorDiaSemana(@Query("negocioId") negocioId: Int): Response<List<ReservaPorDiaDTO>>

    @PUT("$reservaApi/ActualizarEstadoPago/{reservaId}")
    suspend fun actualizarEstadoPago(
        @Path("reservaId") reservaId: Int,
        @Query("nuevoEstado") nuevoEstado: String
    ): Response<Unit>
}