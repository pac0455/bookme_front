package com.example.frontendapp.ui.theme.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapaScreen(navController: NavController) {
    val negocioViewModel: NegocioViewModel = viewModel(navController.currentBackStackEntry!!)
    val uiSettings = remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }

    var selectedLocation = remember { mutableStateOf<LatLng?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            uiSettings = uiSettings.value,
            onMapClick = { latLng ->
                selectedLocation.value = latLng
                negocioViewModel.setUbicacion(latLng.latitude, latLng.longitude)
                navController.popBackStack() // volver a la pantalla anterior
            }
        ) {
            selectedLocation.value?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Ubicación seleccionada"
                )
            }
        }
    }
}
