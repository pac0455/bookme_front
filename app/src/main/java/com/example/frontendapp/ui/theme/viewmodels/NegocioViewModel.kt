package com.example.frontendapp.ui.theme.viewmodels

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Api.ValidationValidateState
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.model.Negocio.Ubicacion
import com.example.frontendapp.data.model.Reserva.Reserva
import com.example.frontendapp.data.model.Reserva.ReservaDetallada
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.utils.UbicacionHelper.getFromLocationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class NegocioViewModel(
    private val negocioRemoteSource: NegocioRemoteSource
) : ViewModel() {

    private val _negocioState = MutableStateFlow(Negocio())
    private var _backupState: Negocio? = null // Respaldo temporal
    val negocioState: StateFlow<Negocio> = _negocioState

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    // Estados separados para cada operación de la API
    private val _createNegocioState = MutableStateFlow<Resource<Negocio>>(Resource.None())
    val createNegocioState: StateFlow<Resource<Negocio>> = _createNegocioState

    private val _getNegocioState = MutableStateFlow<Resource<Negocio>>(Resource.None())
    val getNegocioState: StateFlow<Resource<Negocio>> = _getNegocioState

    private val _updateNegocioState = MutableStateFlow<Resource<Unit>>(Resource.None())
    val updateNegocioState: StateFlow<Resource<Unit>> = _updateNegocioState

    protected val _deleteNegocioState = MutableStateFlow<Resource<Unit>>(Resource.None())
    val deleteNegocioState: StateFlow<Resource<Unit>> = _deleteNegocioState

    protected val _negociosByUserIdState = MutableStateFlow<Resource<List<Negocio>>>(Resource.None())
    val negociosByUserIdState: StateFlow<Resource<List<Negocio>>> = _negociosByUserIdState

    private val _allNegociosState = MutableStateFlow<Resource<List<Negocio>>>(Resource.None())
    val allNegociosState: StateFlow<Resource<List<Negocio>>> = _allNegociosState

    private val _reservasDetalladasState = MutableStateFlow<Resource<List<ReservaDetallada>>>(Resource.None())
    val reservasDetalladasState: StateFlow<Resource<List<ReservaDetallada>>> = _reservasDetalladasState

    private val _reservasState = MutableStateFlow<Resource<List<Reserva>>>(Resource.None())
    val reservasState: StateFlow<Resource<List<Reserva>>> = _reservasState

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _updateNegocioImagenState = MutableStateFlow<Resource<Negocio>>(Resource.None())
    val updateNegocioImagenState: StateFlow<Resource<Negocio>> = _updateNegocioImagenState

    private val _negocioImageUrl = MutableStateFlow<String?>(null)
    val negocioImageUrl: StateFlow<String?> = _negocioImageUrl

    private val _negociosClienteState = MutableStateFlow<Resource<List<NegocioCardCliente>>>(Resource.None())
    val negociosClienteState: StateFlow<Resource<List<NegocioCardCliente>>> = _negociosClienteState

    private val _negocioValidationState = MutableStateFlow(ValidationValidateState())
    val negocioValidationState: StateFlow<ValidationValidateState> = _negocioValidationState




    //    ------------
    //    Validaciones
    //    ------------
    fun validateNegocioForm(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val negocio = _negocioState.value
        val errors = mutableMapOf<String, String>()

        if (negocio.nombre.isBlank()) {
            errors["nombre"] = "El nombre no puede estar vacío"
        }
        if (negocio.descripcion.isBlank()) {
            errors["descripcion"] = "La descripción no puede estar vacía"
        }
        if (negocio.direccion.isBlank()) {
            errors["direccion"] = "Selecciona una ubicación en el mapa"
        }
        if (negocio.latitud == null || negocio.longitud == null) {
            errors["ubicacion"] = "Ubicación no válida"
        }
        if (negocio.categoriaId == -1) {
            errors["categoria"] = "Selecciona una categoría"
        }

        _negocioValidationState.value = ValidationValidateState(errors)

        if (errors.isEmpty()) {
            onSuccess()
        } else {
            onError("Por favor, corrige los errores antes de continuar")
        }
    }

    // -------------------------
    // Operaciones de modificación local
    // -------------------------
    fun setId(id: Int) = _negocioState.update { it.copy(id = id) }
    fun setNombre(nombre: String) = _negocioState.update { it.copy(nombre = nombre) }
    fun setDescripcion(desc: String) = _negocioState.update { it.copy(descripcion = desc) }
    fun setDireccion(geocoder: Geocoder) {
        viewModelScope.launch {
            val dir = obtenerDireccion(geocoder)
            _negocioState.update { currentState ->
                currentState.copy(direccion = dir)
            }
        }
    }

    fun setLatitud(lat: Double) = _negocioState.update { it.copy(latitud = lat) }
    fun setLongitud(lon: Double) = _negocioState.update { it.copy(longitud = lon) }
    fun setUbicacion(lat: Double, lon: Double) = _negocioState.update { it.copy(latitud = lat, longitud = lon) }
    fun setcategoriaId(id: Int) = _negocioState.update { it.copy(categoriaId = id) }
    fun setActivo(activo: Boolean) = _negocioState.update { it.copy(activo = activo) }
    fun setHorarios(horarios: List<Horario>) = _negocioState.update { it.copy(horarioAtencion = horarios) }
    fun setSelectedImageUri(uri: Uri?) { _selectedImageUri.value = uri }

    private suspend fun obtenerDireccion(geocoder: Geocoder): String {
        return withContext(Dispatchers.IO) {
            geocoder.getFromLocationCompat(
                _negocioState.value.latitud!!,
                _negocioState.value.longitud!!,
                1
            ).firstOrNull()?.getAddressLine(0) ?: "Ubicación no encontrada"
        }
    }


    fun addHorarios(dias: List<String>, inicio: String, fin: String) {
        val nuevosHorarios = dias.map { dia ->
            Horario(idNegocio = _negocioState.value.id, diaSemana = dia, horaInicio = inicio, horaFin = fin)
        }
        _negocioState.update { current ->
            current.copy(horarioAtencion = current.horarioAtencion.orEmpty() + nuevosHorarios)
        }
    }

    fun eliminarHorarios(horarios: List<Horario>) {
        _negocioState.update {
            it.copy(horarioAtencion = it.horarioAtencion.orEmpty().filterNot { h -> h in horarios })
        }
    }

    fun editarHorario(original: Horario, nuevaInicio: String, nuevaFin: String) {
        _negocioState.update {
            val horarios = it.horarioAtencion.orEmpty().toMutableList()
            val index = horarios.indexOfFirst { h -> h == original }
            if (index != -1) {
                horarios[index] = original.copy(horaInicio = nuevaInicio, horaFin = nuevaFin)
            }
            it.copy(horarioAtencion = horarios)
        }
    }

    fun resetNegocio() {
        _negocioState.value = Negocio()
        _createNegocioState.value = Resource.None()
        _getNegocioState.value = Resource.None()
        _updateNegocioState.value = Resource.None()
        _deleteNegocioState.value = Resource.None()
    }
    //Construir la url a la que se va a atacar para la imagen
    fun getNegocioImageUrl(): String? {
        val ipAddress = RetrofitInstance.getIp()
        val id = _negocioState.value.id
        return if (id != -1) { // o cualquier valor que consideres inválido para id
            "${ipAddress}api/negocio/$id/imagen"
        } else {
            null
        }
    }
    fun getNegocioImageUrl(id: Int): String? {
        val ipAddress = RetrofitInstance.getIp()
        return if (id != -1) { // o cualquier valor que consideres inválido para id
            "${ipAddress}api/negocio/$id/imagen"
        } else {
            null
        }
    }



    // -------------------------
    // Estado viewModel
    // -------------------------
    fun confirmarDatos() {
        // Fuerza una actualización del estado si es necesario
        _negocioState.update { current -> current.copy() }
        Log.d("VM", "Datos confirmados para navegación")
    }


    fun startNewNegocio() {
        _backupState = _negocioState.value.copy()
        _negocioState.value = Negocio()
        _isEditMode.value = false
        Log.d("VM", "Preparando NUEVO negocio")
    }

    fun loadExistingNegocio(negocio: Negocio) {
        _negocioState.value = negocio.copy()
        _isEditMode.value = true
        Log.d("VM", "Cargando negocio existente: ${negocio.id}")
    }
    fun restorePreviousState() {
        _backupState?.let {
            _negocioState.value = it // Vuelve al estado anterior
            _backupState = null
        }
    }
    // -------------------------
    // Llamadas API
    // -------------------------

    fun getNegociosParaCliente(
        ubicacion: Ubicacion,
        onLoading: () -> Unit = {},
        onSuccess: (List<NegocioCardCliente>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        _negociosClienteState.value = Resource.Loading()

        val result = negocioRemoteSource.getNegociosParaCliente(ubicacion)
        _negociosClienteState.value = result

        when (result) {
            is Resource.Success -> onSuccess(result.data ?: emptyList())
            is Resource.Error -> onError(result.message ?: "Error desconocido")
            else -> {}
        }
    }

    fun addNegocioDB(
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        val result = negocioRemoteSource.addNegocio(_negocioState.value)
        _createNegocioState.value = result
        when (result) {
            is Resource.Success -> { onSuccess(); resetNegocio() }
            is Resource.Error -> onError(result.message ?: "Error al crear")
            else -> {}
        }
    }
    fun updateNegocioImagen(
        id: Int,
        context: Context,
        onLoading: () -> Unit = {},
        onError: (String) -> Unit = {},
        onSuccess: (Negocio) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        val uri = _selectedImageUri.value
        if (uri == null) {
            onError("No se ha seleccionado ninguna imagen")
            return@launch
        }
        val result = negocioRemoteSource.updateNegocioImagen(id, uri, context)
        when (result) {
            is Resource.Success -> onSuccess(result.data!!)
            is Resource.Error -> onError(result.message ?: "Error desconocido")
            else -> {  }
        }
    }

    open fun loadNegocioById(
        id: Int,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        val result = negocioRemoteSource.getNegocio(id)
        _getNegocioState.value = result
        when (result) {
            is Resource.Success -> {
                result.data?.let {
                    _negocioState.value = it
                    _isEditMode.value = true
                    onSuccess()
                } ?: onError("Negocio no encontrado")
            }
            is Resource.Error -> onError(result.message ?: "Error al obtener")
            else -> {}
        }
    }


    fun updateNegocioById(
        id: Int,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        val result = negocioRemoteSource.updateNegocio(id, _negocioState.value)
        _updateNegocioState.value = result
        when (result) {
            is Resource.Success -> onSuccess()
            is Resource.Error -> onError(result.message ?: "Error al actualizar")
            else -> {}
        }
    }

    open fun deleteNegocio(
        id: Int,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        val result = negocioRemoteSource.deleteNegocio(id)
        _deleteNegocioState.value = result
        when (result) {
            is Resource.Success -> onSuccess()
            is Resource.Error -> onError(result.message ?: "Error al eliminar")
            else -> {}
        }
    }

    open fun getNegociosByUserId() = viewModelScope.launch {
        _negociosByUserIdState.value = Resource.Loading()
        _negociosByUserIdState.value = negocioRemoteSource.getNegociosByUserId()
    }

    fun getAllNegocios() = viewModelScope.launch {
        _allNegociosState.value = Resource.Loading()
        _allNegociosState.value = negocioRemoteSource.getAllNegocios()
    }

    fun getReservasDetalladas(id: Int) = viewModelScope.launch {
        _reservasDetalladasState.value = Resource.Loading()
        _reservasDetalladasState.value = negocioRemoteSource.getReservasDetalladasByNegocioId(id)
    }

    fun getReservas(id: Int) = viewModelScope.launch {
        _reservasState.value = Resource.Loading()
        _reservasState.value = negocioRemoteSource.getReservasByNegocioId(id)
    }

    // -------------------------
    // Utilidades de validación local
    // -------------------------
    private fun horaToMinutos(hora: String) = hora.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
    private fun normalizarFin(inicio: Int, fin: Int) = if (fin <= inicio) fin + 1440 else fin

    fun haySolapamientoEnDias(
        dias: List<String>,
        inicio: String,
        fin: String,
        ignorar: Horario? = null
    ): Boolean {
        val nuevoInicio = horaToMinutos(inicio)
        val nuevoFin = normalizarFin(nuevoInicio, horaToMinutos(fin))

        return dias.any { dia ->
            _negocioState.value.horarioAtencion.orEmpty().any { existente ->
                if (ignorar != null && existente == ignorar) {
                    return@any false // Ignora el horario que se está editando
                }

                existente.diaSemana == dia &&
                        nuevoInicio < normalizarFin(
                    horaToMinutos(existente.horaInicio),
                    horaToMinutos(existente.horaFin)
                ) &&
                        nuevoFin > horaToMinutos(existente.horaInicio)
            }
        }
    }


    fun esFinAntesDeInicio(inicio: String, fin: String): Boolean {
        return horaToMinutos(fin) < horaToMinutos(inicio)
    }
}
