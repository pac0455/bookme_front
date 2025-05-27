package com.example.frontendapp.data.model.Api

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String? = null // <-- Agregado para evitar el error
)