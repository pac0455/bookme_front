package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.LoginRequest
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.api.ApiService

class AuthRemoteDataResource(private val apiService: ApiService) {

    // Método para validar registro
    private fun validateRegisterUser(username: String?, email: String?, password: String?): String? {
        return when {
            username.isNullOrBlank() -> "El nombre de usuario no puede estar vacío."
            email.isNullOrBlank() -> "El correo electrónico no puede estar vacío."
            password.isNullOrBlank() -> "La contraseña no puede estar vacía."
            else -> null // Sin errores
        }
    }
    private fun validateRegisterUser(email: String?, password: String?): String? {
        return when {
            email.isNullOrBlank() -> "El correo electrónico no puede estar vacío."
            password.isNullOrBlank() -> "La contraseña no puede estar vacía."
            else -> null // Sin errores
        }
    }
    // Método para registrar un usuario
    suspend fun signupWithGoogle(usuario: Usuario) {
        // Implementación para registrar al usuario
    }

    // Método para iniciar sesión con Google
    suspend fun loginWithGoogle(token: String) {
        // Implementación para iniciar sesión con Google
    }

    //Metodo para registrar un usuario
    //Resource<String>> es como decir devuelve un objeto que tenfnga diferentes estados dependiendo de la solicitud
    suspend fun registerUser(usuario: Usuario): Resource<String> {
        // Validar los datos del usuario
        val validationError = validateRegisterUser(usuario.email, usuario.contrasenaHash)
        if (validationError != null) {
            return Resource.Error(validationError) // Retorna el mensaje de error de validación
        }

        // Intentar registrar al usuario a través de la API
        return try {
            //Logica de registro
            return Resource.Success("Registro exitoso")
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    // Método para iniciar sesión
    suspend fun login(email: String, password: String): Resource<String> {
        // Validar los datos de inicio de sesión
        if (email.isBlank() || password.isBlank()) return Resource.Error("El correo electrónico y la contraseña no pueden estar vacíos.")


        val loginRequest = LoginRequest(email, password)
        return try {
            //Logica de login
            if (true) {
                Resource.Success("Inicio de sesión exitoso")
            } else {
                Resource.Error("Error al iniciar sesión")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
}
