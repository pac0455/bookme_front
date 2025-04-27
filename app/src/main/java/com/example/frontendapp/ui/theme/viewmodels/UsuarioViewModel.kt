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

    // Función para registrar al usuario
    fun registrarUsuario() {
        viewModelScope.launch {
            val usuario = uiState.value // Obtén el estado actual del usuario
            val resultado = auth.registerUser(usuario) // Llama a la función suspend

            when (auth.registerUser(usuario)) {
                is Resource.Success -> {
                    // Manejar el caso de éxito
                    println("Registro exitoso: ${resultado.data}")
                }
                is Resource.Error -> {
                    // Manejar el caso de error
                    println("Error al registrar: ${resultado.message}")
                }
                is Resource.Loading -> {
                    println("estcargando")

                }
            }
        }
    }

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
            currentState.copy(telefono = telefono)
        }
    }

    fun setContrasena(contrasenaHash: String) {
        _uiState.update { currentState ->
            currentState.copy(password = contrasenaHash)
        }
    }
}


