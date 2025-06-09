package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.UI.UpdatePasswordDTO
import com.example.frontendapp.data.model.Usuario.UpdateNombreDTO
import com.example.frontendapp.data.model.Usuario.Usuario
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
    private val _usuarioUI = MutableStateFlow(UpdateNombreDTO.init())
    val usuarioUI: StateFlow<UpdateNombreDTO> = _usuarioUI

    //Estado de la api
    private val _updateNombreState = MutableStateFlow<Resource<UpdateNombreDTO>>(Resource.None())
    private val _updatePasswordState = MutableStateFlow<Resource<UpdatePasswordDTO>>(Resource.None())

    private val _usuariosState = MutableStateFlow<Resource<List<Usuario>>>(Resource.Loading())
    val usuariosState: StateFlow<Resource<List<Usuario>>> = _usuariosState

    private val _bloqueoState = MutableStateFlow<Resource<SingleMessageResponse>>(Resource.None())
    val bloqueoState: StateFlow<Resource<SingleMessageResponse>> = _bloqueoState

    private val _desbloqueoState = MutableStateFlow<Resource<SingleMessageResponse>>(Resource.None())
    val desbloqueoState: StateFlow<Resource<SingleMessageResponse>> = _desbloqueoState





    fun updateNombre(
        usuario: UpdateNombreDTO,
        onSuccess: (UpdateNombreDTO) -> Unit,
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