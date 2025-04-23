package com.example.frontendapp.ui.theme.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.frontendapp.data.model.Usuario

class UsuarioViewModel : ViewModel() {

    // Estado del usuario editable
    var usuario by mutableStateOf(
        Usuario(
            id = "",
            nombre = "",
            email = "",
            firebaseUid = "",
            telefono = "",
            contrasenaHash = ""
        )
    )
        private set

    // Usuario registrado (después de login o submit)
    private val _usuarioRegistrado = mutableStateOf<Usuario?>(null)
    val usuarioRegistrado: State<Usuario?> = _usuarioRegistrado

    fun onNombreChanged(valor: String) {
        usuario = usuario.copy(nombre = valor)
    }

    fun onCorreoChanged(valor: String) {
        usuario = usuario.copy(email = valor)
    }

    fun onTelefonoChanged(valor: String) {
        usuario = usuario.copy(telefono = valor)
    }

    fun onContrasenaChanged(valor: String) {
        usuario = usuario.copy(contrasenaHash = valor)
    }

    fun registrarUsuario() {
        _usuarioRegistrado.value = usuario.copy(id = "nuevo-id")
        Log.d("Registro", "Usuario registrado: ${_usuarioRegistrado.value}")
    }

    fun loginWithGoogle(idToken: String) {
        Log.d("GoogleLogin", "ID Token recibido: $idToken")

        _usuarioRegistrado.value = Usuario(
            id = "firebase-uid",
            nombre = "Desde Google",
            email = "googleuser@example.com",
            firebaseUid = "firebase-uid",
            telefono = "",
            contrasenaHash = ""
        )

        usuario = _usuarioRegistrado.value!!
    }

    fun limpiarFormulario() {
        usuario = Usuario("", "", "", "", "", "")
    }
}
