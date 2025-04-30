package com.example.frontendapp.ui.theme.navigation

enum class Screen {
    REGISTER,
    LOGIN,
    MAIN,
    NEGOCIO_CLIENTE
}
sealed class NavigationItem(val route: String) {
    data object REGISTER : NavigationItem(Screen.REGISTER.name)
    data object NEGOCIO_CLIENTE : NavigationItem(Screen.NEGOCIO_CLIENTE.name)
    data object LOGIN : NavigationItem(Screen.LOGIN.name)
    data object MAIN : NavigationItem(Screen.MAIN.name)
}