package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.Btn.DaySelector
import com.example.frontendapp.ui.theme.composables.Btn.TimePickerInputButton
import com.example.frontendapp.ui.theme.composables.list.HorarioList
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioForm(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    modo: String
) {
    val esCreacion = modo == "crear"
    val negocio = negocioViewModel.negocioState.collectAsState().value

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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {
                    Text(
                        "Horarios de Atención",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                BtnStyle1(
                    text = if (esCreacion) "Crear Negocio" else "Actualizar Negocio",
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
                    icon = if (esCreacion) Icons.Default.Add else Icons.Default.Update,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con información del progreso
            ProgressHeader(
                currentStep = 2,
                totalSteps = 2,
                stepTitle = "Horarios de Atención",
                stepDescription = "Define cuándo estará disponible tu negocio"
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Sección de selección de días
                HorarioSection(
                    title = "Seleccionar Días",
                    icon = Icons.Default.CalendarMonth,
                    description = "Elige los días de la semana que tu negocio estará abierto"
                ) {
                    DaySelector(
                        diasVisuales = diasVisuales,
                        diasInternos = diasInternos,
                        diasSeleccionados = diasSeleccionados,
                        onClearTimeSelection = {
                            horaInicioSeleccionada = null
                            horaFinSeleccionada = null
                        }
                    )
                }

                // Sección de selección de horarios
                HorarioSection(
                    title = "Horario de Atención",
                    icon = Icons.Default.Schedule,
                    description = "Define las horas de apertura y cierre"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TimeSelectionCard(
                            modifier = Modifier.weight(1f),
                            title = "Hora de Apertura",
                            icon = Icons.Default.WbSunny,
                            selectedTime = horaInicioSeleccionada,
                            onTimeSelected = { time ->
                                if (diasSeleccionados.isEmpty()) {
                                    Toast.makeText(context, "Primero selecciona al menos un día", Toast.LENGTH_SHORT).show()
                                    return@TimeSelectionCard
                                }

                                if (horaFinSeleccionada != null &&
                                    negocioViewModel.esFinAntesDeInicio(time, horaFinSeleccionada!!)
                                ) {
                                    Toast.makeText(context, "La hora de cierre no puede ser anterior a la apertura", Toast.LENGTH_SHORT).show()
                                    return@TimeSelectionCard
                                }

                                horaInicioSeleccionada = time
                            }
                        )

                        TimeSelectionCard(
                            modifier = Modifier.weight(1f),
                            title = "Hora de Cierre",
                            icon = Icons.Default.NightlightRound,
                            selectedTime = horaFinSeleccionada,
                            onTimeSelected = { time ->
                                if (diasSeleccionados.isEmpty()) {
                                    Toast.makeText(context, "Primero selecciona al menos un día", Toast.LENGTH_SHORT).show()
                                    return@TimeSelectionCard
                                }

                                if (horaInicioSeleccionada == null) {
                                    horaFinSeleccionada = time
                                    return@TimeSelectionCard
                                }

                                if (negocioViewModel.esFinAntesDeInicio(horaInicioSeleccionada!!, time)) {
                                    Toast.makeText(context, "La hora de cierre no puede ser anterior a la apertura", Toast.LENGTH_SHORT).show()
                                    return@TimeSelectionCard
                                }

                                horaFinSeleccionada = time
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para añadir/editar horario
                    BtnStyle1(
                        text = if (horarioEditando == null) "Añadir Horario" else "Guardar Cambios",
                        icon = if (horarioEditando == null) Icons.Default.Add else Icons.Default.Save,
                        onClick = {
                            if (horaInicioSeleccionada == null || horaFinSeleccionada == null) {
                                Toast.makeText(context, "Debes seleccionar ambas horas", Toast.LENGTH_SHORT).show()
                                return@BtnStyle1
                            }

                            if (negocioViewModel.haySolapamientoEnDias(
                                    diasSeleccionados,
                                    horaInicioSeleccionada!!,
                                    horaFinSeleccionada!!,
                                    horarioEditando
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
                        },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = if (horarioEditando == null)
                            MaterialTheme.colorScheme.primary else
                            ThemeColors.warning
                    )
                }

                // Sección de horarios añadidos
                if (horarios.isNotEmpty()) {
                    HorarioSection(
                        title = "Horarios Configurados",
                        icon = Icons.Default.Schedule,
                        description = "Revisa y edita los horarios de tu negocio"
                    ) {
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
                } else {
                    // Estado vacío
                    EmptyHorariosState()
                }

                // Espaciado adicional para el bottom bar
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HorarioSection(
    title: String,
    icon: ImageVector,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = if (description != null) 8.dp else 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun TimeSelectionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    selectedTime: String?,
    onTimeSelected: (String) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            TimePickerInputButton(
                label = selectedTime ?: "Seleccionar",
                selectedTime = selectedTime,
                onTimeSelected = onTimeSelected
            )
        }
    }
}

@Composable
fun EmptyHorariosState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sin horarios configurados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Añade los horarios de atención de tu negocio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun PreviewHorarioForm() {
    FrontendappTheme {
        HorarioForm(rememberNavController(), FakeNegocioViewModel(), modo = "crear")
    }
}