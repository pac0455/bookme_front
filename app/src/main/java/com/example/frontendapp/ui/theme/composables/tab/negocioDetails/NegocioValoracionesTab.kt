package com.example.frontendapp.ui.theme.composables.tab.negocioDetails

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R
import com.example.frontendapp.data.model.valoracion.ValoracionResponseDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.viewmodels.ValoracionViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeValoracionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NegocioValoracionesTab(
    negocioId: Int,
    valoracionViewModel: ValoracionViewModel,
    modifier: Modifier = Modifier
) {
    val valoracionesState by valoracionViewModel.valoracionesState.collectAsState()

    // Strings for logs (can be externalized or kept as literals based on preference)
    val logLoadingReviews = stringResource(R.string.log_loading_reviews)
    val logReviewsLoaded = stringResource(R.string.log_reviews_loaded)
    val logErrorReviews = stringResource(R.string.log_error_reviews)

    // Cargar valoraciones cuando cambie negocioId
    LaunchedEffect(negocioId) {
        valoracionViewModel.getValoracionesPorNegocio(
            negocioId = negocioId,
            onLoading = { Log.d("ValoracionesTab", logLoadingReviews) },
            onSuccess = { Log.d("ValoracionesTab", String.format(logReviewsLoaded, it.size)) },
            onError = { Log.e("ValoracionesTab", String.format(logErrorReviews, it)) }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (valoracionesState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.loading_reviews_message), // String resource
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is Resource.Success -> {
                val valoraciones = (valoracionesState as Resource.Success<List<ValoracionResponseDTO>>).data ?: emptyList()

                if (valoraciones.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = stringResource(id = R.string.cd_star_icon), // String resource
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(id = R.string.no_reviews_yet_title), // String resource
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.be_the_first_review), // String resource
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Header de la sección con estadísticas
                            ValoracionesHeader(valoraciones = valoraciones)
                        }

                        items(valoraciones, key = { it.id }) { valoracion ->
                            ValoracionItemImproved(valoracion)
                        }
                    }
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = stringResource(id = R.string.cd_error_icon), // String resource
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(id = R.string.error_loading_reviews_title), // String resource
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (valoracionesState as Resource.Error).message ?: stringResource(id = R.string.unknown_error), // String resource
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_data_available), // String resource
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ValoracionesHeader(valoraciones: List<ValoracionResponseDTO>) {
    val promedioRating = valoraciones.map { it.puntuacion }.average()
    val totalValoraciones = valoraciones.size

    val singularReviewText = stringResource(id = R.string.singular_review)
    val pluralReviewsText = stringResource(id = R.string.plural_reviews)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(id = R.string.cd_star_icon), // String resource
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.reviews_header_title), // String resource
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$totalValoraciones",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rating promedio
                Text(
                    text = String.format("%.1f", promedioRating),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Estrellas
                RatingStarsImproved(
                    rating = promedioRating,
                    starSize = 20.dp,
                    starColor = Color(0xFFFFB300)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(
                        id = R.string.of_total_reviews, // String resource with placeholder
                        totalValoraciones,
                        if (totalValoraciones == 1) singularReviewText else pluralReviewsText
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ValoracionItemImproved(valoracion: ValoracionResponseDTO) {
    val formattedDate = try {
        val localDateTime = LocalDateTime.parse(valoracion.fechaValoracion, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        valoracion.fechaValoracion
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header con usuario y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(id = R.string.cd_person_icon), // String resource
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = valoracion.usuario.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rating con estrellas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RatingStarsImproved(
                    rating = valoracion.puntuacion,
                    starSize = 18.dp,
                    starColor = Color(0xFFFFB300)
                )
                Text(
                    text = String.format("%.1f", valoracion.puntuacion),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comentario
            Text(
                text = valoracion.comentario,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}

@Composable
private fun RatingStarsImproved(
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
                contentDescription = stringResource(id = R.string.cd_full_star), // String resource
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = stringResource(id = R.string.cd_half_star), // String resource
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = stringResource(id = R.string.cd_empty_star), // String resource
                tint = starColor.copy(alpha = 0.3f),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewValoracionesTabContentImproved() {
    val fakeViewModel = remember { FakeValoracionViewModel() }

    LaunchedEffect(Unit) {
        fakeViewModel.getValoracionesPorNegocio(
            negocioId = 1,
            onLoading = {},
            onSuccess = {},
            onError = {}
        )
    }

    NegocioValoracionesTab(
        negocioId = 1,
        valoracionViewModel = fakeViewModel,
        modifier = Modifier.fillMaxSize()
    )
}
