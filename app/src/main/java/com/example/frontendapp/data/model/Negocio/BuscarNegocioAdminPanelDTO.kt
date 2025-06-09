package com.example.frontendapp.data.model.Negocio


data class BuscarNegocioDTO(
    val nombre: String,
    val id: String,
    val isNegocio: String,
    val categoria: String,
    val listaFiltrada: List<Negocio>
)
