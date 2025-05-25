package com.example.frontendapp.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapaScreen(navController: NavController, negocioViewModel: NegocioViewModel) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 15f)
    }

    val uiSettings = remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    var ubicacion by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        println("MapaScreen: Comprobando permisos de ubicación")
        val permisoConcedido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!permisoConcedido) {
            println("MapaScreen: Permiso de ubicación NO concedido")
            return@LaunchedEffect
        }
        println("MapaScreen: Permiso de ubicación concedido")

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val nuevaUbicacion = LatLng(it.latitude, it.longitude)
                ubicacion = nuevaUbicacion
                println("MapaScreen: Ubicación actual obtenida: $nuevaUbicacion")
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(nuevaUbicacion, 15f))
            } ?: run {
                println("MapaScreen: Ubicación no disponible")
                Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings.value,
            onMapClick = { latLng ->
                selectedLocation = latLng
                println("MapaScreen: Usuario seleccionó ubicación: $latLng")
            }
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Ubicación seleccionada"
                )
            }
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier=Modifier.fillMaxSize()
        ) {
            BtnStyle1(
                modifier = Modifier.padding(bottom = 82.dp),
                icon = Icons.Default.Check,
                onClick = {
                    println("MapaScreen: Botón aceptar pulsado")
                    selectedLocation?.let {
                        println("MapaScreen: Ubicación seleccionada confirmada: $it")
                        negocioViewModel.setUbicacion(it.latitude, it.longitude)
                        navController.popBackStack()
                        println("MapaScreen: Navegando atrás con popBackStack()")
                    } ?: run {
                        println("MapaScreen: No hay ubicación seleccionada, mostrando Toast")
                        Toast.makeText(context, "Selecciona un punto primero", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}