package com.example.frontendapp.ui.theme.screens

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomBox
import com.example.frontendapp.ui.theme.composables.TopBarBussines
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import java.util.Calendar

@Composable
fun HorarioForm(navController: NavController, negocioViewModel: NegocioViewModel) {
    val diasSemanaCompleto = listOf("L", "M", "X", "J", "V", "S", "D")
    var diaSeleccionado by remember { mutableStateOf("Lunes") }
    var horaInicioSeleccionada by remember { mutableStateOf<String?>(null) }
    var horaFinSeleccionada by remember { mutableStateOf<String?>(null) }

    val horariosSeleccionados = remember { mutableStateListOf<Horario>() }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selecciona un día:")

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(diasSemanaCompleto.size) { index ->
                val dia = diasSemanaCompleto[index]
                BtnStyle1(
                    onClick = {
                        diaSeleccionado = dia
                        horaInicioSeleccionada = null
                        horaFinSeleccionada = null
                    },
                    text = dia.take(1)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selecciona hora de inicio:")
        BtnStyle1(
            text = horaInicioSeleccionada ?: "Elegir hora de inicio",
            onClick = {
                showTimePicker(context) { horaSeleccionada ->
                    horaInicioSeleccionada = horaSeleccionada
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selecciona hora de fin:")
        BtnStyle1(
            text = horaFinSeleccionada ?: "Elegir hora de fin",
            onClick = {
                showTimePicker(context) { horaSeleccionada ->
                    horaFinSeleccionada = horaSeleccionada
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        BtnStyle1(
            text = "Añadir horario",
            onClick = {
                if (horaInicioSeleccionada != null && horaFinSeleccionada != null) {
                    horariosSeleccionados.add(
                        Horario(
                            idNegocio = negocioViewModel.negocio.id,
                            diaSemana = diaSeleccionado,
                            horaInicio = horaInicioSeleccionada!!,
                            horaFin = horaFinSeleccionada!!
                        )
                    )
                    horaInicioSeleccionada = null
                    horaFinSeleccionada = null
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Horarios añadidos:")

        if (horariosSeleccionados.isEmpty()) {
            Text("No hay horarios")
        } else {
            horariosSeleccionados
                .groupBy { it.diaSemana }
                .forEach { (dia, lista) ->
                    Text("$dia:")
                    lista.forEach {
                        Text("  ${it.horaInicio} - ${it.horaFin}")
                    }
                }
        }
    }
}

// Función para mostrar TimePicker y devolver la hora en formato HH:mm
fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            val horaFormateada = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(horaFormateada)
        },
        hour, minute, true
    ).show()
}

@Preview
@Composable
fun PreviewHorarioForm(){
    FrontendappTheme {
        Scaffold(
            topBar = { TopBarBussines() },
            bottomBar = {
                BtnStyle1(text = "Crear negocio", onClick = {})
            }
        )
        { inner ->
            Column(Modifier.padding(inner)) {
                HorarioForm(rememberNavController(), NegocioViewModel())
            }
        }
    }
}