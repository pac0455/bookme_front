package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.ListItems.ListaReservas
import com.example.frontendapp.ui.theme.composables.QuickActionsExpandable
import com.example.frontendapp.ui.theme.composables.list.ListaServicios
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ContentType {
    RESERVAS,
    SERVICIOS,
    SUBSCRIPTOR,
    GALLERIA,
}

@Composable
fun NegocioScreen(
    viewModel: NegocioViewModel,
    navController: NavController,
    reservasViewModel: ReservasViewModel,
    serviciosViewModel_negocioScreen: ServicioViewModel
) {
    val negocio by viewModel.negocioState.collectAsState()
    var selectedContent by remember { mutableStateOf<ContentType?>(ContentType.RESERVAS) }

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
                    Text("Hello,", color = Color.White)
                    Text(
                        text = negocio.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                negocio.logoUrl?.let { logoUrl ->
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = "Logo negocio",
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        error = painterResource(R.drawable.ic_launcher_background),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 24.dp, start = 16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
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
            serviciosViewModel_negocioScreen = serviciosViewModel_negocioScreen,
            negocio = negocio,
            navController = navController
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

) {
    val context = LocalContext.current
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
                ContentType.RESERVAS -> ListaReservas(viewModel = reservasViewModel)
                ContentType.SERVICIOS -> ListaServicios(serviciosViewModel_negocioScreen, negocioId = negocio.id)
                ContentType.SUBSCRIPTOR -> Text("Contenido de Subscriptores", style = MaterialTheme.typography.bodyLarge)
                else -> Text("Selecciona una sección", style = MaterialTheme.typography.bodyLarge)
            }
        }

        // FloatingActionButtaon abajo a la izquierd
        if (selectedContent == ContentType.SERVICIOS) {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavigationItem.SERVICIO_FORM.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Principal_variacion3
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // Puedes cambiar el icono a uno más apropiado
                    contentDescription = "Agregar servicio",
                    tint = Color.White
                )
            }
        }
    }
}

class FakeNegocioViewModel : NegocioViewModel(NegocioRemoteSource(RetrofitInstance.negocioApi)) {
    override fun loadNegocioById(
        id: Int,
        onLoading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        onLoading()
        viewModelScope.launch {
            delay(500)
            _negocioState.value = Negocio(
                id = id,
                nombre = "Centro Estético BellaVida",
                descripcion = "Ofrecemos servicios de estética avanzada, masajes y tratamientos faciales. Atención personalizada.",
                direccion = "Calle del Sol, 123 - Madrid",
                categoria = "Estética",
                horarioAtencion = listOf(
                    Horario(id = 1, idNegocio = id, diaSemana = "Lunes", horaInicio = "09:00", horaFin = "13:00"),
                    Horario(id = 2, idNegocio = id, diaSemana = "Lunes", horaInicio = "17:00", horaFin = "20:00")
                )
            )
            onSuccess()
        }
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
        serviciosViewModel_negocioScreen = FakeServicioViewModel()
    )
}
