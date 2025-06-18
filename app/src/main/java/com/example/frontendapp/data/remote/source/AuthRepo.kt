package com.example.frontendapp.data.remote.source

import android.util.Log
import com.example.frontendapp.data.model.Api.ApiResponse
import com.example.frontendapp.data.model.Api.ValidationErrorResponse
import com.example.frontendapp.data.model.UI.UpdatePasswordDTO
import com.example.frontendapp.data.model.Usuario.ConfirmMailDTO
import com.example.frontendapp.data.model.Usuario.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario.RegisterDTO
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.model.Usuario.UpdateDataUserDTO
import com.example.frontendapp.data.model.Usuario.toRegisterDTO
import com.example.frontendapp.data.remote.api.UserApi
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.reponses.SingleMessageResponse
import com.example.frontendapp.data.remote.request.LoginRequest
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Response

class AuthRepo(private val userApi: UserApi) {

    private fun validateLogin(usuario: Usuario): ValidationErrorResponse? {
        val errors = mutableMapOf<String, String>()

        if (usuario.email.isNullOrBlank()) {
            errors["email"] = "El correo electrónico no puede estar vacío."
        }


        if (usuario.password.isNullOrBlank()) {
            errors["password"] = "La contraseña no puede estar vacía."
        }


        return if (errors.isNotEmpty()) {
            ValidationErrorResponse(success = false, errors = errors)
        } else {
            null // Validacion pasada
        }
    }

    suspend fun updateNombre(usuario: UpdateDataUserDTO): Resource<UpdateDataUserDTO> = try {
        Log.d("AuthRepo", usuario.toString())
        val response = userApi.updateNombre(usuario)
        handleResponse(response)
    } catch (e: Exception) {
        Resource.Error("Excepción de red o inesperada: ${e.localizedMessage}")
    }

    suspend fun desbloquear(usuarioId: String): Resource<SingleMessageResponse> = try {
        Log.d("AuthRepo", usuarioId.toString())
        val response = userApi.desbloquearUsuario(usuarioId)
        handleResponse(response)
    } catch (e: Exception) {
        Resource.Error("Excepción de red o inesperada: ${e.localizedMessage}")
    }
    suspend fun delete(usuarioId: String): Resource<SingleMessageResponse> = try {
        val response = userApi.deleteUser(usuarioId)
        handleResponse(response)
    } catch (e: Exception) {
        Resource.Error("Excepción de red o inesperada: ${e.localizedMessage}")
    }


    suspend fun bloquear(usuarioId: String): Resource<SingleMessageResponse> = try {
        Log.d("AuthRepo", usuarioId.toString())
        val response = userApi.bloquearUsuario(usuarioId)
        handleResponse(response)
    } catch (e: Exception) {
        Resource.Error("Excepción de red o inesperada: ${e.localizedMessage}")
    }

    suspend fun updatePassword(usuario: UpdatePasswordDTO): Resource<UpdatePasswordDTO> = try {
        Log.d("AuthRepo", usuario.toString())
        val response = userApi.updatePassword(usuario)
        handleResponse(response)
    } catch (e: Exception) {
        Resource.Error("Excepción de red o inesperada: ${e.localizedMessage}")
    }


    suspend fun getAll(): Resource<List<Usuario>> = try {
        val response = userApi.getAll()
        if (response.isSuccessful) {
            Resource.Success(response.body() ?: emptyList())
        } else {
            Resource.Error("Error del servidor: ${response.code()} - ${response.errorBody()?.string() ?: "Desconocido"}")
        }
    } catch (ex: Exception) {
        Resource.Error(ex.message ?: "Error al cargar los usuarios")
    }


    suspend fun validateRegistration(registerDTO: RegisterDTO): Resource<ValidationErrorResponse> = try {
        val response = userApi.validateRegistration(registerDTO)
        handleResponse(response)
    } catch (ex: Exception) {
        Resource.Error("Error de red: ${ex.message}")
    }

    suspend fun sendAuthenticationCode(userId: String): Resource<SingleMessageResponse> = try {
        val response = userApi.sendAuthenticationCode(userId)
        handleResponse(response) // Convierte Response<T> a Resource<T>
    } catch (ex: Exception) {
        Resource.Error("Error de red: ${ex.message}")
    }

    suspend fun confirmCode(model: ConfirmMailDTO): Resource<SingleMessageResponse> = try {
        val response = userApi.confirmEmail(model)
        handleResponse(response) // Convierte Response<T> a Resource<T>
    } catch (ex: Exception) {
        Resource.Error("Error de red: ${ex.message}")
    }

    suspend fun registerCliente(baseUsuario: Usuario): Resource<LoginRegisterResultDTO> =
        registerUser(baseUsuario.copy(isNegocio = false))

    suspend fun registerNegocio(baseUsuario: Usuario): Resource<LoginRegisterResultDTO> =
        registerUser(baseUsuario.copy(isNegocio = true))

    suspend fun registerUser(usuario: Usuario): Resource<LoginRegisterResultDTO> = try {
        val response = userApi.signup(usuario.toRegisterDTO())
        wrapperHandleResponse(response)
    } catch (e: Exception) {
        Resource.Error("Excepción de red o inesperada: ${e.localizedMessage}")
    }

    suspend fun login(login: LoginRequest): Resource<LoginRegisterResultDTO> {
        val usuario = Usuario(email = login.email, password = login.password)

        validateLogin(usuario)?.let { validationError ->
            return Resource.Error(
                message = "Error de validación",
                validationResponse = validationError
            )
        }

        return try {
            val response = userApi.login(login)
            handleResponse(response)
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }


    private fun <T> wrapperHandleResponse(response: Response<ApiResponse<T>>): Resource<T> {
        if (response.isSuccessful) {
            val body = response.body()
            return if (body?.success == true && body.data != null) {
                Resource.Success(body.data)
            } else {
                Resource.Error(body?.message ?: "Respuesta del servidor inválida.")
            }
        }

        val rawError = response.errorBody()?.string()
        val validationError = try {
            Gson().fromJson(rawError, ValidationErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

        return if (validationError != null && !validationError.errors.isNullOrEmpty()) {
            Resource.Error(
                message = validationError.errors.values.firstOrNull() ?: "Error de validación.",
                validationResponse = validationError
            )
        } else {
            val fallbackMessage = try {
                JSONObject(rawError ?: "").optString("message", "Error desconocido del servidor.")
            } catch (e: Exception) {
                rawError ?: "Error desconocido del servidor."
            }
            Resource.Error("Error del servidor: ${response.code()} - $fallbackMessage")
        }
    }


    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        if (response.isSuccessful) {
            val result = response.body()
            return if (result != null) Resource.Success(result)
            else if (response.code() == 204) Resource.Success(Unit as T)
            else Resource.Error("Respuesta vacía del servidor.")
        }

        val rawError = response.errorBody()?.string()
        val validationError = try {
            Gson().fromJson(rawError, ValidationErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

        return if (validationError != null && !validationError.errors.isNullOrEmpty()) {
            Resource.Error(
                message = validationError.errors.values.firstOrNull() ?: "Error de validación.",
                validationResponse = validationError
            )
        } else {
            val fallbackMessage = try {
                JSONObject(rawError ?: "").optString("message", "Error desconocido del servidor.")
            } catch (e: Exception) {
                rawError ?: "Error desconocido del servidor."
            }
            Resource.Error("Error del servidor: ${response.code()} - $fallbackMessage")
        }
    }
}