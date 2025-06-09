package com.example.frontendapp.ui.theme.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.data.model.Api.ValidationValidateState
import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.model.Servicio.ServicioDetalleDto
import com.example.frontendapp.data.model.Servicio.ServicioUpdateRequest
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.ServicioRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ServicioViewModel(
    private val servicioRemoteSource: ServicioRepo
) : ViewModel() {


    //    ------------
    //    LOCAL
    //    ------------

    // Variables para pasar servicios de una pantalla a otra
    protected val _tempServicios = MutableStateFlow(Servicio.init())
    val tempServicios: StateFlow<Servicio> = _tempServicios

    // Estado para almacenar errores de validación
    private val _validationState = MutableStateFlow(ValidationValidateState())
    val validationState: StateFlow<ValidationValidateState> = _validationState


    // Función para validar los campos del servicio
    fun validateServicio(): Boolean {
        val servicio = _servicioState.value
        val errors = mutableMapOf<String, String>()

        if (servicio.nombre.isBlank()) {
            errors["nombre"] = "El nombre no puede estar vacío"
        } else if (servicio.nombre.length < 3) {
            errors["nombre"] = "El nombre debe tener al menos 3 caracteres"
        }

        if (servicio.descripcion.isBlank()) {
            errors["descripcion"] = "La descripción no puede estar vacía"
        }

        if (servicio.duracionMinutos <= 0) {
            errors["duracionMinutos"] = "La duración debe ser mayor a cero minutos"
        }

        if (servicio.precio <= 0.0) {
            errors["precio"] = "El precio no puede ser negativo"
        }

        // Actualiza el estado de validación
        _validationState.value = ValidationValidateState(errors)

        // Retorna true si no hay errores
        return errors.isEmpty()
    }

    // Agrega un servicio a la lista temporal (evita duplicados si es necesario)
    fun setTempServicio(servicio: Servicio) {
        _tempServicios.value = servicio
    }



    //    ------------
    //    API
    //    ------------
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri


    protected val _servicioState = MutableStateFlow(Servicio.init())
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

    //Construir la url a la que se va a atacar para la imagen
    fun getServicioImageUrl(): String? {
        val id = _servicioState.value.id

        return if (id != -1) { // o cualquier valor que consideres inválido para id
            "${RetrofitInstance.getIp()}api/servicio/$id/imagen"
        } else {
            null
        }
    }
    fun getServicioImageUrl(id: Int?): String? {
        return "${RetrofitInstance.getIp()}api/servicio/$id/imagen"
    }


    // Estado expuesto a la UI para la lista detallada de servicios
    private val _serviciosDetalleState = MutableStateFlow<Resource<List<ServicioDetalleDto>>>(Resource.None())
    val serviciosDetalleState: StateFlow<Resource<List<ServicioDetalleDto>>> = _serviciosDetalleState

    protected open val _servicioListState = MutableStateFlow<Resource<List<Servicio>>>(Resource.None())
    val servicioList: StateFlow<Resource<List<Servicio>>> = _servicioListState

    protected val _servicioCreatedState = MutableStateFlow<Resource<Servicio>>(Resource.None())
    val servicioCreatedState: StateFlow<Resource<Servicio>> = _servicioCreatedState

    protected val _servicioDeletedState = MutableStateFlow<Resource<Unit>>(Resource.None())
    val servicioDeletedState: StateFlow<Resource<Unit>> = _servicioDeletedState

    protected val _servicioUpdatedState = MutableStateFlow<Resource<Servicio>>(Resource.None())
    val servicioUpdatedState: StateFlow<Resource<Servicio>> = _servicioUpdatedState

    protected val _servicioFetchedState = MutableStateFlow<Resource<Servicio>>(Resource.None())
    val servicioFetchedState: StateFlow<Resource<Servicio>> = _servicioFetchedState


    protected val _serviciosDetalleByNegocioIdState = MutableStateFlow<Resource<List<ServicioDetalleDto>>>(Resource.None())
    open val serviciosDetalleByNegocioIdState: StateFlow<Resource<List<ServicioDetalleDto>>> = _serviciosDetalleByNegocioIdState

    protected val _servicioDetalleState = MutableStateFlow<Resource<Servicio>>(Resource.None())
    val servicioDetalleState: StateFlow<Resource<Servicio>> = _servicioDetalleState


    // Función para cargar todos los servicios con detalle
    fun getServiciosDetalle() {
        viewModelScope.launch {
            _serviciosDetalleState.value = Resource.Loading()
            val result = servicioRemoteSource.getServiciosDetalle()
            _serviciosDetalleState.value = result
        }
    }

    open fun getServicioDetalle(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.getServicio(id)
            _servicioDetalleState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError(response.message.toString())
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
            _serviciosDetalleByNegocioIdState.value = Resource.Error("El identificador del negocio no se pudo obtener")
            return
        }
        viewModelScope.launch {
            onLoading()
            _serviciosDetalleByNegocioIdState.value = Resource.Loading()
            val response = servicioRemoteSource.getServiciosDetalleByNegocioId(negocioId)
            _serviciosDetalleByNegocioIdState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError(response.message ?: "Error desconocido")
                else -> {}
            }
        }
    }

    open fun addServicio(
        context: Context,
        onSuccess: (Servicio) -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()

            val servicio = _servicioState.value
            val imagen = _imagenUri.value

            val response = servicioRemoteSource.addServicio(servicio, imagen, context)
            Log.d("SERVICIOVIEWMODEL","Uri de la imagen insertada: $imagen")
            _servicioCreatedState.value = response

            Log.d("SERVICIOVIEWMODEL","Nueva imagen de servicio en : ${getServicioImageUrl(_servicioCreatedState.value.data?.id)}")

            when (response) {
                is Resource.Success -> {
                    // Aquí pasamos el servicio creado al onSuccess
                    response.data?.let {
                        onSuccess(it)
                    } ?: onError("Servicio creado es nulo")
                }
                is Resource.Error -> onError(response.message ?: "Error desconocido")
                else -> {}
            }
        }
    }

    fun updateImagenServicio(
        id: Int,
        context: Context,
        imagenUri: Uri?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()

            if (imagenUri == null) {
                onError("La imagen no puede ser nula.")
                return@launch
            }

            val response = servicioRemoteSource.updateImagenServicio(id, imagenUri, context)

            if (response is Resource.Success) {
                // Guardar la Uri en el stateFlow privado
                _imagenUri.value = imagenUri

                // Después de actualizar la imagen, opcionalmente puedes obtener el servicio actualizado para mantener el estado actualizado
                val fetchResponse = servicioRemoteSource.getServicio(id)

                _servicioUpdatedState.value = fetchResponse

                when (fetchResponse) {
                    is Resource.Success -> onSuccess()
                    is Resource.Error -> onError(fetchResponse.message ?: "Error al obtener servicio actualizado después de actualizar la imagen")
                    else -> {}
                }
            } else if (response is Resource.Error) {
                _servicioUpdatedState.value = Resource.Error(response.message ?: "Error al actualizar la imagen")
                onError(response.message ?: "Error desconocido al actualizar la imagen.")
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

    open fun getAllServicios(
        onLoading: () -> Unit = {},
        onSuccess: (List<Servicio>) -> Unit = {},
        onError: (String) -> Unit = {}
    ) = viewModelScope.launch {
        onLoading()
        _servicioListState.value = Resource.Loading()

        val response = servicioRemoteSource.getAllServicios()
        _servicioListState.value = response

        when (response) {
            is Resource.Success -> onSuccess(response.data ?: emptyList())
            is Resource.Error -> onError(response.message.toString())
            else -> {}
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

    open fun updateServicio(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()

            val currentServicio = _servicioState.value
            val id = currentServicio.id

            if (id == null) {
                onError("ID del servicio no disponible.")
                return@launch
            }

            // Aquí imprimimos el servicio antes de enviarlo
            val servicioJson = com.google.gson.Gson().toJson(currentServicio)
            Log.d("ServicioForm", "Enviando servicio para actualizar: $servicioJson")
            val servicioSinImagen= ServicioUpdateRequest(
                id = currentServicio.id,
                duracionMinutos = currentServicio.duracionMinutos,
                nombre = currentServicio.nombre,
                descripcion = currentServicio.descripcion,
                negocioId = currentServicio.negocioId,
                precio = currentServicio.precio,

            )
            val response = servicioRemoteSource.updateServicio(id, servicioSinImagen)
            _servicioUpdatedState.value = response

            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError(response.message ?: "Error desconocido")
                else -> {}
            }
        }
    }


    open fun deleteServicio(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        onLoading: () -> Unit = {},
    ) {
        viewModelScope.launch {
            onLoading()
            val response = servicioRemoteSource.deleteServicio(id)
            _servicioDeletedState.value = response
            when (response) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> onError(response.message ?: "Error inesperado")
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
    fun resetServicio(){
        _servicioState.value = Servicio.init()
    }

    fun updateServicioState(servicio: Servicio) {
        _servicioState.value = servicio
    }


}
