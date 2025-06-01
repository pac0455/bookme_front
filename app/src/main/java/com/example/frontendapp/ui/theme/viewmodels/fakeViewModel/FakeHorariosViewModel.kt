package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.HorarioRepo
import com.example.frontendapp.ui.theme.viewmodels.HorariosViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FakeHorariosViewModel() : HorariosViewModel(
    horarioRepo = HorarioRepo(RetrofitInstance.horarioApi)
) {

    init {
        val fakeHorarios = listOf(
            Horario(id = 1, idNegocio = 1, diaSemana = "Lunes", horaInicio = "09:00", horaFin = "17:00"),
            Horario(id = 2, idNegocio = 1, diaSemana = "Martes", horaInicio = "09:00", horaFin = "17:00")
        )
        _horariosByNegocioIdState.value = Resource.Success(fakeHorarios)
    }

    override fun getHorariosByNegocioId(
        negocioId: Int,
        onLoading: () -> Unit,
        onSuccess: (List<Horario>) -> Unit,
        onError: (String) -> Unit
    ): Job {
        return viewModelScope.launch {
            // Ya está prellenado, simplemente llama a onSuccess
            onSuccess((_horariosByNegocioIdState.value as? Resource.Success)?.data ?: emptyList())
        }
    }
}
