package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class CategoriaViewModel(
    private val categoriaRemoteSource: CategoriaRemoteDataSource
) : ViewModel() {

    protected val _categoriasState = MutableStateFlow<Resource<List<Categoria>>>(Resource.None())
    val categoriasState: StateFlow<Resource<List<Categoria>>> = _categoriasState

    open fun getAllCategorias(
        onLoading: () -> Unit = {},
        onSuccess: (List<Categoria>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        _categoriasState.value = Resource.Loading()
        val result = categoriaRemoteSource.getCategorias()
        _categoriasState.value = result

        when (result) {
            is Resource.Success -> result.data?.let { onSuccess(it) }
            is Resource.Error -> onError(result.message ?: "Error desconocido")
            else -> {}
        }
    }
}
