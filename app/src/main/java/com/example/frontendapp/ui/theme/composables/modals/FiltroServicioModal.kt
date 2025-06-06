package com.example.frontendapp.ui.theme.composables.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.model.pago.EstadoPago
import com.example.frontendapp.ui.theme.*
import kotlin.math.roundToInt

// Data classes para filtros de reservas
data class FiltrosReserva(
    val estados: Set<EstadoReserva> = emptySet(),
    val estadosPago: Set<EstadoPago> = emptySet(),
    val servicios: Set<String> = emptySet(),
    val fechaDesde: String? = null,
    val fechaHasta: String? = null,
    val precioMinimo: Double? = null,
    val precioMaximo: Double? = null,
    val soloConPago: Boolean = false,
    val ordenarPor: OrdenarReservaPor = OrdenarReservaPor.FECHA_DESC
)

enum class OrdenarReservaPor(val displayName: String, val icon: ImageVector) {
    FECHA_DESC("Fecha: más reciente", Icons.Default.DateRange),
    FECHA_ASC("Fecha: más antigua", Icons.Default.DateRange),
    PRECIO_DESC("Precio: mayor a menor", Icons.Default.ArrowDownward),
    PRECIO_ASC("Precio: menor a mayor", Icons.Default.ArrowUpward),
    SERVICIO("Por servicio", Icons.Default.Build),
    ESTADO("Por estado", Icons.Default.Schedule)
}

enum class RangoFechaOption(val displayName: String, val value: String?) {
    TODAS("Todas las fechas", null),
    HOY("Hoy", "hoy"),
    ESTA_SEMANA("Esta semana", "semana"),
    ESTE_MES("Este mes", "mes"),
    ULTIMOS_30_DIAS("Últimos 30 días", "30dias"),
    PERSONALIZADO("Personalizado", "custom")
}

@Composable
fun FiltrosReservaModal(
    isVisible: Boolean,
    filtrosActuales: FiltrosReserva,
    serviciosDisponibles: List<String>,
    estadosDisponibles: List<EstadoReserva> = EstadoReserva.values().toList(),
    rangoPrecio: Pair<Double, Double>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosReserva) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            FiltrosReservaContent(
                filtrosActuales = filtrosActuales,
                serviciosDisponibles = serviciosDisponibles,
                estadosDisponibles = estadosDisponibles,
                rangoPrecio = rangoPrecio,
                onDismiss = onDismiss,
                onAplicarFiltros = onAplicarFiltros,
                onLimpiarFiltros = onLimpiarFiltros
            )
        }
    }
}

@Composable
private fun FiltrosReservaContent(
    filtrosActuales: FiltrosReserva,
    serviciosDisponibles: List<String>,
    estadosDisponibles: List<EstadoReserva>,
    rangoPrecio: Pair<Double, Double>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosReserva) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    var filtros by remember { mutableStateOf(filtrosActuales) }
    val scrollState = rememberScrollState()

    // Estado para el rango de precios
    var rangoPrecioState by remember {
        mutableStateOf(
            (filtrosActuales.precioMinimo ?: rangoPrecio.first) to
                    (filtrosActuales.precioMaximo ?: rangoPrecio.second)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header del modal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros de Reservas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Estados de reserva
                FiltroSeccionReserva(
                    titulo = "Estados de Reserva",
                    icono = Icons.Default.Schedule
                ) {
                    EstadosReservaFilter(
                        estadosDisponibles = estadosDisponibles,
                        estadosSeleccionados = filtros.estados,
                        onEstadoToggle = { estado ->
                            filtros = if (estado in filtros.estados) {
                                filtros.copy(estados = filtros.estados - estado)
                            } else {
                                filtros.copy(estados = filtros.estados + estado)
                            }
                        }
                    )
                }

                // Estados de pago
                FiltroSeccionReserva(
                    titulo = "Estados de Pago",
                    icono = Icons.Default.Payment
                ) {
                    EstadosPagoFilter(
                        estadosSeleccionados = filtros.estadosPago,
                        onEstadoToggle = { estado ->
                            filtros = if (estado in filtros.estadosPago) {
                                filtros.copy(estadosPago = filtros.estadosPago - estado)
                            } else {
                                filtros.copy(estadosPago = filtros.estadosPago + estado)
                            }
                        }
                    )
                }

                // Servicios
                if (serviciosDisponibles.isNotEmpty()) {
                    FiltroSeccionReserva(
                        titulo = "Servicios",
                        icono = Icons.Default.Build
                    ) {
                        ServiciosReservaFilter(
                            serviciosDisponibles = serviciosDisponibles,
                            serviciosSeleccionados = filtros.servicios,
                            onServicioToggle = { servicio ->
                                filtros = if (servicio in filtros.servicios) {
                                    filtros.copy(servicios = filtros.servicios - servicio)
                                } else {
                                    filtros.copy(servicios = filtros.servicios + servicio)
                                }
                            }
                        )
                    }
                }

                // Rango de fechas
                FiltroSeccionReserva(
                    titulo = "Rango de fechas",
                    icono = Icons.Default.DateRange
                ) {
                    RangoFechaReservaFilter(
                        fechaDesde = filtros.fechaDesde,
                        fechaHasta = filtros.fechaHasta,
                        onFechaDesdeChange = { fecha ->
                            filtros = filtros.copy(fechaDesde = fecha)
                        },
                        onFechaHastaChange = { fecha ->
                            filtros = filtros.copy(fechaHasta = fecha)
                        }
                    )
                }

                // Rango de precio
                if (rangoPrecio.first < rangoPrecio.second) {
                    FiltroSeccionReserva(
                        titulo = "Rango de precio",
                        icono = Icons.Default.Euro
                    ) {
                        PrecioRangeReservaFilter(
                            rangoPrecio = rangoPrecioState,
                            rangoTotal = rangoPrecio,
                            onRangoChange = { min, max ->
                                rangoPrecioState = min to max
                                filtros = filtros.copy(
                                    precioMinimo = if (min <= rangoPrecio.first) null else min,
                                    precioMaximo = if (max >= rangoPrecio.second) null else max
                                )
                            }
                        )
                    }
                }

                // Solo con pago
                FiltroSeccionReserva(
                    titulo = "Pago",
                    icono = Icons.Default.CreditCard
                ) {
                    SoloConPagoFilter(
                        soloConPago = filtros.soloConPago,
                        onSoloConPagoChange = { soloConPago ->
                            filtros = filtros.copy(soloConPago = soloConPago)
                        }
                    )
                }

                // Ordenar por
                FiltroSeccionReserva(
                    titulo = "Ordenar por",
                    icono = Icons.Default.Sort
                ) {
                    OrdenarPorReservaFilter(
                        ordenSeleccionado = filtros.ordenarPor,
                        onOrdenChange = { orden ->
                            filtros = filtros.copy(ordenarPor = orden)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onLimpiarFiltros()
                        filtros = FiltrosReserva()
                        rangoPrecioState = rangoPrecio.first to rangoPrecio.second
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Limpiar")
                }

                Button(
                    onClick = {
                        onAplicarFiltros(filtros)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aplicar")
                }
            }
        }
    }
}

// Componente para secciones de filtro
@Composable
private fun FiltroSeccionReserva(
    titulo: String,
    icono: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

// Filtro de estados de reserva
@Composable
private fun EstadosReservaFilter(
    estadosDisponibles: List<EstadoReserva>,
    estadosSeleccionados: Set<EstadoReserva>,
    onEstadoToggle: (EstadoReserva) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(estadosDisponibles) { estado ->
            val isSelected = estado in estadosSeleccionados

            FilterChip(
                onClick = { onEstadoToggle(estado) },
                label = { Text(getEstadoReservaDisplayName(estado)) },
                selected = isSelected,
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getEstadoReservaColor(estado),
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// Filtro de estados de pago
@Composable
private fun EstadosPagoFilter(
    estadosSeleccionados: Set<EstadoPago>,
    onEstadoToggle: (EstadoPago) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(EstadoPago.values().toList()) { estado ->
            val isSelected = estado in estadosSeleccionados

            FilterChip(
                onClick = { onEstadoToggle(estado) },
                label = { Text(getEstadoPagoDisplayName(estado)) },
                selected = isSelected,
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getEstadoPagoColor(estado),
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// Filtro de servicios para reservas
@Composable
private fun ServiciosReservaFilter(
    serviciosDisponibles: List<String>,
    serviciosSeleccionados: Set<String>,
    onServicioToggle: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(serviciosDisponibles) { servicio ->
            val isSelected = servicio in serviciosSeleccionados

            FilterChip(
                onClick = { onServicioToggle(servicio) },
                label = { Text(servicio) },
                selected = isSelected,
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// Filtro de rango de fechas
@Composable
private fun RangoFechaReservaFilter(
    fechaDesde: String?,
    fechaHasta: String?,
    onFechaDesdeChange: (String?) -> Unit,
    onFechaHastaChange: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Opciones rápidas
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(RangoFechaOption.values()) { opcion ->
                FilterChip(
                    onClick = {
                        when (opcion.value) {
                            "hoy" -> {
                                val hoy = java.time.LocalDate.now().toString()
                                onFechaDesdeChange(hoy)
                                onFechaHastaChange(hoy)
                            }
                            "semana" -> {
                                val hoy = java.time.LocalDate.now()
                                val inicioSemana = hoy.minusDays(hoy.dayOfWeek.value.toLong() - 1)
                                val finSemana = inicioSemana.plusDays(6)
                                onFechaDesdeChange(inicioSemana.toString())
                                onFechaHastaChange(finSemana.toString())
                            }
                            "mes" -> {
                                val hoy = java.time.LocalDate.now()
                                val inicioMes = hoy.withDayOfMonth(1)
                                val finMes = hoy.withDayOfMonth(hoy.lengthOfMonth())
                                onFechaDesdeChange(inicioMes.toString())
                                onFechaHastaChange(finMes.toString())
                            }
                            "30dias" -> {
                                val hoy = java.time.LocalDate.now()
                                val hace30Dias = hoy.minusDays(30)
                                onFechaDesdeChange(hace30Dias.toString())
                                onFechaHastaChange(hoy.toString())
                            }
                            null -> {
                                onFechaDesdeChange(null)
                                onFechaHastaChange(null)
                            }
                        }
                    },
                    label = { Text(opcion.displayName) },
                    selected = false
                )
            }
        }

        // Campos de fecha personalizados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = fechaDesde ?: "",
                onValueChange = onFechaDesdeChange,
                label = { Text("Desde") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            OutlinedTextField(
                value = fechaHasta ?: "",
                onValueChange = onFechaHastaChange,
                label = { Text("Hasta") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
    }
}

// Filtro de rango de precio para reservas
@Composable
private fun PrecioRangeReservaFilter(
    rangoPrecio: Pair<Double, Double>,
    rangoTotal: Pair<Double, Double>,
    onRangoChange: (Double, Double) -> Unit
) {
    val (minPrecio, maxPrecio) = rangoPrecio
    val (minTotal, maxTotal) = rangoTotal

    Column {
        // Mostrar valores seleccionados
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Desde: ${String.format("%.2f", minPrecio)}€",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Hasta: ${String.format("%.2f", maxPrecio)}€",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // RangeSlider
        RangeSlider(
            value = minPrecio.toFloat()..maxPrecio.toFloat(),
            onValueChange = { range ->
                onRangoChange(range.start.toDouble(), range.endInclusive.toDouble())
            },
            valueRange = minTotal.toFloat()..maxTotal.toFloat(),
            steps = ((maxTotal - minTotal) / 5).roundToInt().coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Mostrar rango total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${String.format("%.2f", minTotal)}€",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${String.format("%.2f", maxTotal)}€",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Filtro de solo con pago
@Composable
private fun SoloConPagoFilter(
    soloConPago: Boolean,
    onSoloConPagoChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSoloConPagoChange(!soloConPago) }
            .background(
                if (soloConPago)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = soloConPago,
            onCheckedChange = onSoloConPagoChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Solo reservas con pago",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (soloConPago)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Mostrar únicamente reservas que tienen pago asociado",
                style = MaterialTheme.typography.bodySmall,
                color = if (soloConPago)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Filtro de ordenamiento
@Composable
private fun OrdenarPorReservaFilter(
    ordenSeleccionado: OrdenarReservaPor,
    onOrdenChange: (OrdenarReservaPor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OrdenarReservaPor.values().forEach { opcion ->
            val isSelected = ordenSeleccionado == opcion

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOrdenChange(opcion) }
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            Color.Transparent
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onOrdenChange(opcion) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = opcion.icon,
                    contentDescription = null,
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = opcion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Funciones helper para obtener colores y nombres
@Composable
private fun getEstadoReservaColor(estado: EstadoReserva): Color {
    return when (estado) {
        EstadoReserva.Pendiente -> ThemeColors.warning
        EstadoReserva.Finalizada -> ThemeColors.success
        EstadoReserva.Cancelada -> ThemeColors.error
    }
}

@Composable
private fun getEstadoPagoColor(estado: EstadoPago): Color {
    return when (estado) {
        EstadoPago.Pendiente -> ThemeColors.warning
        EstadoPago.Confirmado -> ThemeColors.success
        EstadoPago.Fallido -> ThemeColors.error
    }
}

private fun getEstadoReservaDisplayName(estado: EstadoReserva): String {
    return when (estado) {
        EstadoReserva.Pendiente -> "Pendiente"
        EstadoReserva.Finalizada -> "Finalizada"
        EstadoReserva.Cancelada -> "Cancelada"
    }
}

private fun getEstadoPagoDisplayName(estado: EstadoPago): String {
    return when (estado) {
        EstadoPago.Pendiente -> "Pendiente"
        EstadoPago.Confirmado -> "Confirmado"
        EstadoPago.Fallido -> "Fallido"
    }
}

@Preview(showBackground = true)
@Composable
fun FiltrosReservaModalPreview() {
    FrontendappTheme {
        FiltrosReservaModal(
            isVisible = true,
            filtrosActuales = FiltrosReserva(
                estados = setOf(EstadoReserva.Pendiente),
                estadosPago = setOf(EstadoPago.Confirmado),
                servicios = setOf("Corte de cabello"),
                soloConPago = true
            ),
            serviciosDisponibles = listOf("Corte de cabello", "Manicura", "Masaje"),
            rangoPrecio = 10.0 to 100.0,
            onDismiss = {},
            onAplicarFiltros = {},
            onLimpiarFiltros = {}
        )
    }
}
