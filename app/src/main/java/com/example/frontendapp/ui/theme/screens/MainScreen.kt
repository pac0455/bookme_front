package com.example.frontendapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Principal_variacion3,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Bookme")
                }
            )
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
            Text(text = "Crear tu cuenta", fontSize= 34.sp)
            Spacer(modifier = Modifier.height(20.dp))

            //Detectar el tamaño de la pantalla y en base a eso ajustar el tamaño de la imagen
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val imageSize = screenWidth * 1f

            Image(
                painter = painterResource(id = R.drawable.ic_shop),
                contentDescription = "Logo",
                modifier = Modifier.size(imageSize)
            )
            Text(text = "y descubre", fontSize= 34.sp, modifier =  Modifier.padding(bottom = 24.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado entre elementos
            ) {
                Button(onClick = {navController.navigate(NavigationItem.Register.route) }){
                    Text(text = "Comenzar ahora")
                    Icon(painter = painterResource(R.drawable.bootstrap_arrow_right),
                        contentDescription = "Arrow",
                        modifier = Modifier.padding(start = 25.dp)
                    )
                }
                Button(onClick = {navController.navigate(NavigationItem.Login.route) }){
                    Text(text = "Ya tengo cuenta")
                    Icon(painter = painterResource(R.drawable.bootstrap_arrow_right),
                        contentDescription = "Arrow",
                        modifier = Modifier.padding(start = 25.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FrontendappTheme {
        MainScreen(navController = rememberNavController())
    }
}