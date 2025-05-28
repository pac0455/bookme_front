package com.example.frontendapp.ui.theme.composables.list

import android.util.Log
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.ListItems.NegocioCard
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.utils.UbicacionHelper


@Composable
fun NegocioCardList(
    negocioViewModel: NegocioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val negociosState by negocioViewModel.negociosClienteState.collectAsState()

    // Llamamos a la API solo una vez al entrar en el Composable
    LaunchedEffect(Unit) {
        Log.d("NegocioCardList", "Solicitando ubicación del usuario...")
        val ubicacion = UbicacionHelper.obtenerUbicacionActual(context)
        Log.d("NegocioCardList", "Ubicación obtenida: $ubicacion")

        negocioViewModel.getNegociosParaCliente(
            ubicacion = ubicacion,
            onLoading = {
                Log.d("NegocioCardList", "Cargando negocios...")
            },
            onError = {
                Log.e("NegocioCardList", "Error al cargar negocios: $it")
            },
            onSuccess = {
                Log.d("NegocioCardList", "Negocios obtenidos con éxito: ${it.size} negocios")
            }
        )
    }

    when (val state = negociosState) {
        is Resource.Success -> {
            val negocios = state.data.orEmpty()
            Log.d("NegocioCardList", "Mostrando ${negocios.size} negocios")

            val listState = rememberLazyListState()
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val cardWidth = screenWidth * 0.85f

            val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                modifier = modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = (screenWidth - cardWidth) / 2)
            ) {
                items(negocios, key = { it.id }) { negocioCard ->
                    Box(
                        modifier = Modifier.width(cardWidth)
                    ) {
                        NegocioCard(
                            negocio = negocioCard,
                            imagenUrl = negocioViewModel.getNegocioImageUrl(negocioCard.id),
                            mostrarDistancia = negocioCard.distancia != null,
                            onClick = {
                                Log.d("NegocioCardList", "Clic en negocio: ${negocioCard.nombre}")
                            }
                        )
                    }
                }
            }
        }

        is Resource.Error -> {
            Log.e("NegocioCardList", "Error al renderizar: ${state.message}")
            Text(
                text = "No se pudieron cargar los negocios",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
        }

        is Resource.Loading -> {
            Log.d("NegocioCardList", "Cargando estado actual...")
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        else -> {
            Log.d("NegocioCardList", "Estado desconocido o vacío.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNegocioCardList() {
    FrontendappTheme  {
        Surface(color = MaterialTheme.colorScheme.background) {

            NegocioCardList(
                negocioViewModel = FakeNegocioViewModel(),
            )
        }
    }
}
