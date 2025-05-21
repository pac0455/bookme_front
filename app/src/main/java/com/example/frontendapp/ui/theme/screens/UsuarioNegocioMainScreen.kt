package com.example.frontendapp.ui.theme.screens

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.TopBarBussines
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import androidx.core.content.ContextCompat
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.composables.NegocioList
import com.example.frontendapp.ui.theme.viewmodels.BussinesMainViewModel

@Composable
fun UsuarioNegocioMainScreen(
    navController: NavController,
    bussinesMainViewModel: BussinesMainViewModel
) {
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Si no se permite la ubicación no se podrá registrar un negocio", Toast.LENGTH_LONG).show()
        }

        val permisoConcedido = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        navController.navigate(NavigationItem.LOCATION.route)
    }

    Scaffold(
        topBar = {
            TopBarBussines()
        },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(100.dp),
                containerColor = Principal_variacion3
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Seleccionar ubicación",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                BtnStyle1(
                    Modifier.fillMaxWidth(0.8f),
                    onClick = { /* lógica de configuración */ },
                    text = "Configuración",
                    icon = Icons.Default.Settings,
                    horizontalAlignment = Alignment.Start
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text("Locales")
            }

            // Aquí se llama a tu listado dinámico
            NegocioList(bussinesMainViewModel = bussinesMainViewModel, navController = navController)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MAinBussinesingPreview() {
    FrontendappTheme {
        UsuarioNegocioMainScreen(navController = rememberNavController(), BussinesMainViewModel(
            NegocioRemoteSource(RetrofitInstance.negocioApi)
        ))
    }
}