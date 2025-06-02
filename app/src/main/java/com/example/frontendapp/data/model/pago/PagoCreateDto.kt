package com.example.frontendapp.data.model.pago

import com.google.gson.annotations.SerializedName

data class PagoCreateDto(
    val monto: Double,
    @SerializedName("metodoPago")
    val metodo: MetodoPagoDto,
    val moneda: String? = "EUR"
)

enum class MetodoPagoDto(val displayName: String) {
    @SerializedName("Tarjeta")
    TARJETA("Tarjeta de crédito/débito"),

    @SerializedName("Efectivo")
    EFECTIVO("Efectivo"),

    @SerializedName("Transferencia")
    TRANSFERENCIA("Transferencia bancaria"),

    @SerializedName("PayPal")
    PAYPAL("PayPal")
}

