package com.example.frontendapp.ui.theme.composables


import android.annotation.SuppressLint
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
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.ListItems.NegocioListItem
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.BussinesMainViewModel


@Composable
fun NegocioList(navController: NavController,bussinesMainViewModel: BussinesMainViewModel) {
    val negocios by bussinesMainViewModel.negociosUsuario.collectAsState()
    val estado by bussinesMainViewModel.negociosApiState.collectAsState()


    LaunchedEffect (Unit) {
        bussinesMainViewModel.loadNegociosByUser()
    }

    when (estado) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${(estado as Resource.Error).message}")
            }
        }

        is Resource.Success -> {
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
                            negocio = negocio,
                            onEditClick = { navController.navigate(NavigationItem.NEGOCIO.createRoute(it.id))},
                            onDeleteClick = { bussinesMainViewModel.deleteNegocio(it.id) }
                        )

                    }
                }
            }
        }

        else -> {
            // Nada por defecto
        }
    }
}
class FakeBussinesMainViewModel : BussinesMainViewModel(
    negocioRemoteSource = NegocioRemoteSource(RetrofitInstance.negocioApi)
) {
    init {
        _negociosUsuario.value = listOf(
            Negocio(
                id = 1,
                nombre = "Tienda Natura",
                descripcion = "Productos naturales",
                direccion = "Calle Falsa 123",
                latitud = 40.1,
                longitud = -3.1,
                categoria = "Herbolario"
            ),
            Negocio(
                id = 2,
                nombre = "Bar Central",
                descripcion = "Cafetería tradicional",
                direccion = "Calle Real 456",
                latitud = 40.2,
                longitud = -3.2,
                categoria = "Bar"
            )
        )
        _negociosState.value = Resource.Success(_negociosUsuario.value)
    }

    override fun loadNegociosByUser() {
        // No hacer nada
    }
}




@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegocioListPreview() {
    FrontendappTheme {
        Scaffold { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                NegocioList(bussinesMainViewModel = FakeBussinesMainViewModel(), navController = rememberNavController())
            }
        }
    }
}


