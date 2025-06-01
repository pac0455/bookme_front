package com.example.frontendapp.data.model.Servicio

import com.google.gson.annotations.SerializedName

data class ServicioDetalleDto(
    val id: Int,
    val negocioId: Int,
    val nombre: String,
    val descripcion: String,
    val duracionMinutos: Int,
    val precio: Double,
    val negocioNombre: String,
    val categoria: String,
    val valoracionPromedioNegocio: Double,
    val numeroValoracionesNegocio: Int,
    val numeroReservas: Int,
    @SerializedName("imagenUrl")
    val imagen: String? = null
) {
    companion object {
        fun init(): ServicioDetalleDto {
            return ServicioDetalleDto(
                id = 0,
                negocioId = 0,
                nombre = "",
                descripcion = "",
                duracionMinutos = 0,
                precio = 0.0,
                negocioNombre = "",
                categoria = "",
                valoracionPromedioNegocio = 0.0,
                numeroValoracionesNegocio = 0,
                numeroReservas = 0,
                imagen = null
            )
        }
    }
}

