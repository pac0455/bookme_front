package com.example.frontendapp.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.ui.theme.screens.MainScreen
import com.example.frontendapp.ui.theme.screens.RegisterScreen
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel

@Composable
fun Navigator( modifier: Modifier = Modifier,
               navController: NavHostController,
               startDestination: String = NavigationItem.Main.route) {
    //Logica de navegacion
    val usuarioViewModel = UsuarioViewModel(AuthRemoteDataResource(RetrofitInstance.api))

    NavHost(modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Main.route) { MainScreen(navController) }
        composable(NavigationItem.Register.route) { RegisterScreen(navController,usuarioViewModel) }
    }
}

//reource: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740