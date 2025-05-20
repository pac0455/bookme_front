package com.example.frontendapp.data.model

data class Horario(
    val id: Int = 0,
    val idNegocio: Int,
    val idUsuario: Int,
    val diaSemana: String, // Ej: "Lunes", "Martes", etc.
    val horaInicio: String, // formato "HH:mm" o "HH:mm:ss"
    val horaFin: String,    // igual que horaInicio
    val negocio: Negocio? = null
)