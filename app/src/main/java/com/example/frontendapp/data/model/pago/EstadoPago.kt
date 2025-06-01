package com.example.frontendapp.data.model.pago

enum class EstadoPago {
    Pendiente,  // El pago está en proceso y no ha sido confirmado.
    Confirmado,  // El pago ha sido completado y confirmado.
    Fallido,  // El pago no se pudo completar debido a un error.
}