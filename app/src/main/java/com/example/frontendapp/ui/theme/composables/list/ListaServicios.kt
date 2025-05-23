package com.example.frontendapp.ui.theme.composables.list

import android.util.Log
import android.widget.Toast
import androidx.collection.emptyLongSet
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.ServicioDetalleDto
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.composables.ListItems.ServicioListItem

@Composable
fun ListaServicios(
    viewModel: ServicioViewModel,
    modifier: Modifier = Modifier,
    onLoading: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {},
    negocioId: Int,
) {
    val servicioListResource by viewModel.serviciosDetalleState.collectAsState()


    LaunchedEffect (Unit) {
        viewModel.getServiciosDetalleByNegocioId(negocioId, onLoading, onSuccess, onError)
    }




    when (servicioListResource) {
        is Resource.Loading -> {
            onLoading()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Error -> {
            val errorMessage = (servicioListResource as Resource.Error).message ?: "Error desconocido"
            onError(errorMessage)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error al cargar servicios: $errorMessage", color = MaterialTheme.colorScheme.error)
                Log.d("EROOR","La id $negocioId")
            }
        }
        is Resource.Success -> {
            val servicios = (servicioListResource as Resource.Success).data
            onSuccess()
            if (servicios.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay servicios registrados.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = modifier.fillMaxWidth()
                ) {
                    items(servicios) { servicio ->
                        ServicioListItem(servicio = servicio)
                    }
                }
            }
        }
        is Resource.None -> {
            // This state should not occur if the ViewModel is properly initialized
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
    ListaServicios(viewModel = fakeViewModel, negocioId = 0)
}

