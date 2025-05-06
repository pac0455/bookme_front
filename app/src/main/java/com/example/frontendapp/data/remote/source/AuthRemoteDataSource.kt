package com.example.frontendapp.data.remote.source

import com.example.frontendapp.data.model.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.api.UserApi
import com.example.frontendapp.data.remote.reponses.DeleteResponse

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
    suspend fun delete(idUser: String): Resource<String> {
        return try {
            val response = userApi.delete(idUser)

            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    Resource.Success(result.message.toString())
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
            }
        } catch (ex: Exception) {
            Resource.Error(ex.message ?: "Error al eliminar el usuario")
        }
    }


    //Metodo para registrar un usuario
    //Resource<String>> es como decir devuelve un objeto que tenfnga diferentes estados dependiendo de la solicitud
    suspend fun registerUser(usuario: Usuario): Resource<LoginRegisterResultDTO> {
        // Validar los datos del usuario
        val validationError = validateRegisterUser(usuario)
        if (validationError != null) {
            return Resource.Error(validationError)
        }

        return try {
            val response = userApi.signup(usuario)

            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorBody ?: "Desconocido"}")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }



    suspend fun login(usuario: Usuario): Resource<String> {
        val validationError = validateLogin(usuario)
        if (validationError != null) {
            return Resource.Error(validationError)
        }

        return try {
            val response = userApi.login(usuario)

            if (response.isSuccessful) {
                Resource.Success("Inicio de sesión exitoso")
            } else {
                val errorMessage = response.errorBody()?.string()
                Resource.Error("Error del servidor: ${response.code()} - ${errorMessage ?: "Desconocido"}")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }


}
