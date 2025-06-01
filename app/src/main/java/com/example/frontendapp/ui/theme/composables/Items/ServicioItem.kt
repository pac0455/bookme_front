package com.example.frontendapp.ui.theme.composables.Items

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Servicio.ServicioDetalleDto
import com.example.frontendapp.ui.theme.composables.modal.ServicioImagePicker


@Composable
fun ServicioCardItem(
    servicio: ServicioDetalleDto,
    imageUrl: String,
    onClick: (ServicioDetalleDto) -> Unit = {},
    modifier: Modifier,
) {
    val sinValoraciones = servicio.numeroValoracionesNegocio == 0


    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick(servicio) }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            ServicioImagePicker(
                imageUrl = imageUrl,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                iconSize = 48.dp,
                iconAlignment = Alignment.Center,
                contentAlignment = Alignment.Center,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = servicio.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!sinValoraciones) {
                    RatingStars(rating = servicio.valoracionPromedioNegocio.toFloat())
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${servicio.numeroValoracionesNegocio} ${if (servicio.numeroValoracionesNegocio == 1) "valoración" else "valoraciones"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = "Sin valoraciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sin valoraciones",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Precio: ${servicio.precio}€",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}







@Preview(showBackground = true)
@Composable
fun PreviewServicioItem() {
    val fakeServicio = ServicioDetalleDto(
        id = 1,
        negocioId = 10,
        nombre = "Corte de Cabello",
        descripcion = "Un corte profesional con estilo moderno y clásico.",
        duracionMinutos = 45,
        precio = 19.99,
        negocioNombre = "Barbería Estilo",
        categoria = "Belleza",
        valoracionPromedioNegocio = 4.7,
        numeroValoracionesNegocio = 123,
        numeroReservas = 89,
        imagen = "https://via.placeholder.com/150"
    )

    val screenWidth = 360.dp  // simula un ancho típico de pantalla para preview
    val cardWidth = screenWidth * 0.85f

    MaterialTheme {
        ServicioCardItem(
            servicio = fakeServicio,
            imageUrl = "",
            modifier = Modifier.width(cardWidth)
        )
    }
}
