package com.example.frontendapp.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.data.remote.reponses.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


open class NegocioViewModel(private val negocioRemoteSource: NegocioRemoteSource) : ViewModel() {

    protected val _negocioState = MutableStateFlow(Negocio())
    val negocioState : StateFlow<Negocio> = _negocioState


    // Estado de la llamada api
    private val _negocioCreteState = MutableStateFlow<Resource<Negocio>>(Resource.None())
    val negocioApiState: StateFlow<Resource<Negocio>> = _negocioCreteState



    fun setId(update: Int) {
        _negocioState.update { currentState -> currentState.copy(id = update) }
    }

    fun setNombre(update: String) {
        _negocioState.update { currentState -> currentState.copy(nombre = update) }
    }

    fun setDescripcion(update: String) {
        _negocioState.update { currentState -> currentState.copy(descripcion = update) }
    }

    fun setDireccion(update: String) {
        _negocioState.update { currentState -> currentState.copy(direccion = update) }
    }

    fun setLatitud(update: Double) {
        _negocioState.update { currentState -> currentState.copy(latitud = update) }
    }

    fun setLongitud(update: Double) {
        _negocioState.update { currentState -> currentState.copy(longitud = update) }
    }

    fun setUbicacion(lat: Double, lon: Double) {
        _negocioState.update { currentState -> currentState.copy(latitud = lat, longitud = lon) }
    }

    fun setHorarios(update: List<Horario>) {
        _negocioState.update { currentState -> currentState.copy(horarioAtencion = update) }
    }
    fun setcategoria(update: String){
        _negocioState.update { currentState-> currentState.copy(categoria = update) }
    }
    fun setActivo(update: Boolean) {
        _negocioState.update { currentState -> currentState.copy(activo = update) }
    }

    fun eliminarHorarios(horarios: List<Horario>) {
        _negocioState.update { current ->
            val nuevos = current.horarioAtencion.orEmpty().filterNot { it in horarios }
            current.copy(horarioAtencion = nuevos)
        }
    }
    fun editarHorario(original: Horario, nuevaHoraInicio: String, nuevaHoraFin: String) {
        _negocioState.update { current ->
            val horariosActuales = current.horarioAtencion.orEmpty().toMutableList()
            val index = horariosActuales.indexOfFirst { it == original }
            if (index != -1) {
                horariosActuales[index] = original.copy(
                    horaInicio = nuevaHoraInicio,
                    horaFin = nuevaHoraFin
                )
            }
            current.copy(horarioAtencion = horariosActuales)
        }
    }

    fun addHorarios(dias: List<String>, inicio: String, fin: String) {
        val negocioActual = _negocioState.value

        val nuevosHorarios = dias.map { dia ->
            Horario(
                idNegocio = negocioActual.id,
                diaSemana = dia,
                horaInicio = inicio,
                horaFin = fin
            )
        }

        // Añade los nuevos horarios a los ya existentes
        _negocioState.update { current ->
            val horariosActuales = current.horarioAtencion.orEmpty()
            current.copy(horarioAtencion = horariosActuales + nuevosHorarios)
        }
    }
    fun addNegocioDB(
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            onLoading()

            val state = negocioRemoteSource.addNegocio(_negocioState.value)
            _negocioCreteState.value = state

            when (state) {
                is Resource.Success -> {
                    onSuccess()
                    resetNegocio()
                }
                is Resource.Error -> {
                    onError(state.message ?: "Error desconocido")
                }
                else -> {}
            }
        }
    }
    open fun loadNegocioById(
        id: Int,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            onLoading()

            val result = negocioRemoteSource.getNegocio(id)
            _negocioCreteState.value = result

            when (result) {
                is Resource.Success -> {
                    val negocio = result.data
                    if (negocio != null) {
                        _negocioState.value = negocio
                        onSuccess()
                    } else {
                        onError("Negocio no encontrado")
                    }
                }
                is Resource.Error -> {
                    onError(result.message ?: "Error desconocido al cargar el negocio")
                }
                else -> {} // Resource.None
            }
        }
    }

    fun resetNegocio() {
        _negocioState.value = Negocio()
        _negocioCreteState.value = Resource.None()
    }
    private fun horaToMinutos(hora: String): Int {
        val partes = hora.split(":")
        return partes[0].toInt() * 60 + partes[1].toInt()
    }

    private fun normalizarFin(inicio: Int, fin: Int): Int {
        return if (fin <= inicio) fin + 1440 else fin
    }

    fun haySolapamientoEnDias(dias: List<String>, inicio: String, fin: String): Boolean {
        val horarios = _negocioState.value.horarioAtencion.orEmpty()
        val nuevoInicioMin = horaToMinutos(inicio)
        val nuevoFinMin = normalizarFin(nuevoInicioMin, horaToMinutos(fin))

        return dias.any { dia ->
            val existentes = horarios.filter { it.diaSemana == dia }
            existentes.any {
                val inicioExistente = horaToMinutos(it.horaInicio)
                val finExistente = normalizarFin(inicioExistente, horaToMinutos(it.horaFin))
                nuevoInicioMin < finExistente && nuevoFinMin > inicioExistente
            }
        }
    }

    fun esFinAntesDeInicio(inicio: String, fin: String): Boolean {
        return horaToMinutos(fin) < horaToMinutos(inicio)
    }

}

