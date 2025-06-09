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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
                            contentDescription = "Volver"
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
                    title = NegocioDetailTab.SERVICIOS.title,
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
                    title = NegocioDetailTab.HORARIOS.title,
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
                    title = NegocioDetailTab.VALORACIONES.title,
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
                                        .padding(bottom = 60.dp)
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
                                        Log.d(TAG, "Id pasada a valoracion form: ${negocioCard.id}")
                                        navController.navigate(NavigationItem.VALORACION_FORM.createRoute(negocioCard.id))
                                    }
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
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                imageModifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                id = negocioCard.id,
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
                            text = "(${negocioCard.reviewCount} ${if (negocioCard.reviewCount == 1) "reseña" else "reseñas"})",
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
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sin reseñas aún",
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
                        text = negocioCard.direccion
                    )

                    InfoRow(
                        icon = Icons.Default.Navigation,
                        text = negocioCard.distancia?.let { "A %.2f km de distancia".format(it) }
                            ?: "Distancia no disponible"
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
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