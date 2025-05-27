package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.composables.modal.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import com.example.frontendapp.ui.theme.composables.ListItems.NegocioCard
import com.example.frontendapp.ui.theme.composables.list.NegocioCardList
import com.example.frontendapp.ui.theme.composables.navigation.TabPagerScaffold
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import com.example.frontendapp.utils.UbicacionHelper

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun ClienteMainScreen(
    navController: NavController,
    reservasViewModel: ReservasViewModel,
    servicioViewModel: ServicioViewModel,
    negocioViewModel: NegocioViewModel,
    categoriasViewModel: CategoriaViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Interceptar botón atrás
    BackHandler { showDialog = true }

    // Diálogo de logout
    LogoutConfirmationDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirmLogout = {
            showDialog = false
            RetrofitInstance.setToken("")
            RetrofitInstance.setRoles(emptyList())
            navController.navigate(NavigationItem.LOGIN.route) {
                popUpTo(0)
            }
        }
    )

    //Componente que navega entre las disitntas seccion/tabs
    TabPagerScaffold(
        tabItems = listOf(
            TabItem(
                title = "Negocio",
                unSelectedIcon = Icons.Outlined.Storefront,
                selectedIcon = Icons.Filled.Store,
                content = {

                    NegocioCardList(negocioViewModel = negocioViewModel)
                }
            ),
            TabItem(
                title = "Reservas",
                unSelectedIcon = Icons.Outlined.Storefront,
                selectedIcon = Icons.Filled.Store,
                content = {
                    Text("Contenido de reservas")
                }
            ),
            TabItem(
                title = "Mis reservas",
                unSelectedIcon = Icons.Outlined.Storefront,
                selectedIcon = Icons.Filled.Store,
                content = {
                    Text("Contenido de mis reservas")
                }
            )
        )
    )
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ClienteMainScreenPreview() {
    val controller = rememberNavController()
    ClienteMainScreen(
        controller,
        FakeReservasViewModel(),
        servicioViewModel = FakeServicioViewModel(),
        negocioViewModel = FakeNegocioViewModel(),
        categoriasViewModel = CategoriaViewModel(CategoriaRemoteDataSource(RetrofitInstance.categoriaApi))
    )
}