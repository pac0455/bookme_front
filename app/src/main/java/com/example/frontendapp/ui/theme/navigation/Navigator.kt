package com.example.frontendapp.ui.theme.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.frontendapp.ui.theme.screens.ClienteMainScreen
import com.example.frontendapp.ui.theme.screens.UsuarioNegocioMainScreen
import com.example.frontendapp.ui.theme.screens.HorarioForm
import com.example.frontendapp.ui.theme.screens.LoginScreen
import com.example.frontendapp.ui.theme.screens.MainScreen
import com.example.frontendapp.ui.theme.screens.MapaScreen
import com.example.frontendapp.ui.theme.screens.NegocioClienteScrenn
import com.example.frontendapp.ui.theme.screens.NegocioFormScreen
import com.example.frontendapp.ui.theme.screens.NegocioScreen
import com.example.frontendapp.ui.theme.screens.RegisterScreen
import com.example.frontendapp.ui.theme.screens.ServicioForm
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel

@Composable
fun Navigator(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.MAIN.route,
    loginScreenViewModel: LoginViewModel,
    registerScreenViewModel: RegisterViewModel,
    negocioFormViewModel: NegocioViewModel,
    usuarioNegocioMainViewModel: NegocioViewModel,
    reservasNegocioScreenViewModel: ReservasViewModel,
    serviciosNegocioScreenViewModel: ServicioViewModel,
) {
    LaunchedEffect(Unit) {
        Log.d("NAVIGATION_DEBUG", "Navigator parameters received:")
        Log.d("NAVIGATION_DEBUG", "loginScreenViewModel: ${loginScreenViewModel.hashCode()}")
        Log.d("NAVIGATION_DEBUG", "registerScreenViewModel: ${registerScreenViewModel.hashCode()}")
        Log.d("NAVIGATION_DEBUG", "negocioFormViewModel: ${negocioFormViewModel.hashCode()}")
        Log.d("NAVIGATION_DEBUG", "usuarioNegocioMainViewModel: ${usuarioNegocioMainViewModel.hashCode()}")
        Log.d("NAVIGATION_DEBUG", "reservasNegocioScreenViewModel: ${reservasNegocioScreenViewModel.hashCode()}")
        Log.d("NAVIGATION_DEBUG", "serviciosNegocioScreenViewModel: ${serviciosNegocioScreenViewModel.hashCode()}")
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.MAIN.route) {
            MainScreen(navController)
        }
        composable(NavigationItem.REGISTER.route) {
            RegisterScreen(navController, registerScreenViewModel)
        }
        composable(NavigationItem.NEGOCIO_CLIENTE.route) {
            NegocioClienteScrenn(navController, registerScreenViewModel)
        }
        composable(NavigationItem.LOGIN.route) {
            LoginScreen(navController, loginScreenViewModel)
        }
        composable(NavigationItem.NEGOCIO_FORM_SCREEN.route) {
            NegocioFormScreen(navController, negocioFormViewModel)
        }
        composable(NavigationItem.BUSSINES_MAIN.route) {
            UsuarioNegocioMainScreen(navController, usuarioNegocioMainViewModel)
        }
        composable(NavigationItem.MAP_SELECT.route) {
            MapaScreen(navController, negocioFormViewModel)
        }
        composable(NavigationItem.CLIENTE_MAIN_SCREEN.route) {
            ClienteMainScreen(navController,reservasNegocioScreenViewModel)
        }
        composable(
            route = "HORARIO_FORM/{modo}",
            arguments = listOf(navArgument("modo") { defaultValue = "crear" })
        ) { backStackEntry ->
            val modo = backStackEntry.arguments?.getString("modo") ?: "crear"
            HorarioForm(
                navController = navController,
                modo = modo,
                negocioViewModel = negocioFormViewModel
            )
        }


        composable(
            route = NavigationItem.SERVICIO_FORM.route,
            arguments = listOf(navArgument("modo") { defaultValue = "crear" })
        ) { backStackEntry ->
            val modo = backStackEntry.arguments?.getString("modo") ?: "crear"
            ServicioForm(
                navController = navController,
                servicioViewModel = serviciosNegocioScreenViewModel,
                modo = modo
            )
        }

        composable(
            route = "${Screen.NEGOCIO_CONFIG.name}/{negocioId}",
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0

            LaunchedEffect(negocioId) {
                val currentNegocio = negocioFormViewModel.negocioState.value
                if (negocioId != 0 && (currentNegocio == null || currentNegocio.id != negocioId)) {
                    negocioFormViewModel.loadNegocioById(
                        id = negocioId,
                        onLoading = { },
                        onSuccess = { },
                        onError = { mensaje -> }
                    )
                }
            }



            NegocioScreen(
                navController = navController,
                viewModel = negocioFormViewModel,
                reservasViewModel = reservasNegocioScreenViewModel,
                serviciosViewModel_negocioScreen = serviciosNegocioScreenViewModel,
            )
        }
    }
}

//reource: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740