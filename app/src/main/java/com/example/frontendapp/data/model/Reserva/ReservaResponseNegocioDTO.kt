package com.example.frontendapp.data.model.Reserva

import com.example.frontendapp.data.model.pago.EstadoPago

data class ReservaResponseNegocioDTO(
    val id: Int,
    val username: String,
    val nReservasUsuario: Int,
    val servicioNombre: String,
    val servicioDescripcion: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val precio: Double,
    val moneda: String,
    val estadoReserva: EstadoReserva,
    val estadoPago: EstadoPago
)
