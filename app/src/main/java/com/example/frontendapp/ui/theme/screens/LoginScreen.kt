package com.example.frontendapp.ui.theme.screens


import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty

import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
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
import com.example.frontendapp.data.remote.source.AuthRepo
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.frontendapp.data.model.UI.ERol
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.composables.modals.ErrorModal
import com.example.frontendapp.ui.theme.composables.text.TextNavigate
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel

private val TAG= "LoginScreen"
@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {
    val loginState by loginViewModel.loginState.collectAsState()
    val usuario by loginViewModel.usuarioState.collectAsState()
    val validationErrors by loginViewModel.validationErrors.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    var showModal by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val loginLoadingLog = stringResource(id = R.string.login_loading_log)
    val loginSuccessLogFormat = stringResource(id = R.string.login_success_log)
    val loginErrorToastFormat = stringResource(id = R.string.login_error_toast)
    val loginErrorLogFormat = stringResource(id = R.string.login_error_log)
    val loginValidationErrorsLogFormat = stringResource(id = R.string.login_validation_errors_log)

    ErrorModal(
        message = stringResource(id = R.string.blocked_user_modal_message), // String resource
        isVisible = showModal,
        onConfirm = {showModal=false},
        onDismiss = {showModal=false},
        title = stringResource(id = R.string.blocked_user_modal_title) // String resource
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                    .background(Principal_variacion3),
                contentAlignment = Alignment.Center
            ) {

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
                    label = stringResource(id = R.string.login_email_label), // String resource
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
                    label = stringResource(id = R.string.login_password_label), // String resource
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
            TextNavigate(
                texto = stringResource(id = R.string.login_no_account_text), // String resource
                navController = navController,
                destino = NavigationItem.REGISTER.route
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BtnStyle1(
                    onClick = {
                        loginViewModel.loginUsuario(
                            onLoading = {
                                isLoading = true
                                Log.d("Login", loginLoadingLog) // String resource
                            },
                            onSuccess = { result ->
                                isLoading = false
                                Log.d("Login", String.format(loginSuccessLogFormat, result)) // String resource with format
                                if(result.usuario.Bloqueado){
                                    showModal=true
                                    return@loginUsuario
                                }
                                RetrofitInstance.setToken(result.token)
                                Log.d(TAG, result.usuario.toString())
                                RetrofitInstance.setUsuario(result.usuario)
                                result.usuario.id?.let { RetrofitInstance.setUserId(it) }
                                // si esta autenticado, navegar a  las pantallas dependiendo del rol
                                if(result.usuario.isAutentificado){
                                    val roles = result.roles
                                    when {
                                        roles.contains(ERol.ADMIN.toString()) -> navController.navigate(NavigationItem.ADMIN_PANEL_SCREEN.route) { popUpTo(0) }
                                        roles.contains(ERol.CLIENTE.toString()) -> navController.navigate(NavigationItem.CLIENTE_MAIN_SCREEN.route)
                                        roles.contains(ERol.NEGOCIO.toString()) -> navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                                    }
                                } else {
                                    // Navegar a la pantalla de verificacion de gmail
                                    navController.navigate(NavigationItem.SEND_MAIL_SCREEN.route)
                                }
                                loginViewModel.reset()
                            },
                            onError = { error ->
                                isLoading = false
                                Toast.makeText(context, String.format(loginErrorToastFormat, error), Toast.LENGTH_SHORT).show() // String resource with format
                                Log.d("LoginScreen", String.format(loginErrorLogFormat, error)) // String resource with format
                            },
                            onValidationError = { errores ->
                                Log.d("LoginScreen", String.format(loginValidationErrorsLogFormat, errores)) // String resource with format
                            }
                        )
                    },
                    text = if (loginState is Resource.Loading) stringResource(id = R.string.login_button_loading_text) else stringResource(id = R.string.login_button_text), // String resource
                    icon = if (loginState is Resource.Loading) Icons.Default.HourglassEmpty else Icons.Default.VerifiedUser,
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FrontendappTheme {
        val usuario = LoginViewModel(AuthRepo(RetrofitInstance.userApi))
        LoginScreen(navController = rememberNavController(),usuario)
    }
}