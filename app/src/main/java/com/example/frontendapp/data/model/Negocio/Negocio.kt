package com.example.frontendapp.data.model.Negocio

import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Horario

data class Negocio(
    val id: Int = 0,
    var nombre: String = "",
    var descripcion: String = "",
    var direccion: String = "",
    var latitud: Double? = null,
    var longitud: Double? = null,
    var categoriaId: Int = -1,
    val categoria: Categoria?= null,
    var horarioAtencion: List<Horario> = listOf(),
    var activo: Boolean = true,
    val bloqueado: Boolean= false,
    var logoUrl: String = "",
)