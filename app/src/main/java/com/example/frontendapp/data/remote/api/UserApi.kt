package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.reponses.DeleteResponse
import com.example.frontendapp.data.remote.source.Resource
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


private const val controller = "api/usuario"
interface UserApi {

    // Signup con cuenta de Google (envías todos los datos necesarios del usuario)
    @POST("$controller/signup/google")
    suspend fun signupWithGoogle(@Body usuario: Usuario): Response<Usuario>

    // Login con Google (sólo envías el ID Token de Google)
    @GET("$controller/login/{idToken}")
    suspend fun loginWithGoogle(@Path("idToken") idToken: String): Response<LoginRegisterResultDTO>

    // Login tradicional con email/contraseña (puedes usar un modelo LoginRequest si prefieres)
    @POST("$controller/login")
    suspend fun login(@Body usuario: Usuario): Response<LoginRegisterResultDTO>

    // Registro tradicional
    @POST("$controller/register")
    suspend fun signup(@Body usuario: Usuario): Response<LoginRegisterResultDTO>
    //GetAll
    @GET(controller)
    suspend fun getAll(): Response <List<Usuario>>
    @DELETE("usuarios/{id}")
    suspend fun delete(@Path("id") id: String): Response<DeleteResponse>



}