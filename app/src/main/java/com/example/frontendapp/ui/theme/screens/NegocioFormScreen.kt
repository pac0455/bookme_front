package com.example.frontendapp.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioFormScreen(navController: NavController,negocioViewModel: NegocioViewModel ) {
    val context = LocalContext.current
    val geoCoder = Geocoder(context, Locale.getDefault())
    val negocio = negocioViewModel.negocio
    val categorias = listOf("Salón", "Clínica", "Gimnasio", "Otro")
    var categoriaExpanded by remember { mutableStateOf(false) }

    // Si hay una ubicación, usar Geocoder para obtener la dirección
    LaunchedEffect(negocio.latitud, negocio.longitud) {
        val ubi = LatLng(negocio.latitud ?: 0.0, negocio.longitud ?: 0.0)
        if (ubi.latitude != 0.0 && negocio.longitud != 0.0) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geoCoder.getFromLocation(
                        ubi.latitude  ,
                        ubi.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            val direccion = addresses.firstOrNull()?.getAddressLine(0)
                            direccion?.let {
                                negocioViewModel.updateField { copy(direccion = it) }
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            Toast.makeText(context, "Error al obtener dirección", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geoCoder.getFromLocation(ubi.latitude, ubi.longitude, 1)
                    val direccion = addresses?.firstOrNull()?.getAddressLine(0)
                    direccion?.let {
                        negocioViewModel.updateField { copy(direccion = it) }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show()
            }
        }
    }
    Scaffold(
        topBar = {
            TopBarBussines()
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->



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
                    label = "Nombre",
                    value = negocio.nombre,
                    onValueChange = { negocioViewModel.updateField { copy(nombre = it) } }
                )

                CustomTextField(
                    value = negocio.descripcion,
                    onValueChange = { negocioViewModel.updateField { copy(descripcion = it) } },
                    label =  "Descripción" ,
                )
                CustomTextField(
                    value = negocio.direccion,
                    enabled = false,
                    onValueChange = { negocioViewModel.updateField { copy(direccion = it) } },
                    label =  "Dirección" ,
                )

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
                //Ubicación
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
