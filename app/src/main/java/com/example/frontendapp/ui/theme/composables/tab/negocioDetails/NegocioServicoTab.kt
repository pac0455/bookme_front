package com.example.frontendapp.ui.theme.composables.tab.negocioDetails

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    isPreview: Boolean = false
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = serviciosState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando servicios...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error al cargar servicios",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            is Resource.Success -> {
                val servicios = state.data ?: emptyList()

                if (servicios.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sin servicios disponibles",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Este negocio aún no ha agregado servicios",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Header de la sección
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Servicios disponibles",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${servicios.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        items(servicios, key = { it.id }) { servicio ->
                            ServicioItem(
                                servicio = servicio,
                                isSelected = false,
                                onClick = {
                                    Log.d("ListaServiciosTab", "Servicio seleccionado: ${servicio.nombre}")
                                    // Añadir un servicio al temp en servicioViewModel para pasarlo de una pantalla a otra
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
                                    // Pasar el id del negocio a la siguiente pantalla
                                    navController.navigate(NavigationItem.RESERVA_FORM.createRoute(servicio.negocioId))
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin datos disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NegocioServicioTabImprovedPreview() {
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
