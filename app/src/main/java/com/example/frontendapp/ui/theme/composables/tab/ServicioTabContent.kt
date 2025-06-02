package com.example.frontendapp.ui.theme.composables.tab

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.frontendapp.data.model.Servicio.ServicioDetalleDto
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.*
import com.example.frontendapp.ui.theme.composables.Items.ServicioCardItem
import com.example.frontendapp.ui.theme.composables.modal.*
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import com.example.frontendapp.ui.theme.AppColors
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val TAG = "ServicioTabContent"

// ✅ NUEVO: Data class para filtros de servicios
data class FiltrosServicio(
    val categorias: Set<String> = emptySet(),
    val precioMinimo: Double? = null,
    val precioMaximo: Double? = null,
    val duracionMaxima: Int? = null,
    val soloMejorValorados: Boolean = false,
    val ordenarPor: OrdenarServicioPor = OrdenarServicioPor.RELEVANCIA
)

// ✅ NUEVO: Enum para ordenamiento
enum class OrdenarServicioPor(val displayName: String, val icon: ImageVector) {
    RELEVANCIA("Relevancia", Icons.Default.Star),
    PRECIO_ASC("Precio: menor a mayor", Icons.Default.ArrowUpward),
    PRECIO_DESC("Precio: mayor a menor", Icons.Default.ArrowDownward),
    DURACION("Duración", Icons.Default.Schedule),
    VALORACION("Valoración", Icons.Default.ThumbUp)
}

// ✅ NUEVO: Enum para filtros de duración
enum class DuracionOption(val displayName: String, val value: Int?) {
    TODAS("Todas las duraciones", null),
    CORTA("Menos de 30 min", 30),
    MEDIA("Menos de 60 min", 60),
    LARGA("Menos de 120 min", 120)
}

@Composable
fun ServicioTabContent(
    servicioViewModel: ServicioViewModel,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // ✅ NUEVO: Estados para filtros y búsqueda
    var mostrarFiltros by remember { mutableStateOf(false) }
    var filtrosActivos by remember { mutableStateOf(FiltrosServicio()) }
    var busqueda by remember { mutableStateOf("") }

    // Estado de los servicios del ViewModel
    val servicioListState by servicioViewModel.serviciosDetalleState.collectAsState()

    // ✅ NUEVO: Extraer categorías disponibles
    val categoriasDisponibles = remember(servicioListState) {
        if (servicioListState is Resource.Success) {
            (servicioListState as Resource.Success).data
                ?.map { it.categoria }
                ?.distinct()
                ?.sorted() ?: emptyList()
        } else {
            emptyList()
        }
    }

    // ✅ NUEVO: Extraer rango de precios
    val rangoPrecio = remember(servicioListState) {
        if (servicioListState is Resource.Success) {
            val servicios = (servicioListState as Resource.Success).data ?: emptyList()
            if (servicios.isNotEmpty()) {
                val min = servicios.minOfOrNull { it.precio } ?: 0.0
                val max = servicios.maxOfOrNull { it.precio } ?: 100.0
                min to max
            } else {
                0.0 to 100.0
            }
        } else {
            0.0 to 100.0
        }
    }

    // ✅ NUEVO: Lista filtrada según filtros y búsqueda
    val serviciosFiltrados = remember(servicioListState, filtrosActivos, busqueda) {
        val servicios = if (servicioListState is Resource.Success) {
            (servicioListState as Resource.Success).data ?: emptyList()
        } else {
            emptyList()
        }

        var resultado = servicios

        // Filtro por búsqueda
        if (busqueda.isNotBlank()) {
            resultado = resultado.filter { servicio ->
                servicio.nombre.contains(busqueda, ignoreCase = true) ||
                        servicio.descripcion.contains(busqueda, ignoreCase = true) ||
                        servicio.categoria.contains(busqueda, ignoreCase = true) ||
                        servicio.negocioNombre.contains(busqueda, ignoreCase = true)
            }
        }

        // Filtro por categorías
        if (filtrosActivos.categorias.isNotEmpty()) {
            resultado = resultado.filter { servicio ->
                servicio.categoria in filtrosActivos.categorias
            }
        }

        // Filtro por precio mínimo
        filtrosActivos.precioMinimo?.let { min ->
            resultado = resultado.filter { it.precio >= min }
        }

        // Filtro por precio máximo
        filtrosActivos.precioMaximo?.let { max ->
            resultado = resultado.filter { it.precio <= max }
        }

        // Filtro por duración máxima
        filtrosActivos.duracionMaxima?.let { maxDuracion ->
            resultado = resultado.filter { it.duracionMinutos <= maxDuracion }
        }

        // Filtro por mejor valorados
        if (filtrosActivos.soloMejorValorados) {
            resultado = resultado.filter { it.valoracionPromedioNegocio >= 4.0 }
        }

        // Ordenamiento
        resultado = when (filtrosActivos.ordenarPor) {
            OrdenarServicioPor.RELEVANCIA -> resultado // Sin cambios
            OrdenarServicioPor.PRECIO_ASC -> resultado.sortedBy { it.precio }
            OrdenarServicioPor.PRECIO_DESC -> resultado.sortedByDescending { it.precio }
            OrdenarServicioPor.DURACION -> resultado.sortedBy { it.duracionMinutos }
            OrdenarServicioPor.VALORACION -> resultado.sortedByDescending { it.valoracionPromedioNegocio }
        }

        resultado
    }

    // ✅ NUEVO: Verificar si hay filtros activos
    val hayFiltrosActivos = filtrosActivos != FiltrosServicio() || busqueda.isNotBlank()

    // ✅ NUEVO: Agrupar servicios por categoría para mostrarlos en secciones
    val serviciosPorCategoria = remember(serviciosFiltrados) {
        serviciosFiltrados.groupBy { it.categoria }
    }

    // Carga inicial de servicios si no hay datos
    LaunchedEffect(servicioListState) {
        Log.d(TAG, "Estado servicioListState cambiado: $servicioListState")
        if (servicioListState is Resource.None) {
            Log.d(TAG, "Estado None detectado. Iniciando carga de servicios...")
            servicioViewModel.getServiciosDetalle()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ✅ MEJORADO: Header con búsqueda y filtros
            HeaderSeccion(
                titulo = "Servicios",
                searchQuery = busqueda,
                hasActiveFilters = hayFiltrosActivos,
                onSearchChange = { busqueda = it },
                onFilterClick = { mostrarFiltros = true }
            )

            // ✅ NUEVO: Mostrar resumen de filtros activos
            if (hayFiltrosActivos) {
                FiltrosActivosResumen(
                    filtros = filtrosActivos,
                    busqueda = busqueda,
                    totalResultados = serviciosFiltrados.size,
                    onLimpiarFiltros = {
                        filtrosActivos = FiltrosServicio()
                        busqueda = ""
                    }
                )
            }

            // Contenido principal
            when (servicioListState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is Resource.Error -> {
                    val message = (servicioListState as Resource.Error).message
                    Log.e(TAG, "Error al cargar servicios: $message")
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error al cargar servicios",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = message ?: "Error desconocido",
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { servicioViewModel.getServiciosDetalle() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }

                is Resource.Success -> {
                    if (serviciosFiltrados.isEmpty()) {
                        // ✅ NUEVO: Mensaje cuando no hay resultados
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (servicioListState.data?.isEmpty() == true)
                                        Icons.Default.SentimentDissatisfied
                                    else
                                        Icons.Default.FilterListOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (servicioListState.data?.isEmpty() == true)
                                        "No hay servicios disponibles"
                                    else
                                        "No hay servicios con estos filtros",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (servicioListState.data?.isEmpty() == true)
                                        "Intenta más tarde"
                                    else
                                        "Prueba con otros filtros o términos de búsqueda",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )

                                if (hayFiltrosActivos) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    TextButton(
                                        onClick = {
                                            filtrosActivos = FiltrosServicio()
                                            busqueda = ""
                                        }
                                    ) {
                                        Text("Ver todos los servicios")
                                    }
                                }
                            }
                        }
                    } else {
                        // ✅ MEJORADO: Mostrar servicios por categorías
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para navbar
                        ) {
                            // Si hay filtros activos, mostrar todos los servicios en una sola sección
                            if (hayFiltrosActivos) {
                                item {
                                    ServicioSeccion(
                                        titulo = "Resultados",
                                        servicios = serviciosFiltrados,
                                        servicioViewModel = servicioViewModel,
                                        navController = navController,
                                        screenWidth = screenWidth,
                                        icono = Icons.Default.Search,
                                        colorTema = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                // Mostrar servicios mejor valorados primero
                                val mejorValorados = serviciosFiltrados
                                    .sortedByDescending { it.valoracionPromedioNegocio }
                                    .take(10)

                                if (mejorValorados.isNotEmpty()) {
                                    item {
                                        ServicioSeccion(
                                            titulo = "Mejor valorados",
                                            servicios = mejorValorados,
                                            servicioViewModel = servicioViewModel,
                                            navController = navController,
                                            screenWidth = screenWidth,
                                            icono = Icons.Default.Star,
                                            colorTema = ThemeColors.warning
                                        )
                                    }
                                }

                                // Mostrar servicios más reservados
                                val masReservados = serviciosFiltrados
                                    .sortedByDescending { it.numeroReservas }
                                    .take(10)

                                if (masReservados.isNotEmpty()) {
                                    item {
                                        ServicioSeccion(
                                            titulo = "Más reservados",
                                            servicios = masReservados,
                                            servicioViewModel = servicioViewModel,
                                            navController = navController,
                                            screenWidth = screenWidth,
                                            icono = Icons.Default.Favorite,
                                            colorTema = ThemeColors.error
                                        )
                                    }
                                }

                                // Mostrar por categorías
                                serviciosPorCategoria.forEach { (categoria, servicios) ->
                                    item {
                                        ServicioSeccion(
                                            titulo = categoria,
                                            servicios = servicios,
                                            servicioViewModel = servicioViewModel,
                                            navController = navController,
                                            screenWidth = screenWidth,
                                            icono = getCategoriaIcon(categoria),
                                            colorTema = getCategoriaColor(categoria)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                else -> Unit
            }
        }

        // ✅ NUEVO: Modal de filtros
        FiltrosServicioModal(
            isVisible = mostrarFiltros,
            filtrosActuales = filtrosActivos,
            categoriasDisponibles = categoriasDisponibles,
            rangoPrecio = rangoPrecio,
            onDismiss = { mostrarFiltros = false },
            onAplicarFiltros = { nuevosFiltros ->
                filtrosActivos = nuevosFiltros
            },
            onLimpiarFiltros = {
                filtrosActivos = FiltrosServicio()
                busqueda = ""
            }
        )
    }
}

// ✅ NUEVO: Componente para mostrar resumen de filtros activos
@Composable
private fun FiltrosActivosResumen(
    filtros: FiltrosServicio,
    busqueda: String,
    totalResultados: Int,
    onLimpiarFiltros: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.GreenBackground.copy(alpha = 0.3f)
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
            if (busqueda.isNotBlank() || filtros != FiltrosServicio()) {
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

                    if (filtros.soloMejorValorados) {
                        item {
                            FiltroChip(
                                texto = "4+ estrellas",
                                icono = Icons.Default.Star
                            )
                        }
                    }

                    filtros.duracionMaxima?.let { duracion ->
                        item {
                            FiltroChip(
                                texto = "< ${duracion}min",
                                icono = Icons.Default.Schedule
                            )
                        }
                    }

                    if (filtros.precioMinimo != null || filtros.precioMaximo != null) {
                        item {
                            val texto = when {
                                filtros.precioMinimo != null && filtros.precioMaximo != null ->
                                    "${filtros.precioMinimo}€ - ${filtros.precioMaximo}€"
                                filtros.precioMinimo != null ->
                                    "Desde ${filtros.precioMinimo}€"
                                else ->
                                    "Hasta ${filtros.precioMaximo}€"
                            }
                            FiltroChip(
                                texto = texto,
                                icono = Icons.Default.Euro
                            )
                        }
                    }

                    if (filtros.categorias.isNotEmpty()) {
                        items(filtros.categorias.toList()) { categoria ->
                            FiltroChip(
                                texto = categoria,
                                icono = getCategoriaIcon(categoria)
                            )
                        }
                    }

                    if (filtros.ordenarPor != OrdenarServicioPor.RELEVANCIA) {
                        item {
                            FiltroChip(
                                texto = "Orden: ${filtros.ordenarPor.displayName}",
                                icono = filtros.ordenarPor.icon
                            )
                        }
                    }
                }
            }
        }
    }
}

// ✅ NUEVO: Chip para mostrar filtros activos
@Composable
private fun FiltroChip(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = AppColors.GreenBackground.copy(alpha = 0.8f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            AppColors.GreenPrimary.copy(alpha = 0.5f)
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

// ✅ NUEVO: Sección de servicios
@Composable
fun ServicioSeccion(
    titulo: String,
    servicios: List<ServicioDetalleDto>,
    servicioViewModel: ServicioViewModel,
    navController: NavController,
    screenWidth: androidx.compose.ui.unit.Dp,
    icono: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Spa,
    colorTema: Color = MaterialTheme.colorScheme.primary
) {
    val cardWidth = screenWidth * 0.85f
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

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
                    text = "${servicios.size}",
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
            items(servicios, key = { it.id }) { servicio ->
                Box(modifier = Modifier.width(cardWidth)) {
                    ServicioCardItem(
                        modifier = Modifier.fillMaxWidth(),
                        servicio = servicio,
                        imageUrl = servicioViewModel.getServicioImageUrl(servicio.id) ?: "",
                        onClick = {
                            Log.d(TAG, "Servicio seleccionado: ${servicio.nombre} (id: ${servicio.id}) con el negocioId: ${servicio.negocioId}")
                            navController.navigate(NavigationItem.NEGOCIO_CARD_DETAILS.createRoute(servicio.negocioId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FiltrosServicioModal(
    isVisible: Boolean,
    filtrosActuales: FiltrosServicio,
    categoriasDisponibles: List<String>,
    rangoPrecio: Pair<Double, Double>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosServicio) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            FiltrosContent(
                filtrosActuales = filtrosActuales,
                categoriasDisponibles = categoriasDisponibles,
                rangoPrecio = rangoPrecio,
                onDismiss = onDismiss,
                onAplicarFiltros = onAplicarFiltros,
                onLimpiarFiltros = onLimpiarFiltros
            )
        }
    }
}

@Composable
private fun FiltrosContent(
    filtrosActuales: FiltrosServicio,
    categoriasDisponibles: List<String>,
    rangoPrecio: Pair<Double, Double>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosServicio) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    var filtros by remember { mutableStateOf(filtrosActuales) }
    val scrollState = rememberScrollState()

    // Estado para el rango de precios
    var rangoPrecioState by remember {
        mutableStateOf(
            (filtrosActuales.precioMinimo ?: rangoPrecio.first) to
                    (filtrosActuales.precioMaximo ?: rangoPrecio.second)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // ✅ Header del modal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Categorías
                FiltroSeccion(
                    titulo = "Categorías",
                    icono = Icons.Default.Category
                ) {
                    CategoriasFilter(
                        categoriasDisponibles = categoriasDisponibles,
                        categoriasSeleccionadas = filtros.categorias,
                        onCategoriaToggle = { categoria ->
                            filtros = if (categoria in filtros.categorias) {
                                filtros.copy(categorias = filtros.categorias - categoria)
                            } else {
                                filtros.copy(categorias = filtros.categorias + categoria)
                            }
                        }
                    )
                }

                // Rango de precio
                FiltroSeccion(
                    titulo = "Rango de precio",
                    icono = Icons.Default.Euro
                ) {
                    PrecioRangeFilter(
                        rangoPrecio = rangoPrecioState,
                        rangoTotal = rangoPrecio,
                        onRangoChange = { min, max ->
                            rangoPrecioState = min to max
                            filtros = filtros.copy(
                                precioMinimo = if (min <= rangoPrecio.first) null else min,
                                precioMaximo = if (max >= rangoPrecio.second) null else max
                            )
                        }
                    )
                }

                // Duración
                FiltroSeccion(
                    titulo = "Duración",
                    icono = Icons.Default.Schedule
                ) {
                    DuracionFilter(
                        duracionSeleccionada = filtros.duracionMaxima,
                        onDuracionChange = { duracion ->
                            filtros = filtros.copy(duracionMaxima = duracion)
                        }
                    )
                }

                // Solo mejor valorados
                FiltroSeccion(
                    titulo = "Valoración",
                    icono = Icons.Default.Star
                ) {
                    MejorValoradosFilter(
                        soloMejorValorados = filtros.soloMejorValorados,
                        onSoloMejorValoradosChange = { soloMejorValorados ->
                            filtros = filtros.copy(soloMejorValorados = soloMejorValorados)
                        }
                    )
                }

                // Ordenar por
                FiltroSeccion(
                    titulo = "Ordenar por",
                    icono = Icons.Default.Sort
                ) {
                    OrdenarPorFilter(
                        ordenSeleccionado = filtros.ordenarPor,
                        onOrdenChange = { orden ->
                            filtros = filtros.copy(ordenarPor = orden)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //  Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onLimpiarFiltros()
                        filtros = FiltrosServicio()
                        rangoPrecioState = rangoPrecio.first to rangoPrecio.second
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Limpiar")
                }

                Button(
                    onClick = {
                        onAplicarFiltros(filtros)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aplicar")
                }
            }
        }
    }
}

// ✅ Componente para secciones de filtro
@Composable
private fun FiltroSeccion(
    titulo: String,
    icono: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

// ✅ Filtro de categorías
@Composable
private fun CategoriasFilter(
    categoriasDisponibles: List<String>,
    categoriasSeleccionadas: Set<String>,
    onCategoriaToggle: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categoriasDisponibles) { categoria ->
            val isSelected = categoria in categoriasSeleccionadas

            FilterChip(
                onClick = { onCategoriaToggle(categoria) },
                label = { Text(categoria) },
                selected = isSelected,
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// ✅ Filtro de rango de precio
@Composable
private fun PrecioRangeFilter(
    rangoPrecio: Pair<Double, Double>,
    rangoTotal: Pair<Double, Double>,
    onRangoChange: (Double, Double) -> Unit
) {
    val (minPrecio, maxPrecio) = rangoPrecio
    val (minTotal, maxTotal) = rangoTotal

    // Validar rango seguro para evitar errores Float
    val safeStart = min(minPrecio, maxPrecio).toFloat()
    val safeEnd = max(minPrecio, maxPrecio).toFloat()

    val correctedStart = min(safeStart, safeEnd)
    val correctedEnd = max(safeStart, safeEnd)

    Column {
        // Mostrar valores seleccionados
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Desde: ${String.format("%.2f", minPrecio)}€",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Hasta: ${String.format("%.2f", maxPrecio)}€",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // RangeSlider
        RangeSlider(
            value = correctedStart..correctedEnd,
            onValueChange = { range ->
                val start = range.start.coerceIn(minTotal.toFloat(), maxTotal.toFloat())
                val end = range.endInclusive.coerceIn(minTotal.toFloat(), maxTotal.toFloat())
                if (start <= end) {
                    onRangoChange(start.toDouble(), end.toDouble())
                }
            },
            valueRange = minTotal.toFloat()..maxTotal.toFloat(),
            steps = ((maxTotal - minTotal) / 5).roundToInt().coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Mostrar rango total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${String.format("%.2f", minTotal)}€",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${String.format("%.2f", maxTotal)}€",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// Filtro de duración
@Composable
private fun DuracionFilter(
    duracionSeleccionada: Int?,
    onDuracionChange: (Int?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DuracionOption.values().forEach { opcion ->
            val isSelected = duracionSeleccionada == opcion.value

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDuracionChange(opcion.value) }
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            Color.Transparent
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onDuracionChange(opcion.value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = opcion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Filtro de mejor valorados
@Composable
private fun MejorValoradosFilter(
    soloMejorValorados: Boolean,
    onSoloMejorValoradosChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSoloMejorValoradosChange(!soloMejorValorados) }
            .background(
                if (soloMejorValorados)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = soloMejorValorados,
            onCheckedChange = onSoloMejorValoradosChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Solo servicios mejor valorados",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (soloMejorValorados)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Mostrar únicamente servicios con 4+ estrellas",
                style = MaterialTheme.typography.bodySmall,
                color = if (soloMejorValorados)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ✅ Filtro de ordenamiento
@Composable
private fun OrdenarPorFilter(
    ordenSeleccionado: OrdenarServicioPor,
    onOrdenChange: (OrdenarServicioPor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OrdenarServicioPor.entries.forEach { opcion ->
            val isSelected = ordenSeleccionado == opcion

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOrdenChange(opcion) }
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            Color.Transparent
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onOrdenChange(opcion) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = opcion.icon,
                    contentDescription = null,
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = opcion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

//Funciones para obtener iconos y colores por categoría
private fun getCategoriaIcon(categoria: String): ImageVector {
    return when (categoria.lowercase()) {
        "peluquería" -> Icons.Default.ContentCut
        "estética" -> Icons.Default.Face
        "masajes" -> Icons.Default.Spa
        "uñas" -> Icons.Default.Brush
        "maquillaje" -> Icons.Default.Palette
        "barbería" -> Icons.Default.Person
        "depilación" -> Icons.Default.Waves
        "tratamientos faciales" -> Icons.Default.Face
        "tratamientos corporales" -> Icons.Default.Accessibility
        else -> Icons.Default.Spa
    }
}

@Composable
fun getCategoriaColor(categoria: String): Color {
    return when (categoria.lowercase()) {
        "peluquería" -> ThemeColors.info
        "estética" -> ThemeColors.success
        "masajes" -> ThemeColors.warning
        "uñas" -> ThemeColors.error
        "maquillaje" -> AppColors.GreenAccent
        "barbería" -> AppColors.GreenDark
        "depilación" -> AppColors.GreenSecondary
        "tratamientos faciales" -> AppColors.GreenMuted
        "tratamientos corporales" -> AppColors.GreenLight
        else -> MaterialTheme.colorScheme.onPrimary
    }
}

// Preview con datos de ejemplo
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewServicioTabContent() {
    FrontendappTheme {
        ServicioTabContent(
            servicioViewModel = FakeServicioViewModel(),
            navController = rememberNavController()
        )
    }
}
