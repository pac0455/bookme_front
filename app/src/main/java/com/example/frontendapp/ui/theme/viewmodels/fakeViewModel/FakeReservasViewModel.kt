package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import com.example.frontendapp.data.model.ReservaDetallada
import com.example.frontendapp.data.model.ServicioConPago
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel

class FakeReservasViewModel : ReservasViewModel(NegocioRemoteSource(RetrofitInstance.negocioApi)) {
    init {
        _reservasDetalladasState.value = listOf(
            ReservaDetallada(
                reservaId = 1,
                fecha = "2025-05-22",
                estado = "Confirmada",
                comentarioCliente = "Muy buena atención.",
                estadoPagoGeneral = "completado",
                servicios = listOf(
                    ServicioConPago(nombre = "Masaje", precio = 25.0, pago = null),
                    ServicioConPago(nombre = "Facial", precio = 30.0, pago = null)
                ),
                totalReserva = 55.0
            ),
            ReservaDetallada(
                reservaId = 2,
                fecha = "2025-05-23",
                estado = "Pendiente",
                comentarioCliente = "Primera vez que reservo.",
                estadoPagoGeneral = "pendiente",
                servicios = listOf(
                    ServicioConPago(nombre = "Spa", precio = 40.0, pago = null)
                ),
                totalReserva = 40.0
            )
        )
    }
}