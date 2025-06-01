package com.example.frontendapp.ui.theme.composables.tab.negocioDetails
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.viewmodels.HorariosViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeHorariosViewModel


@Composable
fun NegocioHorarioTab(
    viewModel: HorariosViewModel,
    modifier: Modifier = Modifier,
    negocioId: Int
) {
    val horariosState by viewModel.horariosByNegocioIdState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NegocioHorarioTab", "Llamando a GetHorariosByNegocioId con ID: $negocioId")
        viewModel.getHorariosByNegocioId(negocioId = negocioId)
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Días de atención", style = MaterialTheme.typography.titleMedium)

        when (horariosState) {
            is Resource.Loading -> {
                Log.d("NegocioHorarioTab", "Cargando horarios...")
                CircularProgressIndicator()
            }
            is Resource.Error -> {
                val message = (horariosState as Resource.Error).message ?: "Error"
                Log.e("NegocioHorarioTab", "Error al obtener horarios: $message")
                Text(message)
            }
            is Resource.Success -> {
                val horarios = (horariosState as Resource.Success).data ?: emptyList()
                Log.d("NegocioHorarioTab", "Horarios recibidos: ${horarios.size}")
                if (horarios.isEmpty()) {
                    Log.d("NegocioHorarioTab", "La lista de horarios está vacía")
                    Text("Este negocio no tiene horarios definidos.", color = Color.Gray)
                } else {
                    horarios.forEach { horario ->
                        Log.d("NegocioHorarioTab", "Horario: ${horario.diaSemana} de ${horario.horaInicio} a ${horario.horaFin}")
                        Text(
                            text = "${horario.diaSemana}: ${horario.horaInicio} - ${horario.horaFin}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            else -> {
                Log.d("NegocioHorarioTab", "Estado sin datos")
                Text("Sin datos")
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegocioHorarioTabPreview() {
    MaterialTheme {
        Surface {
            NegocioHorarioTab(viewModel = FakeHorariosViewModel(), negocioId = 1)
        }
    }
}

