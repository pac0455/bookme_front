package com.example.frontendapp.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.ui.theme.screens.BussinesMainScreen
import com.example.frontendapp.ui.theme.screens.LoginScreen
import com.example.frontendapp.ui.theme.screens.MainScreen
import com.example.frontendapp.ui.theme.screens.MapaScreen
import com.example.frontendapp.ui.theme.screens.NegocioClienteScrenn
import com.example.frontendapp.ui.theme.screens.NegocioFormScreen
import com.example.frontendapp.ui.theme.screens.RegisterScreen
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.loginViewModel
import com.example.frontendapp.ui.theme.viewmodels.registerViewModel

@Composable
fun Navigator( modifier: Modifier = Modifier,
               navController: NavHostController,
               startDestination: String = NavigationItem.MAIN.route) {
    //Logica de navegacion
    val registerViewModel = registerViewModel(AuthRemoteDataResource(RetrofitInstance.api))
    val loginViewModel = loginViewModel(AuthRemoteDataResource(RetrofitInstance.api))
    val negocioViewModel = NegocioViewModel()

    NavHost(modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.MAIN.route) { MainScreen(navController) }
        composable(NavigationItem.REGISTER.route) { RegisterScreen(navController,registerViewModel) }
        composable(NavigationItem.NEGOCIO_CLIENTE.route) { NegocioClienteScrenn(navController,registerViewModel) }
        composable(NavigationItem.LOGIN.route) { LoginScreen(navController,loginViewModel) }
        composable(NavigationItem.LOCATION.route) { NegocioFormScreen(navController,negocioViewModel) }
        composable(NavigationItem.BUSSINES_MAIN.route) { BussinesMainScreen(navController) }
        composable(NavigationItem.MAP_SELECT.route) { MapaScreen(navController) }


    }
}

//reource: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740