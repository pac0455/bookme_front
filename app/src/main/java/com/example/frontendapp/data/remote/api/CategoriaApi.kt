package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Categoria
import retrofit2.Response
import retrofit2.http.GET


private const val controller = "api/categoria"
interface CategoriaApi {
    @GET("$controller/categorias")
    suspend fun getAll(): Response<List<Categoria>>
}
