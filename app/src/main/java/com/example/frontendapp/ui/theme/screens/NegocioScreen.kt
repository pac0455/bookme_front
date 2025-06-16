package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.Reserva.ReservaPorDiaDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.list.ListaReservas
import com.example.frontendapp.ui.theme.composables.QuickActionsExpandable
import com.example.frontendapp.ui.theme.composables.chart.LineChart
import com.example.frontendapp.ui.theme.composables.list.ServicioList
import com.example.frontendapp.ui.theme.composables.modals.ModalSelectorDeImagen
import com.example.frontendapp.ui.theme.composables.tab.adminPanel.EmptyList
import com.example.frontendapp.ui.theme.composables.tab.negocioDetails.NegocioValoracionesTab
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ValoracionViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeValoracionViewModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.util.Locale


enum class ContentType {
    RESERVAS,
    SERVICIOS,
    SUBSCRIPTOR,
    GALLERIA,
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun NegocioScreen(
    viewModel: NegocioViewModel,
    navController: NavController,
    reservasViewModel: ReservasViewModel,
    servicioViewModel: ServicioViewModel,
    valoracionViewModel: ValoracionViewModel
) {
    val negocio by viewModel.negocioState.collectAsState()
    var selectedContent by remember { mutableStateOf<ContentType?>(ContentType.RESERVAS) }
    var imagenConfirmada by remember { mutableStateOf<Uri?>(null) }

    // Crear la url para imagen
    val urlNegocio = viewModel.getNegocioImageUrl()
    val context = LocalContext.current

    // Pre-load strings for non-composable contexts (Log.d, Toast.makeText)
    val logImageSelectedUriFormat = stringResource(id = R.string.negocio_screen_image_selected_log)
    val toastUploadingImage = stringResource(id = R.string.negocio_screen_uploading_image_toast)
    val logUploadImageStartedFormat = stringResource(id = R.string.negocio_screen_upload_image_started_log)
    val toastUploadImageErrorFormat = stringResource(id = R.string.negocio_screen_upload_image_error_toast)
    val logUploadImageErrorFormat = stringResource(id = R.string.negocio_screen_upload_image_error_log)
    val toastImageUpdatedCorrectly = stringResource(id = R.string.negocio_screen_image_updated_toast)

    Column(modifier = Modifier.fillMaxSize()) {

        // Cabecera con nombre e imagen
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .clip(RoundedCornerShape(bottomEnd = 40.dp, bottomStart = 40.dp))
                    .background(Principal_variacion3)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.negocio_screen_hello), // String resource
                        color = Color.White
                    )
                    Text(
                        text = negocio.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                ModalSelectorDeImagen(
                    logoUrl = urlNegocio,
                    imagenConfirmada = imagenConfirmada,
                    onImagenSeleccionada = { uri ->
                        Log.d("NegocioScreen", String.format(logImageSelectedUriFormat, uri)) // String resource with format
                        viewModel.setSelectedImageUri(uri)
                    },
                    onAccept = { uri, onSuccessCallback ->
                        viewModel.updateNegocioImagen(
                            id = negocio.id,
                            context = context,
                            onLoading = {
                                Toast.makeText(context, toastUploadingImage, Toast.LENGTH_SHORT).show() // String resource
                                Log.d("NegocioScreen", String.format(Locale.getDefault(), logUploadImageStartedFormat, negocio.id)) // String resource with format
                            },
                            onError = { mensajeError ->
                                Toast.makeText(context, String.format(toastUploadImageErrorFormat, mensajeError), Toast.LENGTH_LONG).show() // String resource with format
                                Log.e("NegocioScreen", String.format(logUploadImageErrorFormat, mensajeError)) // String resource with format
                            },
                            onSuccess = { negocioActualizado ->
                                Toast.makeText(context, toastImageUpdatedCorrectly, Toast.LENGTH_SHORT).show() // String resource
                                negocio.logoUrl = negocioActualizado.logoUrl
                                imagenConfirmada = uri
                                onSuccessCallback()
                            }
                        )
                    },
                )
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 24.dp, start = 16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.negocio_screen_back_button_content_description), // String resource
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuickActionsExpandable(
                selectedContent = selectedContent,
                onContentSelected = { selectedContent = it }
            )
        }

        AnimatedContentArea(
            modifier = Modifier.weight(1f),
            selectedContent = selectedContent,
            reservasViewModel = reservasViewModel,
            serviciosViewModel_negocioScreen = servicioViewModel,
            negocio = negocio,
            navController = navController,
            negocioViewModel = viewModel,
            valoracionViewModel = valoracionViewModel
        )
    }
}

@Composable
fun AnimatedContentArea(
    selectedContent: ContentType?,
    reservasViewModel: ReservasViewModel,
    negocio: Negocio,
    serviciosViewModel_negocioScreen: ServicioViewModel,
    modifier: Modifier = Modifier,
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    valoracionViewModel: ValoracionViewModel
) {
    val animatedContentAreaSelectSection = stringResource(id = R.string.animated_content_area_select_section)
    val animatedContentAreaAddServiceContentDescription = stringResource(id = R.string.animated_content_area_add_service_content_description)

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedContent,
            transitionSpec = {
                val direction = if ((targetState?.ordinal ?: 0) > (initialState?.ordinal ?: 0)) 1 else -1
                (slideInHorizontally(
                    animationSpec = tween(durationMillis = 500),
                    initialOffsetX = { fullWidth -> direction * fullWidth }
                ) + fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.9f)) togetherWith
                        (slideOutHorizontally(
                            animationSpec = tween(durationMillis = 500),
                            targetOffsetX = { fullWidth -> -direction * fullWidth }
                        ) + fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 1.1f))
            },
            label = "AdvancedContentAnimation"
        ) { targetContent ->
            when (targetContent) {
                ContentType.RESERVAS -> ListaReservas(
                    viewModel = reservasViewModel,
                    negocioId = negocio.id // Pasar el negocioId aquí
                )
                ContentType.SERVICIOS -> ServicioList(
                    serviciosViewModel_negocioScreen,
                    negocioId = negocio.id,
                    navController = navController
                )
                ContentType.SUBSCRIPTOR -> ReservasPorSemana(
                    negocioId = negocio.id,
                    reservasViewModel= reservasViewModel
                )
                ContentType.GALLERIA -> NegocioValoracionesTab(
                    negocioId = negocio.id,
                    valoracionViewModel = valoracionViewModel
                )
                else -> Text(
                    animatedContentAreaSelectSection, // String resource
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // FloatingActionButton para servicios
        if (selectedContent == ContentType.SERVICIOS) {
            FloatingActionButton(
                onClick = {
                    val negocioActual = negocioViewModel.negocioState.value
                    if (negocioActual.id != 0) {
                        serviciosViewModel_negocioScreen.resetServicio()
                        serviciosViewModel_negocioScreen.setNegocioId(negocioActual.id)
                        navController.navigate(NavigationItem.SERVICIO_FORM.create)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp),
                containerColor = Principal_variacion3
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = animatedContentAreaAddServiceContentDescription, // String resource
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ReservasPorSemana(
    negocioId: Int,
    reservasViewModel: ReservasViewModel
) {
    val resumenPorDiaState by reservasViewModel.resumenPorDiaState.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer() }
    val xLabelsState = remember { mutableStateOf<List<String>>(emptyList()) }

    // Carga los datos al iniciar
    LaunchedEffect(Unit) {
        reservasViewModel.cargarResumenPorDia(negocioId = negocioId)
    }

    // Actualiza el gráfico cuando llegan los datos
    LaunchedEffect(resumenPorDiaState) {
        if (resumenPorDiaState is Resource.Success) {
            val data = (resumenPorDiaState as Resource.Success).data ?: emptyList()

            xLabelsState.value = data.map { it.dia }

            modelProducer.runTransaction {
                lineSeries {
                    series(data.map { it.cantidad.toFloat() })
                }
            }
        }
    }

    // Renderiza el gráfico si hay datos
    if (xLabelsState.value.isNotEmpty()) {
        LineChart(
            modelProducer = modelProducer,
            xLabels = xLabelsState.value
        )
    }else{
        EmptyList() // Assuming EmptyList is a composable that displays an empty state message
    }
}





@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewNegocioScreen() {
    val viewModel = remember { FakeNegocioViewModel() }
    val navController = rememberNavController()

    NegocioScreen(
        viewModel = viewModel,
        navController = navController,
        reservasViewModel = remember { FakeReservasViewModel() },
        servicioViewModel = FakeServicioViewModel(),
        valoracionViewModel = FakeValoracionViewModel()
    )
}
