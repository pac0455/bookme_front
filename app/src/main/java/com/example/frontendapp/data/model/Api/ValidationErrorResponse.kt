package com.example.frontendapp.data.model.Api

import com.google.gson.annotations.SerializedName

data class ValidationErrorResponse(
    val success: Boolean,
    @SerializedName("errores")
    val errors: Map<String, String>? = null
)