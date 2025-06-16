package com.example.frontendapp.ui.theme.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapp.data.model.Negocio.Ubicacion
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.utils.UbicacionHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Properties

data class LocationInfo(
    val address: String = "Ubicación seleccionada",
    val coordinates: String = "",
    val isCurrentLocation: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreenMejorada(
    navController: NavController? = null,
    negocioViewModel: NegocioViewModel? = null,
    onLocationSelected: ((Double, Double) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isUbicacionCargada by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var locationInfo by remember { mutableStateOf(LocationInfo()) }
    var showLocationCard by remember { mutableStateOf(false) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val coroutineScope = rememberCoroutineScope()

    // Observar el estado del negocio para obtener ubicación guardada
    val negocioState by negocioViewModel?.negocioState?.collectAsState() ?: remember { mutableStateOf(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 15f)
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false
            )
        )
    }

    // Animaciones
    val fabScale by animateFloatAsState(
        targetValue = if (isUbicacionCargada) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_scale"
    )

    // Función helper para crear Ubicacion desde coordenadas
    fun createUbicacion(lat: Double?, lng: Double?): Ubicacion? {
        return if (lat != null && lng != null) {
            Ubicacion(latitud = lat, longitud = lng)
        } else null
    }

    // Obtener ubicación al cargar con prioridades correctas
    LaunchedEffect(Unit) {
        Log.d("MapaScreen", "Iniciando obtención de ubicación...")

        // 1. Verificar si ya existe una ubicación guardada en el ViewModel
        val negocio = negocioState
        val ubicacionGuardada = if (negocio?.latitud != null && negocio.longitud != null) {
            createUbicacion(negocio.latitud, negocio.longitud)
        } else null

        if (ubicacionGuardada != null) {
            // Usar ubicación guardada del negocio
            val ubicacionExistente = LatLng(ubicacionGuardada.latitud!!, ubicacionGuardada.longitud!!)
            selectedLocation = ubicacionExistente
            locationInfo = LocationInfo(
                address = "Ubicación guardada del negocio",
                coordinates = "${String.format("%.6f", ubicacionExistente.latitude)}, ${String.format("%.6f", ubicacionExistente.longitude)}",
                isCurrentLocation = false
            )

            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(ubicacionExistente, 16f),
                durationMs = 1000
            )

            isUbicacionCargada = true
            showLocationCard = true
            Log.d("MapaScreen", "Ubicación guardada del negocio cargada: $ubicacionExistente")

        } else {
            // 2. Intentar obtener ubicación actual
            try {
                val ubicacion = UbicacionHelper.obtenerUbicacionActual(context)

                if (ubicacion.latitud != null && ubicacion.longitud != null) {
                    val ubicacionActual = LatLng(ubicacion.latitud, ubicacion.longitud)
                    selectedLocation = ubicacionActual
                    locationInfo = LocationInfo(
                        address = "Tu ubicación actual",
                        coordinates = "${String.format("%.6f", ubicacionActual.latitude)}, ${String.format("%.6f", ubicacionActual.longitude)}",
                        isCurrentLocation = true
                    )

                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(ubicacionActual, 16f),
                        durationMs = 1000
                    )

                    isUbicacionCargada = true
                    showLocationCard = true
                    Log.d("MapaScreen", "Ubicación actual obtenida: $ubicacionActual")

                } else {
                    // 3. Usar ubicación por defecto (Madrid)
                    val ubicacionDefecto = LatLng(40.4168, -3.7038)
                    selectedLocation = ubicacionDefecto
                    locationInfo = LocationInfo(
                        address = "Ubicación por defecto (Madrid)",
                        coordinates = "${String.format("%.6f", ubicacionDefecto.latitude)}, ${String.format("%.6f", ubicacionDefecto.longitude)}",
                        isCurrentLocation = false
                    )

                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(ubicacionDefecto, 12f),
                        durationMs = 1000
                    )

                    isUbicacionCargada = true
                    showLocationCard = true
                    Toast.makeText(context, "Usando ubicación por defecto. Toca el mapa para seleccionar tu ubicación.", Toast.LENGTH_LONG).show()
                    Log.d("MapaScreen", "Usando ubicación por defecto: $ubicacionDefecto")
                }
            } catch (e: Exception) {
                // En caso de error, usar ubicación por defecto
                val ubicacionDefecto = LatLng(40.4168, -3.7038)
                selectedLocation = ubicacionDefecto
                locationInfo = LocationInfo(
                    address = "Ubicación por defecto (Madrid)",
                    coordinates = "${String.format("%.6f", ubicacionDefecto.latitude)}, ${String.format("%.6f", ubicacionDefecto.longitude)}",
                    isCurrentLocation = false
                )

                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(ubicacionDefecto, 12f),
                    durationMs = 1000
                )

                isUbicacionCargada = true
                showLocationCard = true
                Toast.makeText(context, "Error al obtener ubicación. Usando ubicación por defecto.", Toast.LENGTH_LONG).show()
                Log.e("MapaScreen", "Error al obtener ubicación: ${e.message}")
            }
        }

        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = MapProperties(
                mapType = mapType
            ),
            onMapClick = { latLng ->
                selectedLocation = latLng
                locationInfo = LocationInfo(
                    address = "Ubicación personalizada",
                    coordinates = "${String.format("%.6f", latLng.latitude)}, ${String.format("%.6f", latLng.longitude)}",
                    isCurrentLocation = false
                )
                showLocationCard = true
                Log.d("MapaScreen", "Ubicación seleccionada manualmente: $latLng")
            }
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = locationInfo.address,
                    snippet = locationInfo.coordinates
                )
            }
        }

        // Overlay de carga
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        // Header con controles
        TopControlsSection(
            mapType = mapType,
            onMapTypeChanged = { mapType = it },
            onBackPressed = { navController?.popBackStack() }
        )

        // Controles laterales
        SideControlsSection(
            modifier = Modifier.align(Alignment.CenterEnd),
            onMyLocationClick = {

                selectedLocation?.let { location ->
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(location, 16f),
                            durationMs = 800
                        )
                    }
                }
            },
            onZoomIn = {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.zoomIn(),
                        durationMs = 300
                    )
                }

            },
            onZoomOut = {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.zoomOut(),
                        durationMs = 300
                    )
                }
            }

        )

        // Card de información de ubicación
        AnimatedVisibility(
            visible = showLocationCard && selectedLocation != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            LocationInfoCard(
                locationInfo = locationInfo,
                onDismiss = { showLocationCard = false },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 100.dp)
            )
        }

        // FAB de confirmación
        AnimatedVisibility(
            visible = selectedLocation != null,
            enter = scaleIn(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
            exit = scaleOut(),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            ConfirmLocationFAB(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 16.dp),
                scale = fabScale,
                onClick = {
                    selectedLocation?.let { location ->
                        // Si hay un callback personalizado, usarlo
                        if (onLocationSelected != null) {
                            onLocationSelected(location.latitude, location.longitude)
                            Toast.makeText(context, "Ubicación seleccionada correctamente", Toast.LENGTH_SHORT).show()
                            navController?.popBackStack()
                            Log.d("MapaScreen", "Ubicación seleccionada via callback: $location")
                        } else {
                            // Usar el método setUbicacion del NegocioViewModel
                            negocioViewModel?.setUbicacion(location.latitude, location.longitude)
                            Toast.makeText(context, "Ubicación guardada correctamente", Toast.LENGTH_SHORT).show()
                            navController?.popBackStack()
                            Log.d("MapaScreen", "Ubicación guardada en NegocioViewModel: $location")
                        }
                    } ?: run {
                        Toast.makeText(context, "Por favor, selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show()
                        Log.w("MapaScreen", "Intento guardar sin ubicación seleccionada.")
                    }
                }
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        Text(
            text = "Obteniendo tu ubicación...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TopControlsSection(
    mapType: MapType,
    onMapTypeChanged: (MapType) -> Unit,
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de regreso
        FloatingActionButton(
            onClick = onBackPressed,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                modifier = Modifier.size(24.dp)
            )
        }

        // Selector de tipo de mapa
        MapTypeSelector(
            currentMapType = mapType,
            onMapTypeChanged = onMapTypeChanged
        )
    }
}

@Composable
private fun MapTypeSelector(
    currentMapType: MapType,
    onMapTypeChanged: (MapType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FloatingActionButton(
            onClick = { expanded = true },
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = "Tipo de mapa",
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
        ) {
            val mapTypes = listOf(
                MapType.NORMAL to "Normal",
                MapType.SATELLITE to "Satélite",
                MapType.HYBRID to "Híbrido",
                MapType.TERRAIN to "Terreno"
            )

            mapTypes.forEach { (type, name) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = name,
                                color = if (type == currentMapType)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            if (type == currentMapType) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onMapTypeChanged(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SideControlsSection(
    modifier: Modifier = Modifier,
    onMyLocationClick: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mi ubicación
        FloatingActionButton(
            onClick = onMyLocationClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Mi ubicación",
                modifier = Modifier.size(24.dp)
            )
        }

        // Zoom in
        FloatingActionButton(
            onClick = onZoomIn,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Acercar",
                modifier = Modifier.size(24.dp)
            )
        }

        // Zoom out
        FloatingActionButton(
            onClick = onZoomOut,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Alejar",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LocationInfoCard(
    locationInfo: LocationInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (locationInfo.isCurrentLocation)
                            Icons.Default.MyLocation else Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = locationInfo.address,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = locationInfo.coordinates,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (locationInfo.isCurrentLocation) {
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Esta es tu ubicación actual",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmLocationFAB(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .shadow(8.dp, CircleShape)
            .clip(CircleShape),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Confirmar ubicación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapaScreenMejoradaPreview() {
    FrontendappTheme {
        MapaScreenMejorada()
    }
}
