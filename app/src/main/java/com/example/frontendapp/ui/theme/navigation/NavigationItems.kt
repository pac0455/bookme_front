package com.example.frontendapp.ui.theme.navigation

enum class Screen {
    REGISTER,
    LOGIN,
    MAIN,
    NEGOCIO_CLIENTE,
    NEGOCIO_FORM_SCREEN,
    BUSSINES_MAIN,
    MAP_SELECT,
    HORARIO_FORM,
    NEGOCIO_CONFIG,
    SERVICIO_FORM,
    CLIENTE_MAIN_SCREEN,
    NEGOCIO_CARD_DETAILS,
    RESERVA_FORM,
    VALORACION_FORM,
    PREFERENCES_SCREEN,
    EDIT_PROFILE,
    CHANGE_PASSWORD,
}

sealed class NavigationItem(val route: String) {
    data object REGISTER : NavigationItem(Screen.REGISTER.name)
    data object PREFERENCES_SCREEN : NavigationItem(Screen.PREFERENCES_SCREEN.name)
    data object EDIT_PROFILE : NavigationItem(Screen.EDIT_PROFILE.name)
    data object CHANGE_PASSWORD : NavigationItem(Screen.CHANGE_PASSWORD.name)



    data object NEGOCIO_CLIENTE : NavigationItem(Screen.NEGOCIO_CLIENTE.name)
    data object LOGIN : NavigationItem(Screen.LOGIN.name)
    data object MAIN : NavigationItem(Screen.MAIN.name)
    data object NEGOCIO_FORM_SCREEN : NavigationItem(Screen.NEGOCIO_FORM_SCREEN.name)
    data object VALORACION_FORM : NavigationItem("${Screen.VALORACION_FORM.name}/{negocioId}"){
        fun createRoute(negocioId: Int): String = "${Screen.VALORACION_FORM.name}/${negocioId}"
    }
    data object BUSSINES_MAIN : NavigationItem(Screen.BUSSINES_MAIN.name)
    data object RESERVA_FORM : NavigationItem("${Screen.RESERVA_FORM.name}/negocioId"){
        fun createRoute(negocioId: Int): String = "${Screen.RESERVA_FORM.name}/$negocioId"
    }
    data object CLIENTE_MAIN_SCREEN : NavigationItem(Screen.CLIENTE_MAIN_SCREEN.name)
    data object NEGOCIO_CARD_DETAILS : NavigationItem("${Screen.NEGOCIO_CARD_DETAILS.name}/negocioId") {
        fun createRoute(negocioId: Int) = "${Screen.NEGOCIO_CARD_DETAILS.name}/$negocioId"
    }

    // Mapa con parámetros opcionales para definir el callback
    data object MAP_SELECT: NavigationItem("${Screen.MAP_SELECT.name}?callback={callback}") {
        // Rutas con diferentes tipos de callback
        fun createRouteWithCallback(callbackType: String = "default"): String =
            "${Screen.MAP_SELECT.name}?callback=$callbackType"

        // Rutas predefinidas para diferentes contextos
        val forNegocioForm = createRouteWithCallback("negocio_form")
        val forUserLocation = createRouteWithCallback("user_location")
        val default = createRouteWithCallback("default")
    }

    data object HORARIO_FORM : NavigationItem("${Screen.HORARIO_FORM.name}/{modo}") {
        fun createRoute(modo: String): String = "${Screen.HORARIO_FORM.name}/$modo"
    }
    data object SERVICIO_FORM : NavigationItem("${Screen.SERVICIO_FORM.name}/{modo}") {
        fun createRoute(modo: String): String = "${Screen.SERVICIO_FORM.name}/$modo"
        // Rutas predefinidas
        val create = createRoute("crear")
        val edit = createRoute("editar")
    }
    data object NEGOCIO_CONFIG: NavigationItem("${Screen.NEGOCIO_CONFIG.name}/{negocioId}") {
        fun createRoute(negocioId: Int): String = "${Screen.NEGOCIO_CONFIG.name}/$negocioId"
    }
}
