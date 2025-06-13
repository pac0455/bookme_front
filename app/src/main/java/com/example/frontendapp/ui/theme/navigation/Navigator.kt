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
import com.example.frontendapp.ui.theme.screens.AdminPanel
import com.example.frontendapp.ui.theme.screens.CambiarContrasenaScreen
import com.example.frontendapp.ui.theme.screens.EditarUsuarioScreenMejorada
import com.example.frontendapp.ui.theme.screens.NegociosScreen
import com.example.frontendapp.ui.theme.screens.HorarioForm
import com.example.frontendapp.ui.theme.screens.LoginScreen
import com.example.frontendapp.ui.theme.screens.MainScreen
import com.example.frontendapp.ui.theme.screens.MapaScreenMejorada
import com.example.frontendapp.ui.theme.screens.NegocioClienteScrenn
import com.example.frontendapp.ui.theme.screens.NegocioDetailScreen
import com.example.frontendapp.ui.theme.screens.NegocioFormScreen
import com.example.frontendapp.ui.theme.screens.NegocioScreen
import com.example.frontendapp.ui.theme.screens.PreferenciasScreenMejorada
import com.example.frontendapp.ui.theme.screens.RegisterScreen
import com.example.frontendapp.ui.theme.screens.ReservaForm
import com.example.frontendapp.ui.theme.screens.SendMailScreen
import com.example.frontendapp.ui.theme.screens.ServicioForm
import com.example.frontendapp.ui.theme.screens.ValoracionFormulario
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.HorariosViewModel
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
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
        usuarioViewModel_EditProfile: UsuarioViewModel,
        usuarioViewModel_AdminPanel: UsuarioViewModel,
        negocioViewModel_AdminPanel: NegocioViewModel,
    ) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.MAIN.route) {
            MainScreen(navController)
        }
        composable(NavigationItem.SEND_MAIL_SCREEN.route) {
            SendMailScreen(
                usuarioViewModel = usuarioViewModel_AdminPanel,
                navController = navController
            )
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

        // Mapa con callback configurable
        composable(
            route = NavigationItem.MAP_SELECT.route,
            arguments = listOf(
                navArgument("callback") {
                    type = NavType.StringType
                    defaultValue = "default"
                }
            )
        ) { backStackEntry ->
            val callbackType = backStackEntry.arguments?.getString("callback") ?: "default"

            // Configurar el callback según el tipo
            val onLocationSelected: ((Double, Double) -> Unit)? = when (callbackType) {
                "negocio_form" -> { lat, lng ->
                    // Usar el setUbicacion del ViewModel de negocio
                    negocioFormViewModel.setUbicacion(lat, lng)
                    Log.d(TAG, "Ubicación establecida en negocio form: $lat, $lng")
                }
                "user_location" -> { lat, lng ->
                    // Para ubicación de usuario (si necesitas otro comportamiento)
                    negocioViewModel_ClienteMain.setUbicacion(lat, lng)
                    Log.d(TAG, "Ubicación establecida para usuario: $lat, $lng")
                }
                "default" -> null // Usar el comportamiento por defecto del mapa
                else -> null
            }

            MapaScreenMejorada(
                navController = navController,
                negocioViewModel = negocioFormViewModel,
                onLocationSelected = onLocationSelected
            )
        }

        composable(NavigationItem.CLIENTE_MAIN_SCREEN.route) {
            ClienteMainScreen(
                navController=navController,
                servicioViewModel = servicioViewModel_ClienteMain,
                reservasViewModel =reservaViewModel_ClienteMain,
                negocioViewModel = negocioViewModel_ClienteMain,

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
        composable(NavigationItem.PREFERENCES_SCREEN.route) {
            PreferenciasScreenMejorada(
                onNavigateToEditProfile = {
                    navController.navigate(NavigationItem.EDIT_PROFILE.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(NavigationItem.CHANGE_PASSWORD.route)
                },
            )
        }
        composable(NavigationItem.CHANGE_PASSWORD.route) {
            CambiarContrasenaScreen(
                usuarioViewModel = usuarioViewModel_EditProfile,
                navController = navController,
            )
        }


        composable(NavigationItem.EDIT_PROFILE.route) {
            EditarUsuarioScreenMejorada(
                navController = navController,
                usuarioViewModel = usuarioViewModel_EditProfile
            )
        }
        composable(NavigationItem.ADMIN_PANEL_SCREEN.route) {
            AdminPanel(
                usuarioViewModel= usuarioViewModel_AdminPanel,
                negocioViewModel = negocioViewModel_AdminPanel,
                navController=navController
            )
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
            route = "${Screen.HORARIO_FORM.name}/{modo}",
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
