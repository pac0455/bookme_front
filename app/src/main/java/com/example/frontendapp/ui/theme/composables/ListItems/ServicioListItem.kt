package com.example.frontendapp.ui.theme.composables.ListItems

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R
import com.example.frontendapp.data.model.ServicioDetalleDto

@Composable
fun ServicioListItem(
    servicio: ServicioDetalleDto,
    onEditClick: (ServicioDetalleDto) -> Unit = {},
    onDeleteClick: (ServicioDetalleDto) -> Unit = {}
) {
    val colorEstado = if (servicio.valoracionPromedio >= 4.0) Color(0xFF4CAF50) else Color(0xFFF44336) // Green for high ratings, red otherwise
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Placeholder image
                contentDescription = "Imagen del servicio",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = servicio.nombre ?: "Sin nombre", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${servicio.categoria ?: "Sin categoría"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "Precio: ${servicio.precio ?: 0.0} €",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
                Text(
                    text = "Valoración: ${servicio.valoracionPromedio} (${servicio.numeroValoraciones} valoraciones)",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorEstado,
                    maxLines = 1
                )
                Text(
                    text = "Reservas: ${servicio.numeroReservas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            IconButton (
                onClick = { expanded = !expanded },
                modifier = Modifier.semantics {
                    contentDescription = if (expanded) "Cerrar opciones" else "Abrir opciones"
                }
            ) {
                AnimatedContent (
                    targetState = expanded,
                    transitionSpec = {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    }
                ) { targetExpanded ->
                    if (targetExpanded) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Más opciones"
                        )
                    }
                }
            }
        }

        if (expanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton (onClick = { onEditClick(servicio) }) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onDeleteClick(servicio) }) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicioListItemPreview() {
    val servicioEjemplo = ServicioDetalleDto(
        id = 1,
        negocioId = 10,
        nombre = "Corte de cabello",
        descripcion = "Un corte moderno y estilizado",
        duracionMinutos = 30,
        precio = 15.0,
        negocioNombre = "Peluquería Estilo",
        categoria = "Belleza",
        valoracionPromedio = 4.5,
        numeroValoraciones = 25,
        numeroReservas = 40
    )

    ServicioListItem(
        servicio = servicioEjemplo,
        onEditClick = { /* Acción editar */ },
        onDeleteClick = { /* Acción eliminar */ }
    )
}

