package com.example.frontendapp.ui.theme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.HorarioRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

open class HorariosViewModel(private val horarioRepo: HorarioRepo): ViewModel() {

    // Estado para los horarios por negocio
    val _horariosByNegocioIdState = MutableStateFlow<Resource<List<Horario>>>(Resource.None())
    val horariosByNegocioIdState: StateFlow<Resource<List<Horario>>> = _horariosByNegocioIdState

    // Estado para los horarios disponibles
    private val _horariosDisponiblesState = MutableStateFlow<Resource<List<Horario>>>(Resource.None())
    val horariosDisponiblesState: StateFlow<Resource<List<Horario>>> = _horariosDisponiblesState

    // Mes actual seleccionado, controlado desde la UI
    private val _mesActual = MutableStateFlow(YearMonth.now())
    val mesActual: StateFlow<YearMonth> = _mesActual

    //Dias del mes disponibles calculados
    protected val _diasDelMes = MutableStateFlow<Set<LocalDate>>(emptySet())
    val diasDelMes: StateFlow<Set<LocalDate>> = _diasDelMes.asStateFlow()


    private fun calcularDiasHabilitados(
        horarios: List<Horario>,
        mes: YearMonth
    ): Set<LocalDate> {
        // Calculamos los días habilitados según los horarios
        val diasHabilitados = horarios.mapNotNull { horario ->
            when (horario.diaSemana.lowercase()) {
                "lunes" -> DayOfWeek.MONDAY
                "martes" -> DayOfWeek.TUESDAY
                "miércoles", "miercoles" -> DayOfWeek.WEDNESDAY
                "jueves" -> DayOfWeek.THURSDAY
                "viernes" -> DayOfWeek.FRIDAY
                "sábado", "sabado" -> DayOfWeek.SATURDAY
                "domingo" -> DayOfWeek.SUNDAY
                else -> null
            }
        }.toSet()

        // Generamos las fechas habilitadas para el mes actual
        val start = mes.atDay(1) // Primer día del mes
        val end = mes.atEndOfMonth() // Último día del mes

        val dias = mutableSetOf<LocalDate>()
        var current = start
        while (!current.isAfter(end)) { // Mientras no sea después del último día del mes
            if (current.dayOfWeek in diasHabilitados) {
                dias.add(current)
            }
            current = current.plusDays(1) // Avanzamos un día
        }

        return dias
    }

    // Setter para actualizar el estado de horarios disponibles
    fun setHorariosDisponibles(resource: Resource<List<Horario>>) {
        _horariosDisponiblesState.value = resource
    }

    // Por ejemplo, para limpiar horarios (usaría Resource.None())
    fun clearHorariosDisponibles() {
        _horariosDisponiblesState.value = Resource.None()
    }

    // Método para cambiar el mes actual desde la UI
    fun setMesActual(mes: YearMonth) {
        _mesActual.value = mes

        // Verifica si hay datos disponibles en _horariosByNegocioIdState
        val horarios = _horariosByNegocioIdState.value.data
        if (horarios != null) {
            // Calcula los días habilitados solo si hay horarios disponibles
            val diasHabilitados = calcularDiasHabilitados(horarios, _mesActual.value)
            _diasDelMes.value = diasHabilitados // Actualiza el StateFlow con los días habilitados
        } else {
            // Si no hay horarios, limpia los días habilitados
            _diasDelMes.value = emptySet()
        }
    }



    // Método para obtener horarios por negocio
    open fun getHorariosByNegocioId(
        negocioId: Int,
        onLoading: () -> Unit = {},
        onSuccess: (List<Horario>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {

        onLoading()
        _horariosByNegocioIdState.value = Resource.Loading()
        val result = horarioRepo.getHorariosByNegocioId(negocioId)
        _horariosByNegocioIdState.value = result

        when (result) {
            is Resource.Success -> result.data?.let { horarios ->
                // Calculamos los días habilitados usando la función privada
                val diasHabilitados = calcularDiasHabilitados(horarios, _mesActual.value)
                // Actualizamos el StateFlow con los nuevos días habilitados
                _diasDelMes.value = diasHabilitados
                _diasDelMes.value.forEach { fecha ->
                    Log.d("HorariosViewModel", fecha.toString())
                }
                onSuccess(horarios)
            }
            is Resource.Error -> onError(result.message ?: "Error desconocido")
            else -> {}
        }
    }


    // Método para obtener horarios disponibles
    fun getHorariosDisponibles(
        negocioId: Int,
        servicioId: Int,
        selectedDay: String,
        onLoading: () -> Unit = {},
        onSuccess: (List<Horario>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        _horariosDisponiblesState.value = Resource.Loading()
        val result = horarioRepo.getHorariosDisponibles(negocioId, servicioId, selectedDay)
        _horariosDisponiblesState.value = result

        when (result) {
            is Resource.Success -> result.data?.let { onSuccess(it) }
            is Resource.Error -> onError(result.message ?: "Error desconocido")
            else -> {}
        }
    }
}