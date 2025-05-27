package com.example.frontendapp.data.model.Usuario

data class LoginRegisterResultDTO (
    val token: String,
    val usuario: Usuario,
    val roles: List<String>
)