package com.example.frontendapp.ui.theme.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class UsuarioViewModel(private val auth: AuthRemoteDataResource) : ViewModel() {

    // Estado del usuario
    private val _uiState = MutableStateFlow(Usuario())
    val uiState: StateFlow<Usuario> = _uiState

    // Estado para el registro
    private val _registerState = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val registerState: StateFlow<Resource<String>> = _registerState

    // Estado para el login
    private val _loginState = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val loginState: StateFlow<Resource<String>> = _loginState

/*    fun registrarUsuario() {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val usuario = _uiState.value
            _registerState.value = auth.registerUser(usuario)
        }
    }*/




    // Métodos para actualizar el estado del usuario
    fun setNombre(nombre: String) {
        _uiState.update { currentState ->
            currentState.copy(username = nombre)
        }
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
}


