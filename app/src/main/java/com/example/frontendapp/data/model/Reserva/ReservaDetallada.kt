package com.example.frontendapp.data.model.Reserva

data class ReservaDetallada(
    val reservaId: Int,
    val fecha: String?,
    val estado: String?,
    val comentarioCliente: String?,
    val servicios: List<ServicioConPago>,
    val estadoPagoGeneral: String,
    val totalReserva: Double
)

data class ServicioConPago(
    val nombre: String?,
    val precio: Double?,
    val pago: Pago?
)

data class Pago(
    val monto: Double,
    val estado: String,
    val metodo: String,
    val fechaPago: String
)
