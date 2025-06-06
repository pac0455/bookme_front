package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.modals.ServicioImagePicker
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioForm(
    navController: NavController,
    servicioViewModel: ServicioViewModel,

    modo: String
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val esCreacion = modo == "crear"

    val servicioCreatedState by servicioViewModel.servicioCreatedState.collectAsState()
    val servicio by servicioViewModel.servicioState.collectAsState()
    val servicioValidateState by servicioViewModel.validationState.collectAsState()
    val errors = servicioValidateState.errors

    // Loguear cuando cambia el servicio para ver qué datos llegan
    LaunchedEffect(servicio) {
        Log.d("ServicioForm", "Servicio recibido: id=${servicio.id}, nombre='${servicio.nombre}', descripcion='${servicio.descripcion}', precio=${servicio.precio}")
    }

    LaunchedEffect(servicioCreatedState) {
        when (servicioCreatedState) {
            is Resource.Success -> {
                Log.d("ServicioForm", "Servicio creado/actualizado con éxito")
                navController.popBackStack()
                servicioViewModel.resetStates()
            }
            is Resource.Error -> {
                val error = (servicioCreatedState as Resource.Error).message ?: "Error desconocido"
                Log.d("ServicioForm", "Error creando/actualizando servicio: $error")
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
            else -> {}
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
                    IconButton(onClick = { navController.popBackStack() }) {
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
                isLoading = isLoading,
                onClick = {
                    isLoading = true

                    val onErrorUpdateServicio: (String) -> Unit = { errorMsg ->
                        isLoading = false
                        Toast.makeText(context, "Error actualizando servicio: $errorMsg", Toast.LENGTH_SHORT).show()
                    }

                    val onSuccessUpdateServicio = { id: Int ->
                        Log.d("ServicioForm", "Servicio actualizado con éxito, ahora actualizando imagen")
                        Log.d("ServicioForm", "Actualizando el servicio para añadir la imagen: $id")
                        val imagenUri = servicioViewModel.imagenUri.value
                        val servicioId = id

                        if (imagenUri != null) {
                            servicioViewModel.updateImagenServicio(
                                id = servicioId,
                                context = context,
                                imagenUri = imagenUri,
                                onLoading = { /* Aquí puedes manejar loading si quieres */ },
                                onSuccess = {
                                    Log.d("ServicioForm", "Imagen actualizada con éxito")
                                    isLoading = false
                                    navController.popBackStack()
                                    servicioViewModel.resetStates()
                                },
                                onError = { errorMsg ->
                                    Log.d("ServicioForm", "Error actualizando imagen: $errorMsg")
                                    isLoading = false
                                    Toast.makeText(context, "Servicio actualizado pero error al actualizar imagen: $errorMsg", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                    servicioViewModel.resetStates()
                                }
                            )
                        } else {
                            // No hay imagen para actualizar, solo cerrar
                            isLoading = false
                            navController.popBackStack()
                            servicioViewModel.resetStates()
                        }
                    }

                    if (esCreacion) {
                        servicioViewModel.addServicio(
                            onLoading = { /* Opcional */ },
                            onSuccess = { servicioCreado ->
                                onSuccessUpdateServicio(servicioCreado.id)
                                Toast.makeText(context, "Servicio agregado exitosamente", Toast.LENGTH_SHORT).show()
                            },
                            onError = onErrorUpdateServicio,
                            context = context
                        )
                    } else {
                        Log.d("ServicioForm", "Enviando para actualizar: $servicio")
                        servicioViewModel.updateServicio(
                            onLoading = { /* Opcional */ },
                            onSuccess = {
                                // Aquí servicio.id debe existir
                                onSuccessUpdateServicio(servicio.id)
                            },
                            onError = onErrorUpdateServicio,
                        )
                    }
                },
                text = if (isLoading) "Cargando..." else if (esCreacion) "Añadir" else "Actualizar",
                icon = if (isLoading) Icons.Default.HourglassEmpty else Icons.Default.Add,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = servicio.nombre,
                    onValueChange = {
                        Log.d("ServicioForm", "Nuevo nombre: $it")
                        servicioViewModel.setNombre(it)
                    },
                    label = "Nombre",
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = errors["nombre"]
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = servicio.descripcion,
                    onValueChange = {
                        Log.d("ServicioForm", "Nueva descripción: $it")
                        servicioViewModel.setDescripcion(it)
                    },
                    label = "Descripción",
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = errors["descripcion"]
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = servicio.duracionMinutos.toString(),
                    onValueChange = { newValue ->
                        Log.d("ServicioForm", "Nuevo duración input: $newValue")
                        val filtered = newValue.filter { it.isDigit() }
                        if (filtered != servicio.duracionMinutos.toString()) {
                            val duracion = filtered.toIntOrNull() ?: 0
                            servicioViewModel.setDuracionMinutos(duracion)
                            Log.d("ServicioForm", "Duración parseada y seteada: $duracion")
                        }
                    },
                    label = "Duración (minutos)",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    errorMessage = errors["duracionMinutos"]
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = servicio.precio.toString(),
                    onValueChange = { newValue ->
                        Log.d("ServicioForm", "Nuevo precio input: $newValue")
                        val filtered = newValue.filter { it.isDigit() || it == '.' }
                        if (filtered.count { it == '.' } <= 1) {
                            servicioViewModel.setPrecio(filtered.toDoubleOrNull() ?: 0.0)
                            Log.d("ServicioForm", "Precio parseado y seteado: $filtered")
                        }
                    },
                    label = "Precio (€)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = errors["precio"]
                )

                Spacer(modifier = Modifier.height(8.dp))

                Spacer(modifier = Modifier.height(16.dp))

                ServicioImagePicker(
                    imageUrl = servicioViewModel.getServicioImageUrl(servicio.id),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    clickable = true,
                    onImageSelected = {
                        Log.d("ServicioForm", "Imagen seleccionada: $it")
                        servicioViewModel.setImagenUri(it)
                    }
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewServicioForm() {
    val navController = rememberNavController()
    val viewModel = remember { FakeServicioViewModel() }

    ServicioForm(navController = navController, servicioViewModel = viewModel, modo = "crear")
}