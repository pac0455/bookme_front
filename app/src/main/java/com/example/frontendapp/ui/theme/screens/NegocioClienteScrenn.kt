package com.example.frontendapp.ui.theme.screens

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.frontendapp.ui.theme.composables.CustomBox
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.GoogleButton
import com.example.frontendapp.ui.theme.composables.MySwitch
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun NegocioClienteScrenn(navController: NavController, usuarioViewModel: UsuarioViewModel){

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
            verticalArrangement = Arrangement.Bottom,
        ) {

            var isBusiness by remember { mutableStateOf(false) }
            var isClient by remember { mutableStateOf(true) }

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
                BtnStyle1(onClick = { }, text = "Registarse")
            }

        }
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewNegocioClienteScrenn(){
    FrontendappTheme {
        val navController = rememberNavController()
        val usuarioViewModel: UsuarioViewModel = UsuarioViewModel()
        NegocioClienteScrenn(navController, usuarioViewModel)
    }
}