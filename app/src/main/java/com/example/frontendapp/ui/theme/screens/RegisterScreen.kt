package com.example.frontendapp.ui.theme.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.Btn.GoogleButton
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel


@Composable
fun RegisterScreen(navController: NavController, usuarioViewModel: RegisterViewModel) {
    val context = LocalContext.current
    val uiState by usuarioViewModel.uiState.collectAsState()
    val validationState by usuarioViewModel.validationState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

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
            // Sección de inputs
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CustomTextField(
                    icon = Icons.Default.Person,
                    label = "Nombre",
                    value = uiState.username ?: "",
                    onValueChange = {
                        Log.d("RegisterScreen", "Nombre actualizado: $it")
                        usuarioViewModel.setNombre(it)
                    },
                    errorMessage = validationState.data?.errors?.get("username")
                )
                CustomTextField(
                    icon = Icons.Default.Email,
                    label = "Correo",
                    value = uiState.email ?: "",
                    onValueChange = {
                        Log.d("RegisterScreen", "Correo actualizado: $it")
                        usuarioViewModel.setCorreo(it)
                    },
                    errorMessage = validationState.data?.errors?.get("email")
                )
                CustomTextField(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = uiState.phoneNumber ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    onValueChange = { input ->
                        if (input.length <= 9 && input.all { it.isDigit() }) {
                            Log.d("RegisterScreen", "Teléfono actualizado: $input")
                            usuarioViewModel.setTelefono(input)
                        } else {
                            Log.d("RegisterScreen", "Teléfono inválido: $input")
                        }
                    },
                    errorMessage = validationState.data?.errors?.get("phoneNumber")
                )
                CustomTextField(
                    icon = Icons.Default.RemoveRedEye,
                    label = "Contraseña",
                    value = uiState.password ?: "",
                    isPassword = true,
                    onValueChange = {
                        Log.d("RegisterScreen", "Contraseña actualizada")
                        usuarioViewModel.setContrasena(it)
                    },
                    errorMessage = validationState.data?.errors?.get("password")
                )
            }

            // Sección de botones
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BtnStyle1(
                    text = if (isLoading) "Cargando..." else "Siguiente",
                    icon = if (isLoading) Icons.Default.HourglassEmpty else Icons.Default.ArrowForwardIos,
                    onClick = {
                        Log.d("RegisterScreen", "Botón 'Siguiente' presionado")
                        usuarioViewModel.validateRegistration(
                            onLoading = {
                                Log.d("RegisterScreen", "Validación iniciada")
                                isLoading = true
                            },
                            onSuccess = { validationResponse ->
                                Log.d("RegisterScreen", "Validación exitosa: $validationResponse")
                                isLoading = false
                                if (validationResponse.success) {
                                    navController.navigate(NavigationItem.NEGOCIO_CLIENTE.route)
                                }
                            },
                            onError = { errorMessage ->
                                Log.e("RegisterScreen", "Error en la validación: $errorMessage")
                                isLoading = false
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                )
                val indicatorWidth = remember { mutableStateOf(1f) } // Grosor de la línea
                Column(
                    modifier = Modifier.padding(16.dp), // Añadiendo un margen de 16dp
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
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = "También puedes registrarte con ...")
                    }
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    FrontendappTheme {
        val usuario= RegisterViewModel(AuthRemoteDataResource(RetrofitInstance.userApi))
        RegisterScreen(navController = rememberNavController(),usuario)
    }
}
