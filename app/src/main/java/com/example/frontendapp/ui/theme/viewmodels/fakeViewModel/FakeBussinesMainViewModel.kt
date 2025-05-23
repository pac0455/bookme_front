package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.viewmodels.BussinesMainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FakeBussinesMainViewModel : BussinesMainViewModel(
    negocioRemoteSource = NegocioRemoteSource(RetrofitInstance.negocioApi)
) {

    override fun loadNegociosByUser() {

            _negociosState.value = Resource.Loading()



            val negociosFalsos = listOf(
                Negocio(
                    id = 1,
                    nombre = "Clínica Bienestar",
                    descripcion = "Centro médico especializado en bienestar integral.",
                    direccion = "Calle Salud, 42",
                    latitud = 40.4168,
                    longitud = -3.7038,
                    categoria = "Clínica",
                    activo = true
                ),
                Negocio(
                    id = 2,
                    nombre = "Gimnasio PowerFit",
                    descripcion = "Entrena con los mejores equipos y entrenadores.",
                    direccion = "Av. del Deporte, 10",
                    latitud = 40.4180,
                    longitud = -3.7100,
                    categoria = "Gimnasio",
                    activo = false
                )
            )

            _negociosUsuario.value = negociosFalsos
            _negociosState.value = Resource.Success(negociosFalsos)

    }
}

