package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.ui.theme.composables.Btn.BtnIconRounded
import com.example.frontendapp.ui.theme.composables.Items.EstadoEtiqueta
import com.example.frontendapp.ui.theme.composables.Items.RatingStars
import com.example.frontendapp.ui.theme.composables.modals.ServicioImagePicker
import com.example.frontendapp.ui.theme.composables.navigation.TabPagerScaffold
import com.example.frontendapp.ui.theme.composables.tab.negocioDetails.*
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.*
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.*

private val TAG = "NegocioDetailScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioDetailScreen(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    servicioViewModel: ServicioViewModel,
    horariosViewModel: HorariosViewModel,
    reservaViewModel: ReservasViewModel,
    modifier: Modifier = Modifier,
    valoracionesViewModel: ValoracionViewModel
) {
    val negocioCard by negocioViewModel.tempNegocioCard.collectAsState()
    val imagenUrl by remember { mutableStateOf(negocioViewModel.getNegocioImageUrl(negocioCard.id)) }
    val logMessage = stringResource(id = R.string.log_valoracion_form_id, negocioCard.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = negocioCard.nombre,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_content_description) // String resource
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header con imagen
            NegocioDetailHeader(
                negocioCard = negocioCard,
                imagenUrl = imagenUrl ?: ""
            )

            // Tabs
            val tabItems = listOf(
                TabItem(
                    title = stringResource(id = R.string.tab_title_services), // String resource
                    selectedIcon = Icons.Default.Build,
                    unSelectedIcon = Icons.Default.Build,
                    content = {
                        NegocioServicioTab(
                            viewModel = servicioViewModel,
                            reservaViewModel = reservaViewModel,
                            negocioId = negocioCard.id,
                            navController = navController,
                            servicioViewModel = servicioViewModel
                        )
                    }
                ),
                TabItem(
                    title = stringResource(id = R.string.tab_title_schedules), // String resource
                    selectedIcon = Icons.Default.Schedule,
                    unSelectedIcon = Icons.Default.Schedule,
                    content = {
                        NegocioHorarioTab(
                            viewModel = horariosViewModel,
                            negocioId = negocioCard.id
                        )
                    }
                ),
                TabItem(
                    title = stringResource(id = R.string.tab_title_reviews), // String resource
                    selectedIcon = Icons.Default.Star,
                    unSelectedIcon = Icons.Default.StarBorder,
                    content = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                NegocioValoracionesTab(
                                    negocioId = negocioCard.id,
                                    valoracionViewModel = valoracionesViewModel,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(bottom = 30.dp)
                                )
                            }


                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                BtnIconRounded(
                                    icon = Icons.Default.Add,
                                    onClick = {
                                        Log.d("NegocioDetailScreen", logMessage)
                                        navController.navigate(NavigationItem.VALORACION_FORM.createRoute(negocioCard.id))
                                    },
                                )
                            }
                        }
                    }
                )
            )

            TabPagerScaffold(tabItems = tabItems)
        }
    }
}


@Composable
private fun NegocioDetailHeader(
    negocioCard: NegocioCardCliente,
    imagenUrl: String
) {
    val singularReviewText = stringResource(id = R.string.singular_review_count)
    val pluralReviewText = stringResource(id = R.string.plural_review_count)
    val noReviewsYetText = stringResource(id = R.string.no_reviews_yet)
    val distanceAvailableText = stringResource(id = R.string.distance_available)
    val distanceNotAvailableText = stringResource(id = R.string.distance_not_available)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Imagen del negocio
            ServicioImagePicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                imageModifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                imageUpdatedAt = negocioCard.logoUpdatedAt,
                clickable = false,
                showIconEdit = false,
                borderColor = MaterialTheme.colorScheme.primary,
                borderWidth = 0.dp,
                imageUrl = imagenUrl
            )

            // Información del negocio
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Nombre y categoría
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = negocioCard.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = negocioCard.categoria,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    EstadoEtiqueta(isOpen = negocioCard.isOpen)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating y reseñas
                if (negocioCard.reviewCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RatingStars(rating = negocioCard.rating)
                        Text(
                            text = "${negocioCard.rating}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                id = R.string.of_total_reviews, // Reusing existing string for consistency
                                negocioCard.reviewCount,
                                if (negocioCard.reviewCount == 1) singularReviewText else pluralReviewText
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.cd_star_icon), // String resource
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = noReviewsYetText, // String resource
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Descripción
                Text(
                    text = negocioCard.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Información adicional
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        text = negocioCard.direccion,
                        contentDescription = stringResource(id = R.string.cd_location_icon) // Added content description
                    )

                    InfoRow(
                        icon = Icons.Default.Navigation,
                        text = negocioCard.distancia?.let { String.format(distanceAvailableText, it) }
                            ?: distanceNotAvailableText, // String resource with format
                        contentDescription = stringResource(id = R.string.cd_navigation_icon) // Added content description
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    contentDescription: String? = null // Added contentDescription parameter
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription, // Use contentDescription parameter
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class NegocioDetailTab(val title: String) {
    HORARIOS("Horarios"),
    VALORACIONES("Valoraciones"),
    SERVICIOS("Servicios");
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewNegocioDetailScreenImproved() {
    val negocio = NegocioCardCliente(
        id = 1,
        nombre = "Peluquería Bella",
        descripcion = "Cortes modernos y tratamientos capilares de alta calidad",
        categoria = "Belleza",
        direccion = "Calle Falsa 123, Centro",
        rating = 4.5f,
        reviewCount = 34,
        isActive = true,
        isOpen = true,
        distancia = 1.35,
        latitud = 40.4168,
        longitud = -3.7038
    )

    MaterialTheme {
        NegocioDetailScreen(
            navController = rememberNavController(),
            negocioViewModel = FakeNegocioViewModel(),
            servicioViewModel = FakeServicioViewModel(),
            horariosViewModel = FakeHorariosViewModel(),
            reservaViewModel = FakeReservasViewModel(),
            valoracionesViewModel = FakeValoracionViewModel()
        )
    }
}