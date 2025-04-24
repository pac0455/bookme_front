package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.LoginRequest
import com.example.frontendapp.data.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Signup con cuenta de Google (envías todos los datos necesarios del usuario)
    @POST("api/Usuarios/signup/google")
    suspend fun signupWithGoogle(@Body usuario: Usuario): Response<Usuario>

    // Login con Google (sólo envías el ID Token de Google)
    @POST("api/Usuarios/login/google/{idToken}")
    suspend fun loginWithGoogle(@Path("idToken") idToken: String): Response<Usuario>

    // Login tradicional con email/contraseña (puedes usar un modelo LoginRequest si prefieres)
    @POST("api/Usuarios/login")
    suspend fun login(@Body credentials: LoginRequest): Response<Usuario>

    // Registro tradicional
    @POST("api/Usuarios/signup")
    suspend fun signup(@Body usuario: Usuario): Response<Usuario>
}