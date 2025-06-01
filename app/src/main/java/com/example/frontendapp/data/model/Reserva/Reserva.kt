package com.example.frontendapp.data.model.Reserva

data class Reserva(
    var id: Int,
    var negocioId: Int,
    val usuarioId: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: String,
    val comentarioCliente: String,
    val fechaCreacion: String,
){
    companion object {
        fun init(): Reserva {
            return Reserva(
                id = 0,
                negocioId = 0,
                usuarioId = "",
                fecha = "",
                horaInicio = "",
                horaFin = "",
                estado = "",
                comentarioCliente = "",
                fechaCreacion = ""
            )
        }
    }
}
