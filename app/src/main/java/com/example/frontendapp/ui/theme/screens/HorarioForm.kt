package com.example.frontendapp.ui.theme.screens


import android.util.Log

import android.widget.Toast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import com.example.frontendapp.ui.theme.composables.TopBarBussines
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.composables.DaySelector
import com.example.frontendapp.ui.theme.composables.HorarioList
import com.example.frontendapp.ui.theme.composables.TimePickerButton
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel

@Composable
fun HorarioForm(
    navController: NavController,
    negocioViewModel: NegocioViewModel
)
{



    val negocio = negocioViewModel.negocioState.collectAsState().value

    val diasVisuales = listOf("L", "M", "X", "J", "V", "S", "D")
    val diasInternos = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val diasSeleccionados = remember { mutableStateListOf<String>() }

    var horaInicioSeleccionada by remember { mutableStateOf<String?>(null) }
    var horaFinSeleccionada by remember { mutableStateOf<String?>(null) }

    val horarios = negocio.horarioAtencion.orEmpty()

    val horariosMarcados = remember { mutableStateListOf<Horario>() }

    var horarioEditando by remember { mutableStateOf<Horario?>(null) }
    val context = LocalContext.current



    Scaffold(

        topBar = { TopBarBussines() },
        bottomBar = {
            BtnStyle1(
                text = "Crear",
                onClick = {
                    negocioViewModel.addNegocioDB(
                        onLoading = {
                            Log.d("Loading necgocio", "Cargando el negocio ${negocio.toString()}")
                            Toast.makeText(context, "Creando negocio...", Toast.LENGTH_SHORT).show()
                        },
                        onSuccess = {
                            Toast.makeText(context, "Negocio creado correctamente", Toast.LENGTH_SHORT).show()
                            navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                            Log.d("Negocio error","Error: $errorMsg" )
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            )
        }


    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text("Selecciona uno o varios días:")
            Spacer(modifier = Modifier.height(24.dp))
            DaySelector(
                diasVisuales = diasVisuales,
                diasInternos = diasInternos,
                diasSeleccionados = diasSeleccionados,
                onClearTimeSelection = {
                    horaInicioSeleccionada = null
                    horaFinSeleccionada = null
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecciona hora de inicio:")
            TimePickerButton(
                label = "Elegir hora de inicio",
                selectedTime = horaInicioSeleccionada,
                onTimeSelected = {
                    //Comprobar si se ha seleccionado alguno hora+
                   if(diasSeleccionados.isEmpty()){
                        Toast.makeText(context, "Elige un dia", Toast.LENGTH_SHORT).show()
                   }
                  if(horaFinSeleccionada == null){
                      horaInicioSeleccionada = it
                      return@TimePickerButton
                  }
                  if( horaToMinutos(it) < horaToMinutos(horaFinSeleccionada!!)){
                      Toast.makeText(context, "El fin no puede ser anterior al incio", Toast.LENGTH_SHORT).show()
                  }
                  horaInicioSeleccionada = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecciona hora de fin:")
            TimePickerButton(
                label = "Elegir hora de fin",
                selectedTime = horaFinSeleccionada,
                onTimeSelected = {
                    if(diasSeleccionados.isEmpty()){
                        Toast.makeText(context, "Elige un dia", Toast.LENGTH_SHORT).show()
                    }
                    if(horaInicioSeleccionada== null){
                        horaFinSeleccionada = it
                        return@TimePickerButton
                        //Comprobar si se ha seleccionado alguno hora de inicio
                    }else if(horaToMinutos(it) > horaToMinutos(horaInicioSeleccionada!!)){
                        // Comprobar que la hora de fin no es anterior a la hora de inicio
                        Toast.makeText(context, "El fin no puede ser anterior al incio", Toast.LENGTH_SHORT).show()
                    }
                    horaFinSeleccionada = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            BtnStyle1(
                // Texto dinámico: cambia según estés editando o creando un horario nuevo
                text = if (horarioEditando == null) "Añadir horario" else "Guardar cambios",

                onClick = {
                    // Solo procede si se han seleccionado ambas horas
                    if (horaInicioSeleccionada == null || horaFinSeleccionada == null) {
                        // Si falta alguna hora, no hacemos nada
                        return@BtnStyle1
                    }

                    val inicio = horaInicioSeleccionada!!
                    val fin = horaFinSeleccionada!!
                    val horarios = negocio.horarioAtencion.orEmpty()

                    val hayConflicto = diasSeleccionados.any { dia ->
                        val existentes = horarios.filter { it.diaSemana == dia }
                        haySolapamiento(inicio, fin, existentes)
                    }

                    if (hayConflicto) {
                        Toast.makeText(
                            context,
                            "Ya existe un horario que se solapa en ese día",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@BtnStyle1
                    }

                    // No hay conflicto: editamos o añadimos
                    if (horarioEditando != null) {
                        negocioViewModel.editarHorario(horarioEditando!!, inicio, fin)
                        horarioEditando = null
                    } else {
                        negocioViewModel.addHorarios(diasSeleccionados, inicio, fin)
                    }

                    // Limpiamos estado tras guardar
                    horaInicioSeleccionada = null
                    horaFinSeleccionada = null
                    diasSeleccionados.clear()
                })

            Spacer(modifier = Modifier.height(24.dp))
            Text("Horarios añadidos:")
            HorarioList(
                horarios = horarios,
                horariosMarcados = horariosMarcados,
                onEditar = { horario ->
                    horarioEditando = horario
                    horaInicioSeleccionada = horario.horaInicio
                    horaFinSeleccionada = horario.horaFin
                },
                onEliminar = { horariosAEliminar ->
                    negocioViewModel.eliminarHorarios(horariosAEliminar)
                    horariosMarcados.removeAll(horariosAEliminar)
                }
            )
        }
    }
}

fun haySolapamiento(
    nuevoInicio: String,
    nuevoFin: String,
    existentes: List<Horario>
): Boolean {
    val nuevoInicioMin = horaToMinutos(nuevoInicio)
    val nuevoFinMin = normalizarFin(horaToMinutos(nuevoInicio), horaToMinutos(nuevoFin))

    return existentes.any {
        val inicioExistente = horaToMinutos(it.horaInicio)
        val finExistente = normalizarFin(inicioExistente, horaToMinutos(it.horaFin))
        Log.i("HORAS","$nuevoInicioMin < $finExistente && $nuevoFinMin > $inicioExistente")
        nuevoInicioMin < finExistente && nuevoFinMin > inicioExistente
    }
}

// Si la hora fin está antes que la hora inicio, asumimos que pasa de medianoche
fun normalizarFin(inicio: Int, fin: Int): Int {
    return if (fin <= inicio) fin + 1440 else fin // 1440 minutos = 24h
}

fun horaToMinutos(hora: String): Int {
    val partes = hora.split(":")
    return partes[0].toInt() * 60 + partes[1].toInt()
}





@Preview
@Composable
fun PreviewHorarioForm(){
FrontendappTheme {
            HorarioForm(rememberNavController(),  NegocioViewModel(NegocioRemoteSource(RetrofitInstance.negocioApi)))
    }
}
