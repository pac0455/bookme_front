package com.example.frontendapp.ui.theme.screens

import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.example.frontendapp.R
import com.example.frontendapp.auth.GoogleAuthUiClient
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, viewModel: UsuarioViewModel = viewModel()) {

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
                    viewModel.onNombreChanged(nombre.value)
                    viewModel.onCorreoChanged(correo.value)
                    viewModel.onTelefonoChanged(telefono.value)
                    viewModel.onContrasenaChanged(contrasenia.value)
                    viewModel.registrarUsuario()

                } else {
                    Log.w("RegisterScreen", "Campos vacíos")
                    // Puedes usar un Snackbar o Toast si quieres mostrarlo al usuario
                }
            }) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.height(32.dp))

            GoogleButton(
                onGoogleTokenReceived = ::onGoogleTokenReceived,
                onError = ::onError
            )
        }
    }
}

@Composable
fun CustomTextField(
    icon: ImageVector,
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = label) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun GoogleButton(
    context: Context = LocalContext.current,
    onGoogleTokenReceived: (String) -> Unit,
    onError: (Throwable) -> Unit
) {
    Button(
        onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                GoogleAuthUiClient.launchSignIn(
                    context = context,
                    onSuccess = { idToken ->
                        Log.d("GoogleButton", "Token recibido: $idToken")
                        onGoogleTokenReceived(idToken)  // <- ahora sí está definido
                    },
                    onError = { error ->
                        Log.e("GoogleButton", "Error al hacer login con Google", error)
                        onError(error)  // <- ahora sí está definido
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_googleiconcolor),
            contentDescription = "Iniciar sesión con Google",
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Google", color = Color.Black)
    }
}



@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    FrontendappTheme {
        RegisterScreen(navController = rememberNavController())
    }
}
fun onGoogleTokenReceived(idToken: String) {
    Log.d("GoogleAuth", "Token recibido correctamente: $idToken")
}

fun onError(error: Throwable) {
    Log.e("GoogleAuth", "Error al iniciar sesión con Google", error)
}