package com.example.frontendapp.data.remote.source


// Clase que representa el estado de una llamada a cualquiera de las APIs
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data) //resultado correcto.
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)//ocurrió un error
    class Loading<T> : Resource<T>()  // llamada en curso.
    class None<T> : Resource<T>() //recurso no llamado
}