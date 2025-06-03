package com.example.frontendapp.data.model.Reserva

import com.example.frontendapp.data.model.pago.PagoCreateDto
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class ReservaCreateDto(
    var negocioId: Int,
    var usuarioId: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: String? = null,
    val servicioId: Int,
    val pago: PagoCreateDto? = null
) {
    companion object {
        fun init(): ReservaCreateDto {
            return ReservaCreateDto(
                negocioId = 0,
                usuarioId = "",
                fecha = LocalDate.MIN.format(DateTimeFormatter.ISO_LOCAL_DATE),
                horaInicio = "",
                horaFin = "",
                estado = null,
                servicioId = -1,
                pago = null
            )
        }
    }
}
