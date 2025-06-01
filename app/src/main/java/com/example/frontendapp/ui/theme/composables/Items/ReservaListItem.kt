package com.example.frontendapp.ui.theme.composables.Items

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Reserva.ReservaDetallada
import com.example.frontendapp.data.model.Reserva.ServicioConPago

@Composable
fun ReservaListItem(
    reserva: ReservaDetallada,
    onDeleteClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    val estadoColor = when (reserva.estadoPagoGeneral.lowercase()) {
        "completado" -> Color(0xFF4CAF50)
        "pendiente" -> Color(0xFFFFC107)
        "cancelado", "fallido", "reembolsado" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Reserva",
                tint = estadoColor,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Reserva del ${reserva.fecha}", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Estado: ${reserva.estadoPagoGeneral.orEmpty().replaceFirstChar { it.uppercaseChar() }}",
                    color = estadoColor
                )
            }

            IconButton(onClick = { expanded = !expanded }) {
                AnimatedContent(targetState = expanded, label = "expansion") {
                    Icon(
                        imageVector = if (it) Icons.Default.Close else Icons.Default.ArrowDownward,
                        contentDescription = "Expandir"
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()

                reserva.servicios.forEach { servicio ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(servicio.nombre ?: "Servicio")
                        Text("€%.2f".format(servicio.precio ?: 0.0))
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("€%.2f".format(reserva.totalReserva), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar", color = Color.Red)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewReservaListItem() {
    val reserva = ReservaDetallada(
        reservaId = 1,
        fecha = "2025-05-22",
        estado = "Confirmada",
        comentarioCliente = "Muy buena atención.",
        estadoPagoGeneral = "completado",
        servicios = listOf(
            ServicioConPago(nombre = "Masaje", precio = 25.0, pago = null),
            ServicioConPago(nombre = "Facial", precio = 30.0, pago = null)
        ),
        totalReserva = 55.0
    )


    ReservaListItem(reserva = reserva)
}



