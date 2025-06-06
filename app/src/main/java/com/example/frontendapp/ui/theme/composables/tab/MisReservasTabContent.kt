package com.example.frontendapp.ui.theme.composables.tab

import PagoDTO
import ReservaResponseDTO
import ServicioDTO
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.model.pago.EstadoPago
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.*
import com.example.frontendapp.ui.theme.composables.Items.ReservaCard
import com.example.frontendapp.ui.theme.composables.modals.FiltrosReserva
import com.example.frontendapp.ui.theme.composables.modals.FiltrosReservaModal
import com.example.frontendapp.ui.theme.composables.modals.OrdenarReservaPor
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import kotlinx.coroutines.launch

@Composable
fun ReservaTabContent(
    reservaViewModel: ReservasViewModel,
    navController: NavController
) {
    val listState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Estados para filtros y búsqueda
    var mostrarFiltros by remember { mutableStateOf(false) }
    var filtrosActivos by remember { mutableStateOf(FiltrosReserva()) }
    var busqueda by remember { mutableStateOf("") }

    val reservasState by reservaViewModel.reservasByUserState.collectAsState()

    val reservas = reservasState.let {
        if (it is Resource.Success) it.data ?: emptyList()
        else emptyList()
    }



    // Servicios disponibles
    val serviciosDisponibles = remember(reservas) {
        reservas.map { it.servicio.nombre }.distinct()
    }

    // Rango de precios
    val rangoPrecio = remember(reservas) {
        if (reservas.isNotEmpty()) {
            val precios = reservas.mapNotNull { it.pago?.monto }
            if (precios.isNotEmpty()) {
                precios.minOrNull()!! to precios.maxOrNull()!!
            } else {
                0.0 to 100.0
            }
        } else {
            0.0 to 100.0
        }
    }

    // Carga inicial de reservas
    LaunchedEffect(Unit) {
        reservaViewModel.getReservasByUserId(userId = RetrofitInstance.getUserId())
    }

    // Mostrar estado de carga
    when (reservasState) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }
        is Resource.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar reservas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            reservaViewModel.getReservasByUserId(userId = RetrofitInstance.getUserId())
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
            return
        }
        else -> {}
    }

    // Función para aplicar filtros
    val reservasFiltradas = remember(reservas, filtrosActivos, busqueda) {
        var resultado = reservas

        // Filtro por búsqueda
        if (busqueda.isNotBlank()) {
            resultado = resultado.filter { reserva ->
                reserva.servicio.nombre.contains(busqueda, ignoreCase = true)
            }
        }

        // Filtro por estados de reserva
        if (filtrosActivos.estados.isNotEmpty()) {
            resultado = resultado.filter { reserva ->
                reserva.estado in filtrosActivos.estados
            }
        }

        // Filtro por estados de pago
        if (filtrosActivos.estadosPago.isNotEmpty()) {
            resultado = resultado.filter { reserva ->
                reserva.pago?.estadoPago in filtrosActivos.estadosPago
            }
        }

        // Filtro por servicios
        if (filtrosActivos.servicios.isNotEmpty()) {
            resultado = resultado.filter { reserva ->
                reserva.servicio.nombre in filtrosActivos.servicios
            }
        }

        // Filtro por rango de fechas
        filtrosActivos.fechaDesde?.let { fechaDesde ->
            resultado = resultado.filter { reserva ->
                reserva.fecha >= fechaDesde
            }
        }
        filtrosActivos.fechaHasta?.let { fechaHasta ->
            resultado = resultado.filter { reserva ->
                reserva.fecha <= fechaHasta
            }
        }

        // Filtro por precio
        filtrosActivos.precioMinimo?.let { minPrecio ->
            resultado = resultado.filter { reserva ->
                reserva.pago?.monto?.let { it >= minPrecio } ?: false
            }
        }
        filtrosActivos.precioMaximo?.let { maxPrecio ->
            resultado = resultado.filter { reserva ->
                reserva.pago?.monto?.let { it <= maxPrecio } ?: false
            }
        }

        // Filtro por solo con pago
        if (filtrosActivos.soloConPago) {
            resultado = resultado.filter { it.pago != null }
        }

        // Ordenamiento
        when (filtrosActivos.ordenarPor) {
            OrdenarReservaPor.FECHA_DESC -> resultado.sortedByDescending { it.fecha + it.horaInicio }
            OrdenarReservaPor.FECHA_ASC -> resultado.sortedBy { it.fecha + it.horaInicio }
            OrdenarReservaPor.PRECIO_DESC -> resultado.sortedByDescending { it.pago?.monto ?: 0.0 }
            OrdenarReservaPor.PRECIO_ASC -> resultado.sortedBy { it.pago?.monto ?: 0.0 }
            OrdenarReservaPor.SERVICIO -> resultado.sortedBy { it.servicio.nombre }
            OrdenarReservaPor.ESTADO -> resultado.sortedBy { it.estado.name }
        }
    }

    // Verificar si hay filtros activos
    val hayFiltrosActivos = filtrosActivos != FiltrosReserva() || busqueda.isNotBlank()

    // Solo mostrar contenido si hay datos
    if (reservas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No tienes reservas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cuando hagas una reserva, aparecerá aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HeaderSeccion(
                    titulo = "Mis Reservas",
                    searchQuery = busqueda,
                    hasActiveFilters = hayFiltrosActivos,
                    onSearchChange = { busqueda = it },
                    onFilterClick = { mostrarFiltros = true }
                )
            }

            // Mostrar resumen de filtros activos
            if (hayFiltrosActivos) {
                item {
                    FiltrosActivosResumenReserva(
                        filtros = filtrosActivos,
                        busqueda = busqueda,
                        totalResultados = reservasFiltradas.size,
                        onLimpiarFiltros = {
                            filtrosActivos = FiltrosReserva()
                            busqueda = ""
                        }
                    )
                }
            }

            if (hayFiltrosActivos) {
                // Mostrar todas las reservas filtradas en una sola sección
                item {
                    ReservaSeccion(
                        titulo = "Resultados",
                        reservas = reservasFiltradas,
                        reservaViewModel = reservaViewModel,
                        listState = rememberLazyListState(),
                        flingBehavior = flingBehavior,
                        cardWidth = cardWidth,
                        screenWidth = screenWidth,
                        navController = navController,
                        icono = Icons.Default.Search,
                        colorTema = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // Sección de pendientes
                val pendientes = reservasFiltradas.filter { it.estado == EstadoReserva.Pendiente }
                if (pendientes.isNotEmpty()) {
                    item {
                        ReservaSeccion(
                            titulo = "Pendientes",
                            reservas = pendientes,
                            reservaViewModel = reservaViewModel,
                            listState = rememberLazyListState(),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                            cardWidth = cardWidth,
                            screenWidth = screenWidth,
                            navController = navController,
                            icono = Icons.Default.HourglassEmpty,
                            colorTema = ThemeColors.warning
                        )
                    }
                }

                // Sección de finalizadas
                val finalizadas = reservasFiltradas.filter { it.estado == EstadoReserva.Finalizada }
                if (finalizadas.isNotEmpty()) {
                    item {
                        ReservaSeccion(
                            titulo = "Finalizadas",
                            reservas = finalizadas,
                            reservaViewModel = reservaViewModel,
                            listState = rememberLazyListState(),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                            cardWidth = cardWidth,
                            screenWidth = screenWidth,
                            navController = navController,
                            icono = Icons.Default.CheckCircle,
                            colorTema = ThemeColors.success
                        )
                    }
                }

                // Sección de canceladas
                val canceladas = reservasFiltradas.filter { it.estado == EstadoReserva.Cancelada }
                if (canceladas.isNotEmpty()) {
                    item {
                        ReservaSeccion(
                            titulo = "Canceladas",
                            reservas = canceladas,
                            reservaViewModel = reservaViewModel,
                            listState = rememberLazyListState(),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                            cardWidth = cardWidth,
                            screenWidth = screenWidth,
                            navController = navController,
                            icono = Icons.Default.Cancel,
                            colorTema = ThemeColors.error
                        )
                    }
                }
            }
        }

        // Modal de filtros
        FiltrosReservaModal(
            isVisible = mostrarFiltros,
            filtrosActuales = filtrosActivos,
            serviciosDisponibles = serviciosDisponibles,
            rangoPrecio = rangoPrecio,
            onDismiss = { mostrarFiltros = false },
            onAplicarFiltros = { nuevosFiltros ->
                filtrosActivos = nuevosFiltros
            },
            onLimpiarFiltros = {
                filtrosActivos = FiltrosReserva()
                busqueda = ""
            }
        )
    }
}

// Componente para mostrar resumen de filtros activos
@Composable
private fun FiltrosActivosResumenReserva(
    filtros: FiltrosReserva,
    busqueda: String,
    totalResultados: Int,
    onLimpiarFiltros: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
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
                    text = "$totalResultados reservas encontradas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = onLimpiarFiltros,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar")
                }
            }

            // Mostrar filtros activos
            if (busqueda.isNotBlank() || filtros != FiltrosReserva()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (busqueda.isNotBlank()) {
                        item {
                            FiltroChipReserva(
                                texto = "\"$busqueda\"",
                                icono = Icons.Default.Search
                            )
                        }
                    }

                    if (filtros.soloConPago) {
                        item {
                            FiltroChipReserva(
                                texto = "Con pago",
                                icono = Icons.Default.Payment
                            )
                        }
                    }

                    if (filtros.fechaDesde != null || filtros.fechaHasta != null) {
                        item {
                            val texto = when {
                                filtros.fechaDesde != null && filtros.fechaHasta != null ->
                                    "${filtros.fechaDesde} - ${filtros.fechaHasta}"
                                filtros.fechaDesde != null ->
                                    "Desde ${filtros.fechaDesde}"
                                else ->
                                    "Hasta ${filtros.fechaHasta}"
                            }
                            FiltroChipReserva(
                                texto = texto,
                                icono = Icons.Default.DateRange
                            )
                        }
                    }

                    if (filtros.precioMinimo != null || filtros.precioMaximo != null) {
                        item {
                            val texto = when {
                                filtros.precioMinimo != null && filtros.precioMaximo != null ->
                                    "${filtros.precioMinimo}€ - ${filtros.precioMaximo}€"
                                filtros.precioMinimo != null ->
                                    "Desde ${filtros.precioMinimo}€"
                                else ->
                                    "Hasta ${filtros.precioMaximo}€"
                            }
                            FiltroChipReserva(
                                texto = texto,
                                icono = Icons.Default.Euro
                            )
                        }
                    }

                    if (filtros.estados.isNotEmpty()) {
                        items(filtros.estados.toList()) { estado ->
                            FiltroChipReserva(
                                texto = when (estado) {
                                    EstadoReserva.Pendiente -> "Pendiente"
                                    EstadoReserva.Finalizada -> "Finalizada"
                                    EstadoReserva.Cancelada -> "Cancelada"
                                },
                                icono = Icons.Default.Schedule
                            )
                        }
                    }

                    if (filtros.estadosPago.isNotEmpty()) {
                        items(filtros.estadosPago.toList()) { estado ->
                            FiltroChipReserva(
                                texto = when (estado) {
                                    EstadoPago.Pendiente -> "Pago pendiente"
                                    EstadoPago.Confirmado -> "Pago confirmado"
                                    EstadoPago.Fallido -> "Pago fallido"
                                },
                                icono = Icons.Default.Payment
                            )
                        }
                    }

                    if (filtros.servicios.isNotEmpty()) {
                        items(filtros.servicios.toList()) { servicio ->
                            FiltroChipReserva(
                                texto = servicio,
                                icono = Icons.Default.Build
                            )
                        }
                    }

                    if (filtros.ordenarPor != OrdenarReservaPor.FECHA_DESC) {
                        item {
                            FiltroChipReserva(
                                texto = "Orden: ${filtros.ordenarPor.displayName}",
                                icono = filtros.ordenarPor.icon
                            )
                        }
                    }
                }
            }
        }
    }
}

// Chip para mostrar filtros activos
@Composable
private fun FiltroChipReserva(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Sección mejorada con colores del tema
@Composable
fun ReservaSeccion(
    titulo: String,
    reservas: List<ReservaResponseDTO>,
    reservaViewModel: ReservasViewModel,
    listState: LazyListState,
    flingBehavior: FlingBehavior,
    navController: NavController,
    cardWidth: Dp,
    screenWidth: Dp,
    icono: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.EventNote,
    colorTema: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Header de sección mejorado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorTema.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorTema,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = colorTema.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${reservas.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorTema,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = (screenWidth - cardWidth) / 2)
        ) {
            items(reservas, key = { it.id }) { reserva ->
                Box(modifier = Modifier.width(cardWidth)) {
                    ReservaCard(
                        reserva = reserva,
                        onDelete = {
                            Log.d("ReservaCardList", "Cancelar reserva: ${reserva.id}")
                            reservaViewModel.canecelarReserva(
                                reserva.id,
                                onLoading = {},
                                onError = {
                                    Toast.makeText(context, "Error al cancelar la reserva", Toast.LENGTH_SHORT).show()
                                },
                                onSucces = {
                                    Toast.makeText(context, "Reserva cancelada", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ReservaTabContentPreview() {
    val navController = rememberNavController()
    FrontendappTheme {
        ReservaTabContent(
            reservaViewModel = FakeReservasViewModel(),
            navController = navController
        )
    }
}
