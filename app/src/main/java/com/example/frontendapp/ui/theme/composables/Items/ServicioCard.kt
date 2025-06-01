package com.example.frontendapp.ui.theme.composables.Items

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel


@Composable
fun ServicioCard(
    viewModel: ServicioViewModel,
    negocioId: Int,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false
) {
    val serviciosState by viewModel.serviciosDetalleByNegocioIdState.collectAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f  // Usa el 85% del ancho de pantalla

    val imagenUrl = viewModel.getServicioImageUrl(negocioId)

    if (!isPreview) {
        LaunchedEffect(negocioId) {
            viewModel.getServiciosDetalleByNegocioId(
                negocioId,
                onLoading = { /* loading */ },
                onSuccess = { /* success */ },
                onError = { error -> Log.e("ServicioTab", error) }
            )
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Servicios disponibles", style = MaterialTheme.typography.titleMedium)

        if (isPreview) {
            // Mostrar datos simulados sin estado de carga ni error
            val servicios = (viewModel as? FakeServicioViewModel)
                ?.serviciosDetalleByNegocioIdState
                ?.value
                ?.data ?: emptyList()

            if (servicios.isEmpty()) {
                Text("Este negocio no tiene servicios registrados.", color = Color.Gray)
            } else {
                servicios.forEach {
                    ServicioCardItem(
                        servicio = it,
                        imageUrl = imagenUrl ?: "",
                        modifier = androidx.compose.ui.Modifier.Companion.width(cardWidth)
                    )
                }
            }
        } else {
            when (serviciosState) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Error -> {
                    val errorMessage = (serviciosState as Resource.Error).message ?: "Error"
                    Text(errorMessage, color = Color.Red)
                }
                is Resource.Success -> {
                    val servicios = (serviciosState as Resource.Success).data ?: emptyList()
                    if (servicios.isEmpty()) {
                        Text("Este negocio no tiene servicios registrados.", color = Color.Gray)
                    } else {
                        servicios.forEach {
                            ServicioCardItem(
                                servicio = it,
                                imageUrl = imagenUrl ?: "",
                                modifier = androidx.compose.ui.Modifier.Companion.width(cardWidth)
                            )
                        }
                    }
                }
                else -> Text("Sin datos")
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ServicioTabPreview() {
    MaterialTheme {
        ServicioCard(
            viewModel = FakeServicioViewModel(),
            negocioId = 1,
            isPreview = true
        )
    }
}


