package com.example.frontendapp.ui.theme.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Servicio
import com.example.frontendapp.data.model.ServicioDetalleDto
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.ServicioRemoteSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ServicioViewModel(
    private val servicioRemoteSource: ServicioRemoteSource
) : ViewModel() {
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri


    protected val _servicioState = MutableStateFlow(Servicio(negocioId = -1))
    val servicioState: StateFlow<Servicio> = _servicioState

    fun setImagenUri(uri: Uri?) {
        _imagenUri.value = uri
    }

    fun setNombre(nombre: String) {
        _servicioState.value = _servicioState.value.copy(nombre = nombre)
    }

    fun setDescripcion(descripcion: String) {
        _servicioState.value = _servicioState.value.copy(descripcion = descripcion)
    }

    fun setDuracionMinutos(duracion: Int) {
        _servicioState.value = _servicioState.value.copy(duracionMinutos = duracion)
    }

    fun setPrecio(precio: Double) {
        _servicioState.value = _servicioState.value.copy(precio = precio)
    }

    fun setImagen(imagen: String) {
        _servicioState.value = _servicioState.value.copy(imagen = imagen)
    }

    fun setId(id: Int) {
        _servicioState.value = _servicioState.value.copy(id = id)
    }

    fun setNegocioId(negocioId: Int) {
        _servicioState.value = _servicioState.value.copy(negocioId = negocioId)
    }


    // Existing methods...


    protected open val _servicioListState = MutableStateFlow<Resource<List<Servicio>>>(Resource.None())
    val servicioList: StateFlow<Resource<List<Servicio>>> = _servicioListState

    protected val _servicioCreatedState = MutableStateFlow<Resource<Servicio>>(Resource.None())
    val servicioCreatedState: StateFlow<Resource<Servicio>> = _servicioCreatedState

    protected val _servicioDeletedState = MutableStateFlow<Resource<Unit>>(Resource.None())
    val servicioDeletedState: StateFlow<Resource<Unit>> = _servicioDeletedState

    protected val _servicioUpdatedState = MutableStateFlow<Resource<Unit>>(Resource.None())
    val servicioUpdatedState: StateFlow<Resource<Unit>> = _servicioUpdatedState

    protected val _servicioFetchedState = MutableStateFlow<Resource<Servicio>>(Resource.None())
    val servicioFetchedState: StateFlow<Resource<Servicio>> = _servicioFetchedState


    private val _serviciosDetalleState = MutableStateFlow<Resource<List<ServicioDetalleDto>>>(Resource.None())
    open val serviciosDetalleState: StateFlow<Resource<List<ServicioDetalleDto>>> = _serviciosDetalleState



    private val _servicioDetalleState = MutableStateFlow<Resource<Servicio>>(Resource.None())
    val servicioDetalleState: StateFlow<Resource<Servicio>> = _servicioDetalleState

    open fun getServicioDetalle(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.getServicio(id)
            _servicioDetalleState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError()
                else -> {}
            }
        }
    }

    open fun getServiciosDetalleByNegocioId(
        negocioId: Int,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if(negocioId==-1){
            _serviciosDetalleState.value = Resource.Error("El identificador del negocio no se pudo obtener")
            return
        }
        viewModelScope.launch {
            onLoading()
            _serviciosDetalleState.value = Resource.Loading()
            val response = servicioRemoteSource.getServiciosDetalleByNegocioId(negocioId)
            _serviciosDetalleState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError(response.message ?: "Error desconocido")
                else -> {}
            }
        }
    }

    open fun addServicio(
        context: Context,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()

            val servicio = _servicioState.value
            val imagen = _imagenUri.value

            val response = servicioRemoteSource.addServicio(servicio, imagen, context)

            _servicioCreatedState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError(response.message ?: "Error desconocido")
                else -> {}
            }
        }
    }


    fun getServicioById(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.getServicio(id)
            _servicioFetchedState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError()
                else -> {}
            }
        }
    }

    fun getAllServicios(
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.getAllServicios()
            _servicioListState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError()
                else -> {}
            }
        }
    }

    fun getServiciosByNegocioId(
        negocioId: Int,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.getServiciosByNegocioId(negocioId)
            _servicioListState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError()
                else -> {}
            }
        }
    }

    fun updateServicio(
        id: Int,
        servicio: Servicio,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.updateServicio(id, servicio)
            _servicioUpdatedState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError()
                else -> {}
            }
        }
    }

    fun deleteServicio(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.deleteServicio(id)
            _servicioDeletedState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError()
                else -> {}
            }
        }
    }

    fun resetStates() {
        _servicioCreatedState.value = Resource.None()
        _servicioDeletedState.value = Resource.None()
        _servicioUpdatedState.value = Resource.None()
        _servicioFetchedState.value = Resource.None()
        _servicioListState.value = Resource.None()
    }

    fun updateServicioState(servicio: Servicio) {
        _servicioState.value = servicio
    }


}
