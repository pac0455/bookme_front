package com.example.frontendapp.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.frontendapp.ui.theme.screens.UsuarioNegocioMainScreen
import com.example.frontendapp.ui.theme.screens.HorarioForm
import com.example.frontendapp.ui.theme.screens.LoginScreen
import com.example.frontendapp.ui.theme.screens.MainScreen
import com.example.frontendapp.ui.theme.screens.MapaScreen
import com.example.frontendapp.ui.theme.screens.NegocioClienteScrenn
import com.example.frontendapp.ui.theme.screens.NegocioFormScreen
import com.example.frontendapp.ui.theme.screens.NegocioScreen
import com.example.frontendapp.ui.theme.screens.RegisterScreen
import com.example.frontendapp.ui.theme.viewmodels.BussinesMainViewModel
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel

@Composable
fun Navigator(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.MAIN.route,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    negocioViewModel: NegocioViewModel,
    bussinesMainViewModel: BussinesMainViewModel,
    reservasViewModel: ReservasViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.MAIN.route) {
            MainScreen(navController)
        }
        composable(NavigationItem.REGISTER.route) {
            RegisterScreen(navController, registerViewModel)
        }
        composable(NavigationItem.NEGOCIO_CLIENTE.route) {
            NegocioClienteScrenn(navController, registerViewModel)
        }
        composable(NavigationItem.LOGIN.route) {
            LoginScreen(navController, loginViewModel)
        }
        composable(NavigationItem.LOCATION.route) {
            negocioViewModel.resetNegocio()
            NegocioFormScreen(navController, negocioViewModel)
        }
        composable(NavigationItem.BUSSINES_MAIN.route) {
            UsuarioNegocioMainScreen(navController, bussinesMainViewModel)
        }
        composable(NavigationItem.MAP_SELECT.route) {
            MapaScreen(navController, negocioViewModel)
        }
        composable(NavigationItem.HORARIO_FORM.route) {
            HorarioForm(navController, negocioViewModel)
        }
        composable(
            route = "${Screen.NEGOCIO.name}/{negocioId}",
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0

            // Aquí puedes usar LaunchedEffect para llamar la carga solo cuando cambie el negocioId
            LaunchedEffect(negocioId) {
                if (negocioId != 0) {
                    negocioViewModel.loadNegocioById(
                        id = negocioId,
                        onLoading = { /* mostrar loading UI si quieres */ },
                        onSuccess = { /* ocultar loading UI */ },
                        onError = { mensaje -> /* mostrar error UI */ }
                    )
                }
            }

            NegocioScreen(viewModel = negocioViewModel, reservasViewModel= reservasViewModel, navController = navController)
        }
    }
}


//reource: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740