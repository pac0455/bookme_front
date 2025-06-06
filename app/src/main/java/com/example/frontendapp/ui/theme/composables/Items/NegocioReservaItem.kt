package com.example.frontendapp.ui.theme.composables.Items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Reserva.EstadoReserva
import com.example.frontendapp.data.model.Reserva.ReservaResponseNegocioDTO
import com.example.frontendapp.data.model.pago.EstadoPago
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.CustomSelector
import com.example.frontendapp.ui.theme.composables.modals.ConfirmationModal

@Composable
fun NegocioReservaItem(
    reservaNegocio: ReservaResponseNegocioDTO,
    onCancelarReserva: () -> Unit = {},
    onCambiarEstadoPago: (EstadoPago) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Sección de Usuario
            UserSection(reservaNegocio,)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Servicio
            ServicioSection(reservaNegocio = reservaNegocio)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Pago
            PagoSection(
                reservaNegocio = reservaNegocio,
                onCambiarEstadoPago = onCambiarEstadoPago
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botones de Acción
            ActionButtons(
                estadoReserva = reservaNegocio.estadoReserva,
                onCancelarReserva = onCancelarReserva
            )
        }
    }
}

@Composable
fun UserSection(
    reservaNegocio: ReservaResponseNegocioDTO,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuario",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = reservaNegocio.username,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${reservaNegocio.nReservasUsuario} reservas totales",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ServicioSection(reservaNegocio: ReservaResponseNegocioDTO) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Servicio",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Detalles del Servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = reservaNegocio.servicioNombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reservaNegocio.servicioDescripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Fecha",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = reservaNegocio.fecha.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column {
                        Text(
                            text = "Horario",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${reservaNegocio.horaInicio} - ${reservaNegocio.horaFin}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Estado de la reserva
        AssistChip(
            onClick = { },
            label = {
                Text(
                    text = reservaNegocio.estadoReserva.name,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = when (reservaNegocio.estadoReserva) {
                    EstadoReserva.Finalizada -> ThemeColors.success.copy(alpha = 0.2f)
                    EstadoReserva.Cancelada -> ThemeColors.error.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.primaryContainer
                },
                labelColor = when (reservaNegocio.estadoReserva) {
                    EstadoReserva.Finalizada -> ThemeColors.success
                    EstadoReserva.Cancelada -> ThemeColors.error
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        )
    }
}

@Composable
fun PagoSection(
    reservaNegocio: ReservaResponseNegocioDTO,
    onCambiarEstadoPago: (EstadoPago) -> Unit
) {
    var selectedEstado by remember { mutableStateOf(reservaNegocio.estadoPago) }
    var showConfirmationModal by remember { mutableStateOf(false) }
    var nuevoEstadoPendiente by remember { mutableStateOf<EstadoPago?>(null) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Payment,
                contentDescription = "Pago",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Información de Pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Precio Total",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${reservaNegocio.precio} ${reservaNegocio.moneda}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomSelector(
                modifier = Modifier.width(180.dp),
                options = EstadoPago.entries.map { it.name },
                selectedOption = selectedEstado.name,
                onOptionSelected = { nuevoEstadoStr ->
                    val nuevoEstado = EstadoPago.valueOf(nuevoEstadoStr)
                    if (nuevoEstado != selectedEstado) {
                        nuevoEstadoPendiente = nuevoEstado
                        showConfirmationModal = true
                    }
                },
                label = "Estado Pago"
            )
        }


        // Modal de confirmación
        if (showConfirmationModal && nuevoEstadoPendiente != null) {
            ConfirmationModal(
                isVisible = showConfirmationModal,
                title = "Cambiar Estado de Pago",
                message = "¿Estás seguro de que quieres cambiar el estado de pago a \"${nuevoEstadoPendiente!!.name}\"?",
                confirmText = "Sí, Cambiar",
                cancelText = "Cancelar",
                onConfirm = {
                    selectedEstado = nuevoEstadoPendiente!!
                    onCambiarEstadoPago(nuevoEstadoPendiente!!)
                    showConfirmationModal = false
                    nuevoEstadoPendiente = null
                },
                onDismiss = {
                    showConfirmationModal = false
                    nuevoEstadoPendiente = null
                }
            )
        }
    }
}

@Composable
fun ActionButtons(
    estadoReserva: EstadoReserva,
    onCancelarReserva: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        if (estadoReserva != EstadoReserva.Cancelada && estadoReserva != EstadoReserva.Finalizada) {
            Button(
                onClick = onCancelarReserva,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColors.error,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancelar Reserva")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNegocioReservaItem() {
    val mockReserva = ReservaResponseNegocioDTO(
        username = "juan123",
        nReservasUsuario = 3,
        servicioNombre = "Masaje Relajante",
        servicioDescripcion = "Masaje de 1 hora con aceites esenciales para relajación completa.",
        fecha = "2025-06-03",
        horaInicio = "10:00",
        horaFin = "11:00",
        precio = 49.99,
        moneda = "EUR",
        estadoReserva = EstadoReserva.Finalizada,
        estadoPago = EstadoPago.Confirmado,
        id = 1
    )

    FrontendappTheme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            NegocioReservaItem(
                reservaNegocio = mockReserva,
                onCancelarReserva = { /* Acción cancelar */ },
                onCambiarEstadoPago = { /* Cambiar estado pago */ }
            )
        }
    }
}