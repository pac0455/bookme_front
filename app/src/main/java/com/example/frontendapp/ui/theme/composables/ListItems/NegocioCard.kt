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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.composables.list.darken

@Composable
fun BusinessCard(
    name: String,
    description: String,
    category: String,
    address: String,
    rating: Float,
    isActive: Boolean,
    logoResId: Int,
    onClick: () -> Unit,
    isOpen: Boolean,
    Distancia: Int
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(enabled = isActive, onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp) // Cambia aquí
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Mostrar el logo
            Box(
                modifier = Modifier.height(200.dp),

            )
            {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Logo del negocio",
                    modifier = Modifier.fillMaxSize()
                )
            }


            // Nombre y categoría
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dirección y valoración

            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0.8f))
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))

            ){
                Text(
                    text = if(isOpen) "Abierto" else "Cerrado",
                    color = if(isOpen)
                        Color.Red.darken(0.7f).copy(alpha = 0.7f)
                    else
                        Color.Green.darken(0.7f).copy(alpha = 0.7f)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ){
            RatingStars(rating = rating)
        }


        // Estado activo
        if (!isActive) {
            Text(
                text = "No disponible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
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

@Preview(showBackground = true)
@Composable
fun BusinessCardPreview() {
    BusinessCard(
        name = "Nombre del Negocio",
        description = "Descripción breve del negocio que ofrece servicios de calidad.",
        category = "Gimnasio",
        address = "Calle Falsa 123, Ciudad",
        rating = 4.5f,
        isActive = true,
        logoResId = R.drawable.logo, // Asegúrate de que el recurso de imagen esté disponible
        Distancia = 5,
        isOpen = false,
        onClick = { /* Acción al hacer clic */ }
    )
}
