package com.example.frontendapp.data.remote.reponses

import com.example.frontendapp.data.model.Api.ValidationErrorResponse


// Clase que representa el estado de una llamada a cualquiera de las APIs
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val validationResponse: ValidationErrorResponse? = null
) {
    class Success<T>(data: T) : Resource<T>(data)

    class Error<T>(
        message: String,
        data: T? = null,
        validationResponse: ValidationErrorResponse? = null
    ) : Resource<T>(data, message, validationResponse)

    class Loading<T> : Resource<T>()
    class None<T> : Resource<T>()
}