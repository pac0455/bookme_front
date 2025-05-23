package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Servicio
import com.example.frontendapp.data.model.ServicioDetalleDto
import retrofit2.Response
import retrofit2.http.*

interface ServicioApi {

    @GET("api/servicio")
    suspend fun getAll(): Response<List<Servicio>>

    @GET("api/servicio/{id}")
    suspend fun get(@Path("id") id: Int): Response<Servicio>

    @POST("api/servicio")
    suspend fun add(@Body servicio: Servicio): Response<Servicio>

    @PUT("api/servicio/{id}")
    suspend fun update(@Path("id") id: Int, @Body servicio: Servicio): Response<Unit>

    @DELETE("api/servicio/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>

    // Obtener servicios por negocioId
    @GET("api/servicio/Negocio/{negocioId}")
    suspend fun getServiciosByNegocioId(@Path("negocioId") negocioId: Int): Response<List<Servicio>>

    // Obtener servicios con detalle por negocioId
    @GET("api/servicio/Detalle/Negocio/{negocioId}")
    suspend fun getServiciosDetalleByNegocioId(@Path("negocioId") negocioId: Int): Response<List<ServicioDetalleDto>>
}
