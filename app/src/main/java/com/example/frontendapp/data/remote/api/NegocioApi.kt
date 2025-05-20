package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Negocio
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


private const val controller = "api/negocio"
interface NegocioApi {
    // Create a new Negocio
    @POST("$controller")
    suspend fun add(@Body negocio: Negocio): Response<Negocio>

    // Get a single Negocio by ID
    @GET("$controller/{id}")
    suspend fun get(@Path("id") id: Int): Response<Negocio>

    // Get all Negocios
    @GET("$controller")
    suspend fun getAll(): Response<List<Negocio>>

    // Update an existing Negocio
    @PUT("$controller/{id}")
    suspend fun update(@Path("id") id: Int, @Body negocio: Negocio): Response<Unit>

    // Delete a Negocio by ID
    @DELETE("$controller/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit>
}