package com.example.frontendapp.ui.theme.composables.Items

import PagoDTO
import ReservaResponseDTO
import ServicioDTO
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.model.pago.EstadoPago
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ReservaCard(
    reserva: ReservaResponseDTO,
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ✅ DEBUG: Agregar logs para debuggear
    val puedeSerCanceladaResult = puedeSerCancelada(reserva)
    Log.d("ReservaCard", "Reserva ${reserva.id}: Estado=${reserva.estado}, Fecha=${reserva.fecha}, PuedeSerCancelada=$puedeSerCanceladaResult")

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Cancelar Reserva",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Estás seguro de que quieres cancelar esta reserva?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Servicio: ${reserva.servicio.nombre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Fecha: ${formatearFecha(reserva.fecha)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Hora: ${formatearHora(reserva.horaInicio)} - ${formatearHora(reserva.horaFin)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (reserva.pago != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ Esta reserva tiene un pago asociado. La cancelación puede estar sujeta a políticas de reembolso.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ThemeColors.warning
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancelar Reserva")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Mantener Reserva")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Servicio, Estado y Botón de Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reserva.servicio.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EstadoReservaChip(estado = reserva.estado)

                    // BOTÓN DE DEBUG: Mostrar siempre para testing
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                            //Logica para borrar reserva

                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Cancelar reserva",
                            tint = if (puedeSerCanceladaResult) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = if (puedeSerCanceladaResult) "✓" else "✗",
                        color = if (puedeSerCanceladaResult) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha y Hora
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Fecha",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = formatearFecha(reserva.fecha),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${formatearHora(reserva.horaInicio)} - ${formatearHora(reserva.horaFin)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ID de Reserva
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = "ID Reserva",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reserva #${reserva.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Información de Pago (si existe)
            if (reserva.pago != null) {
                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Título de Pago
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Pago",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Información de Pago",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Monto y Método
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "€${String.format("%.2f", reserva.pago.monto)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatearMetodoPago(reserva.pago.metodoPago),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    EstadoPagoChip(estado = reserva.pago.estadoPago)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha de Creación y Botón de Acción Alternativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Fecha creación",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Creada: ${formatearFechaCreacion(reserva.fechaCreacion)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // ✅ BOTÓN DE DEBUG: Mostrar siempre para testing
                TextButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (puedeSerCanceladaResult) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

// ✅ FUNCIÓN MEJORADA: Con más logging para debug
private fun puedeSerCancelada(reserva: ReservaResponseDTO): Boolean {
    Log.d("puedeSerCancelada", "=== DEBUGGING CANCELACIÓN ===")
    Log.d("puedeSerCancelada", "Reserva ID: ${reserva.id}")
    Log.d("puedeSerCancelada", "Estado actual: ${reserva.estado}")

    val estadosPermitidos = listOf(EstadoReserva.Pendiente)
    val estadoPermitido = reserva.estado in estadosPermitidos
    Log.d("puedeSerCancelada", "Estado permitido: $estadoPermitido (debe ser Confirmada o Pendiente)")

    if (!estadoPermitido) {
        Log.d("puedeSerCancelada", "❌ Estado no permitido para cancelación")
        return false
    }

    return try {
        val fechaReserva = LocalDate.parse(reserva.fecha)
        val fechaHoy = LocalDate.now()

        Log.d("puedeSerCancelada", "Fecha reserva: $fechaReserva")
        Log.d("puedeSerCancelada", "Fecha hoy: $fechaHoy")

        val esFuturaOHoy = fechaReserva.isAfter(fechaHoy) || fechaReserva.isEqual(fechaHoy)
        Log.d("puedeSerCancelada", "Es futura o hoy: $esFuturaOHoy")

        if (esFuturaOHoy) {
            Log.d("puedeSerCancelada", "✅ Reserva PUEDE ser cancelada")
        } else {
            Log.d("puedeSerCancelada", "❌ Reserva es del pasado, NO puede ser cancelada")
        }

        esFuturaOHoy
    } catch (e: Exception) {
        Log.e("puedeSerCancelada", "❌ Error parseando fecha: ${e.message}")
        Log.e("puedeSerCancelada", "Fecha recibida: '${reserva.fecha}'")
        false
    }
}


@Composable
fun EstadoReservaChip(estado: EstadoReserva) {
    val (backgroundColor, textColor, text) = when (estado) {
        EstadoReserva.Cancelada -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.error,
            "Cancelada"
        )
        EstadoReserva.Finalizada -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.tertiary,
            "Finalizada"
        )
        EstadoReserva.Pendiente -> Triple(
            ThemeColors.warning.copy(alpha = 0.15f),
            ThemeColors.warning,
            "Pendiente"
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun EstadoPagoChip(estado: EstadoPago) {
    val (backgroundColor, textColor, text) = when (estado) {
        EstadoPago.Confirmado -> Triple(
            ThemeColors.success.copy(alpha = 0.15f),
            ThemeColors.success,
            "Pagado"
        )
        EstadoPago.Fallido -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.error,
            "Fallido"
        )
        EstadoPago.Pendiente -> Triple(
            ThemeColors.warning.copy(alpha = 0.15f),
            ThemeColors.warning,
            "Pendiente"
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// Funciones de formateo
private fun formatearFecha(fecha: String): String {
    return try {
        val localDate = LocalDate.parse(fecha)
        localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    } catch (e: Exception) {
        fecha
    }
}

private fun formatearHora(hora: String): String {
    return try {
        val localTime = LocalTime.parse(hora)
        localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        hora
    }
}

private fun formatearFechaCreacion(fechaCreacion: String?): String {
    return try {
        if (fechaCreacion != null) {
            // Asumiendo formato ISO: "2024-01-15T14:30:00"
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            // Aquí necesitarías parsear según el formato que recibas del backend
            fechaCreacion.substring(0, 10) // Simplificado para el ejemplo
        } else {
            "No disponible"
        }
    } catch (e: Exception) {
        fechaCreacion ?: "No disponible"
    }
}

private fun formatearMetodoPago(metodoPago: String): String {
    return when (metodoPago.uppercase()) {
        "TARJETA" -> "Tarjeta de crédito/débito"
        "EFECTIVO" -> "Efectivo"
        "TRANSFERENCIA" -> "Transferencia bancaria"
        "PAYPAL" -> "PayPal"
        else -> metodoPago
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun ReservaCardPreview() {
    FrontendappTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reserva con pago
            ReservaCard(
                reserva = ReservaResponseDTO(
                    id = 1,
                    negocioId = 1,
                    usuarioId = "user123",
                    fecha = "2024-01-15",
                    horaInicio = "14:30:00",
                    horaFin = "15:30:00",
                    estado = EstadoReserva.Pendiente,
                    fechaCreacion = "2024-01-10T10:00:00",
                    servicioId = 1,
                    servicio = ServicioDTO(
                        id = 1,
                        nombre = "Corte de cabello"
                    ),
                    pago = PagoDTO(
                        id = 1,
                        monto = 25.50,
                        estadoPago = EstadoPago.Confirmado,
                        metodoPago = "TARJETA",
                        creado = "2024-01-10T10:00:00"
                    )
                )
            )

            // Reserva sin pago
            ReservaCard(
                reserva = ReservaResponseDTO(
                    id = 2,
                    negocioId = 1,
                    usuarioId = "user123",
                    fecha = "2024-01-20",
                    horaInicio = "16:00:00",
                    horaFin = "17:00:00",
                    estado = EstadoReserva.Pendiente,
                    fechaCreacion = "2024-01-15T09:30:00",
                    servicioId = 2,
                    servicio = ServicioDTO(
                        id = 2,
                        nombre = "Manicura y pedicura"
                    ),
                    pago = null
                )
            )
        }
    }
}
