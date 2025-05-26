package com.example.frontendapp.ui.theme.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassEmpty

import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.VerifiedUser

import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.frontendapp.ui.theme.composables.CustomBox
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.Btn.GoogleButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import com.example.frontendapp.data.model.ERol
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.composables.text.TextNavigate
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {
    val loginState by loginViewModel.loginState.collectAsState()
    val usuario by loginViewModel.usuarioState.collectAsState()
    val validationErrors by loginViewModel.validationErrors.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current


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
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomTextField(
                    icon = Icons.Default.Person,
                    label = "Correo electrónico",
                    value = usuario.email.orEmpty(),
                    onValueChange = { loginViewModel.setEmail(it) },
                    errorMessage = validationErrors["email"],
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        passwordFocusRequester.requestFocus()
                    }),
                    modifier = Modifier.focusRequester(emailFocusRequester)
                )

                CustomTextField(
                    icon = Icons.Default.Lock,
                    label = "Contraseña",
                    value = usuario.password.orEmpty(),
                    isPassword = true,
                    onValueChange = { loginViewModel.setPassword(it) },
                    errorMessage = validationErrors["password"],
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onNext = {
                        emailFocusRequester.requestFocus()
                    }),
                    modifier = Modifier.focusRequester(passwordFocusRequester)
                )
            }
            TextNavigate("¿No tienes cuenta? Registrate",navController, NavigationItem.REGISTER.route)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BtnStyle1(
                    onClick = {
                        loginViewModel.loginUsuario(
                            onLoading = {
                                isLoading = true
                                Log.d("Login", "Cargando...")
                            },
                            onSuccess = { result ->
                                isLoading = false
                                Log.d("Login", "Éxito: $result")
                                RetrofitInstance.setToken(result.token)
                                val roles = result.roles
                                when {
                                    roles.contains("CLIENTE") -> navController.navigate(NavigationItem.CLIENTE_MAIN_SCREEN.route)
                                    roles.contains("NEGOCIO") -> navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                                }
                                loginViewModel.reset()
                            },
                            onError = { error ->
                                isLoading = false
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                Log.d("LoginScreen", error)
                            },
                            onValidationError = { errores ->
                                Log.d("LoginScreen", "Errores de validación: $errores")
                            }
                        )
                    },
                    text = if (loginState is Resource.Loading) "Cargando..." else "Iniciar Sesión",
                    icon = if (loginState is Resource.Loading) Icons.Default.HourglassEmpty else Icons.Default.VerifiedUser,
                )

                CustomBox(
                    borderTop = true,
                    borderBottom = true,
                    msg = "o"
                )

                GoogleButton(context)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FrontendappTheme {
        val usuario = LoginViewModel(AuthRemoteDataResource(RetrofitInstance.userApi))
        LoginScreen(navController = rememberNavController(),usuario)
    }
}