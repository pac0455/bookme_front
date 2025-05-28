package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.composables.modal.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.Principal_variacion6
import com.example.frontendapp.ui.theme.composables.tab.NegocioTabContent
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import com.example.frontendapp.utils.UbicacionHelper
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.ShapeCornerRadius
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable

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
    val context = LocalContext.current
    val negociosCardClienteState by negocioViewModel.negociosClienteState.collectAsState()
    val negociosCard = remember { mutableListOf<NegocioCardCliente>() }

    val navigationBar  =  remember { NavItems.values() }

    LaunchedEffect(Unit) {
        val ubi = UbicacionHelper.obtenerUbicacionActual(context = context)
        negocioViewModel.getNegociosParaCliente(ubi)
    }

    when (negociosCardClienteState) {
        is Resource.Success -> {
            negociosCard.clear()
            negociosCardClienteState.data?.let {
                Log.d("ClienteMainScreen", "Negocios recibidos: ${it.size}")
                negociosCard.addAll(it) }
        }
        is Resource.Error -> {
            // Mostrar error, log o Snackbar
        }
        else -> { /* Loading o Idle */ }
    }

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
    val  tabs = listOf(
        TabItem(
            title = "Negocio",
            unSelectedIcon = Icons.Outlined.Storefront,
            selectedIcon = Icons.Filled.Store,
            content = { NegocioTabContent(negocioViewModel) }
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
    Scaffold(
        modifier = Modifier.padding(12.dp),
        bottomBar = {
            AnimatedNavigationBar(
                cornerRadius =  shapeCornerRadius(34.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(100.dp),
                selectedIndex = selectedTabIndex,
                ballColor = Principal_variacion3,
                indentAnimation = Height(tween(400)),
                ballAnimation = Parabolic(tween(400)),
                barColor = Principal_variacion6,
            ) {
                navigationBar.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { selectedTabIndex = item.ordinal },
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            imageVector = item.icon,
                            contentDescription = "",

                            )
                    }
                }
            }
        }
    ) { p ->
        Column(modifier = Modifier.padding(p)) {

        }

    }






    //Componente que navega entre las disitntas seccion/tabs

}
enum class NavItems(
val icon : ImageVector
){
    Person(icon = Icons.Default.Person),
    CALL(icon = Icons.Default.Call)

}
/*TabPagerScaffold(
        tabItems = listOf(
            TabItem(
                title = "Negocio",
                unSelectedIcon = Icons.Outlined.Storefront,
                selectedIcon = Icons.Filled.Store,
                content = { NegocioTabContent(negocioViewModel) }
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
    )*/

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