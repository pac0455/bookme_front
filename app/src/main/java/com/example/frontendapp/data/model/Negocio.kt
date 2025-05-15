package com.example.frontendapp.data.model

data class Negocio(
    val id: Int = 0,
    var nombre: String = "",
    var descripcion: String = "",
    var direccion: String = "",
    var latitud: Double? = null,
    var longitud: Double? = null,
    var categoria: String = "",
    var horarioAtencion: String = "",
    var activo: Boolean = true
)
