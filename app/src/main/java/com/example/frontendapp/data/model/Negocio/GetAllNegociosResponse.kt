package com.example.frontendapp.data.model.Negocio

import com.example.frontendapp.data.model.Categoria

data class GetAllNegociosResponse(
    val success: Boolean,
    val message: String?,
    val innerMessage: String?,
    val errorCode: String?,
    val data: List<NegocioResponseAdminDTO>?
)

data class NegocioResponseAdminDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: Categoria,
    val direccion: String,
    val rating: Float,
    val reviewCount: Int,
    val isActive: Boolean,
    val isOpen: Boolean,
    val latitud: Double?,
    val longitud: Double?,
    val bloqueado: Boolean,
)

