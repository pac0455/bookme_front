package com.example.frontendapp.ui.theme.navigation

import ClienteMainScreen
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.frontendapp.ui.theme.composables.loadPages.TripleOrbitLoadingAnimation
import com.example.frontendapp.ui.theme.screens.NegociosScreen
import com.example.frontendapp.ui.theme.screens.HorarioForm
import com.example.frontendapp.ui.theme.screens.LoginScreen
import com.example.frontendapp.ui.theme.screens.MainScreen
import com.example.frontendapp.ui.theme.screens.MapaScreen
import com.example.frontendapp.ui.theme.screens.NegocioClienteScrenn
import com.example.frontendapp.ui.theme.screens.NegocioDetailScreen
import com.example.frontendapp.ui.theme.screens.NegocioFormScreen
import com.example.frontendapp.ui.theme.screens.NegocioScreen
import com.example.frontendapp.ui.theme.screens.RegisterScreen
import com.example.frontendapp.ui.theme.screens.ReservaForm
import com.example.frontendapp.ui.theme.screens.ServicioForm
import com.example.frontendapp.ui.theme.screens.ValoracionFormulario
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.HorariosViewModel
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ValoracionViewModel
import com.example.frontendapp.utils.UbicacionHelper

private val TAG="NAVIGATOR"
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
    servicioViewModel_ClienteMain: ServicioViewModel,
    reservaViewModel_ClienteMain: ReservasViewModel,
    negocioViewModel_ClienteMain: NegocioViewModel,
    categoriasViewModel: CategoriaViewModel,
    horarioViewModel_ClienteMain: HorariosViewModel,
    valoracionesViewModel_ClienteMain: ValoracionViewModel,
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
            RegisterScreen(navController, registerScreenViewModel)
        }
        composable(NavigationItem.NEGOCIO_CLIENTE.route) {
            NegocioClienteScrenn(navController, registerScreenViewModel)
        }
        composable(NavigationItem.LOGIN.route) {
            LoginScreen(navController, loginScreenViewModel)
        }
        composable(NavigationItem.NEGOCIO_FORM_SCREEN.route) {
            NegocioFormScreen(
                navController = navController,
                negocioViewModel = negocioFormViewModel,
                categoriasViewModel = categoriasViewModel
            )
        }
        composable(NavigationItem.BUSSINES_MAIN.route) {
            NegociosScreen(navController, usuarioNegocioMainViewModel)
        }
        composable(NavigationItem.MAP_SELECT.route) {
            MapaScreen(navController, negocioFormViewModel)
        }
        composable(NavigationItem.CLIENTE_MAIN_SCREEN.route) {
            ClienteMainScreen(
                navController=navController,
                servicioViewModel = servicioViewModel_ClienteMain,
                reservasViewModel =reservaViewModel_ClienteMain,
                negocioViewModel = negocioViewModel_ClienteMain,
                categoriasViewModel = categoriasViewModel,
                horarioViewModel = horarioViewModel_ClienteMain,
                valoracionesViewModel = valoracionesViewModel_ClienteMain
            )
        }
        composable(
            route = "${Screen.NEGOCIO_CARD_DETAILS.name}/{negocioId}",
            arguments = listOf(navArgument("negocioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: 0
            var negocioCargado by remember { mutableStateOf(false) }


            Log.d(TAG, "Pasando a la pantalla NegocioDetailScreen el id: $negocioId")
            //Cargar el negocio a partir de su id
            LaunchedEffect(negocioId) {
                negocioViewModel_ClienteMain.getNegocioParaCliente(
                    negocioId = negocioId,
                    ubicacion = UbicacionHelper.obtenerUbicacionActual(context),
                    onSuccess = {
                        if (it != null) {
                            negocioViewModel_ClienteMain.setTmpNegocioCard(it)
                            negocioCargado = true  // Marcar como cargado
                        }
                    },
                    onError = {
                        Log.d(TAG, "Error al cargar el negocio de pantalla servicio a negocio")
                        negocioCargado = true  // Evita loading infinito en caso de error
                    }
                )
            }
            if (negocioCargado) {
                NegocioDetailScreen(
                    navController = navController,
                    negocioViewModel = negocioViewModel_ClienteMain,
                    servicioViewModel = servicioViewModel_ClienteMain,
                    horariosViewModel = horarioViewModel_ClienteMain,
                    reservaViewModel = reservaViewModel_ClienteMain,
                    valoracionesViewModel = valoracionesViewModel_ClienteMain
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TripleOrbitLoadingAnimation()
                }
            }
        }

        composable(
            route= "${Screen.RESERVA_FORM.name}/{negocioId}",
            arguments = listOf(navArgument("negocioId") {type = NavType.IntType})
        ) { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: -1
            ReservaForm(
                negocioId = negocioId,
                navController = navController,
                reservasViewModel = reservaViewModel_ClienteMain,
                servicioViewModel = servicioViewModel_ClienteMain,
                horariosViewModel = horarioViewModel_ClienteMain
            )
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
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: -1
            var negocioCargado by remember { mutableStateOf(false) }

            LaunchedEffect(negocioId) {
                Log.d("NegocioConfig", "negocioId recibido: $negocioId")
                val currentNegocio = negocioFormViewModel.negocioState.value

                if (negocioId <= 0) {
                    Log.w("NegocioConfig", "ID inválido: $negocioId")
                    negocioCargado = true // evitar quedarse en loading
                } else if (currentNegocio.id == negocioId) {
                    Log.d("NegocioConfig", "Negocio ya cargado")
                    negocioCargado = true
                } else {
                    Log.d("NegocioConfig", "Cargando negocio con ID: $negocioId")
                    negocioFormViewModel.loadNegocioById(
                        id = negocioId,
                        onLoading = {
                            Log.d("NegocioConfig", "Cargando...")
                            negocioCargado = false
                        },
                        onSuccess = {
                            Log.d("NegocioConfig", "Negocio cargado exitosamente")
                            negocioCargado = true
                        },
                        onError = {
                            Log.e("NegocioConfig", "Error al cargar el negocio")
                            negocioCargado = true
                        }
                    )
                }
            }

            if (negocioCargado) {
                NegocioScreen(
                    navController = navController,
                    viewModel = negocioFormViewModel,
                    reservasViewModel = reservasNegocioScreenViewModel,
                    servicioViewModel = serviciosNegocioScreenViewModel,
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TripleOrbitLoadingAnimation()
                }
            }
        }
        composable(
           route="${Screen.VALORACION_FORM.name}/{negocioId}",
            arguments = listOf(navArgument("negocioId") {type = NavType.IntType})
        ){ backStackEntry ->
            val negocioId = backStackEntry.arguments?.getInt("negocioId") ?: -1
            Log.d("Navigation", "NegocioId recibido en ValoracionFormulario: $negocioId")
            ValoracionFormulario(
                valoracionesViewModel=valoracionesViewModel_ClienteMain,
                negocioId = negocioId,
                navController = navController
            )
        }
    }
}

//reource: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740