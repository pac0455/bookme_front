package com.example.frontendapp.ui.theme.viewmodels

import PagoDTO
import ReservaResponseDTO
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Api.ValidationValidateState
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Reserva.Reserva
import com.example.frontendapp.data.model.Reserva.ReservaCreateDto
import com.example.frontendapp.data.model.Reserva.ReservaDetallada
import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.model.pago.EstadoPago
import com.example.frontendapp.data.model.pago.MetodoPagoDto
import com.example.frontendapp.data.model.pago.PagoCreateDto
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.NegocioRepo
import com.example.frontendapp.data.remote.source.ReservaRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


open class ReservasViewModel(
    private val reservaRepo: ReservaRepo
) : ViewModel() {

    //    -------
    //    LOCAL
    //    --------

    // Reservas simples
    protected val _reservaState = MutableStateFlow(ReservaCreateDto.init())
    val reservasState: StateFlow<ReservaCreateDto> = _reservaState

    protected val _validationState= MutableStateFlow(ValidationValidateState())
    val validationState: StateFlow<ValidationValidateState> = _validationState

    fun setServicio(servicioId: Int) {
        _reservaState.value = _reservaState.value.copy(servicioId = servicioId)
    }
    fun setNegocioId(negocioId: Int){
        _reservaState.value.negocioId = negocioId
    }
    fun setUserId(userId: String){
        _reservaState.value.usuarioId = userId
    }

    fun addServicio(
        horaInicio: String,
        horaFin: String,
        servicio: Servicio,
    ) {
        val reservaActual = _reservaState.value

        // Actualizar con el servicio único y las horas
        val nuevaReserva = reservaActual.copy(
            servicioId = servicio.id,
            horaInicio = horaInicio,
            horaFin = horaFin
        )
        _reservaState.value = nuevaReserva
    }



    fun validateReserva(reserva: ReservaCreateDto) {
        val errors = mutableMapOf<String, String>()

        if (reserva.horaInicio.isBlank()) {
            errors["horaInicio"] = "La hora de inicio debe estar seleccionada."
        }

        _validationState.value = ValidationValidateState(errors)
    }




    //    ----------
    //    API
    //    ----------


    // Reservas detalladas
    protected val _reservasDetalladasState = MutableStateFlow<List<ReservaDetallada>>(emptyList())
    val reservasDetalladasState: StateFlow<List<ReservaDetallada>> = _reservasDetalladasState

    protected val _reservaCreateState = MutableStateFlow<Resource<ReservaResponseDTO>>(Resource.None())
    val reservaCreateState: StateFlow<Resource<ReservaResponseDTO>> = _reservaCreateState

    // Estado para manejar la lista de reservas y estados de carga/error específicos
    private val _reservasByUserState = MutableStateFlow<Resource<List<ReservaResponseDTO>>>(Resource.None())
    val reservasByUserState: StateFlow<Resource<List<ReservaResponseDTO>>> = _reservasByUserState
    // Estado para manejar la cancelacion de reservas y estados de carga/error específicos
    private val _reservaCancelaState = MutableStateFlow<Resource<ReservaResponseDTO>>(Resource.None())
    val reservaCancelaState: StateFlow<Resource<ReservaResponseDTO>> = _reservaCancelaState

    // Estado de carga y error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun canecelarReserva(
        reservaId: Int,
        onSucces: (ReservaResponseDTO) -> Unit,
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) {
        viewModelScope.launch {
            onLoading()
            Log.d("RervarViewModel", "Cancelando la reserva con id: $reservaId")
            _reservaCancelaState.value = Resource.Loading()

            when (val result = reservaRepo.cancelarReserva(reservaId)) {
                is Resource.Success -> {
                    _reservaCancelaState.value = result
                    result.data?.let { onSucces(it) }
                }
                is Resource.Error -> {
                    _reservaCancelaState.value = result
                    onError(result.message ?: "Ocurrió un error inesperado")
                }
                else -> {
                    _reservaCancelaState.value = Resource.Error("Estado inesperado")
                    onError("Estado inesperado")
                }
            }
        }
    }

    fun addReserva(
        metodoPago: MetodoPagoDto,
        monto: Double,
        onSucces: (ReservaResponseDTO) -> Unit,
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) {
        viewModelScope.launch {
            onLoading()

            // Obtienes el estado actual de la reserva
            val currentReserva = _reservaState.value
            // Crear reserva formateada con método de pago insertado
            val reservaFormateada = currentReserva.copy(
                fecha = LocalDate.parse(currentReserva.fecha)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE),
                pago = PagoCreateDto(
                    monto = monto,
                    metodo = metodoPago
                )
            )
            Log.d("RervarViewModel", reservaFormateada.toString())
            _reservaCreateState.value = Resource.Loading()

            when (val result = reservaRepo.addReserva(reservaFormateada)) {
                is Resource.Success -> {
                    _reservaCreateState.value = result
                    result.data?.let { onSucces(it) }
                }
                is Resource.Error -> {
                    _reservaCreateState.value = result
                    onError(result.message ?: "Ocurrió un error inesperado")
                }
                else -> {
                    _reservaCreateState.value = Resource.Error("Estado inesperado")
                    onError("Estado inesperado")
                }
            }
        }
    }


    fun getReservasByUserId(
        userId: String,
        onSuccess: (List<ReservaResponseDTO>) -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {}
    ) {
        viewModelScope.launch {
            onLoading()
            _reservasByUserState.value = Resource.Loading()

            when (val result = reservaRepo.getReservasByUserId(userId)) {
                is Resource.Success -> {
                    val reservas = result.data ?: emptyList()

                    // Imprimir cada reserva en el log para inspección
                    reservas.forEach { reserva ->
                        Log.d("ReservasDebug", "Reserva recibida: $reserva")
                    }

                    _reservasByUserState.value = Resource.Success(reservas)
                    onSuccess(reservas)
                }
                is Resource.Error -> {
                    _reservasByUserState.value = Resource.Error(result.message ?: "Error desconocido")
                    onError(result.message ?: "Error desconocido")
                }
                else -> {
                    _reservasByUserState.value = Resource.Error("Estado inesperado")
                    onError("Estado inesperado")
                }
            }
        }
    }
}



