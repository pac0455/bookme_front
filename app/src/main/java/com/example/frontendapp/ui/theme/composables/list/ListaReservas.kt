package com.example.frontendapp.ui.theme.composables.list

import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.model.Reserva.ReservaResponseNegocioDTO
import com.example.frontendapp.data.model.pago.EstadoPago
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.Items.NegocioReservaItem
import java.time.LocalDate

@Composable
fun ListaReservas(
    viewModel: ReservasViewModel,
    negocioId: Int,
    modifier: Modifier = Modifier
) {
    // Estados para filtros
    var searchText by remember { mutableStateOf("") }
    var selectedEstadoReserva by remember { mutableStateOf<EstadoReserva?>(null) }
    var selectedEstadoPago by remember { mutableStateOf<EstadoPago?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var selectedDateFilter by remember { mutableStateOf(DateFilter.TODAS) }

    // Estado de las reservas desde el ViewModel
    val reservasState by viewModel.reservasByNegocioState.collectAsState()

    // Cargar reservas al inicializar el componente
    LaunchedEffect(negocioId) {
        viewModel.getReservaByNegocioId(negocioId)
    }

    // Filtrar reservas cuando hay datos disponibles
    val filteredReservas = remember(reservasState.data, searchText, selectedEstadoReserva, selectedEstadoPago, selectedDateFilter) {
        reservasState.data?.filter { reserva ->
            val matchesSearch = searchText.isEmpty() ||
                    reserva.username.contains(searchText, ignoreCase = true) ||
                    reserva.servicioNombre.contains(searchText, ignoreCase = true)

            val matchesEstadoReserva = selectedEstadoReserva == null ||
                    reserva.estadoReserva == selectedEstadoReserva

            val matchesEstadoPago = selectedEstadoPago == null ||
                    reserva.estadoPago == selectedEstadoPago

            val matchesDate = try {
                val reservaFecha = LocalDate.parse(reserva.fecha)

                when (selectedDateFilter) {
                    DateFilter.TODAS -> true
                    DateFilter.HOY -> reservaFecha == LocalDate.now()
                    DateFilter.ESTA_SEMANA -> {
                        val now = LocalDate.now()
                        val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
                        val endOfWeek = startOfWeek.plusDays(6)
                        reservaFecha in startOfWeek..endOfWeek
                    }
                    DateFilter.ESTE_MES -> {
                        val now = LocalDate.now()
                        reservaFecha.month == now.month && reservaFecha.year == now.year
                    }
                }
            } catch (e: Exception) {
                false
            }
            matchesSearch && matchesEstadoReserva && matchesEstadoPago && matchesDate
        } ?: emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (reservasState) {
            is Resource.Loading -> {
                LoadingState()
            }
            is Resource.Error -> {
                ErrorState(
                    message = reservasState.message ?: "Error desconocido",
                    onRetry = { viewModel.getReservaByNegocioId(negocioId) }
                )
            }
            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        // Barra de búsqueda y contador
                        SearchAndFilterBar(
                            searchText = searchText,
                            onSearchTextChange = { searchText = it },
                            showFilters = showFilters,
                            onToggleFilters = { showFilters = !showFilters },
                            filteredCount = filteredReservas.size,
                            totalCount = reservasState.data?.size ?: 0
                        )
                    }

                    if (showFilters) {
                        item {
                            FilterPanel(
                                selectedEstadoReserva = selectedEstadoReserva,
                                onEstadoReservaChange = { selectedEstadoReserva = it },
                                selectedEstadoPago = selectedEstadoPago,
                                onEstadoPagoChange = { selectedEstadoPago = it },
                                selectedDateFilter = selectedDateFilter,
                                onDateFilterChange = { selectedDateFilter = it },
                                onClearFilters = {
                                    selectedEstadoReserva = null
                                    selectedEstadoPago = null
                                    selectedDateFilter = DateFilter.TODAS
                                    searchText = ""
                                }
                            )
                        }
                    }

                    if (filteredReservas.isEmpty()) {
                        item {
                            EmptyState(
                                hasFilters = searchText.isNotEmpty() || selectedEstadoReserva != null ||
                                        selectedEstadoPago != null || selectedDateFilter != DateFilter.TODAS,
                                hasData = reservasState.data?.isNotEmpty() == true
                            )
                        }
                    } else {
                        items(filteredReservas) { reserva ->
                            NegocioReservaItem(
                                reservaNegocio = convertToReservaResponseNegocio(reserva),
                                onVerUsuario = { /* vacío */ },
                                onCancelarReserva = { viewModel.canecelarReserva(
                                    reserva.id,
                                    onSucces = {
                                        viewModel.getReservaByNegocioId(negocioId)
                                               },

                                ) },
                                onCambiarEstadoPago = { _ -> /* cambiar estado */ }
                            )
                        }
                    }
                }
            }
            is Resource.None -> {
                // Estado inicial, mostrar mensaje de carga o vacío
                InitialState(onLoadData = { viewModel.getReservaByNegocioId(negocioId) })
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando reservas...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ThemeColors.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Error al cargar reservas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

@Composable
fun InitialState(onLoadData: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reservas del Negocio",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Toca el botón para cargar las reservas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onLoadData) {
            Text("Cargar Reservas")
        }
    }
}

@Composable
fun SearchAndFilterBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    showFilters: Boolean,
    onToggleFilters: () -> Unit,
    filteredCount: Int,
    totalCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por usuario o servicio...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { onSearchTextChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fila con botón de filtros y contador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    onClick = onToggleFilters,
                    label = { Text("Filtros") },
                    selected = showFilters,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                Text(
                    text = "$filteredCount de $totalCount reservas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterPanel(
    selectedEstadoReserva: EstadoReserva?,
    onEstadoReservaChange: (EstadoReserva?) -> Unit,
    selectedEstadoPago: EstadoPago?,
    onEstadoPagoChange: (EstadoPago?) -> Unit,
    selectedDateFilter: DateFilter,
    onDateFilterChange: (DateFilter) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(onClick = onClearFilters) {
                    Text("Limpiar todo")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtro por fecha
            Text(
                text = "Período",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateFilter.entries.forEach { dateFilter ->
                    FilterChip(
                        onClick = { onDateFilterChange(dateFilter) },
                        label = { Text(dateFilter.displayName) },
                        selected = selectedDateFilter == dateFilter,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtro por estado de reserva
            Text(
                text = "Estado de Reserva",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onEstadoReservaChange(null) },
                    label = { Text("Todas") },
                    selected = selectedEstadoReserva == null
                )
                EstadoReserva.entries.forEach { estado ->
                    FilterChip(
                        onClick = { onEstadoReservaChange(estado) },
                        label = { Text(estado.name) },
                        selected = selectedEstadoReserva == estado
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtro por estado de pago
            Text(
                text = "Estado de Pago",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onEstadoPagoChange(null) },
                    label = { Text("Todos") },
                    selected = selectedEstadoPago == null
                )
                EstadoPago.entries.forEach { estado ->
                    FilterChip(
                        onClick = { onEstadoPagoChange(estado) },
                        label = { Text(estado.name) },
                        selected = selectedEstadoPago == estado
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(hasFilters: Boolean, hasData: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                hasFilters && hasData -> "No se encontraron reservas"
                hasFilters && !hasData -> "No hay reservas que coincidan"
                else -> "No hay reservas disponibles"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Text(
            text = when {
                hasFilters -> "Intenta ajustar los filtros de búsqueda"
                else -> "Las reservas aparecerán aquí cuando se creen"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

enum class DateFilter(val displayName: String) {
    TODAS("Todas"),
    HOY("Hoy"),
    ESTA_SEMANA("Semana"),
    ESTE_MES("Mes")
}

// Función para convertir DTO a modelo del componente
fun convertToReservaResponseNegocio(dto: ReservaResponseNegocioDTO): ReservaResponseNegocioDTO {
    return ReservaResponseNegocioDTO(
        username = dto.username,
        nReservasUsuario = dto.nReservasUsuario,
        servicioNombre = dto.servicioNombre,
        servicioDescripcion = dto.servicioDescripcion,
        fecha = dto.fecha,
        horaInicio = dto.horaInicio,
        horaFin = dto.horaFin,
        precio = dto.precio,
        moneda = dto.moneda,
        estadoReserva = dto.estadoReserva,
        estadoPago = dto.estadoPago,
        id = 1
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewListaReservasActualizada() {
    FrontendappTheme {
        // Preview con estado de carga
        LoadingState()
    }
}