package com.example.frontendapp.data.dto

import com.example.frontendapp.data.remote.reponses.Resource
import com.google.gson.annotations.SerializedName

data class ValidationErrorResponse(
    val success: Boolean,
    @SerializedName("errores") // Asegúrate de que el nombre coincida con el JSON
    val errors: Map<String, String>? = null // Cambia a Map<String, String> si los errores son cadenas
)
