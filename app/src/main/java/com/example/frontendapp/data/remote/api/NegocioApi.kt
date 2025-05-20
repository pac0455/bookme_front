package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.source.Resource
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


private const val controller = "api/negocio"
interface NegocioApi{
    @POST("$controller")
    suspend fun add(@Body negocio: Negocio): Response<Negocio>

}