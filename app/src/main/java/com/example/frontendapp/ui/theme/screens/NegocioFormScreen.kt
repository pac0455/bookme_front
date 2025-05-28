package com.example.frontendapp.ui.theme.screens


import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Negocio.Ubicacion
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.BtnIconRounded
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomMultilineTextField
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.Btn.IconPosition
import com.example.frontendapp.ui.theme.composables.CustomSelector
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel

import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioFormScreen(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    enableGeocoder: Boolean = true,
    categoriasViewModel: CategoriaViewModel,
    ) {

    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    val categoriaSelecionada by remember { mutableStateOf(Categoria()) }
    val validationState by negocioViewModel.negocioValidationState.collectAsState()
    val context = LocalContext.current
    val negocio = negocioViewModel.negocioState.collectAsState().value
    val isEdit = negocioViewModel.isEditMode.collectAsState().value
    val tituloPantalla = if (isEdit) "Editar Negocio" else "Crear Negocio"
    var geocoder = remember { Geocoder(context, Locale.getDefault()) }
    val ubicacion by remember { mutableStateOf(Ubicacion(negocio.latitud, negocio.latitud)) }

    if(enableGeocoder) geocoder = remember { Geocoder(context, Locale.getDefault()) }
    LaunchedEffect(Unit) {
        categoriasViewModel.getAllCategorias(
            onLoading = {
                Log.d("Categorias", "Cargando...")
            },
            onError = { errorMsg ->
                Log.e("Categorias", "Error: $errorMsg")
            },
            onSuccess = { list ->
                list.forEach{Log.d("NegocioFormScreen_Categorias", it.toString())}
                categorias = list
            }
        )
    }
    //Cada vez que la ubicacion cambie que me setee la direccción
    LaunchedEffect(ubicacion) { negocioViewModel.setDireccion(geocoder) }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Principal_variacion3,
                    titleContentColor = Color.White
                ),
                title = { Text(tituloPantalla) },
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
                    Log.d("NegocioFormScreen", "Navegando a HORARIO_FORM con datos: nombre=${negocio.nombre}, direccion=${negocio.direccion}, categoria=${negocio.categoria}")
                    val modo = if (isEdit) "editar" else "crear"
                    negocioViewModel.validateNegocioForm(
                        onSuccess = {
                            navController.navigate(NavigationItem.HORARIO_FORM.createRoute(modo))
                        },
                        onError = { errores ->

                            Log.d("FieldERRORS", errores)
                        }
                    )
                },
                iconPosition = IconPosition.END,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
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
                    errorMessage = validationState.errors["nombre"],
                    onValueChange = { negocioViewModel.setNombre(it)  }
                )

                CustomMultilineTextField(
                    value = negocio.descripcion,
                    errorMessage = validationState.errors["descripcion"],
                    onValueChange = { negocioViewModel.setDescripcion(it)  },
                    label = "Descripción"
                )

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    if (negocio.direccion.isEmpty()) {
                        negocio.direccion = negocioViewModel.descripcionLabel
                    }

                    CustomTextField(
                        modifier = Modifier.weight(1f),
                        value = negocio.direccion,
                        enabled = false,
                        errorMessage = validationState.errors["direccion"],
                        onValueChange = { negocioViewModel.setDireccion(geocoder) },
                        label = "Dirección",
                    )

                    BtnIconRounded(
                        icon = Icons.Default.Place,
                        onClick = {
                            navController.navigate(NavigationItem.MAP_SELECT.route)
                        },
                        modifier = Modifier.size(56.dp),
                        size = 56.dp,
                        iconSize = 24.dp
                    )
                }
                // Selector de Categoría
                CustomSelector(
                    selectedOption = categoriaSelecionada.nombre,
                    options = categorias.map { it.nombre }, //Lista de categorias pero con nombres
                    onOptionSelected = { selectedOption ->
                        categorias.find { it.nombre == selectedOption }?.let {
                            negocioViewModel.setcategoriaId(it.id)
                        }
                    },
                    label = "Categoría",
                    errorMessage = validationState.errors["categoria"] // Obtener el mensaje de error
                )
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegocioFormScreenPreview() {
    FrontendappTheme {
        NegocioFormScreen(
            navController = rememberNavController(),
            negocioViewModel = FakeNegocioViewModel(),
            enableGeocoder = false,
            categoriasViewModel = CategoriaViewModel(CategoriaRemoteDataSource(RetrofitInstance.categoriaApi))  // Desactivas Geocoder para el preview
        )
    }
}