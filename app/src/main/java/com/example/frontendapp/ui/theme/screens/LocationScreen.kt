package com.example.frontendapp.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.TopBarBussines
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun LocationScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopBarBussines()
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(40.4168, -3.7038), 10f) // Madrid, ejemplo
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = LatLng(40.4168, -3.7038)),
                    title = "Madrid",
                    snippet = "Capital de España"
                )
            }
        }
    }
}

@Composable
@Preview
fun LocationPreviewScreen(){
    FrontendappTheme {
        LocationScreen(navController = rememberNavController())
    }
}
