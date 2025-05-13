package com.example.frontendapp.ui.theme.navigation

enum class Screen {
    REGISTER,
    LOGIN,
    MAIN,
    NEGOCIO_CLIENTE,
    LOCATION,
    BUSSINES_MAIN
}
sealed class NavigationItem(val route: String) {
    data object REGISTER : NavigationItem(Screen.REGISTER.name)
    data object NEGOCIO_CLIENTE : NavigationItem(Screen.NEGOCIO_CLIENTE.name)
    data object LOGIN : NavigationItem(Screen.LOGIN.name)
    data object MAIN : NavigationItem(Screen.MAIN.name)
    data object LOCATION : NavigationItem(Screen.LOCATION.name)
    data object BUSSINES_MAIN : NavigationItem(Screen.BUSSINES_MAIN.name)

}