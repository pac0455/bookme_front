package com.example.frontendapp.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.GoogleButton
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel


@Composable
fun RegisterScreen(navController: NavController, usuarioViewModel:  UsuarioViewModel) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                    .background(Principal_variacion3),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val nombre = remember { mutableStateOf("") }
            val correo = remember { mutableStateOf("") }
            val telefono = remember { mutableStateOf("") }
            val contrasenia = remember { mutableStateOf("") }
            //Seccion de inputs
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                CustomTextField(Icons.Default.AccountCircle, "Nombre", nombre.value) { nombre.value = it }
                CustomTextField(Icons.Default.Email, "Correo", correo.value) { correo.value = it }
                CustomTextField(Icons.Default.Phone, "Teléfono", telefono.value) { telefono.value = it }
                CustomTextField(Icons.Default.Lock, "Contraseña", contrasenia.value, isPassword = true) { contrasenia.value = it }
            }
            //Seccion de buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BtnStyle1(onClick = { OnclickRegister() }, text = "Registarse")
                val indicatorWidth = remember { mutableStateOf(1f) } // Grosor de la línea
                Column(
                    modifier = Modifier.padding(16.dp),// Añadiendo un margen de 16dp
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Borde superior
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(indicatorWidth.value.dp) // Altura del borde superior
                            .border(BorderStroke(indicatorWidth.value.dp, Color.Black))
                    )
                    // Contenido central
                    Box(modifier = Modifier.padding(vertical = 8.dp)) { Text(text = "Tambien puedes registrarte con ...") }
                    // Borde inferior
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(indicatorWidth.value.dp) // Altura del borde inferior
                            .border(BorderStroke(indicatorWidth.value.dp, Color.Black))
                    )
                }
                GoogleButton(context)
            }
        }
    }
}
fun OnclickRegister(){
    // Aquí puedes manejar el evento de clic para el botón de registro
    Log.d("RegisterScreen", "Botón de registro clicado")
    // Aquí puedes agregar la lógica para registrar al usuario
}
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    FrontendappTheme {
        val usuario= UsuarioViewModel()
        RegisterScreen(navController = rememberNavController(),usuario)
    }
}
