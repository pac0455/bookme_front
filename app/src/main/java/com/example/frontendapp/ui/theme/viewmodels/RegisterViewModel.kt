package com.example.frontendapp.ui.theme.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Api.ValidationErrorResponse
import com.example.frontendapp.data.model.Usuario.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.model.Usuario.toRegisterDTO
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.data.remote.reponses.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val auth: AuthRepo) : ViewModel() {

    // Estado del usuario
    private val _uiState = MutableStateFlow(Usuario())
    val uiState: StateFlow<Usuario> = _uiState

    // Estado para el registro
    private val _registerState = MutableStateFlow<Resource<LoginRegisterResultDTO>>(Resource.None<LoginRegisterResultDTO>())
    val registerState: StateFlow<Resource<LoginRegisterResultDTO>> = _registerState

    val _registerResult = MutableStateFlow<Resource<LoginRegisterResultDTO>>(Resource.None())
    val registerResult: StateFlow<Resource<LoginRegisterResultDTO>> = _registerResult

    // Estado para la validación
    private val _validationState = MutableStateFlow<Resource<ValidationErrorResponse>>(Resource.None())
    val validationState: StateFlow<Resource<ValidationErrorResponse>> = _validationState

    fun resetUi(){
        _uiState.value = Usuario()
        _registerResult.value = Resource.None()
        _validationState.value = Resource.None()
    }
    // Métodos para actualizar el estado del usuario
    fun setNombre(nombre: String) {
        _uiState.update { currentState ->
            currentState.copy(username = nombre)
        }
    }
    fun setEsNegocio(esNegocio: Boolean) {
        _uiState.update { it.copy(isNegocio = esNegocio) }
    }

    fun setCorreo(email: String) {
        _uiState.update { currentState ->
            currentState.copy(email = email)
        }
    }

    fun setTelefono(telefono: String) {
        _uiState.update { currentState ->
            currentState.copy(phoneNumber = telefono)
        }
    }

    fun setContrasena(contrasenaHash: String) {
        _uiState.update { currentState ->
            currentState.copy(password = contrasenaHash)
        }
    }
    // Función para registrar como cliente
    fun registrarUsuario(
        onLoading: (() -> Unit)? = null,
        onSuccess: ((LoginRegisterResultDTO) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val user = _uiState.value

            onLoading?.invoke()
            _registerState.value = Resource.Loading()

            try {
                val result = auth.registerUser(user)
                when (result) {
                    is Resource.Success -> {
                        _registerState.value = result
                        result.data?.let { onSuccess?.invoke(it) }
                    }
                    is Resource.Error -> {
                        _registerState.value = result
                        onError?.invoke(result.message ?: "Error al registrar")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error inesperado"
                _registerState.value = Resource.Error(errorMsg)
                onError?.invoke(errorMsg)
            }
        }
    }
    fun validateRegistration(
        onLoading: () -> Unit = {},
        onSuccess: (ValidationErrorResponse) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            onLoading()
            _validationState.value = Resource.Loading()

            val user = _uiState.value.toRegisterDTO()

            try {
                val result = auth.validateRegistration(user)
                _validationState.value = result // Actualiza el estado de validación

                when (result) {
                    is Resource.Success -> {
                        val validationResponse = result.data
                        if (validationResponse != null) {
                            onSuccess(validationResponse)
                        }
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Error al validar") // Invoca onError con el mensaje de error
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error inesperado"
                _validationState.value = Resource.Error(errorMsg)
                onError(errorMsg) // Invoca onError con el mensaje de error
            }
        }
    }
}


