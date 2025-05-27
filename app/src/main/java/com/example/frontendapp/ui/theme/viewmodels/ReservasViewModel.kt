package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Reserva.Reserva
import com.example.frontendapp.data.model.Reserva.ReservaDetallada
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


open class ReservasViewModel(
    private val negocioRemoteSource: NegocioRemoteSource
) : ViewModel() {

    // Reservas simples
    protected val _reservasState = MutableStateFlow<List<Reserva>>(emptyList())
    val reservasState: StateFlow<List<Reserva>> = _reservasState

    // Reservas detalladas
    protected val _reservasDetalladasState = MutableStateFlow<List<ReservaDetallada>>(emptyList())
    val reservasDetalladasState: StateFlow<List<ReservaDetallada>> = _reservasDetalladasState

    // Estado de carga y error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarReservasDeNegocio(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = negocioRemoteSource.getReservasByNegocioId(id)
            _isLoading.value = false
            when (result) {
                is Resource.Success -> _reservasState.value = result.data ?: emptyList()
                is Resource.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun cargarReservasDetalladasDeNegocio(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = negocioRemoteSource.getReservasDetalladasByNegocioId(id)
            _isLoading.value = false
            when (result) {
                is Resource.Success -> _reservasDetalladasState.value = result.data ?: emptyList()
                is Resource.Error -> _error.value = result.message
                else -> {}
            }
        }
    }
}


