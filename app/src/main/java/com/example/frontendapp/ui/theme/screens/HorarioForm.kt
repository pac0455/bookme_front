package com.example.frontendapp.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomBox
import com.example.frontendapp.ui.theme.composables.TopBarBussines

@Composable
fun HorarioForm(navController: NavController) {

    BtnStyle1(
        modifier = Modifier.fillMaxWidth(0.3f),
        text = "ADD", onClick = {

    })
    CustomBox(
        border = true,
    )
}

@Preview
@Composable
fun PreviewHorarioForm(){
    FrontendappTheme {
        Scaffold(
            topBar = { TopBarBussines() },
            bottomBar = {
                BtnStyle1(text = "Crear negocio", onClick = {})
            }
        )
        { inner ->
            Column(Modifier.padding(inner)) {

                HorarioForm(rememberNavController())
            }

        }
    }
}