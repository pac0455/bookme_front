package com.example.frontendapp.data.model.Reserva

import com.google.gson.annotations.SerializedName

data class ReservaPorDiaDTO(
    @SerializedName("diaSemana")
    val dia: String,

    @SerializedName("totalReservas")
    val cantidad: Int
)