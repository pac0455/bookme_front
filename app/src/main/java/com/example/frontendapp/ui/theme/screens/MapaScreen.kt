package com.example.frontendapp.ui.theme.screens

import android.util.Log
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
import androidx.navigation.NavController
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.utils.UbicacionHelper
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
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isUbicacionCargada by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 15f)
    }

    val uiSettings = remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }

    // Obtener ubicación al cargar el composable
    LaunchedEffect(Unit) {
        Log.d("MapaScreen", "Intentando obtener ubicación actual...")
        val ubicacion = UbicacionHelper.obtenerUbicacionActual(context)

        if (ubicacion.latitud != null && ubicacion.longitud != null) {
            val ubicacionActual = LatLng(ubicacion.latitud, ubicacion.longitud)
            selectedLocation = LatLng(ubicacionActual.latitude, ubicacionActual.longitude)
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 15f))
            isUbicacionCargada = true

            Log.d("MapaScreen", "Ubicación obtenida: $ubicacionActual")
        } else {
            Toast.makeText(context, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show()
            Log.w("MapaScreen", "Ubicación actual es nula.")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings.value,
            onMapClick = { latLng ->
                    selectedLocation = latLng
                    Log.d("MapaScreen", "Ubicación seleccionada manualmente: $latLng")
            }
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Ubicación seleccionada"
                )
            }
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            BtnStyle1(
                modifier = Modifier.padding(bottom = 82.dp),
                icon = Icons.Default.Check,
                enabled = isUbicacionCargada,
                text = "Aceptar",
                onClick = {
                    selectedLocation?.let {
                        negocioViewModel.setUbicacion(it.latitude, it.longitude)
                        navController.popBackStack()
                        Log.d("MapaScreen", "Ubicación guardada y regresando: $it")
                    } ?: run {
                        Toast.makeText(context, "Selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show()
                        Log.w("MapaScreen", "Intento guardar sin ubicación seleccionada.")
                    }
                }
            )
        }
    }
}