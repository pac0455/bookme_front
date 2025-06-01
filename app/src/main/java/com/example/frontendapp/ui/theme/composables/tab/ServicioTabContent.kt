package com.example.frontendapp.ui.theme.composables.tab

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.composables.Items.ServicioCardItem
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel

private const val TAG = "ServicioTabContent"
@Composable
fun ServicioTabContent(
    servicioViewModel: ServicioViewModel,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val focusRequester = remember { FocusRequester() }

    // Estado de los servicios del ViewModel
    val servicioListState by servicioViewModel.serviciosDetalleState.collectAsState()

    // Lista filtrada según query
    val serviciosFiltered = remember(servicioListState, query) {
        (servicioListState.data ?: emptyList()).filter {
            it.nombre.contains(query, ignoreCase = true) || it.categoria.contains(query, ignoreCase = true)
        }
    }

    Column {
        HeaderSeccion("Servicios")

        // Carga inicial de servicios si no hay datos
        LaunchedEffect(servicioListState) {
            Log.d(TAG, "Estado servicioListState cambiado: $servicioListState")
            if (servicioListState is Resource.None) {
                Log.d(TAG, "Estado None detectado. Iniciando carga de servicios...")
                servicioViewModel.getServiciosDetalle()
            }
        }

        when (servicioListState) {
            is Resource.Loading -> {
                Log.d(TAG, "Mostrando loading")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                val message = (servicioListState as Resource.Error).message
                Log.e(TAG, "Error al cargar servicios: $message")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error al cargar servicios: $message")
                }
            }
            is Resource.Success -> {
                Log.d(TAG, "Mostrando lista de servicios. Cantidad: ${serviciosFiltered.size}")
                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                    flingBehavior = flingBehavior,
                    contentPadding = PaddingValues(horizontal = (screenWidth - cardWidth) / 2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(serviciosFiltered) { servicio ->
                        ServicioCardItem(
                            modifier = Modifier
                                .width(cardWidth),
                            servicio = servicio,
                            imageUrl = servicioViewModel.getServicioImageUrl(servicio.id) ?: "",
                            onClick = {
                                Log.d(TAG, "Servicio seleccionado: ${servicio.nombre} (id: ${servicio.id}) con el negocioId: ${servicio.negocioId}")


                                navController.navigate(NavigationItem.NEGOCIO_CARD_DETAILS.createRoute(servicio.negocioId))
                            }
                        )
                    }
                    if (serviciosFiltered.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No se encontraron servicios")
                                Log.d(TAG, "No se encontraron servicios con la búsqueda actual")
                            }
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}


// Preview con datos de ejemplo
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewServicioTabContent() {
    // Fake ViewModel para preview con estado hardcodeado
    ServicioTabContent(
        servicioViewModel = FakeServicioViewModel(),
        navController = rememberNavController(),

    )
}