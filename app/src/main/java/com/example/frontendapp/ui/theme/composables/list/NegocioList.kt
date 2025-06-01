package com.example.frontendapp.ui.theme.composables.list


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.Items.NegocioListItem
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel


@Composable
fun NegocioList(
    navController: NavController,
    bussinesMainViewModel: NegocioViewModel
) {
    val negociosState by bussinesMainViewModel.negociosByUserIdState.collectAsState()
    val deleteState by bussinesMainViewModel.deleteNegocioState.collectAsState()

    // Cargar negocios al entrar por primera vez
    LaunchedEffect(Unit) {
        bussinesMainViewModel.getNegociosByUserId()
    }

    // Muestra resultado de eliminación
    LaunchedEffect(deleteState) {
        if (deleteState is Resource.Success) {
            bussinesMainViewModel.getNegociosByUserId()
        }
    }

    when (negociosState) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error al cargar los negocios.")
                Log.d("NegocioList","Error: ${(negociosState as Resource.Error).message}")
            }

        }

        is Resource.Success -> {
            val negocios = (negociosState as Resource.Success<List<Negocio>>).data.orEmpty()
            if (negocios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay negocios para mostrar.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(negocios) { negocio ->
                        NegocioListItem(
                            viewModel = bussinesMainViewModel,
                            negocio = negocio,
                            onCLickVer = {
                                navController.navigate(NavigationItem.NEGOCIO_CONFIG.createRoute(it.id))
                            },
                            onEditClick = {
                                bussinesMainViewModel.loadNegocioById(it.id)
                                navController.navigate(NavigationItem.NEGOCIO_FORM_SCREEN.route)
                            },
                            onDeleteClick = {
                                bussinesMainViewModel.deleteNegocio(negocio.id)
                            }
                        )
                    }
                }
            }
        }

        else -> {}
    }
}
@Composable
fun NoNegocios(){
    Box(Modifier.fillMaxSize()){

    }
}



@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegocioListPreview() {
    FrontendappTheme {
        Scaffold { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                NegocioList(bussinesMainViewModel = FakeNegocioViewModel(), navController = rememberNavController())
            }
        }
    }
}