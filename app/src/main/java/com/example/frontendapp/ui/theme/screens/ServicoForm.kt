package com.example.frontendapp.ui.theme.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Servicio
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.onError
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioForm(
    navController: NavController,
    servicioViewModel: ServicioViewModel
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val servicioCreatedState by servicioViewModel.servicioCreatedState.collectAsState()
    val servicio by servicioViewModel.servicioState.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle image selection here if needed
    }

    LaunchedEffect(servicioCreatedState) {
        when (servicioCreatedState) {
            is Resource.Success -> {
                navController.popBackStack()
                servicioViewModel.resetStates()
            }
            is Resource.Error -> {
                val error = (servicioCreatedState as Resource.Error).message ?: "Error desconocido"
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                Log.d("ERROR AÑADIR SERVICIO", error)
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
                onClick = {
                    isLoading = true
                    servicioViewModel.addServicio(
                        onLoading = {},
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Servicio agregado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                            isLoading = false
                        },
                        onError = { errorMessage ->
                            Toast.makeText(
                                context,
                                "Error al agregar servicio: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("ERROR AL INSERTAR", errorMessage)
                            isLoading = false
                        },
                        context = context
                    )
                },
                text = if (isLoading) "Cargando..." else "Añadir",
                icon = if (isLoading) Icons.Default.HourglassEmpty else Icons.Default.Add,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = servicio.nombre ?: "",
                    onValueChange = { servicioViewModel.setNombre(it) }, // Use setter method
                    label = "Nombre",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = servicio.descripcion ?: "",
                    onValueChange = { servicioViewModel.setDescripcion(it) }, // Use setter method
                    label = "Descripción",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = servicio.precio?.toString() ?: "",
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() || it == '.' }
                        if (filtered.count { it == '.' } <= 1) {
                            servicioViewModel.setPrecio(filtered.toDoubleOrNull() ?: 0.0) // Use setter method
                        }
                    },
                    label = "Precio (€)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    // Handle image display logic here
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewServicioForm() {
    val navController = rememberNavController()
    val viewModel = remember { FakeServicioViewModel() }

    ServicioForm(navController = navController, servicioViewModel = viewModel )
}
