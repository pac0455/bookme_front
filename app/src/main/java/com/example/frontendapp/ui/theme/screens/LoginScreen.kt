package com.example.frontendapp.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.navigation.NavigationItem
import com.example.frontendapp.ui.theme.Composables.CustomTextField
import com.example.frontendapp.ui.theme.Composables.GoogleButton
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3



@Composable
fun LoginScreen(navController: NavController){
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val nombre = remember { mutableStateOf("") }
            val correo = remember { mutableStateOf("") }
            val telefono = remember { mutableStateOf("") }
            val contrasenia = remember { mutableStateOf("") }

            CustomTextField(Icons.Default.AccountCircle, "Nombre", nombre.value) { nombre.value = it }
            CustomTextField(Icons.Default.Email, "Correo", correo.value) { correo.value = it }
            CustomTextField(Icons.Default.Phone, "Teléfono", telefono.value) { telefono.value = it }
            CustomTextField(Icons.Default.Lock, "Contraseña", contrasenia.value, isPassword = true) { contrasenia.value = it }

            Button(onClick = {
                if (nombre.value.isNotBlank() &&
                    correo.value.isNotBlank() &&
                    telefono.value.isNotBlank() &&
                    contrasenia.value.isNotBlank()) {



                } else {
                    Log.w("RegisterScreen", "Campos vacíos")
                    // Puedes usar un Snackbar o Toast si quieres mostrarlo al usuario
                }
            }) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.height(32.dp))

            val context = LocalContext.current

            GoogleButton(
                context = context,
                onGoogleTokenReceived = { idToken ->
                    // Aquí haces algo con el idToken, por ejemplo pasarlo a Firebase
                    Log.d("LoginScreen", "Token recibido: $idToken")
                },
                onError = { error ->
                    // Maneja el error aquí, por ejemplo mostrando un Toast
                    Log.e("LoginScreen", "Error de Google Sign-In", error)
                }
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FrontendappTheme {
        LoginScreen(navController = rememberNavController())
    }
}