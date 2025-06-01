package com.example.frontendapp.ui.theme.composables.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.composables.Items.ReservaListItem
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel

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

