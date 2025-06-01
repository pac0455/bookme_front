package com.example.frontendapp.data.model.valoracion

data class ValoracionCreateDTO(
    val negocioId: Int,
    val usuarioId: String,
    val puntuacion: Double,
    val comentario: String
) {
    companion object {
        fun init(): ValoracionCreateDTO {
            return ValoracionCreateDTO(
                negocioId = -1,
                usuarioId = "desconocido",
                puntuacion = 0.0,
                comentario = ""
            )
        }
    }
}
