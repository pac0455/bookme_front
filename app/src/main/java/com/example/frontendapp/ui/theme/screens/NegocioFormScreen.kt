package com.example.frontendapp.ui.theme.screens


import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.Principal
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.Principal_variacion4
import com.example.frontendapp.ui.theme.Principal_variacion5
import com.example.frontendapp.ui.theme.composables.BtnIconRounded
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomMultilineTextField
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.IconPosition
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.google.android.gms.maps.model.LatLng

import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioFormScreen(navController: NavController, negocioViewModel: NegocioViewModel, enableGeocoder: Boolean = true ) {
    val context = LocalContext.current
    val negocio = negocioViewModel.negocioState.collectAsState().value
    val geoCoder = remember(context, enableGeocoder) {
        if (enableGeocoder) Geocoder(context, Locale.getDefault()) else null
    }


    val categorias = listOf("Salón", "Clínica", "Gimnasio", "Otro")
    var categoriaExpanded by remember { mutableStateOf(false) }

    // Si hay una ubicación, usar Geocoder para obtener la dirección

        LaunchedEffect(negocio.latitud, negocio.longitud) {
            val ubi = LatLng(negocio.latitud ?: 0.0, negocio.longitud ?: 0.0)
            if (ubi.latitude != 0.0 && ubi.longitude != 0.0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geoCoder?.getFromLocation(
                        ubi.latitude,
                        ubi.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                val direccion = addresses.firstOrNull()?.getAddressLine(0)
                                direccion?.let {
                                    negocioViewModel.setDireccion(it)
                                }
                            }

                            override fun onError(errorMessage: String?) {}
                        }
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geoCoder?.getFromLocation(ubi.latitude, ubi.longitude, 1)
                    val direccion = addresses?.firstOrNull()?.getAddressLine(0)
                    direccion?.let {
                        negocioViewModel.setDireccion(it)
                    }
                }
            }
        }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Principal_variacion3,
                    titleContentColor = Color.White
                ),
                title = { Text("Registro de negocio") },
                navigationIcon = {
                    IconButton (onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BtnStyle1(
                text = "Siguiente",
                onClick = {
                    navController.navigate(NavigationItem.HORARIO_FORM.route)
                },
                iconPosition = IconPosition.END,
                icon = Icons.Default.ArrowForward,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding() // esto evita que se solape con la barra del sistema
            )
        }


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
                    onValueChange = { negocioViewModel.setNombre(it)  }
                )

                CustomMultilineTextField(
                    value = negocio.descripcion,
                    onValueChange = { negocioViewModel.setDescripcion(it)  },
                    label = "Descripción"
                )

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    if (negocio.direccion.isNullOrEmpty()) {
                        negocio.direccion = "Pulsa el icono para poder insertar una dirección"
                    }

                    CustomTextField(
                        modifier = Modifier.weight(1f),
                        value = negocio.direccion,
                        enabled = false,
                        onValueChange = { negocioViewModel.setDireccion(it) },
                        label = "Dirección"
                    )

                    BtnIconRounded(
                        icon = Icons.Default.Place,
                        onClick = {
                            navController.navigate(NavigationItem.MAP_SELECT.route)
                        },
                        size = 56.dp,        //  tamaño fijo del botón
                        iconSize = 24.dp     //  tamaño visible del icono
                    )
                }





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
                                    negocioViewModel.setcategoria(it)
                                    categoriaExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NegocioFormScreenPreview() {
    // 1. Creas una instancia REAL de NegocioViewModel


    // 3. Pasas la instancia real al composable
    FrontendappTheme {
        NegocioFormScreen(
            navController = rememberNavController(),// <- Tipo correcto: NegocioViewModel,
            negocioViewModel = NegocioViewModel(NegocioRemoteSource(RetrofitInstance.negocioApi)),
            enableGeocoder = false  // Desactivas Geocoder para el preview
        )
    }
}
