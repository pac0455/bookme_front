package com.example.frontendapp.ui.theme.composables.tab.negocioDetails

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.composables.Items.ServicioItem
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel


@Composable
fun NegocioServicioTab(
    viewModel: ServicioViewModel,
    navController: NavController,
    reservaViewModel: ReservasViewModel,
    servicioViewModel: ServicioViewModel,
    negocioId: Int,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false  // NUEVO parámetro
) {
    val serviciosState by viewModel.serviciosDetalleByNegocioIdState.collectAsState()



    // Solo llamar en modo normal, NO en preview
    if (!isPreview) {
        LaunchedEffect(negocioId) {
            viewModel.getServiciosDetalleByNegocioId(
                negocioId = negocioId,
                onLoading = { /* ... */ },
                onSuccess = { /* ... */ },
                onError = { /* ... */ }
            )
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Servicios del negocio", style = MaterialTheme.typography.titleMedium)

        when (val state = serviciosState) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }

            is Resource.Error -> {
                Text(
                    text = state.message ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error
                )
            }

            is Resource.Success -> {
                val servicios = state.data ?: emptyList()

                if (servicios.isEmpty()) {
                    Text("No hay servicios disponibles.")
                } else {
                    LazyColumn {
                        items(servicios, key = {it.id}) { servicio ->
                            ServicioItem(
                                servicio = servicio,
                                isSelected = false,
                                onClick = {
                                    Log.d("ListaServiciosTab", "Servicio seleccionado: ${servicio.nombre}")
                                    //Añadir un servicio al temp en servicioViewModel para pasarlo de una pantalla a otra
                                    servicioViewModel.setTempServicio(
                                        Servicio(
                                            negocioId = servicio.negocioId,
                                            precio = servicio.precio,
                                            descripcion = servicio.descripcion,
                                            id = servicio.id,
                                            imagen = null,
                                            duracionMinutos = servicio.duracionMinutos,
                                            nombre = servicio.nombre,
                                        )
                                    )
                                    //Pasar el id del negocio a la sigueinte pantalla
                                    navController.navigate(NavigationItem.RESERVA_FORM.createRoute(servicio.negocioId))
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                Text("Sin datos")
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListaServiciosTabPreview() {
    MaterialTheme {
        NegocioServicioTab(
            viewModel = FakeServicioViewModel(),
            negocioId = 1,
            reservaViewModel = FakeReservasViewModel(),
            isPreview = true,
            navController = rememberNavController(),
            servicioViewModel = FakeServicioViewModel()
        )
    }
}