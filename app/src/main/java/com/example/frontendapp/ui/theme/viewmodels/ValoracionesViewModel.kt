package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Api.ValidationValidateState
import com.example.frontendapp.data.model.valoracion.ValoracionCreateDTO
import com.example.frontendapp.data.model.valoracion.ValoracionResponseDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.ValoracionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ValoracionViewModel(
    private val valoracionRepo: ValoracionRepo
) : ViewModel() {
    //Estado para de la pantalla
    protected var _valoracionStateUI= MutableStateFlow(ValoracionCreateDTO.init())
    val valoracionStateUi: StateFlow<ValoracionCreateDTO> = _valoracionStateUI

    //Estado errores de campo de la UI
    protected val _valoracionValidationState = MutableStateFlow(ValidationValidateState())
    val valoracionValidationState: StateFlow<ValidationValidateState> = _valoracionValidationState

    fun setComentario(comentario: String) {
        _valoracionStateUI.value = _valoracionStateUI.value.copy(comentario = comentario)
    }

    fun setPuntuacion(puntuacion: Double) {
        _valoracionStateUI.value = _valoracionStateUI.value.copy(puntuacion = puntuacion)
    }

    fun setNegocioId(negocioId: Int) {
        _valoracionStateUI.value = _valoracionStateUI.value.copy(negocioId = negocioId)
    }

    fun setUsuarioId(usuarioId: String) {
        _valoracionStateUI.value = _valoracionStateUI.value.copy(usuarioId = usuarioId)
    }
    fun resetUIState(){
        _valoracionStateUI=MutableStateFlow(ValoracionCreateDTO.init())
    }
    fun clearError(field: String) {
        val currentErrors = valoracionValidationState.value.errors.toMutableMap()
        currentErrors.remove(field)
        _valoracionValidationState.value = valoracionValidationState.value.copy(errors = currentErrors)
    }


    fun validateUI(): Boolean {
        val valoracion = _valoracionStateUI.value
        val errors = mutableMapOf<String, String>()

        if (valoracion.comentario.isBlank()) {
            errors["comentario"] = "El comentario no puede estar vacío"
        }
        if (valoracion.puntuacion !in 1.0..5.0) {
            errors["puntuacion"] = "La puntuación debe estar entre 1 y 5"
        }
        if (valoracion.negocioId == -1) {
            errors["negocioId"] = "Negocio inválido"
        }
        if (valoracion.usuarioId == "desconocido") {
            errors["usuarioId"] = "Usuario no válido"
        }

        _valoracionValidationState.value = ValidationValidateState(errors)
        return errors.isEmpty()
    }







    // Estado para lista de valoraciones por negocio
    protected val _valoracionesState = MutableStateFlow<Resource<List<ValoracionResponseDTO>>>(Resource.None())
    val valoracionesState: StateFlow<Resource<List<ValoracionResponseDTO>>> = _valoracionesState

    // Estado para cuando se crea una nueva valoración
    protected val _crearValoracionState = MutableStateFlow<Resource<ValoracionResponseDTO>>(Resource.None())
    val crearValoracionState: StateFlow<Resource<ValoracionResponseDTO>> = _crearValoracionState

    open fun getValoracionesPorNegocio(
        negocioId: Int,
        onLoading: () -> Unit = {},
        onSuccess: (List<ValoracionResponseDTO>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        _valoracionesState.value = Resource.Loading()
        val result = valoracionRepo.getValoracionesPorNegocio(negocioId)
        _valoracionesState.value = result

        when (result) {
            is Resource.Success -> result.data?.let { onSuccess(it) }
            is Resource.Error -> onError(result.message ?: "Error desconocido")
            else -> {}
        }
    }

    open fun addValoracion(
        onLoading: () -> Unit = {},
        onSuccess: (ValoracionResponseDTO) -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()

        val valoracion = _valoracionStateUI.value

        _crearValoracionState.value = Resource.Loading()

        val result = valoracionRepo.addValoracion(valoracion)

        _crearValoracionState.value = result

        when (result) {
            is Resource.Success -> result.data?.let { onSuccess(it) }
            is Resource.Error -> onError(result.message ?: "Error desconocido")
            else -> {}
        }
    }

}