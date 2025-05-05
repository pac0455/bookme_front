package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.api.UserApi

class AuthRemoteDataResource(private val userApi: UserApi) {


    private fun validateRegisterUser(user: Usuario): String? {
        return when {
            user.username.isNullOrBlank() -> "El nombre de usuario no puede estar vacío."
            user.email.isNullOrBlank() -> "El correo electrónico no puede estar vacío."
            user.password.isNullOrBlank() -> "La contraseña no puede estar vacía."
            user.phoneNumber.isNullOrBlank() -> "EL telefono no puede estar vacía."
            else -> null // Sin errores
        }
    }

    private fun validateLogin(usuario: Usuario): String? {
        return when {
            usuario.email.isNullOrBlank() -> "El correo electrónico no puede estar vacío."
            usuario.password.isNullOrBlank() -> "La contraseña no puede estar vacía."
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

    suspend fun getAll(): Resource<List<Usuario>>{
        return try {
            val response = userApi.getAll()

            if (response.isSuccessful){
                Resource.Success(response.body() ?: emptyList())
            }else{
                val errorBody = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
            }
        }catch (ex : Exception){
            Resource.Error(ex.message ?: "error al cargar todos los usuarios")
        }
    }
    suspend fun delete(idUser: String): Resource<String>{
        return try {
            val response = userApi.delete(idUser)

            if (response.isSuccessful){
                Resource.Success(response.body().toString())
            }else{
                val errorBody = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
            }
        }catch (ex : Exception){
            Resource.Error(ex.message ?: "error al cargar todos los usuarios")
        }
    }

    //Metodo para registrar un usuario
    //Resource<String>> es como decir devuelve un objeto que tenfnga diferentes estados dependiendo de la solicitud
    suspend fun registerUser(usuario: Usuario): Resource<String> {
        // Validar los datos del usuario
        val validationError = validateRegisterUser(usuario)
        if (validationError != null) {
            return Resource.Error(validationError) // Retorna el mensaje de error de validación
        }

        // Intentar registrar al usuario a través de la API
        return try {
            val response = userApi.signup(usuario)

            if (response.isSuccessful) {
                Resource.Success("Registro exitoso")
            } else {
                // Podés extraer el error del body si querés más detalle
                val errorBody = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }

    // Método para iniciar sesión
    suspend fun login(usuario: Usuario): Resource<String> {
        // Validar los datos de inicio de sesión
        val validationError = validateLogin(usuario)
        if (validationError != null) {
            return Resource.Error(validationError) // Retorna el mensaje de error de validación
        }



        return try {
            val response = userApi.login(usuario)
            //Logica de login
            if (response.isSuccessful) {
                Resource.Success("Inicio de sesión exitoso")
            } else {
                Resource.Error(response.errorBody().toString())
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
}
