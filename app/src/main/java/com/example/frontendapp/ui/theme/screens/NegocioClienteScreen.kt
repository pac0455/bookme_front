package com.example.frontendapp.ui.theme.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.Btn.MySwitch
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.frontendapp.data.model.UI.ERol
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.ui.theme.composables.Btn.IconPosition
import com.example.frontendapp.ui.theme.navigation.NavigationItem


@Composable
fun NegocioClienteScrenn(navController: NavController, registerViewModel: RegisterViewModel){
    var isBusiness by remember { mutableStateOf(false) }
    val registerState by registerViewModel.registerState.collectAsState()

    var isClient by remember { mutableStateOf(true) }
    //Detectar el tamaño de la pantalla y en base a eso ajustar el tamaño de la imagen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val imageSize = screenWidth * 1f
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        navController.navigate(NavigationItem.LOGIN.route) {
            popUpTo(0) // Limpia todo el back stack
        }
    }

    LaunchedEffect(Unit) {
        val token = RetrofitInstance.getToken()
        val roles = RetrofitInstance.getRoles()
        if (!token.isNullOrBlank()) {
            when {
                roles.contains(ERol.CLIENTE.toString()) -> navController.navigate(NavigationItem.CLIENTE_MAIN_SCREEN.route) { popUpTo(0) }
                roles.contains(ERol.NEGOCIO.toString()) -> navController.navigate(NavigationItem.BUSSINES_MAIN.route) { popUpTo(0) }
            }
        }
    }


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

            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,

        ) {


            Image(
                painter = painterResource(id = R.drawable.ic_shop),
                contentDescription = "Logo",
                modifier = Modifier.size(imageSize)
            )

            MySwitch(
                text = "Soy un cliente",
                checked = isClient,
                onCheckedChange = {
                    isClient = !isClient
                    isBusiness = !isBusiness
                }
            )

            MySwitch(
                text = "Soy un negocio",
                checked = isBusiness,
                onCheckedChange = {
                    isBusiness = !isBusiness
                    isClient = !isClient
                }
            )


            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                BtnStyle1(
                    onClick = {
                        // Log de los datos del usuario que se está enviando
                        Log.d("NegocioClienteScrenn", "Registrando usuario:" +
                                " Nombre=${registerViewModel.uiState.value.username}," +
                                " Email=${registerViewModel.uiState.value.email}," +
                                " Teléfono=${registerViewModel.uiState.value.phoneNumber}, EsNegocio=$isBusiness")

                        registerViewModel.setEsNegocio(isBusiness)
                        registerViewModel.registrarUsuario(
                            onLoading = { isLoading = true },
                            onSuccess = { result ->
                                isLoading = false
                                // Verificar si el token no es nulo antes de establecerlo
                                result.token.let { token ->
                                    Log.d("NegocioClienteScrenn", "Token recibido: $token")
                                    RetrofitInstance.setToken(token)
                                }
                                result.usuario.id?.let { RetrofitInstance.setUserId(it) }
                                val roles = result.roles
                                when {
                                    roles.contains(ERol.CLIENTE.toString()) -> navController.navigate(NavigationItem.CLIENTE_MAIN_SCREEN.route)
                                    roles.contains(ERol.NEGOCIO.toString()) -> navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                                }
                            },
                            onError = { errorMsg ->
                                isLoading = false
                                Log.d("NegocioClienteScrenn", "Error en el registro: $errorMsg")
                                Toast.makeText(context, "Ha habido algún error a la hora de registrarse", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    iconPosition = IconPosition.END,
                    text = if (isLoading) "Cargando..." else "Registrarse",
                    icon = if (isLoading) Icons.Default.HourglassEmpty else Icons.Default.PersonAdd
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewNegocioClienteScrenn(){
    FrontendappTheme {
        val navController = rememberNavController()
        val usuarioViewModel = RegisterViewModel(AuthRepo(RetrofitInstance.userApi))
        NegocioClienteScrenn(navController, usuarioViewModel)
    }
}