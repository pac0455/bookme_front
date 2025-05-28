package com.example.frontendapp.data.model.Negocio

data class NegocioCardCliente(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val direccion: String,
    val rating: Float,
    val reviewCount: Int,
    val isActive: Boolean,
    val isOpen: Boolean,
    val distancia: Double?,
    val latitud: Double?,
    val longitud: Double?
) {
    companion object {
        fun init(): NegocioCardCliente {
            return NegocioCardCliente(
                id = 0,
                nombre = "Negocio por defecto",
                descripcion = "Descripción por defecto",
                categoria = "General",
                direccion = "Dirección por defecto",
                rating = 0.0f,
                reviewCount = 0,
                isActive = true,
                isOpen = true,
                distancia = null,
                latitud = null,
                longitud = null
            )
        }
    }
}

