package com.example.frontendapp.ui.theme.composables.tab

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.*
import com.example.frontendapp.ui.theme.composables.Items.NegocioCard
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.utils.UbicacionHelper

@Composable
fun NegocioTabContent(
    negocioViewModel: NegocioViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Estados para filtros y búsqueda
    var mostrarFiltros by remember { mutableStateOf(false) }
    var filtrosActivos by remember { mutableStateOf(FiltrosNegocio()) }
    var busqueda by remember { mutableStateOf("") }

    val negociosCardClienteState by negocioViewModel.negociosClienteState.collectAsState()
    val negociosCard = remember { mutableStateListOf<NegocioCardCliente>() }

    // Categorías disponibles (esto debería venir del ViewModel)
    val categoriasDisponibles = remember(negociosCardClienteState.data) {
        negociosCardClienteState.data?.map { it.categoria }?.distinct() ?: emptyList()
    }

    LaunchedEffect(Unit) {
        val ubi = UbicacionHelper.obtenerUbicacionActual(context = context)
        negocioViewModel.getNegociosParaCliente(ubi)
    }

    when (negociosCardClienteState) {
        is Resource.Success -> {
            negociosCard.clear()
            negociosCardClienteState.data?.let {
                Log.d("NegocioTabContent", "Negocios recibidos: ${it.size}")
                negociosCard.addAll(it)
            }
        }
        is Resource.Error -> {
            Log.e("NegocioTabContent", "Error al obtener negocios")
        }
        else -> Unit
    }

    // ✅ Función para aplicar filtros
    val negociosFiltrados = remember(negociosCard, filtrosActivos, busqueda) {
        var resultado = negociosCard.toList()

        // Filtro por búsqueda
        if (busqueda.isNotBlank()) {
            resultado = resultado.filter { negocio ->
                negocio.nombre.contains(busqueda, ignoreCase = true) ||
                        negocio.descripcion?.contains(busqueda, ignoreCase = true) == true
            }
        }

        // Filtro por categorías
        if (filtrosActivos.categorias.isNotEmpty()) {
            resultado = resultado.filter { negocio ->
                negocio.categoria in filtrosActivos.categorias
            }
        }

        // Filtro por distancia
        filtrosActivos.distanciaMaxima?.let { maxDistancia ->
            resultado = resultado.filter { negocio ->
                negocio.distancia?.let { it <= maxDistancia } ?: true
            }
        }

        // Filtro por rating
        filtrosActivos.ratingMinimo?.let { minRating ->
            resultado = resultado.filter { negocio ->
                negocio.rating >= minRating
            }
        }

        // Filtro por estado abierto
        if (filtrosActivos.soloAbiertos) {
            resultado = resultado.filter { it.isOpen }
        }

        // Ordenamiento
        when (filtrosActivos.ordenarPor) {
            OrdenarPor.RELEVANCIA -> resultado
            OrdenarPor.DISTANCIA -> resultado.sortedBy { it.distancia ?: Float.MAX_VALUE.toDouble() }
            OrdenarPor.RATING -> resultado.sortedByDescending { it.rating }
            OrdenarPor.NOMBRE -> resultado.sortedBy { it.nombre }
            OrdenarPor.PRECIO -> resultado // Placeholder, necesitarías precio en el modelo
        }
    }

    // Verificar si hay filtros activos
    val hayFiltrosActivos = filtrosActivos != FiltrosNegocio() || busqueda.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HeaderSeccion(
                    titulo = "Negocios",
                    searchQuery = busqueda,
                    hasActiveFilters = hayFiltrosActivos,
                    onSearchChange = { busqueda = it },
                    onFilterClick = { mostrarFiltros = true }
                )
            }

            // Mostrar resumen de filtros activos
            if (hayFiltrosActivos) {
                item {
                    FiltrosActivosResumen(
                        filtros = filtrosActivos,
                        busqueda = busqueda,
                        totalResultados = negociosFiltrados.size,
                        onLimpiarFiltros = {
                            filtrosActivos = FiltrosNegocio()
                            busqueda = ""
                        }
                    )
                }
            }

            // Secciones mejoradas con animaciones
            item {
                AnimatedVisibility(
                    visible = negociosFiltrados.filter { it.isOpen }.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    NegocioSeccion(
                        titulo = "Abiertos ahora",
                        negocios = negociosFiltrados.filter { it.isOpen },
                        negocioViewModel = negocioViewModel,
                        listState = rememberLazyListState(),
                        flingBehavior = rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                        cardWidth = cardWidth,
                        screenWidth = screenWidth,
                        navController = navController,
                        icono = Icons.Default.Schedule,
                        colorTema = ThemeColors.success
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = negociosFiltrados.sortedByDescending { it.rating }.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    NegocioSeccion(
                        titulo = "Mejor valorados",
                        negocios = negociosFiltrados.sortedByDescending { it.rating },
                        negocioViewModel = negocioViewModel,
                        listState = rememberLazyListState(),
                        flingBehavior = rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                        cardWidth = cardWidth,
                        screenWidth = screenWidth,
                        navController = navController,
                        icono = Icons.Default.Star,
                        colorTema = ThemeColors.warning
                    )
                }
            }

            // ✅ Nueva sección: Cerca de ti
            if (filtrosActivos.distanciaMaxima == null) {
                item {
                    AnimatedVisibility(
                        visible = negociosFiltrados.sortedBy { it.distancia ?: Float.MAX_VALUE.toDouble() }.isNotEmpty(),
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        NegocioSeccion(
                            titulo = "Cerca de ti",
                            negocios = negociosFiltrados.sortedBy { it.distancia ?: Float.MAX_VALUE.toDouble() }.take(10),
                            negocioViewModel = negocioViewModel,
                            listState = rememberLazyListState(),
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                            cardWidth = cardWidth,
                            screenWidth = screenWidth,
                            navController = navController,
                            icono = Icons.Default.LocationOn,
                            colorTema = ThemeColors.info
                        )
                    }
                }
            }
        }

        // ✅ Modal de filtros
        FiltrosNegocioModal(
            isVisible = mostrarFiltros,
            filtrosActuales = filtrosActivos,
            categoriasDisponibles = categoriasDisponibles,
            onDismiss = { mostrarFiltros = false },
            onAplicarFiltros = { nuevosFiltros ->
                filtrosActivos = nuevosFiltros
            },
            onLimpiarFiltros = {
                filtrosActivos = FiltrosNegocio()
                busqueda = ""
            }
        )
    }
}

// ✅ Componente para mostrar resumen de filtros activos
@Composable
private fun FiltrosActivosResumen(
    filtros: FiltrosNegocio,
    busqueda: String,
    totalResultados: Int,
    onLimpiarFiltros: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$totalResultados resultados encontrados",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = onLimpiarFiltros,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar")
                }
            }

            // Mostrar filtros activos
            if (busqueda.isNotBlank() || filtros != FiltrosNegocio()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (busqueda.isNotBlank()) {
                        item {
                            FiltroChip(
                                texto = "\"$busqueda\"",
                                icono = Icons.Default.Search
                            )
                        }
                    }

                    if (filtros.soloAbiertos) {
                        item {
                            FiltroChip(
                                texto = "Solo abiertos",
                                icono = Icons.Default.Schedule
                            )
                        }
                    }

                    filtros.distanciaMaxima?.let { distancia ->
                        item {
                            FiltroChip(
                                texto = "< ${distancia}km",
                                icono = Icons.Default.LocationOn
                            )
                        }
                    }

                    filtros.ratingMinimo?.let { rating ->
                        item {
                            FiltroChip(
                                texto = "${rating}+ ⭐",
                                icono = Icons.Default.Star
                            )
                        }
                    }

                    if (filtros.categorias.isNotEmpty()) {
                        items(filtros.categorias.toList()) { categoria ->
                            FiltroChip(
                                texto = categoria,
                                icono = Icons.Default.Category
                            )
                        }
                    }
                }
            }
        }
    }
}

// Chip para mostrar filtros activos
@Composable
private fun FiltroChip(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ✅ Sección mejorada con colores del tema
@Composable
fun NegocioSeccion(
    titulo: String,
    negocios: List<NegocioCardCliente>,
    negocioViewModel: NegocioViewModel,
    listState: LazyListState,
    flingBehavior: FlingBehavior,
    navController: NavController,
    cardWidth: Dp,
    screenWidth: Dp,
    icono: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Store,
    colorTema: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // ✅ Header de sección mejorado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorTema.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorTema,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = colorTema.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${negocios.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorTema,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = (screenWidth - cardWidth) / 2)
        ) {
            items(negocios, key = { it.id }) { negocio ->
                Box(modifier = Modifier.width(cardWidth)) {
                    NegocioCard(
                        negocio = negocio,
                        imagenUrl = negocioViewModel.getNegocioImageUrl(negocio.id),
                        mostrarDistancia = negocio.distancia != null,
                        onClick = {
                            Log.d("NegocioCardList", "Clic en negocio: ${negocio.nombre}")
                            negocioViewModel.setTmpNegocioCard(negocio)
                            navController.navigate(
                                NavigationItem.NEGOCIO_CARD_DETAILS.createRoute(negocio.id)
                            )
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegocioTabContentPreview() {
    val navController = rememberNavController()
    FrontendappTheme {
        NegocioTabContent(
            negocioViewModel = FakeNegocioViewModel(),
            navController = navController
        )
    }
}
