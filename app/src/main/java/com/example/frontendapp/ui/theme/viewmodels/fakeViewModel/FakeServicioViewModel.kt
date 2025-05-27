package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Servicio.ServicioDetalleDto
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.ServicioRemoteSource
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FakeServicioViewModel : ServicioViewModel(ServicioRemoteSource(RetrofitInstance.servicioApi)) {

    // StateFlow for detailed servicios
    private val _serviciosDetalleState = MutableStateFlow<Resource<List<ServicioDetalleDto>>>(Resource.None())
    override  var serviciosDetalleState: StateFlow<Resource<List<ServicioDetalleDto>>> = _serviciosDetalleState

    override fun getServiciosDetalleByNegocioId(
        negocioId: Int,
        onLoading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Set loading state
        _serviciosDetalleState.value = Resource.Loading()
        onLoading()

        viewModelScope.launch {
            delay(1000) // Simulate network delay

            // Simulate fetching data based on negocioId
            val fakeData = when (negocioId) {
                1 -> listOf(
                    ServicioDetalleDto(
                        id = 1,
                        negocioId = 1,
                        nombre = "Corte de cabello",
                        descripcion = "Corte profesional con estilista.",
                        duracionMinutos = 30,
                        precio = 15.0,
                        negocioNombre = "Salón de Belleza",
                        categoria = "Cuidado Personal",
                        valoracionPromedio = 4.5,
                        numeroValoraciones = 20,
                        numeroReservas = 50
                    ),
                    ServicioDetalleDto(
                        id = 2,
                        negocioId = 1,
                        nombre = "Manicura",
                        descripcion = "Servicio completo de manicura.",
                        duracionMinutos = 45,
                        precio = 20.0,
                        negocioNombre = "Salón de Belleza",
                        categoria = "Cuidado Personal",
                        valoracionPromedio = 4.0,
                        numeroValoraciones = 15,
                        numeroReservas = 30
                    )
                )
                else -> emptyList() // No services for other negocioIds
            }

            // Update the state with the fake data
            if (fakeData.isNotEmpty()) {
                _serviciosDetalleState.value = Resource.Success(fakeData)
                onSuccess() // Call onSuccess if data is fetched successfully
            } else {
                _serviciosDetalleState.value = Resource.Success(emptyList())
                onError("No hay servicios disponibles para este negocio.")
            }
        }
    }
}


