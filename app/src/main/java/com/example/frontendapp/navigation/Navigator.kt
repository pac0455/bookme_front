package com.example.frontendapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.screens.MainScreen
import com.example.frontendapp.ui.theme.screens.RegisterScreen

@Composable
fun Navigator( modifier: Modifier = Modifier,
               navController: NavHostController,
               startDestination: String = NavigationItem.Main.route) {
    NavHost(modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Main.route) { MainScreen(navController) }
        composable(NavigationItem.Register.route) { RegisterScreen(navController) }
    }
}

//reource: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740