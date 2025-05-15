package com.example.frontendapp.ui.theme.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.frontendapp.data.model.Negocio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class NegocioViewModel : ViewModel() {
    var negocio by mutableStateOf(Negocio())
        private set

    fun updateField(update: Negocio.() -> Negocio) {
        negocio = negocio.update()
    }

    fun setUbicacion(lat: Double, lon: Double) {
        negocio = negocio.copy(latitud = lat, longitud = lon)
    }
}
