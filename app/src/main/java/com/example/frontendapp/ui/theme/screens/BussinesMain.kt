package com.example.frontendapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.TopBarBussines
import com.example.frontendapp.ui.theme.composables.onClick
import com.example.frontendapp.ui.theme.navigation.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BussinesMainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopBarBussines()
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (horizontalArrangement = Arrangement.Start){
                BtnStyle1(
                    Modifier.fillMaxWidth(0.8f),
                    onClick = {},
                    text = "Configuración",
                    icon = Icons.Default.Settings,
                    horizontalAlignment = Alignment.Start)
            }
            Row(Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.Start) { Text("Locales") }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // margen desde el borde
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { 
                        navController.navigate(NavigationItem.LOCATION.route)
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

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MAinBussinesingPreview() {
    FrontendappTheme {
        BussinesMainScreen(navController = rememberNavController())
    }
}