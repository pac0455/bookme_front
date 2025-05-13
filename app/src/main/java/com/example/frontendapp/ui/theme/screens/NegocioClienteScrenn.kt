package com.example.frontendapp.ui.theme.screens

import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.MySwitch
import com.example.frontendapp.ui.theme.viewmodels.registerViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import com.example.frontendapp.data.model.ERol
import com.example.frontendapp.data.model.LoginRegisterResultDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.Resource
import com.example.frontendapp.ui.theme.navigation.NavigationItem


@Composable
fun NegocioClienteScrenn(navController: NavController, registerViewModel: registerViewModel){
    var isBusiness by remember { mutableStateOf(false) }
    val registerState by registerViewModel.registerState.collectAsState()

    var isClient by remember { mutableStateOf(true) }
    //Detectar el tamaño de la pantalla y en base a eso ajustar el tamaño de la imagen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val imageSize = screenWidth * 1f
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
                    isClient = true
                    isBusiness = false
                }
            )

            MySwitch(
                text = "Soy un negocio",
                checked = isBusiness,
                onCheckedChange = {
                    isBusiness = true
                    isClient = false
                }
            )


            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                BtnStyle1(onClick = {
                    if (isBusiness) {
                        registerViewModel.registrarNegocio()
                    } else {
                        registerViewModel.registrarCliente()
                    }
                }, text = "Registrarse")
                // Manejo del resultado del registro
                when (val result = registerState) {
                    is Resource.Success -> {
                        val roles = result.data?.roles ?: emptyList()
                        when {
                            roles.contains(ERol.CLIENTE.toString()) -> navController.navigate(NavigationItem.MAIN.route)
                            roles.contains(ERol.NEGOCIO.toString()) -> navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                        }
                    }
                    is Resource.Error -> {
                        val errorMessage = result.message ?: "Error desconocido"
                        // Aquí puedes mostrar un Snackbar, Dialog o Log
                        println("Error en el registro: $errorMessage")
                    }
                    else -> {}
                }
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewNegocioClienteScrenn(){
    FrontendappTheme {
        val navController = rememberNavController()
        val usuarioViewModel = registerViewModel(AuthRemoteDataResource(RetrofitInstance.api))
        NegocioClienteScrenn(navController, usuarioViewModel)
    }
}