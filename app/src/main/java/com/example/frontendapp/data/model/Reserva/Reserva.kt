package com.example.frontendapp.data.model.Reserva

data class Reserva(
    val id: Int? = null,
    val negocioId: Int,
    val usuarioId: String,
    val fecha: String?,             // Formato ISO 8601 recomendado (ej: "2024-05-22")
    val horaInicio: String?,        // Formato "HH:mm:ss"
    val horaFin: String?,
    val estado: String?,
    val comentarioCliente: String?,
    val fechaCreacion: String?      // Formato "yyyy-MM-dd'T'HH:mm:ss"
)
