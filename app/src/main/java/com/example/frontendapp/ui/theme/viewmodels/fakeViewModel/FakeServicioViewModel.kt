package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.model.Servicio.ServicioDetalleDto
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.ServicioRepo
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FakeServicioViewModel : ServicioViewModel(
    servicioRemoteSource = ServicioRepo(RetrofitInstance.servicioApi)
) {
    init {
        _tempServicios.value =
            Servicio(
                id = 1,
                negocioId = 100,
                nombre = "Corte profesional",
                descripcion = "Corte de cabello con estilo profesional.",
                duracionMinutos = 60,
                precio = 20.0,
                imagen = null
            )

        // Simula datos precargados para pruebas
        _servicioListState.value = Resource.Success(
            listOf(
                Servicio(
                    id = 1,
                    nombre = "Corte de Cabello",
                    precio = 100.0,
                    negocioId = 1,
                    descripcion = "aafsa",
                    duracionMinutos = 50,
                    imagen = null
                ),
                Servicio(
                    id = 2,
                    nombre = "Manicure",
                    precio = 80.0,
                    negocioId = 1,
                    descripcion = "aafsa",
                    duracionMinutos = 50,
                    imagen = null
                )
            )
        )
        _serviciosDetalleByNegocioIdState.value = Resource.Success(
            listOf(
                ServicioDetalleDto(
                    id = 1,
                    negocioId = 1,
                    nombre = "Preview Corte Cabello",
                    descripcion = "Vista previa de un corte de cabello.",
                    duracionMinutos = 30,
                    precio = 100.0,
                    negocioNombre = "Barbería Preview",
                    categoria = "Barbería",
                    valoracionPromedioNegocio = 4.8,
                    numeroValoracionesNegocio = 100,
                    numeroReservas = 250,
                    imagen = null
                )
            )
        )
    }

    override fun getServicioDetalle(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit
    ) {
        viewModelScope.launch {
            _servicioDetalleState.value = Resource.Loading()
            onLoading()

            val servicio = Servicio(
                id = id,
                negocioId = 1,
                nombre = "Mock Servicio",
                descripcion = "Servicio simulado para pruebas",
                duracionMinutos = 45,
                precio = 99.99,
                imagen = null
            )
            _servicioDetalleState.value = Resource.Success(servicio)
            onSuccess()
        }
    }

    override fun getServiciosDetalleByNegocioId(
        negocioId: Int,
        onLoading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _serviciosDetalleByNegocioIdState.value = Resource.Loading()
                onLoading()

                delay(300) // Simula llamada a red

                val listaServicios = listOf(
                    ServicioDetalleDto(
                        id = 1,
                        negocioId = negocioId,
                        nombre = "Corte de Cabello",
                        descripcion = "Un corte moderno y profesional.",
                        duracionMinutos = 30,
                        precio = 100.0,
                        negocioNombre = "Barbería Don Pepe",
                        categoria = "Barbería",
                        valoracionPromedioNegocio = 4.7,
                        numeroValoracionesNegocio = 56,
                        numeroReservas = 120,
                        imagen = "https://example.com/imagenes/corte_cabello.jpg"
                    ),
                    ServicioDetalleDto(
                        id = 2,
                        negocioId = negocioId,
                        nombre = "Afeitado Clásico",
                        descripcion = "Afeitado con navaja y toalla caliente.",
                        duracionMinutos = 20,
                        precio = 80.0,
                        negocioNombre = "Barbería Don Pepe",
                        categoria = "Barbería",
                        valoracionPromedioNegocio = 4.9,
                        numeroValoracionesNegocio = 32,
                        numeroReservas = 75,
                        imagen = "https://example.com/imagenes/afeitado.jpg"
                    )
                )

                _serviciosDetalleByNegocioIdState.value = Resource.Success(listaServicios)
                onSuccess()

            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error desconocido"
                _serviciosDetalleByNegocioIdState.value = Resource.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    override fun addServicio(
        context: Context,
        onSuccess: (Servicio) -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit
    ) {
        viewModelScope.launch {
            onLoading()
            delay(300)
            val servicio = _servicioState.value.copy(id = 999)
            _servicioCreatedState.value = Resource.Success(servicio)
            onSuccess(servicio)
        }
    }

    override fun updateServicio(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit
    ) {
        viewModelScope.launch {
            onLoading()
            delay(300)
            _servicioUpdatedState.value = Resource.Success(_servicioState.value)
            onSuccess()
        }
    }

    override fun deleteServicio(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit
    ) {
        viewModelScope.launch {
            onLoading()
            delay(200)
            _servicioDeletedState.value = Resource.Success(Unit)
            onSuccess()
        }
    }

    override fun getAllServicios(
        onLoading: () -> Unit,
        onSuccess: (List<Servicio>) -> Unit,
        onError: (String) -> Unit
    ): Job {  // <-- Retorna Job
        return viewModelScope.launch {
            onLoading()
            delay(400)
            _servicioListState.value = Resource.Success(
                listOf(
                    Servicio(
                        id = 10,
                        nombre = "Fake Servicio 10",
                        precio = 150.0,
                        negocioId = 2,
                        duracionMinutos = 50,
                        imagen = null,
                        descripcion = ""
                    )
                )
            )
            onSuccess(_servicioListState.value.data ?: listOf())
        }
    }

}