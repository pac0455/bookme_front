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
)
