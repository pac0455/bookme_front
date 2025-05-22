package com.example.frontendapp.data.model

data class Servicio(
    val id: Int? = null,
    val negocioId: Int,
    val nombre: String? = null,
    val descripcion: String? = null,
    val duracionMinutos: Int? = null,
    val precio: Double? = null
)

