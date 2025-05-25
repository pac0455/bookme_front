package com.example.frontendapp.data.model

data class ServicioDetalleDto(
    val id: Int,
    val negocioId: Int,
    val nombre: String?,
    val descripcion: String?,
    val duracionMinutos: Int?,
    val precio: Double?,
    val negocioNombre: String?,
    val categoria: String?,
    val valoracionPromedio: Double,
    val numeroValoraciones: Int,
    val numeroReservas: Int,
    val imagen: String? = null
)
