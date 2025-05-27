package com.example.frontendapp.ui.theme.composables.list

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

            val listaAnimada by remember { mutableStateOf(negocios) }

            LazyRow(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(listaAnimada, key = { it.id }) { negocioCard ->
                    NegocioCard(
                        negocio = negocioCard,
                        imagenUrl = null,
                        mostrarDistancia = negocioCard.distancia != null,
                        onClick = { Log.d("NegocioCardList", "Clic en negocio: ${negocioCard.nombre}") }
                    )
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
    val negocios = listOf(
        NegocioCardCliente(
            id = 1,
            nombre = "Psicología Madrid",
            descripcion = "Consultas para el bienestar emocional y mental.",
            categoria = "Psicología",
            direccion = "C. de Alcalá, 45",
            rating = 4.8f,
            reviewCount = 12,
            isActive = true,
            isOpen = true,
            distancia = 2.3,
            latitud = 40.4168,
            longitud = -3.7038
        ),
        NegocioCardCliente(
            id = 2,
            nombre = "Clínica Dental Sonrisa",
            descripcion = "Expertos en salud bucal con atención personalizada.",
            categoria = "Odontología",
            direccion = "Gran Vía, 100",
            rating = 4.5f,
            reviewCount = 20,
            isActive = true,
            isOpen = false,
            distancia = 5.7,
            latitud = 40.4200,
            longitud = -3.7050
        ),
        NegocioCardCliente(
            id = 3,
            nombre = "FisioActiva",
            descripcion = "Fisioterapia deportiva y rehabilitación avanzada.",
            categoria = "Fisioterapia",
            direccion = "Paseo del Prado, 15",
            rating = 5.0f,
            reviewCount = 8,
            isActive = true,
            isOpen = true,
            distancia = 1.2,
            latitud = 40.4140,
            longitud = -3.6950
        )
    )

    FrontendappTheme  {
        Surface(color = MaterialTheme.colorScheme.background) {
            NegocioCardList(
                negocioViewModel = FakeNegocioViewModel(),
            )
        }
    }
}
