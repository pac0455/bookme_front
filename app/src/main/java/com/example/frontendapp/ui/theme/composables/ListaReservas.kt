package com.example.frontendapp.ui.theme.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.ReservaDetallada
import com.example.frontendapp.data.model.ServicioConPago
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.composables.ListItems.ReservaListItem
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel

@Composable
fun ListaReservas(
    viewModel: ReservasViewModel,
    modifier: Modifier = Modifier,
    onLoading: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val reservas by viewModel.reservasDetalladasState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    when {
        isLoading -> {
            onLoading()
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        error != null -> {
            onError(error!!)
            Text("Error al cargar reservas: $error", color = MaterialTheme.colorScheme.error)
        }

        reservas.isEmpty() -> {
            onSuccess()
            Text("No hay reservas registradas.", style = MaterialTheme.typography.bodyMedium)
        }

        else -> {
            onSuccess()
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier.fillMaxWidth()
            ) {
                items(reservas) { reserva ->
                    ReservaListItem(
                        reserva = reserva,
                        onDeleteClick = {
                            // Acción de eliminación aquí si la necesitas
                        }
                    )
                }
            }
        }
    }
}
class FakeReservasViewModel : ReservasViewModel(negocioRemoteSource = NegocioRemoteSource(RetrofitInstance.negocioApi)) {

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

@Preview(showBackground = true)
@Composable
fun PreviewListaReservas() {
    val fakeViewModel = remember { FakeReservasViewModel() }

    ListaReservas(
        viewModel = fakeViewModel,
        onLoading = { println("Cargando...") },
        onSuccess = { println("Cargado correctamente") },
        onError = { error -> println("Error: $error") }
    )
}

