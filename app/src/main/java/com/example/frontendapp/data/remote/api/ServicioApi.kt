package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.model.Servicio.ServicioDetalleDto
import com.example.frontendapp.data.model.Servicio.ServicioUpdateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ServicioApi {

    @GET("api/servicio")
    suspend fun getAll(): Response<List<Servicio>>

    @GET("api/servicio/{id}")
    suspend fun get(@Path("id") id: Int): Response<Servicio>

    @Multipart
    @POST("api/servicio")
    suspend fun addServicio(
        @Part("nombre") nombre: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("duracionMinutos") duracionMinutos: RequestBody,
        @Part("precio") precio: RequestBody,
        @Part("negocioId") negocioId: RequestBody,
        @Part imagen: MultipartBody.Part? // null si no hay imagen
    ): Response<Servicio>

    @Multipart
    @PUT("api/servicio/{id}/imagen")
    suspend fun updateImagenServicio(
        @Path("id") id: Int,
        @Part imagen: MultipartBody.Part
    ): Response<Unit>

    @PUT("api/servicio/{id}")
    suspend fun updateServicio(
        @Path("id") id: Int,
        @Body servicio: ServicioUpdateRequest
    ): Response<Servicio>


    @DELETE("api/servicio/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>

    // Obtener servicios por negocioId
    @GET("api/servicio/Negocio/{negocioId}")
    suspend fun getServiciosByNegocioId(@Path("negocioId") negocioId: Int): Response<List<Servicio>>

    // Obtener servicios con detalle por negocioId
    @GET("api/servicio/Detalle/Negocio/{negocioId}")
    suspend fun getServiciosDetalleByNegocioId(@Path("negocioId") negocioId: Int): Response<List<ServicioDetalleDto>>
}
