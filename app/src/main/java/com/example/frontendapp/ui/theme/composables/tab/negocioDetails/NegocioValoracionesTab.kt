package com.example.frontendapp.ui.theme.composables.tab.negocioDetails

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.valoracion.ValoracionResponseDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.viewmodels.ValoracionViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeValoracionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ValoracionesTabContent(
    negocioId: Int,
    valoracionViewModel: ValoracionViewModel,
    modifier: Modifier = Modifier
) {
    val valoracionesState by valoracionViewModel.valoracionesState.collectAsState()

    // Cargar valoraciones cuando cambie negocioId
    LaunchedEffect(negocioId) {
        valoracionViewModel.getValoracionesPorNegocio(
            negocioId = negocioId,
            onLoading = { Log.d("ValoracionesTab", "Cargando valoraciones...") },
            onSuccess = { Log.d("ValoracionesTab", "Valoraciones cargadas: ${it.size}") },
            onError = { Log.e("ValoracionesTab", "Error: $it") }
        )
    }

    when (valoracionesState) {
        is Resource.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            val valoraciones = (valoracionesState as Resource.Success<List<ValoracionResponseDTO>>).data ?: emptyList()
            if (valoraciones.isEmpty()) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay valoraciones aún.")
                }
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(valoraciones, key = { it.id }) { valoracion ->
                        ValoracionItem(valoracion)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
        is Resource.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${(valoracionesState as Resource.Error).message ?: "Desconocido"}")
            }
        }
        else -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Carga las valoraciones.")
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
}

@SuppressLint("DefaultLocale")
@Composable
fun ValoracionItem(valoracion: ValoracionResponseDTO) {
    val formattedDate = try {
        val localDateTime = LocalDateTime.parse(valoracion.fechaValoracion, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        valoracion.fechaValoracion
    }

    val DarkYellow = remember { mutableStateOf(Color(0xFFFFB300)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = valoracion.usuario.userName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            RatingStars(
                rating = valoracion.puntuacion,
                starSize = 20.dp,
                starColor = DarkYellow.value,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = String.format("%.1f", valoracion.puntuacion),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = valoracion.comentario,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    }
}

@Composable
fun RatingStars(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp,
    starColor: Color = Color(0xFFFFB300)
) {
    val fullStars = rating.toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    Row(modifier = modifier) {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewValoracionesScreen() {
    // Usamos el FakeViewModel con repositorio simulado ya inicializado correctamente
    val fakeViewModel = remember { FakeValoracionViewModel() }

    // Lanzamos la carga simulada de valoraciones solo una vez
    LaunchedEffect(Unit) {
        fakeViewModel.getValoracionesPorNegocio(
            negocioId = 1,
            onLoading = {},
            onSuccess = {},
            onError = {}
        )
    }

    // Mostramos directamente el composable real
    ValoracionesTabContent(
        negocioId = 1,
        valoracionViewModel = fakeViewModel,
        modifier = Modifier.fillMaxSize()
    )
}
