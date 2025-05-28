package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FakeCategoriaViewModel : CategoriaViewModel(
    categoriaRemoteSource = CategoriaRemoteDataSource(RetrofitInstance.categoriaApi)
) {

    override fun getAllCategorias(
        onLoading: () -> Unit,
        onSuccess: (List<Categoria>) -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        _categoriasState.value = Resource.Loading()
        onLoading()

        delay(500) // Simula una espera de red

        val categoriasFalsas = listOf(
            Categoria(id = 1, nombre = "Peluquería"),
            Categoria(id = 2, nombre = "Estética"),
            Categoria(id = 3, nombre = "Masajes")
        )

        _categoriasState.value = Resource.Success(categoriasFalsas)
        onSuccess(categoriasFalsas)
    }
}
