package com.example.frontendapp.data.model.Servicio

data class ServicioUpdateRequest(
    val id: Int? = null,
    val negocioId: Int? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val duracionMinutos: Int? = null,
    val precio: Double? = null,
)
