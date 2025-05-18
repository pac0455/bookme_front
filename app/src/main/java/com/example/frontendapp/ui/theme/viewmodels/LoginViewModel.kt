package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private  val auth: AuthRemoteDataResource): ViewModel() {
    // Estado del usuario
    private val _usuarioState = MutableStateFlow(Usuario())
    val usuarioState: StateFlow<Usuario> = _usuarioState


    //Estado de la respuesta, si esta cargando, si hubo un error o si fue exitoso
    private val _loginState = MutableStateFlow<Resource<LoginRegisterResultDTO>>(Resource.None())
    val loginState: StateFlow<Resource<LoginRegisterResultDTO>> = _loginState

    // Métodos para actualizar el estado del usuario
    fun setNombre(nombre: String) {
        _usuarioState.update { currentState ->
            currentState.copy(username = nombre)
        }
    }

    fun setEmail(email: String) {
        _usuarioState.update { currentState ->
            currentState.copy(email = email)
        }
    }

    fun setPassword(password: String) {
        _usuarioState.update { currentState ->
            currentState.copy(password = password)
        }
    }


    fun loginUsuario() {
        viewModelScope.launch {
            val user = _usuarioState.value
            _loginState.value = Resource.Loading()
            val result = auth.login(LoginRequest(user.email, user.password))
            _loginState.value = result
        }
    }
}