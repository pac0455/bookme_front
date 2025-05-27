package com.example.frontendapp.ui.theme.composables.ListItems

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.ui.theme.composables.list.darken
import com.example.frontendapp.ui.theme.composables.modal.ServicioImagePicker

@Composable
fun NegocioCard(
    negocio: NegocioCardCliente,
    imagenUrl: String? = null,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(enabled = negocio.isActive, onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(modifier = Modifier.height(200.dp)) {
                ServicioImagePicker(
                    icon = Icons.Filled.NoPhotography,
                    imageUrl = imagenUrl,
                    modifier = Modifier.fillMaxSize(),
                    iconSize = 68.dp,
                    iconAlignment = Alignment.Center,
                    contentAlignment = Alignment.Center,
                    backgroundColor = Color.LightGray,
                )
            }
            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = negocio.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = negocio.category,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = negocio.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = negocio.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            EstadoEtiqueta(negocio.isOpen)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (negocio.reviewCount > 0) {
                    RatingStars(rating = negocio.rating)
                    Text(
                        text = "(${negocio.reviewCount} ${if (negocio.reviewCount == 1) "reseña" else "reseñas"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = "Sin reseñas",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Sin reseñas todavía",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        if (!negocio.isActive) {
            Text(
                text = "No disponible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun EstadoEtiqueta(isOpen: Boolean) {
    val backgroundColor = if (isOpen) Color(0xFFDFF5E1) else Color(0xFFFFE0E0)
    val textColor = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828)
    val icon = if (isOpen) Icons.Default.CheckCircle else Icons.Default.Lock

    val shape = RoundedCornerShape(50.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isOpen) "Abierto" else "Cerrado",
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
fun RatingStars(rating: Float) {
    val color= Color.Yellow.darken(0.2f)
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Calcular el número de estrellas llenas y medias
        val fullStars = rating.toInt()
        val hasHalfStar = rating % 1 >= 0.5

        // Dibujar estrellas llenas
        for (i in 0 until fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = color // Color de las estrellas llenas
            )
        }

        // Dibujar estrella media si corresponde
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = "Half Star",
                tint = color // Color de la estrella media
            )
        }

        // Dibujar estrellas vacías
        val startEmptyStars = fullStars + if (hasHalfStar) 1 else 0
        for (i in startEmptyStars until 5) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = "Empty Star",
                tint = color // Color de las estrellas vacías
            )
        }

        // Mostrar la calificación
        Text(
            text = "$rating/5",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp) // Espacio entre estrellas y texto
        )
    }
}

// Preview actualizado
@Preview(showBackground = true)
@Composable
fun NegocioCardPreview() {
    Column {
        NegocioCard(
            negocio = NegocioCardCliente(
                name = "Gimnasio Power",
                description = "Gimnasio completamente equipado con entrenadores profesionales.",
                category = "Gimnasio",
                address = "Av. Principal 456, Ciudad",
                rating = 4.5f,
                reviewCount = 12,
                isActive = true,
                isOpen = false,
                distancia = 5,
                id = 0
            ),
            onClick = { }
        )

        Spacer(modifier = Modifier.height(16.dp))

        NegocioCard(
            negocio = NegocioCardCliente(
                name = "Spa Relajante",
                description = "Servicios de spa y masajes relajantes.",
                category = "Bienestar",
                address = "Calle Secundaria 789, Ciudad",
                rating = 0f,
                reviewCount = 0,
                isActive = false,
                isOpen = true,
                id = 1,
                distancia = 5
            ),
            onClick = { }
        )
    }
}

