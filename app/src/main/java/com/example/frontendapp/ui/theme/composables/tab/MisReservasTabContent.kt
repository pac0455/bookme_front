package com.example.frontendapp.ui.theme.composables.tab

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.Items.ReservaCard
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel

import java.time.LocalDate

private val TAG= "MisReservasTabContent"
@SuppressLint("ShowToast")
@Composable
fun MisReservasTabContent(
    reservasViewModel: ReservasViewModel
) {
    val reservasResource by reservasViewModel.reservasByUserState.collectAsState()

    // Estados para filtros
    var filtroEstado by remember { mutableStateOf<EstadoReserva?>(null) }
    var filtroFecha by remember { mutableStateOf<FiltroFecha>(FiltroFecha.TODAS) }
    var busqueda by remember { mutableStateOf("") }
    var mostrarFiltros by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        reservasViewModel.getReservasByUserId(RetrofitInstance.getUserId())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con título y botón de filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Reservas",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Row {
                // Botón de búsqueda
                IconButton(
                    onClick = { /* Implementar búsqueda si es necesario */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Botón de filtros
                IconButton(
                    onClick = { mostrarFiltros = !mostrarFiltros }
                ) {
                    Icon(
                        imageVector = if (mostrarFiltros) Icons.Default.FilterListOff else Icons.Default.FilterList,
                        contentDescription = "Filtros",
                        tint = if (filtroEstado != null || filtroFecha != FiltroFecha.TODAS)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Panel de filtros expandible
        if (mostrarFiltros) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Filtros",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filtro por estado
                    Text(
                        text = "Estado de la reserva",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                onClick = { filtroEstado = null },
                                label = { Text("Todas") },
                                selected = filtroEstado == null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }

                        items(EstadoReserva.values()) { estado ->
                            FilterChip(
                                onClick = {
                                    filtroEstado = if (filtroEstado == estado) null else estado
                                },
                                label = { Text(estado.name) },
                                selected = filtroEstado == estado,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtro por fecha
                    Text(
                        text = "Período",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(FiltroFecha.entries.toTypedArray()) { fecha ->
                            FilterChip(
                                onClick = { filtroFecha = fecha },
                                label = { Text(fecha.displayName) },
                                selected = filtroFecha == fecha,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    // Botón para limpiar filtros
                    if (filtroEstado != null || filtroFecha != FiltroFecha.TODAS) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = {
                                filtroEstado = null
                                filtroFecha = FiltroFecha.TODAS
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Limpiar filtros")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Contenido principal
        when (reservasResource) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp), // Espacio para navbar
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando reservas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is Resource.Error -> {
                val message = (reservasResource as Resource.Error).message ?: "Error desconocido"
                Log.e("MisReservasTab", "Error al cargar reservas: $message") // ✅ Log del error detallado


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 80.dp), // Espacio para navbar
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error al cargar reservas",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { reservasViewModel.getReservasByUserId(RetrofitInstance.getUserId()) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }

            is Resource.Success -> {
                val todasLasReservas = (reservasResource as Resource.Success).data ?: emptyList()

                // Aplicar filtros
                val reservasFiltradas = todasLasReservas.filter { reserva ->
                    val cumpleEstado = filtroEstado == null || reserva.estado == filtroEstado
                    val cumpleFecha = when (filtroFecha) {
                        FiltroFecha.TODAS -> true
                        FiltroFecha.HOY -> esHoy(reserva.fecha)
                        FiltroFecha.ESTA_SEMANA -> esEstaSemanana(reserva.fecha)
                        FiltroFecha.ESTE_MES -> esEsteMes(reserva.fecha)
                        FiltroFecha.PROXIMAS -> esFutura(reserva.fecha)
                        FiltroFecha.PASADAS -> esPasada(reserva.fecha)
                    }
                    cumpleEstado && cumpleFecha
                }

                if (reservasFiltradas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 80.dp), // Espacio para navbar
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (todasLasReservas.isEmpty()) Icons.Default.CalendarToday else Icons.Default.FilterListOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (todasLasReservas.isEmpty()) "No tienes reservas" else "No hay reservas con estos filtros",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (todasLasReservas.isEmpty())
                                    "Explora servicios para hacer una reserva"
                                else
                                    "Prueba con otros filtros o fechas",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )

                            if (todasLasReservas.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                TextButton(
                                    onClick = {
                                        filtroEstado = null
                                        filtroFecha = FiltroFecha.TODAS
                                    }
                                ) {
                                    Text("Ver todas las reservas")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 96.dp // Espacio extra para navbar
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Mostrar contador de resultados
                        item {
                            if (filtroEstado != null || filtroFecha != FiltroFecha.TODAS) {
                                Text(
                                    text = "${reservasFiltradas.size} de ${todasLasReservas.size} reservas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        items(reservasFiltradas) { reserva ->
                            ReservaCard(
                                reserva = reserva,
                                onDelete = {
                                    reservasViewModel.canecelarReserva(
                                        reservaId =  reserva.id,
                                        onSucces = {
                                            Toast.makeText(context, "Reserva cancelada exitosamente", Toast.LENGTH_SHORT).show()
                                            //Recargar la pagina
                                            reservasViewModel.getReservasByUserId(RetrofitInstance.getUserId())
                                        },
                                        onError = {
                                            Toast.makeText(context, "Error inesperado al caencelar la reserva", Toast.LENGTH_SHORT).show()
                                            Log.d(TAG, "Error al cancelar la reserva: $it")
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp), // Espacio para navbar
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay datos disponibles",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Enum para filtros de fecha
enum class FiltroFecha(val displayName: String) {
    TODAS("Todas"),
    HOY("Hoy"),
    ESTA_SEMANA("Esta semana"),
    ESTE_MES("Este mes"),
    PROXIMAS("Próximas"),
    PASADAS("Pasadas")
}

// Funciones auxiliares para filtros de fecha
private fun esHoy(fecha: String): Boolean {
    return try {
        val fechaReserva = LocalDate.parse(fecha)
        fechaReserva == LocalDate.now()
    } catch (e: Exception) {
        false
    }
}

private fun esEstaSemanana(fecha: String): Boolean {
    return try {
        val fechaReserva = LocalDate.parse(fecha)
        val hoy = LocalDate.now()
        val inicioSemana = hoy.minusDays(hoy.dayOfWeek.value - 1L)
        val finSemana = inicioSemana.plusDays(6)
        fechaReserva in inicioSemana..finSemana
    } catch (e: Exception) {
        false
    }
}

private fun esEsteMes(fecha: String): Boolean {
    return try {
        val fechaReserva = LocalDate.parse(fecha)
        val hoy = LocalDate.now()
        fechaReserva.month == hoy.month && fechaReserva.year == hoy.year
    } catch (e: Exception) {
        false
    }
}

private fun esFutura(fecha: String): Boolean {
    return try {
        val fechaReserva = LocalDate.parse(fecha)
        fechaReserva.isAfter(LocalDate.now())
    } catch (e: Exception) {
        false
    }
}

private fun esPasada(fecha: String): Boolean {
    return try {
        val fechaReserva = LocalDate.parse(fecha)
        fechaReserva.isBefore(LocalDate.now())
    } catch (e: Exception) {
        false
    }
}



@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun MisReservasScreenPreview() {
    FrontendappTheme {
        MisReservasTabContent(
            reservasViewModel = FakeReservasViewModel()
        )
    }
}
