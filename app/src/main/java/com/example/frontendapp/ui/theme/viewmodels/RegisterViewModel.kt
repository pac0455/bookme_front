package com.example.frontendapp.ui.theme.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.reponses.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val auth: AuthRemoteDataResource) : ViewModel() {

    // Estado del usuario
    private val _uiState = MutableStateFlow(Usuario())
    val uiState: StateFlow<Usuario> = _uiState

    // Estado para el registro
    private val _registerState = MutableStateFlow<Resource<LoginRegisterResultDTO>>(Resource.None<LoginRegisterResultDTO>())
    val registerState: StateFlow<Resource<LoginRegisterResultDTO>> = _registerState

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
    fun registrarCliente() {
        viewModelScope.launch {
            val usuario = _uiState.value.copy(isNegocio = false)
            _registerState.value = auth.registerCliente(usuario)
        }
    }

    // Función para registrar como negocio
    fun registrarNegocio() {
        viewModelScope.launch {
            val usuario = _uiState.value.copy(isNegocio = true)
            _registerState.value = auth.registerNegocio(usuario)
        }
    }
}


