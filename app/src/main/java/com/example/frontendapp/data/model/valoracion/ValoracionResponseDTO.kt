package com.example.frontendapp.data.model.valoracion

data class ValoracionResponseDTO(
    val id: Int,
    val negocioId: Int,
    val usuarioId: String,
    val puntuacion: Double,
    val comentario: String,
    val fechaValoracion: String,
    val usuario: UsuarioDTO
)


data class UsuarioDTO(
    val id: String,
    val userName: String,
    val email: String
)
