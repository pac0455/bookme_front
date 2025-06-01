package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.model.Negocio.Ubicacion
import com.example.frontendapp.data.model.Reserva.Reserva
import com.example.frontendapp.data.model.Reserva.ReservaDetallada
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


private const val controller = "api/negocio"


interface NegocioApi {
    @POST("$controller")
    suspend fun add(@Body negocio: Negocio): Response<Negocio>

    @GET("$controller/{id}")
    suspend fun get(@Path("id") id: Int): Response<Negocio>

    @GET(controller)
    suspend fun getAll(): Response<List<Negocio>>

    @PUT("$controller/{id}")
    suspend fun update(@Path("id") id: Int, @Body negocio: Negocio): Response<Unit>

    @DELETE("$controller/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>

    @GET("$controller/ByUserId")
    suspend fun getByUserId(): Response<List<Negocio>>

    @PUT("$controller/ByNombre/{nombre}")
    suspend fun updateByNombre(@Path("nombre") nombre: String, @Body negocio: Negocio): Response<Unit>

    @GET("$controller/{id}/reservas")
    suspend fun getReservasByNegocioId(@Path("id") negocioId: Int): Response<List<Reserva>>
    @GET("$controller/{id}/reservas/detalladas")
    suspend fun getReservasDetalladas(@Path("id") negocioId: Int): Response<List<ReservaDetallada>>
    @Multipart
    @PUT("$controller/{id}/imagen")
    suspend fun updateNegocioImagen(
        @Path("id") id: Int,
        @Part nuevaImagen: MultipartBody.Part
    ): Response<Negocio>
    @POST("$controller/cliente/negocios")
    suspend fun getNegociosParaCliente(@Body ubicacion: Ubicacion?): Response<List<NegocioCardCliente>>
    @POST("api/Negocio/cliente/negocio/{id}")
    suspend fun getNegocioParaCliente(
        @Path("id") negocioId: Int,
        @Body ubicacion: Ubicacion? = null
    ): Response<NegocioCardCliente>


}

