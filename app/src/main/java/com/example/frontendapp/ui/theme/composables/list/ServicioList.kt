    package com.example.frontendapp.ui.theme.composables.list

    import android.util.Log
    import android.widget.Toast
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import androidx.navigation.compose.rememberNavController
    import com.example.frontendapp.data.model.Servicio
    import com.example.frontendapp.data.remote.reponses.Resource
    import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
    import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
    import com.example.frontendapp.ui.theme.composables.ListItems.ServicioListItem
    import com.example.frontendapp.ui.theme.navigation.NavigationItem

    @Composable
    fun ServicioList(
        viewModel: ServicioViewModel,
        modifier: Modifier = Modifier,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        negocioId: Int,
        navController: NavController
    ) {
        Log.d("ListaServicios", "Composición iniciada con negocioId=$negocioId")

        val servicioListResource by viewModel.serviciosDetalleState.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            Log.d("ListaServicios", "LaunchedEffect triggered: cargando servicios para negocioId=$negocioId")
            viewModel.getServiciosDetalleByNegocioId(
                negocioId,
                onLoading = {
                    Log.d("ListaServicios", "onLoading callback ejecutado")
                    onLoading()
                },
                onSuccess = {
                    Log.d("ListaServicios", "onSuccess callback ejecutado")
                    onSuccess()
                },
                onError = { errorMsg ->
                    Log.d("ListaServicios", "onError callback ejecutado con mensaje: $errorMsg")
                    onError(errorMsg)
                }
            )
        }

        Log.d("ListaServicios", "Estado actual del recurso: $servicioListResource")

        when (servicioListResource) {
            is Resource.Loading -> {
                Log.d("ListaServicios", "Estado: Loading")
                onLoading()
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                val errorMessage = (servicioListResource as Resource.Error).message ?: "Error desconocido"
                Log.d("ListaServicios", "Estado: Error -> $errorMessage")
                onError(errorMessage)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error al cargar servicios: $errorMessage", color = MaterialTheme.colorScheme.error)
                    Log.d("ListaServicios", "La id de negocio: $negocioId")
                }
            }
            is Resource.Success -> {
                val servicios = (servicioListResource as Resource.Success).data
                Log.d("ListaServicios", "Estado: Success con ${servicios?.size ?: 0} servicios")
                onSuccess()
                if (servicios.isNullOrEmpty()) {
                    Log.d("ListaServicios", "No hay servicios registrados")
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay servicios registrados.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = modifier.fillMaxWidth()
                    ) {
                        items(servicios, key = { it.id ?: 0 }) { servicio ->
                            Log.d("ListaServicios", "Mostrando servicio id=${servicio.id}, nombre=${servicio.nombre}")
                            ServicioListItem(
                                servicioDetalleDto = servicio,
                                viewModel = viewModel,
                                onDeleteClick = {
                                    Log.d("ListaServicios", "Intentando borrar servicio id=${servicio.id}")
                                    viewModel.deleteServicio(servicio.id,
                                        onSuccess = {
                                            Log.d("ListaServicios", "Servicio id=${servicio.id} borrado con éxito")
                                            // Recarga la lista después de borrar
                                            viewModel.getServiciosDetalleByNegocioId(servicio.negocioId)
                                        },
                                        onError = {
                                            Toast.makeText(context, "Error inesperado al borrar el servicio", Toast.LENGTH_SHORT).show()
                                            Log.d("ERROR BORRAR SERVICIO", it)
                                        },
                                        onLoading = {
                                            Log.d("ListaServicios", "Borrando servicio id=${servicio.id}...")
                                        }
                                    )
                                },
                                onEditNavigate = {
                                    Log.d("ListaServicios", "Navegando a editar servicio id=${servicio.id}")
                                    viewModel.updateServicioState(
                                        Servicio(
                                            id = servicio.id ?: -1,
                                            negocioId = servicio.negocioId ?: -1,
                                            nombre = servicio.nombre ?: "",
                                            descripcion = servicio.descripcion ?: "",
                                            duracionMinutos = servicio.duracionMinutos ?: 0,
                                            precio = servicio.precio ?: 0.0,
                                            imagen = servicio.imagen ?: ""
                                        )
                                    )
                                    navController.navigate(NavigationItem.SERVICIO_FORM.edit)
                                }
                            )
                        }
                    }
                }
            }
            is Resource.None -> {
                Log.d("ListaServicios", "Estado: None (sin datos)")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Estado inicial: sin datos.")
                }
            }
        }
    }




    @Preview(showBackground = true)
    @Composable
    fun PreviewListaServicios() {
        val fakeViewModel = remember { FakeServicioViewModel() }
        ServicioList(viewModel = fakeViewModel, negocioId = 0, navController = rememberNavController())
    }

