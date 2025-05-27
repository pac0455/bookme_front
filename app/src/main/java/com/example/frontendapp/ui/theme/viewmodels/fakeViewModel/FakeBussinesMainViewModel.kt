package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch





class FakeNegocioViewModel : NegocioViewModel(
    negocioRemoteSource = NegocioRemoteSource(RetrofitInstance.negocioApi)
) {

    override fun getNegociosByUserId(): Job {
        return viewModelScope.launch {
            _negociosByUserIdState.value = Resource.Loading()

            delay(500) // Simula latencia

            val negociosFalsos = listOf(
                Negocio(
                    id = 1,
                    nombre = "Clínica Bienestar",
                    descripcion = "Centro médico especializado en bienestar integral.",
                    direccion = "Calle Salud, 42",
                    latitud = 40.4168,
                    longitud = -3.7038,
                    categoriaId = 1,
                    categoria = Categoria(nombre = "Gym"),
                    activo = true
                ),
                Negocio(
                    id = 2,
                    nombre = "Gimnasio PowerFit",
                    descripcion = "Entrena con los mejores equipos y entrenadores.",
                    direccion = "Av. del Deporte, 10",
                    latitud = 40.4180,
                    longitud = -3.7100,
                    categoriaId = 1,
                    categoria = Categoria(nombre = "Spa"),
                    activo = false
                )
            )

            _negociosByUserIdState.value = Resource.Success(negociosFalsos)
        }
    }

    override fun deleteNegocio(
        negocioId: Int,
        onLoading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ): Job {
        return viewModelScope.launch {
            _deleteNegocioState.value = Resource.Loading()

            delay(300) // Simula latencia

            _deleteNegocioState.value = Resource.Success(Unit)
        }
    }
}


