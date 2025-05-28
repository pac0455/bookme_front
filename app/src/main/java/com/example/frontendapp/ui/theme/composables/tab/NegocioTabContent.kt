package com.example.frontendapp.ui.theme.composables.tab

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.Principal_variacion6
import com.example.frontendapp.ui.theme.composables.CustomSeachBar
import com.example.frontendapp.ui.theme.composables.ListItems.NegocioCard
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.utils.UbicacionHelper

@Composable
fun NegocioTabContent(
    negocioViewModel: NegocioViewModel,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }


    val negociosCardClienteState by negocioViewModel.negociosClienteState.collectAsState()
    val negociosCard = remember { mutableStateListOf<NegocioCardCliente>() }

    // Obtener ubicación al iniciar
    LaunchedEffect(Unit) {
        val ubi = UbicacionHelper.obtenerUbicacionActual(context = context)
        negocioViewModel.getNegociosParaCliente(ubi)
    }

    // Manejar estados
    when (negociosCardClienteState) {
        is Resource.Success -> {
            negociosCard.clear()
            negociosCardClienteState.data?.let {
                Log.d("NegocioTabContent", "Negocios recibidos: ${it.size}")
                negociosCard.addAll(it)
            }
        }
        is Resource.Error -> {
            // Manejo de errores
            Log.e("NegocioTabContent", "Error al obtener negocios")
        }
        else -> Unit
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .clip(RoundedCornerShape(bottomEnd = 40.dp, bottomStart = 40.dp))
                .background(Principal_variacion3)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Puedes colocar contenido aquí (iconos, título, etc.)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(68.dp)
                .offset(y = (-32).dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(Principal_variacion6, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            CustomSeachBar(
                query = query,
                backgroundColor = Color.Transparent,
                onQueryChange = { query = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .focusRequester(focusRequester)
                    .clip(RoundedCornerShape(16.dp))
            )
            IconButton(
                onClick = { /* acción filtro */ },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .shadow(1.dp, RoundedCornerShape(12.dp))  // sombra muy sutil
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "Filtro",
                    tint = Color.Black.copy(alpha = 0.4f)  // negro con baja opacidad, sutil
                )
            }
        }

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = (screenWidth - cardWidth) / 2)
        ) {
            items(negociosCard, key = { it.id }) {
                Box(modifier = Modifier.width(cardWidth)) {
                    NegocioCard(
                        negocio = it,
                        imagenUrl = negocioViewModel.getNegocioImageUrl(it.id), // Aquí puedes enlazar imagen real si tienes
                        mostrarDistancia = it.distancia != null,
                        onClick = {
                            Log.d("NegocioCardList", "Clic en negocio: ${it.nombre}")
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNegocioTabContentWithAnimation() {
    val fakeNegocio = NegocioCardCliente(
        id = 1,
        nombre = "Negocio de prueba",
        descripcion = "Descripción breve",
        categoria = "Comida",
        direccion = "Calle Falsa 123",
        rating = 4.5f,
        reviewCount = 23,
        isActive = true,
        isOpen = true,
        distancia = 1.2,
        latitud = -34.6037,
        longitud = -58.3816
    )
    val fakeList = listOf(fakeNegocio)


    NegocioTabContent(
        negocioViewModel = FakeNegocioViewModel(),
        modifier = Modifier.fillMaxSize()
    )
}
