package com.example.frontendapp.ui.theme.composables.ListItems

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Servicio
import com.example.frontendapp.data.model.ServicioDetalleDto
import com.example.frontendapp.ui.theme.composables.modal.ServicioImagePicker
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel

@Composable
fun ServicioListItem(
    servicioDetalleDto: ServicioDetalleDto,
    viewModel: ServicioViewModel,
    onDeleteClick: (ServicioDetalleDto) -> Unit = {},
    onEditNavigate: () -> Unit = {} // Llamar a la navegación hacia pantalla edición
) {
    val colorEstado = when {
        servicioDetalleDto.valoracionPromedio < 3.0 -> Color(0xFFF44336) // rojo menor a 3
        servicioDetalleDto.valoracionPromedio < 4.0 -> Color(0xFFFFC107) // amarillo entre 3 y 4
        else -> Color(0xFF4CAF50) // verde de 4 a 5
    }

    var expanded by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(expanded) {
        println("ServicioListItem: expanded changed to $expanded for servicio id=${servicioDetalleDto.id}")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            ServicioImagePicker(
                id = servicioDetalleDto.id,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                size = 48.dp,
                shape = CircleShape,
                clickable = false,
                iconSize = 24.dp,
                iconAlignment = Alignment.Center,
                contentAlignment = Alignment.Center,
                backgroundColor = Color.LightGray,
            )


            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = servicioDetalleDto.nombre ?: "Sin nombre", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${servicioDetalleDto.categoria ?: "Sin categoría"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "Precio: ${servicioDetalleDto.precio ?: 0.0} €",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
                Text(
                    text = "Valoración: ${servicioDetalleDto.valoracionPromedio} (${servicioDetalleDto.numeroValoraciones} valoraciones)",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorEstado,
                    maxLines = 1
                )
                Text(
                    text = "Reservas: ${servicioDetalleDto.numeroReservas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.semantics {
                    contentDescription = if (expanded) "Cerrar opciones" else "Abrir opciones"
                }
            ) {
                AnimatedContent(
                    targetState = expanded,
                    transitionSpec = {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    }
                ) { targetExpanded ->
                    if (targetExpanded) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    } else {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Más opciones")
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + androidx.compose.animation.slideInVertically(),
            exit = fadeOut() + androidx.compose.animation.slideOutVertically()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        println("ServicioListItem: Editar clicked for servicio id=${servicioDetalleDto.id}")

                        // Cargar los datos en el ViewModel
                        viewModel.updateServicioState(
                            Servicio(
                                id = servicioDetalleDto.id ?: -1,
                                negocioId = servicioDetalleDto.negocioId ?: -1,
                                nombre = servicioDetalleDto.nombre ?: "",
                                descripcion = servicioDetalleDto.descripcion ?: "",
                                duracionMinutos = servicioDetalleDto.duracionMinutos ?: 0,
                                precio = servicioDetalleDto.precio ?: 0.0,
                                imagen = servicioDetalleDto.imagen ?: ""
                            )
                        )

                        // Navegar a pantalla edición o mostrar el formulario
                        onEditNavigate()
                    }
                ) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onDeleteClick(servicioDetalleDto) }) {
                    Text("Eliminar")
                }
            }
        }
    }
}



@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ServicioListItemPreview() {
    val servicioEjemplo = ServicioDetalleDto(
        id = 1,
        negocioId = 10,
        nombre = "Corte de cabello",
        descripcion = "Un corte moderno y estilizado",
        duracionMinutos = 30,
        precio = 15.0,
        negocioNombre = "Peluquería Estilo",
        categoria = "Belleza",
        valoracionPromedio = 2.5,
        numeroValoraciones = 25,
        numeroReservas = 40
    )

    ServicioListItem(
        servicioDetalleDto = servicioEjemplo,
        onEditNavigate = { /* Acción editar */ },
        onDeleteClick = { /* Acción eliminar */ },
        viewModel = FakeServicioViewModel(),
    )
}

