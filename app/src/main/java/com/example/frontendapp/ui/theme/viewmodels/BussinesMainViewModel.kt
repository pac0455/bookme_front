package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.NegocioRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BussinesMainViewModel(protected val negocioRemoteSource: NegocioRepo) : ViewModel() {

    protected val _negociosUsuario = MutableStateFlow<List<Negocio>>(emptyList())
    val negociosUsuario: StateFlow<List<Negocio>> = _negociosUsuario

    protected val _negociosState = MutableStateFlow<Resource<List<Negocio>>>(Resource.None())
    val negociosApiState: StateFlow<Resource<List<Negocio>>> = _negociosState

    // Estado específico para delete (puede ser Resource<Unit>)
    protected val _negocioDeleteState = MutableStateFlow<Resource<Unit>>(Resource.None())
    val negocioDeleteState: StateFlow<Resource<Unit>> = _negocioDeleteState

    open fun loadNegociosByUser() {
        viewModelScope.launch {
            _negociosState.value = Resource.Loading()
            val result = negocioRemoteSource.getNegociosByUserId()
            _negociosState.value = result
            if (result is Resource.Success) {
                _negociosUsuario.value = result.data ?: emptyList()
            }
        }
    }

}

