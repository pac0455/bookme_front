package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.UI.UpdatePasswordDTO
import com.example.frontendapp.data.model.Usuario.UpdateNombreDTO
import com.example.frontendapp.data.remote.reponses.Resource
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