package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.valoracion.UsuarioDTO
import com.example.frontendapp.data.model.valoracion.ValoracionCreateDTO
import com.example.frontendapp.data.model.valoracion.ValoracionResponseDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.ValoracionRepo
import com.example.frontendapp.ui.theme.viewmodels.ValoracionViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class FakeValoracionViewModel : ValoracionViewModel(valoracionRepo = ValoracionRepo(RetrofitInstance.valoracionApi)) {
    private val isoFormatter = DateTimeFormatter.ISO_INSTANT
    val nowIsoString = isoFormatter.format(Instant.now())


    init {
        val valoracionesFalsas = listOf(
            ValoracionResponseDTO(
                id = 1,
                negocioId = 1,
                usuarioId = "user1",
                puntuacion = 5.0,
                comentario = "Excelente servicio",
                fechaValoracion = nowIsoString,
                usuario = UsuarioDTO(id = "user1", userName = "Usuario1", email = "user1@mail.com")
            ),
            ValoracionResponseDTO(
                id = 2,
                negocioId = 1,
                usuarioId = "user2",
                puntuacion = 4.5,
                comentario = "Muy buena atención",
                fechaValoracion = nowIsoString,
                usuario = UsuarioDTO(id = "user2", userName = "Usuario2", email = "user2@mail.com")
            )
        )

        _valoracionesState.value = Resource.Success(valoracionesFalsas)
    }


    override fun getValoracionesPorNegocio(
        negocioId: Int,
        onLoading: () -> Unit,
        onSuccess: (List<ValoracionResponseDTO>) -> Unit,
        onError: (String) -> Unit
    ): Job = viewModelScope.launch {
        _valoracionesState.value = Resource.Loading()

        val valoracionesFalsas = listOf(
            ValoracionResponseDTO(
                id = 1,
                negocioId = negocioId,
                usuarioId = "user1",
                puntuacion = 4.9,
                comentario = "Excelente servicio",
                fechaValoracion = nowIsoString,
                usuario = UsuarioDTO(id = "user1", userName = "Usuario1", email = "user1@mail.com")
            ),
            ValoracionResponseDTO(
                id = 2,
                negocioId = negocioId,
                usuarioId = "user2",
                puntuacion = 4.3,
                comentario = "Muy buena atención",
                fechaValoracion = nowIsoString,
                usuario = UsuarioDTO(id = "user2", userName = "Usuario2", email = "user2@mail.com")
            )
        )

        _valoracionesState.value = Resource.Success(valoracionesFalsas)
        onSuccess(valoracionesFalsas)
    }

}
