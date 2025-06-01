package com.example.frontendapp.ui.theme.composables.tab

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.Principal_variacion6
import com.example.frontendapp.ui.theme.composables.CustomSeachBar
import com.example.frontendapp.ui.theme.composables.Items.NegocioCard
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.utils.UbicacionHelper

@Composable
fun NegocioTabContent(
    negocioViewModel: NegocioViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val negociosCardClienteState by negocioViewModel.negociosClienteState.collectAsState()
    val negociosCard = remember { mutableStateListOf<NegocioCardCliente>() }

    LaunchedEffect(negociosCardClienteState) {
        if (negociosCardClienteState is Resource.None) {
            val ubi = UbicacionHelper.obtenerUbicacionActual(context = context)
            negocioViewModel.getNegociosParaCliente(ubi)
        }
    }


    when (negociosCardClienteState) {
        is Resource.Success -> {
            negociosCard.clear()
            negociosCardClienteState.data?.let {
                Log.d("NegocioTabContent", "Negocios recibidos: ${it.size}")
                negociosCard.addAll(it)
            }
        }
        is Resource.Error -> {
            Log.e("NegocioTabContent", "Error al obtener negocios")
        }
        else -> Unit
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            HeaderSeccion("Negocios")
        }


        item {
            NegocioSeccion(
                "Abiertos ahora",
                negociosCard.filter { it.isOpen },
                negocioViewModel,
                rememberLazyListState(),
                rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                cardWidth = cardWidth,
                screenWidth = screenWidth,
                navController = navController,
            )
        }

        item {
            NegocioSeccion(
                "Mejor valorados",
                negociosCard.sortedByDescending { it.rating },
                negocioViewModel,
                rememberLazyListState(),
                rememberSnapFlingBehavior(lazyListState = rememberLazyListState()),
                cardWidth = cardWidth,
                screenWidth = screenWidth,
                navController = navController,
            )
        }
    }
}


    @Composable
fun NegocioSeccion(
    titulo: String,
    negocios: List<NegocioCardCliente>,
    negocioViewModel: NegocioViewModel,
    listState: LazyListState,
    flingBehavior: FlingBehavior,
    navController: NavController,
    cardWidth: Dp,
    screenWidth: Dp
) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = (screenWidth - cardWidth) / 2)
        ) {
            items(negocios, key = { it.id }) {
                Box(modifier = Modifier.width(cardWidth)) {
                    NegocioCard(
                        negocio = it,
                        imagenUrl = negocioViewModel.getNegocioImageUrl(it.id),
                        mostrarDistancia = it.distancia != null,
                        onClick = {
                            Log.d("NegocioCardList", "Clic en negocio: ${it.nombre}")
                            negocioViewModel.setTmpNegocioCard(it)
                            navController.navigate(
                                NavigationItem.NEGOCIO_CARD_DETAILS.createRoute(it.id)
                            )
                        }
                    )
                }
            }
        }
    }
}
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegocioTabContentPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        NegocioTabContent(
            negocioViewModel = FakeNegocioViewModel(),
            navController = navController
        )
    }
}
