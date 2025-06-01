package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Api.ApiResponse
import com.example.frontendapp.data.model.Horario
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate


private const val horarioApi="api/horario"
interface HorarioApi {
    @GET("$horarioApi/ByNegocioId/{id}")
    suspend fun GetHorariosByNegocioId(@Path("id") negocioId: Int) : Response<List<Horario>>

    @GET("$horarioApi/Disponibles/{negocioId}/{servicioId}/{fecha}")
    suspend fun getHorarioDisponible(
        @Path("negocioId") negocioId: Int,
        @Path("servicioId") servicioId: Int,
        @Path("fecha") date: String
    ): Response<List<Horario>>
}