package com.example.frontendapp.ui.theme.screens


import android.annotation.SuppressLint
import android.util.Log

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.ui.theme.Principal
import com.example.frontendapp.ui.theme.composables.Btn.DaySelector
import com.example.frontendapp.ui.theme.composables.list.HorarioList
import com.example.frontendapp.ui.theme.composables.Btn.TimePickerInputButton
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioForm(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    modo: String
)
{

    val esCreacion = modo == "crear"

    val negocio = negocioViewModel.negocioState.collectAsState().value
    // Log para ver qué datos llegan desde el ViewModel
    Log.d("HorarioForm", "Datos recibidos: nombre=${negocio.nombre}, direccion=${negocio.direccion}, categoria=${negocio.categoria}, horarios=${negocio.horarioAtencion}")

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

        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Principal,
                    titleContentColor = Color.White
                ),

               title = { Text("Horarios") },
                navigationIcon = {
                    IconButton (onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },

        bottomBar = {
            BtnStyle1(
                text = if (esCreacion) "Crear" else "Actualizar",
                onClick = {
                    if (esCreacion) {
                        negocioViewModel.addNegocioDB(
                            onLoading = {
                                Log.d("Create Negocio", "Creando el negocio: $negocio")
                                Toast.makeText(context, "Creando negocio...", Toast.LENGTH_SHORT).show()
                            },
                            onSuccess = {
                                Toast.makeText(context, "Negocio creado correctamente", Toast.LENGTH_SHORT).show()
                                navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                Log.e("Create Negocio", "Error: $errorMsg")
                            }
                        )
                    } else {
                        negocioViewModel.updateNegocioById(
                            id = negocio.id,
                            onLoading = {
                                Log.d("Update Negocio", "Actualizando el negocio: $negocio")
                                Toast.makeText(context, "Actualizando negocio...", Toast.LENGTH_SHORT).show()
                            },
                            onSuccess = {
                                Toast.makeText(context, "Negocio actualizado correctamente", Toast.LENGTH_SHORT).show()
                                navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                Log.e("Update Negocio", "Error: $errorMsg")
                            }
                        )
                    }
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
            Text("Selecciona el horario:")

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Inicio", style = MaterialTheme.typography.labelSmall)
                    TimePickerInputButton(
                        label = "Elegir inicio",
                        selectedTime = horaInicioSeleccionada,
                        onTimeSelected = {
                            if (diasSeleccionados.isEmpty()) {
                                Toast.makeText(context, "Elige un día", Toast.LENGTH_SHORT).show()
                                return@TimePickerInputButton
                            }

                            if (horaFinSeleccionada != null &&
                                negocioViewModel.esFinAntesDeInicio(it, horaFinSeleccionada!!)
                            ) {
                                Toast.makeText(context, "El fin no puede ser anterior al inicio", Toast.LENGTH_SHORT).show()
                                return@TimePickerInputButton
                            }

                            horaInicioSeleccionada = it
                        }
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Fin", style = MaterialTheme.typography.labelSmall)
                    TimePickerInputButton(
                        label = "Elegir fin",
                        selectedTime = horaFinSeleccionada,
                        onTimeSelected = {
                            if (diasSeleccionados.isEmpty()) {
                                Toast.makeText(context, "Elige un día", Toast.LENGTH_SHORT).show()
                                return@TimePickerInputButton
                            }

                            if (horaInicioSeleccionada == null) {
                                horaFinSeleccionada = it
                                return@TimePickerInputButton
                            }

                            if (negocioViewModel.esFinAntesDeInicio(horaInicioSeleccionada!!, it)) {
                                Toast.makeText(context, "El fin no puede ser anterior al inicio", Toast.LENGTH_SHORT).show()
                                return@TimePickerInputButton
                            }

                            horaFinSeleccionada = it
                        }
                    )
                }
            }



            Spacer(modifier = Modifier.height(16.dp))
            BtnStyle1(
                // Texto dinámico: cambia según estés editando o creando un horario nuevo
                text = if (horarioEditando == null) "Añadir horario" else "Guardar cambios",

                onClick = {
                    if (horaInicioSeleccionada == null || horaFinSeleccionada == null) {
                        Toast.makeText(context, "Debes seleccionar ambas horas", Toast.LENGTH_SHORT).show()
                        return@BtnStyle1
                    }

                    if (negocioViewModel.haySolapamientoEnDias(
                            diasSeleccionados,
                            horaInicioSeleccionada!!,
                            horaFinSeleccionada!!,
                            horarioEditando //  Pasa el horario actual si estás editando
                        )
                    ) {
                        Toast.makeText(context, "Ya existe un horario que se solapa en ese día", Toast.LENGTH_SHORT).show()
                        return@BtnStyle1
                    }


                    if (horarioEditando != null) {
                        negocioViewModel.editarHorario(horarioEditando!!, horaInicioSeleccionada!!, horaFinSeleccionada!!)
                        horarioEditando = null
                    } else {
                        negocioViewModel.addHorarios(diasSeleccionados, horaInicioSeleccionada!!, horaFinSeleccionada!!)
                    }

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
                    diasSeleccionados.clear()
                    diasSeleccionados.add(horario.diaSemana)
                },
                onEliminar = { horariosAEliminar ->
                    negocioViewModel.eliminarHorarios(horariosAEliminar)
                    horariosMarcados.removeAll(horariosAEliminar)
                }
            )
        }
    }
}





@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun PreviewHorarioForm(){
FrontendappTheme {
            HorarioForm(rememberNavController(),  FakeNegocioViewModel(), modo = "crear")
    }
}
