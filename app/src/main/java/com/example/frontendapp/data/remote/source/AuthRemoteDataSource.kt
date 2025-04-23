package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.api.ApiService

class AuthRemoteDataSource(private val api: ApiService) {
    suspend fun createUser(usuario: Usuario) = api.singup(usuario)
    suspend fun login(idToken: Usuario) = api.loginGoogle(idToken)

}