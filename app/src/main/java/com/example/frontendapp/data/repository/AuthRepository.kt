package com.example.frontendapp.data.repository

import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource


class AuthRepository(private val remote: AuthRemoteDataResource) {
    suspend fun signupGoogle(usuario: Usuario) = remote.signupWithGoogle(usuario)
    suspend fun signup(usuario: Usuario) = remote.registerUser(usuario)
    suspend fun loginWithGoogle(token: String) = remote.loginWithGoogle(token)
    suspend fun login(usuario: Usuario) = remote.login(usuario)
}
