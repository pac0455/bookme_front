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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Categoria
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
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeCategoriaViewModel
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
    val tituloPantalla = if (isEdit) stringResource(id = R.string.screen_title_edit_business) else stringResource(id = R.string.screen_title_create_business) // String resource
    var geocoder = remember { Geocoder(context, Locale.getDefault()) }

    val categoriaSelecionada = categorias.find { it.id == negocio.categoriaId }?.nombre ?: ""

    if (enableGeocoder) geocoder = remember { Geocoder(context, Locale.getDefault()) }

    val logNavigatingMessage = stringResource(
        id = R.string.log_navigating_to_schedule_form,
        negocio.nombre,
        negocio.direccion,
        negocio.categoria?.nombre ?: "Sin categoría"
    )

    val loadingMsg = stringResource(id = R.string.log_categories_loading)
    val errorMsgTemplate = stringResource(id = R.string.log_categories_error)
    val itemDebugTemplate = stringResource(id = R.string.log_categories_item_debug)


    // Cargar categorías
    LaunchedEffect(Unit) {
        categoriasViewModel.getAllCategorias(
            onLoading = {
                Log.d("Categorias", loadingMsg)
            },
            onError = { error ->
                Log.e("Categorias", String.format(errorMsgTemplate, error))
            },
            onSuccess = { list ->
                list.forEach {
                    Log.d("NegocioFormScreen_Categorias", String.format(itemDebugTemplate, it.toString()))
                }
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
                            contentDescription = stringResource(id = R.string.back_button_content_description), // String resource
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
                    text = stringResource(id = R.string.next_button_text),
                    onClick = {
                        val modo = if (isEdit) "editar" else "crear"
                        // Now use the pre-loaded string resources for logging
                        Log.d("NegocioFormScreen", logNavigatingMessage) // Use the pre-loaded string

                        negocioViewModel.validateNegocioForm(
                            onSuccess = {
                                navController.navigate(NavigationItem.HORARIO_FORM.createRoute(modo))
                            },
                            onError = { errores -> }
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
                stepTitle = stringResource(id = R.string.step_title_basic_info), // String resource
                stepDescription = stringResource(id = R.string.step_description_basic_info) // String resource
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
                    title = stringResource(id = R.string.business_info_section_title), // String resource
                    icon = Icons.Default.Business
                ) {
                    CustomTextField(
                        label = stringResource(id = R.string.business_name_label), // String resource
                        value = negocio.nombre,
                        errorMessage = validationState.errors["nombre"],
                        onValueChange = { negocioViewModel.setNombre(it) },
                        leadingIcon = Icons.Default.Storefront
                    )

                    CustomMultilineTextField(
                        value = negocio.descripcion,
                        errorMessage = validationState.errors["descripcion"],
                        onValueChange = { negocioViewModel.setDescripcion(it) },
                        label = stringResource(id = R.string.description_label), // String resource
                        leadingIcon = Icons.Default.Description
                    )
                }

                // Sección de ubicación
                FormSection(
                    title = stringResource(id = R.string.location_section_title), // String resource
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
                            label = stringResource(id = R.string.address_label), // String resource
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
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,


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
                                    contentDescription = null, // Or provide a specific CD for GPS fixed icon
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.coordinates_label, negocio.latitud ?: 0.0, negocio.longitud ?: 0.0), // String resource with format
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Sección de categoría
                FormSection(
                    title = stringResource(id = R.string.category_section_title), // String resource
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
                        label = stringResource(id = R.string.select_category_label), // String resource
                        errorMessage = validationState.errors["categoria"],
                        leadingIcon = Icons.Default.Category
                    )
                }

                // Sección de estado del negocio
                FormSection(
                    title = stringResource(id = R.string.business_status_section_title), // String resource
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
                    text = stringResource(id = R.string.progress_step_indicator, currentStep, totalSteps), // String resource with format
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
                    contentDescription = null, // Icon is decorative, or add specific CD if meaningful
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
                        contentDescription = stringResource(id = R.string.business_status_content_description), // String resource
                        tint = if (isActive) ThemeColors.success else ThemeColors.error,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isActive) stringResource(id = R.string.status_active_business) else stringResource(id = R.string.status_inactive_business), // String resource
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isActive)
                        stringResource(id = R.string.status_active_description) else // String resource
                        stringResource(id = R.string.status_inactive_description), // String resource
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
            categoriasViewModel = FakeCategoriaViewModel()
        )
    }
}
