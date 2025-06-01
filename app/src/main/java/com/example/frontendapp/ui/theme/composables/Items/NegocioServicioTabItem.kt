package com.example.frontendapp.ui.theme.composables.Items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Servicio.ServicioDetalleDto
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomBox

@Composable
fun ServicioItem(
    servicio: ServicioDetalleDto,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor =
            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surface

        )
    ) {
        CustomBox(
            modifier = Modifier.fillMaxWidth(),
            borderTop = true,
            borderBottom = true,
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp) // margen para evitar solapamiento con botón
                ) {
                    Text(servicio.nombre ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)
                    Text("Duración: ${servicio.duracionMinutos ?: 0} min", style = MaterialTheme.typography.bodySmall)
                    Text("Precio: $${servicio.precio ?: 0.0}", style = MaterialTheme.typography.bodySmall)
                }

                BtnStyle1(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(0.4f),
                    shape = RoundedCornerShape(12.dp),
                    text ="Reservar"
                )
            }
        }

    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 120)
@Composable
fun ServicioItemPreview() {
    val servicio = ServicioDetalleDto(
        id = 1,
        negocioId = 1,
        nombre = "Corte de Cabello",
        descripcion = "Un corte moderno y profesional.",
        duracionMinutos = 30,
        precio = 100.0,
        negocioNombre = "Barbería Don Pepe",
        categoria = "Barbería",
        valoracionPromedioNegocio = 4.7,
        numeroValoracionesNegocio = 56,
        numeroReservas = 120,
        imagen = null
    )
    MaterialTheme {
        Surface {
            Column {
                ServicioItem(servicio = servicio, isSelected = false, onClick = {})
                ServicioItem(servicio = servicio, isSelected = true, onClick = {})
            }
        }
    }
}
