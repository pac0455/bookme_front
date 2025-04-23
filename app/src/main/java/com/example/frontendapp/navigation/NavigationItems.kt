package com.example.frontendapp.navigation

enum class Screen {
    REGISTER,
    LOGIN,
    MAIN,
}
sealed class NavigationItem(val route: String) {
    data object Register : NavigationItem(Screen.REGISTER.name)
    data object Login : NavigationItem(Screen.LOGIN.name)
    data object Main : NavigationItem(Screen.MAIN.name)
}