package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import com.example.frontendapp.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {

    // Exponer el estado de la UI
    private val _uiState = MutableStateFlow(Usuario())
    val uiState: StateFlow<Usuario> = _uiState

    // Función para actualizar el estado basado en la acción
    fun actualizarEstado(campo: String, valor: String) {
        _uiState.update { currentState ->
            when (campo) {
                "nombre" -> currentState.copy(nombre = valor)
                "correo" -> currentState.copy(email = valor)
                "telefono" -> currentState.copy(telefono = valor)
                "contrasenia" -> currentState.copy(contrasenaHash = valor)
                else -> currentState // No hacer nada si el campo no es válido
            }
        }
    }
}
