package com.example.frontendapp.data.model.Servicio

data class Servicio(
    val id: Int,
    val negocioId: Int,
    val nombre: String,
    val descripcion: String,
    val duracionMinutos: Int,
    val precio: Double,
    val imagen: String?
) {
    companion object {
        fun init(): Servicio {
            return Servicio(
                id = 0,
                negocioId = 0,
                nombre = "",
                descripcion = "",
                duracionMinutos = 0,
                precio = 0.0,
                imagen = ""
            )
        }
    }
}
