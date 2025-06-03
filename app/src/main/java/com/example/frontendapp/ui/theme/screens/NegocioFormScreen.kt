package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Negocio.Ubicacion
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.Btn.BtnIconRounded
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.Btn.IconPosition
import com.example.frontendapp.ui.theme.composables.CustomMultilineTextField
import com.example.frontendapp.ui.theme.composables.CustomSelector
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioFormScreen(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    enableGeocoder: Boolean = true,
    categoriasViewModel: CategoriaViewModel,
) {
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    val validationState by negocioViewModel.negocioValidationState.collectAsState()
    val context = LocalContext.current
    val negocio = negocioViewModel.negocioState.collectAsState().value
    val isEdit = negocioViewModel.isEditMode.collectAsState().value
    val tituloPantalla = if (isEdit) "Editar Negocio" else "Crear Negocio"
    var geocoder = remember { Geocoder(context, Locale.getDefault()) }

    val categoriaSelecionada = categorias.find { it.id == negocio.categoriaId }?.nombre ?: ""

    if (enableGeocoder) geocoder = remember { Geocoder(context, Locale.getDefault()) }

    // Cargar categorías
    LaunchedEffect(Unit) {
        categoriasViewModel.getAllCategorias(
            onLoading = {
                Log.d("Categorias", "Cargando...")
            },
            onError = { errorMsg ->
                Log.e("Categorias", "Error: $errorMsg")
            },
            onSuccess = { list ->
                list.forEach { Log.d("NegocioFormScreen_Categorias", it.toString()) }
                categorias = list
            }
        )
    }

    // Actualizar dirección cuando cambien las coordenadas
    if (enableGeocoder) {
        LaunchedEffect(negocio.latitud, negocio.longitud) {
            if (negocio.latitud != null && negocio.longitud != null) {
                negocioViewModel.setDireccion(geocoder)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {
                    Text(
                        text = tituloPantalla,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
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
                        .navigationBarsPadding()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con información del progreso
            ProgressHeader(
                currentStep = 1,
                totalSteps = 2,
                stepTitle = "Información Básica",
                stepDescription = "Completa los datos principales de tu negocio"
            )

            // Formulario principal
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxWidth()
            ) {
                // Sección de información básica
                FormSection(
                    title = "Información del Negocio",
                    icon = Icons.Default.Business
                ) {
                    CustomTextField(
                        label = "Nombre del negocio",
                        value = negocio.nombre,
                        errorMessage = validationState.errors["nombre"],
                        onValueChange = { negocioViewModel.setNombre(it) },
                        leadingIcon = Icons.Default.Storefront
                    )

                    CustomMultilineTextField(
                        value = negocio.descripcion,
                        errorMessage = validationState.errors["descripcion"],
                        onValueChange = { negocioViewModel.setDescripcion(it) },
                        label = "Descripción",
                        leadingIcon = Icons.Default.Description
                    )
                }

                // Sección de ubicación mejorada
                FormSection(
                    title = "Ubicación",
                    icon = Icons.Default.LocationOn
                ) {
                    // Campo de dirección
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            modifier = Modifier.weight(1f),
                            value = if (negocio.direccion.isEmpty()) negocioViewModel.descripcionLabel else negocio.direccion,
                            enabled = false,
                            errorMessage = validationState.errors["direccion"],
                            onValueChange = { },
                            label = "Dirección",
                            leadingIcon = Icons.Default.Place
                        )

                        BtnIconRounded(
                            icon = Icons.Default.MyLocation,
                            onClick = {
                                // Navegar al mapa con callback específico para negocio form
                                navController.navigate(NavigationItem.MAP_SELECT.forNegocioForm)
                            },
                            modifier = Modifier.size(56.dp),
                            size = 56.dp,
                            iconSize = 24.dp,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Mostrar coordenadas si están disponibles
                    AnimatedVisibility(
                        visible = negocio.latitud != null && negocio.longitud != null,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Coordenadas: ${String.format("%.6f", negocio.latitud ?: 0.0)}, ${String.format("%.6f", negocio.longitud ?: 0.0)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Sección de categoría
                FormSection(
                    title = "Categoría",
                    icon = Icons.Default.Category
                ) {
                    CustomSelector(
                        selectedOption = categoriaSelecionada,
                        options = categorias.map { it.nombre },
                        onOptionSelected = { selectedOption ->
                            categorias.find { it.nombre == selectedOption }?.let {
                                negocioViewModel.setcategoriaId(it.id)
                            }
                        },
                        label = "Selecciona una categoría",
                        errorMessage = validationState.errors["categoria"],
                        leadingIcon = Icons.Default.Category
                    )
                }

                // Sección de estado del negocio
                FormSection(
                    title = "Estado del Negocio",
                    icon = Icons.Default.ToggleOn
                ) {
                    NegocioStatusToggle(
                        isActive = negocio.activo,
                        onStatusChange = { newStatus ->
                            negocioViewModel.setActivo(newStatus)
                        }
                    )
                }

                // Espaciado adicional para el bottom bar
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    stepTitle: String,
    stepDescription: String
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Indicador de progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paso $currentStep de $totalSteps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                LinearProgressIndicator(
                    progress = currentStep.toFloat() / totalSteps.toFloat(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stepTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = stepDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun NegocioStatusToggle(
    isActive: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isActive)
            ThemeColors.success.copy(alpha = 0.1f) else
            ThemeColors.error.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "Estado del negocio",
                        tint = if (isActive) ThemeColors.success else ThemeColors.error,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isActive) "Negocio Activo" else "Negocio Inactivo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isActive)
                        "Tu negocio será visible para los clientes" else
                        "Tu negocio estará oculto para los clientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isActive,
                onCheckedChange = onStatusChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
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
            categoriasViewModel = CategoriaViewModel(CategoriaRemoteDataSource(RetrofitInstance.categoriaApi))
        )
    }
}
