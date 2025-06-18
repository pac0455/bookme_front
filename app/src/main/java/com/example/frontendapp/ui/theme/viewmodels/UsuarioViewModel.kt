package com.example.frontendapp.ui.theme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Api.ValidationErrorResponse
import com.example.frontendapp.data.model.UI.UpdatePasswordDTO
import com.example.frontendapp.data.model.Usuario.ConfirmMailDTO
import com.example.frontendapp.data.model.Usuario.UpdateDataUserDTO
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.model.Usuario.toRegisterDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.reponses.SingleMessageResponse
import com.example.frontendapp.data.remote.source.AuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class UsuarioViewModel(
    private val authRepo: AuthRepo
) : ViewModel() {
    //Estado de la UI
    private val _usuarioUI = MutableStateFlow(UpdateDataUserDTO.init())
    val usuarioUI: StateFlow<UpdateDataUserDTO> = _usuarioUI

    //Estado de la api
    private val _updateNombreState = MutableStateFlow<Resource<UpdateDataUserDTO>>(Resource.None())
    private val _updatePasswordState = MutableStateFlow<Resource<UpdatePasswordDTO>>(Resource.None())

    private val _usuariosState = MutableStateFlow<Resource<List<Usuario>>>(Resource.Loading())
    val usuariosState: StateFlow<Resource<List<Usuario>>> = _usuariosState

    private val _bloqueoState = MutableStateFlow<Resource<SingleMessageResponse>>(Resource.None())
    val bloqueoState: StateFlow<Resource<SingleMessageResponse>> = _bloqueoState

    private val _desbloqueoState = MutableStateFlow<Resource<SingleMessageResponse>>(Resource.None())
    val desbloqueoState: StateFlow<Resource<SingleMessageResponse>> = _desbloqueoState


    // Estado para la validación
    private val _validationState = MutableStateFlow<Resource<ValidationErrorResponse>>(Resource.None())
    val validationState: StateFlow<Resource<ValidationErrorResponse>> = _validationState




    fun validateRegistration(
        userDTO: Usuario,
        onLoading: () -> Unit = {},
        onSuccess: (ValidationErrorResponse) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            onLoading()
            _validationState.value = Resource.Loading()

            val user = userDTO.toRegisterDTO()
            Log.d("ValidaciónRegistro", "Datos convertidos a RegisterDTO: $user")


            try {
                val result = authRepo.validateRegistration(user)
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

    fun updateNombre(
        usuario: UpdateDataUserDTO,
        onSuccess: (UpdateDataUserDTO) -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit
    ) = viewModelScope.launch {
        _updateNombreState.value = Resource.Loading()
            val response = authRepo.updateNombre(usuario)
            when(response){
                is Resource.Success -> onSuccess(response.data!!)
                is Resource.Error -> onError(response.message ?: "Error desconocido")
                is Resource.Loading -> onLoading()
                is Resource.None -> TODO()
            }
        }

    fun getAllUsuarios() = viewModelScope.launch {
        _usuariosState.value = Resource.Loading()
        _usuariosState.value = authRepo.getAll()
    }

    fun eliminarUsuario(
        usuarioId: String,
        onSuccess: (SingleMessageResponse) -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        when (val response = authRepo.delete(usuarioId)) {
            is Resource.Success -> {
                onSuccess(response.data!!)
                getAllUsuarios() // Refrescar lista tras eliminación
            }

            is Resource.Error -> {
                onError(response.message ?: "Error al eliminar usuario")
            }

            else -> {
                onError("Error desconocido")
            }
        }
    }

    fun confirmCode(
        model: ConfirmMailDTO,
        onSuccess: (SingleMessageResponse) -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        when (val response = authRepo.confirmCode(model)) {
            is Resource.Success -> {
                onSuccess(response.data!!)
            }
            is Resource.Error -> {
                    onError(response.message ?: "Error desconocido")
            }
            else -> {
                onError("Error desconocido")
            }
        }
    }


    fun enviarCodigoAutenticacion(
        usuarioId: String,
        onSuccess: (SingleMessageResponse) -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        when (val response = authRepo.sendAuthenticationCode(usuarioId)) {
            is Resource.Success -> {
                onSuccess(response.data!!)
            }

            is Resource.Error -> {
                onError(response.message ?: "Error al enviar el código")
            }

            else -> {
                onError("Error desconocido")
            }
        }
    }



    fun bloquearUsuario(
        usuarioId: String,
        onSuccess: (SingleMessageResponse) -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) = viewModelScope.launch {
        _bloqueoState.value = Resource.Loading()
        onLoading()
        when (val response = authRepo.bloquear(usuarioId)) {
            is Resource.Success -> {
                _bloqueoState.value = response
                onSuccess(response.data!!)
            }

            is Resource.Error -> {
                _bloqueoState.value = Resource.Error(response.message ?: "Error al bloquear")
                onError(response.message ?: "Error al bloquear")
            }

            else -> onError("Error desconocido")
        }
    }

    fun desbloquearUsuario(
        usuarioId: String,
        onSuccess: (SingleMessageResponse) -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) = viewModelScope.launch {
        _desbloqueoState.value = Resource.Loading()
        onLoading()
        when (val response = authRepo.desbloquear(usuarioId)) {
            is Resource.Success -> {
                _desbloqueoState.value = response
                onSuccess(response.data!!)
            }

            is Resource.Error -> {
                _desbloqueoState.value = Resource.Error(response.message ?: "Error al desbloquear")
                onError(response.message ?: "Error al desbloquear")
            }

            else -> onError("Error desconocido")
        }
    }


    fun updatePassword(
        usuario: UpdatePasswordDTO,
        onSuccess: (UpdatePasswordDTO) -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit
    ) = viewModelScope.launch {
        _updateNombreState.value = Resource.Loading()
        val response = authRepo.updatePassword(usuario)
        when(response){
            is Resource.Success -> onSuccess(response.data!!)
            is Resource.Error -> onError(response.message ?: "Error desconocido")
            is Resource.Loading -> onLoading()
            is Resource.None -> TODO()
        }
    }

}