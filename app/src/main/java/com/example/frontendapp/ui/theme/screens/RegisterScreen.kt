package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.text.TextNavigate
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel


@Composable
fun RegisterScreen(navController: NavController, usuarioViewModel: RegisterViewModel) {
    val context = LocalContext.current
    val uiState by usuarioViewModel.uiState.collectAsState()
    val validationState by usuarioViewModel.validationState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val nombreFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val telefonoFocus = remember { FocusRequester() }
    val contrasenaFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scroll = rememberScrollState()



    LaunchedEffect(Unit) {
        usuarioViewModel.resetUi()
    }
    //Resetear el estado cada vez que entr
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
                .verticalScroll(scroll)
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
                    modifier = Modifier.focusRequester(nombreFocus),
                    icon = Icons.Default.Person,
                    label = "Nombre",
                    value = uiState.username ?: "",
                    onValueChange = {
                        val cleaned = it.filterNot { c -> c.isWhitespace() } // Elimina espacios, tabs y saltos de línea
                        usuarioViewModel.setNombre(cleaned)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { emailFocus.requestFocus() }),
                    errorMessage = validationState.data?.errors?.get("username")
                )

                CustomTextField(
                    modifier = Modifier.focusRequester(emailFocus),
                    icon = Icons.Default.Email,
                    label = "Correo",
                    value = uiState.email ?: "",
                    onValueChange = {
                        usuarioViewModel.setCorreo(it.filterNot { c -> c == '\n' || c == '\t' })
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { telefonoFocus.requestFocus() }),
                    errorMessage = validationState.data?.errors?.get("email")
                )

                CustomTextField(
                    modifier = Modifier.focusRequester(telefonoFocus),
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = uiState.phoneNumber ?: "",
                    onValueChange = { input ->
                        val cleaned = input.filterNot { c -> c == '\n' || c == '\t' }
                        if (cleaned.length <= 9 && cleaned.all { it.isDigit() }) {
                            usuarioViewModel.setTelefono(cleaned)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { contrasenaFocus.requestFocus() }),
                    errorMessage = validationState.data?.errors?.get("phoneNumber")
                )

                CustomTextField(
                    modifier = Modifier.focusRequester(contrasenaFocus),
                    icon = Icons.Default.RemoveRedEye,
                    label = "Contraseña",
                    value = uiState.password ?: "",
                    isPassword = true,
                    onValueChange = {
                        val cleaned = it.filterNot { c -> c.isWhitespace() }
                        usuarioViewModel.setContrasena(cleaned)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    errorMessage = validationState.data?.errors?.get("password")
                )

               TextNavigate("¿Ya tientes cuenta? Logueate",navController, NavigationItem.LOGIN.route)
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
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    FrontendappTheme {
        val usuario= RegisterViewModel(AuthRepo(RetrofitInstance.userApi))
        RegisterScreen(navController = rememberNavController(),usuario)
    }
}
