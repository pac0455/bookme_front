package com.example.frontendapp.ui.theme.screens


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
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold

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
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomBox
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.GoogleButton
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
import androidx.compose.runtime.getValue

@Composable
fun LoginScreen(navController: NavController, usuarioViewModel: UsuarioViewModel){
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
            horizontalAlignment = Alignment.CenterHorizontally,


            ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                val usuario by usuarioViewModel.uiState.collectAsState()

                CustomTextField(
                    icon = Icons.Default.Person,
                    label = "Usuario",
                    value = usuario.username.orEmpty(),
                    onValueChange = { usuarioViewModel.setNombre(it) }
                )

                CustomTextField(
                    icon = Icons.Default.Lock,
                    label = "Contraseña",
                    value = usuario.password.orEmpty(),
                    isPassword = true,
                    onValueChange = { usuarioViewModel.setContrasena(it) }
                )

            }


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BtnStyle1(
                    onClick =  {

                    },
                    text = "Registrase"
                )

                CustomBox(
                    borderTop = true,
                    borderBottom = true,
                    msg = "or"
                )

                GoogleButton(LocalContext.current)
            }

        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FrontendappTheme {
        val usuario = UsuarioViewModel(AuthRemoteDataResource(RetrofitInstance.api))
        LoginScreen(navController = rememberNavController(),usuario)
    }
}