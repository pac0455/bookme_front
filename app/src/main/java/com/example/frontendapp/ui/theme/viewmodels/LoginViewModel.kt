package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Usuario.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.data.remote.reponses.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val auth: AuthRepo) : ViewModel() {
    // Estado del usuario
    private val _usuarioState = MutableStateFlow(Usuario())
    val usuarioState: StateFlow<Usuario> = _usuarioState

    // Estado de la respuesta general
    private val _loginState = MutableStateFlow<Resource<LoginRegisterResultDTO>>(Resource.None())
    val loginState: StateFlow<Resource<LoginRegisterResultDTO>> = _loginState

    // Estado de errores de validación del backend
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors

    fun setNombre(nombre: String) {
        _usuarioState.update { it.copy(username = nombre) }
    }

    fun setEmail(email: String) {
        _usuarioState.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        _usuarioState.update { it.copy(password = password) }
    }

    fun reset() {
        _usuarioState.value = Usuario()
        _validationErrors.value = emptyMap()
    }

    fun loginUsuario(
        onLoading: (() -> Unit)? = null,
        onSuccess: ((LoginRegisterResultDTO) -> Unit)? = null,
        onError: ((String) -> Unit)? = null,
        onValidationError: ((Map<String, String>) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val user = _usuarioState.value
            onLoading?.invoke()
            _loginState.value = Resource.Loading()

            try {
                val result = auth.login(LoginRequest(user.email, user.password))
                when (result) {
                    is Resource.Success -> {
                        _loginState.value = result
                        _validationErrors.value = emptyMap()
                        onSuccess?.invoke(result.data!!)
                    }
                    is Resource.Error -> {
                        _loginState.value = result
                        val validation = result.validationResponse?.errors
                        if (!validation.isNullOrEmpty()) {
                            _validationErrors.value = validation
                            onValidationError?.invoke(validation)
                        } else {
                            onError?.invoke(result.message ?: "Error desconocido")
                        }
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error inesperado"
                _loginState.value = Resource.Error(errorMsg)
                onError?.invoke(errorMsg)
            }
        }
    }
}