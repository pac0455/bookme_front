package com.example.frontendapp.ui.theme.composables.Items

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.list.darken
import com.example.frontendapp.ui.theme.composables.modals.ServicioImagePicker

@Composable
fun NegocioCard(
    negocio: NegocioCardCliente,
    imagenUrl: String? = null,
    onClick: () -> Unit,
    mostrarDistancia: Boolean = false
) {
    val sinReseñas = negocio.reviewCount == 0
    val sinDistancia = negocio.distancia == null || negocio.distancia <= 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(enabled = negocio.isActive, onClick = onClick)
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
                icon = Icons.Filled.NoPhotography,
                imageUrl = imagenUrl,
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
                    text = negocio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!sinReseñas) {

                    RatingStars(rating = negocio.rating)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${negocio.reviewCount} ${if (negocio.reviewCount == 1) "reseña" else "reseñas"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = "Sin reseñas",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sin reseñas",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))
                EstadoEtiqueta(isOpen = negocio.isOpen)

                if (!sinDistancia && mostrarDistancia) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${String.format("%.1f", negocio.distancia)} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (sinReseñas || sinDistancia) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = negocio.categoria,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = negocio.direccion,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}




@Composable
fun EstadoEtiqueta(isOpen: Boolean) {
    val backgroundColor = if (isOpen) Color(0xFFDFF5E1) else Color(0xFFFFE0E0)
    val textColor = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828)
    val icon = if (isOpen) Icons.Default.Circle else Icons.Default.Circle // ícono más sutil

    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(textColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isOpen) "Abierto" else "Cerrado",
            color = textColor,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
        )
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
    val negocio = NegocioCardCliente(
        id = 1,
        nombre = "Mi negocio",
        descripcion = "Descripción",
        categoria = "Psicología",
        direccion = "Calle Ejemplo 123",
        rating = 4.5f,
        reviewCount = 10,
        isActive = true,
        isOpen = true,
        distancia = 5.0,
        latitud = 40.123,
        longitud = -3.456
    )

    val listState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f

    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    FrontendappTheme {
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = (screenWidth - cardWidth) / 2)
        ) {
            items(listOf(negocio), key = { it.id }) { negocioCard ->
                Box(
                    modifier = Modifier.width(cardWidth)
                ) {
                    NegocioCard(
                        negocio = negocioCard,
                        imagenUrl = "https://example.com",
                        mostrarDistancia = negocioCard.distancia != null,
                        onClick = {
                            Log.d("NegocioCardList", "Clic en negocio: ${negocioCard.nombre}")
                        }
                    )
                }
            }
        }
    }
}
