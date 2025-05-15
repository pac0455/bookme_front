package com.example.frontendapp.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.TopBarBussines
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.navigation.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioFormScreen(navController: NavController,negocioViewModel: NegocioViewModel ) {
    Scaffold(
        topBar = {
            TopBarBussines()
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        val negocio = negocioViewModel.negocio UN
        val categorias = listOf("Salón", "Clínica", "Gimnasio", "Otro")
        var categoriaExpanded by remember { mutableStateOf(false) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            //Inputs
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier= Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(0.9f)) {
                CustomTextField(
                    label = "nombre",
                    value = negocio.nombre,
                    onValueChange = { negocioViewModel.updateField { copy(nombre = it) } }
                )

                CustomTextField(
                    value = negocio.descripcion,
                    onValueChange = { negocioViewModel.updateField { copy(descripcion = it) } },
                    label =  "Descripción" ,
                )
                //Categoria
                Spacer(modifier = Modifier.height(8.dp))

                // Selector de Categoría
                ExposedDropdownMenuBox (
                    expanded = categoriaExpanded,
                    onExpandedChange = { categoriaExpanded = !categoriaExpanded }
                ) {
                    OutlinedTextField(
                        value = negocio.categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoriaExpanded,
                        onDismissRequest = { categoriaExpanded = false }
                    ) {
                        categorias.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    negocioViewModel.updateField { copy(categoria = it) }
                                    categoriaExpanded = false
                                }
                            )
                        }
                    }
                }
                //Direccion
                BtnStyle1(
                    text = "Elegir ubicación",
                    onClick = {
                        //Mandarlo a la pantalla de mapScreen
                        navController.navigate(NavigationItem.MAP_SELECT.route)
                    }, icon = Icons.Default.Place)
            }
        }
    }
}

@Composable
@Preview
fun LocationPreviewScreen(){
    FrontendappTheme {
        NegocioFormScreen(navController = rememberNavController(), negocioViewModel = NegocioViewModel())
    }
}
