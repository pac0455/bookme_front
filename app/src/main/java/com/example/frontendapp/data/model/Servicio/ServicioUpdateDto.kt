package com.example.frontendapp.data.model.Servicio

data class ServicioUpdateRequest(
    val id: Int,
    val negocioId: Int,
    val nombre: String,
    val descripcion: String,
    val duracionMinutos: Int,
    val precio: Double,
)
