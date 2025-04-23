package com.example.frontendapp.data.repository

import com.example.frontendapp.data.remote.source.AuthRemoteDataSource

class AuthRepository(private val remote: AuthRemoteDataSource) {
    suspend fun loginGoogle(token: String) = remote.login(token)
}
