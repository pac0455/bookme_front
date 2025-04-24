package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.api.ApiService

class AuthRemoteDataSource(private val api: ApiService) {
    suspend fun createUser(usuario: Usuario) = api.signup(usuario)
    suspend fun login(idToken: String) = api.loginWithGoogle(idToken)

}