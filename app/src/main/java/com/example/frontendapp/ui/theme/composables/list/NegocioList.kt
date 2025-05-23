package com.example.frontendapp.ui.theme.composables.list


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
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.ListItems.NegocioListItem
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.BussinesMainViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeBussinesMainViewModel


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

        else -> { }
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


