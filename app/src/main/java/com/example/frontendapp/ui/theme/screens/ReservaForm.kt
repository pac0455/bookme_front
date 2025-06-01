package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.CustomBox
import com.example.frontendapp.ui.theme.composables.calendar.Calendar
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.HorariosViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeHorariosViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaForm(
    servicioViewModel: ServicioViewModel,
    reservasViewModel: ReservasViewModel,
    horariosViewModel: HorariosViewModel,
    navController: NavController,
    negocioId: Int
) {
    var fechaSeleccionada by remember { mutableStateOf(LocalDate.now()) }
    val mesActual by horariosViewModel.mesActual.collectAsState()
    val servicioSeleccionado by servicioViewModel.tempServicios.collectAsState()
    val estadoHorariosDisponibles by horariosViewModel.horariosDisponiblesState.collectAsState()
    val diasDisponibles by horariosViewModel.diasDelMes.collectAsState()
    var horarioSeleccionado by remember { mutableStateOf<Horario?>(null) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    // Estado para el mensaje de error (inicialmente vacío)
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        reservasViewModel.setServicio(servicioSeleccionado.id)
    }
    LaunchedEffect(mesActual) {
        horariosViewModel.getHorariosByNegocioId(negocioId)
    }
    LaunchedEffect(fechaSeleccionada, servicioSeleccionado) {
        horariosViewModel.getHorariosDisponibles(
            servicioId = servicioSeleccionado.id,
            negocioId = negocioId,
            selectedDay = fechaSeleccionada.format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reserva") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BtnStyle1(
                modifier = Modifier.navigationBarsPadding(),
                isLoading=isLoading,
                text = "Añadir",
                onClick = {
                    if (horarioSeleccionado == null) {
                        errorMessage = "Debe seleccionar una hora de inicio"
                    } else {
                        errorMessage = ""
                        reservasViewModel.addServicio(
                            horaInicio = horarioSeleccionado!!.horaInicio.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                            horaFin = horarioSeleccionado!!.horaFin.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                            servicio = servicioSeleccionado
                        )
                    }

                    if(errorMessage.isEmpty()){
                        reservasViewModel.setNegocioId(negocioId)
                        reservasViewModel.setUserId(RetrofitInstance.getUserId())
                        reservasViewModel.addReserva(
                            onSucces = {
                                isLoading=false
                                //Navegar a otra pantalla
                                Toast.makeText(context,"Operación realizada con exito", Toast.LENGTH_SHORT).show()
                                navController.navigate(NavigationItem.CLIENTE_MAIN_SCREEN.route)
                            },
                            onError = {
                                isLoading=false
                                Log.d("ReservaForm", it)
                                Toast.makeText(context,"La reserva no se ha podido completar", Toast.LENGTH_SHORT).show()
                            },
                            onLoading = {
                                isLoading= true
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Calendar(
                initialDate = fechaSeleccionada,
                fechasConHorarios = diasDisponibles,
                onDateSelected = { fechaSeleccionada = it },
                onMonthChanged = { horariosViewModel.setMesActual(it) }
            )

            when (estadoHorariosDisponibles) {
                is Resource.Loading -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val horarios = (estadoHorariosDisponibles as Resource.Success<List<Horario>>).data
                    if (horarios.isNullOrEmpty()) {
                        Text("No hay horarios disponibles para este día")
                    } else {
                        LazyRowConFlechas(
                            horarios = horarios,
                            horarioSeleccionado = horarioSeleccionado,
                            onHorarioClick = { horarioSeleccionado = it }
                        )
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = "Error: ${(estadoHorariosDisponibles as Resource.Error<List<Horario>>).message ?: "Desconocido"}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is Resource.None -> {
                    Text("No hay servicio disponible", modifier = Modifier.padding(16.dp))
                }
            }

            ServicioReservaItem(
                servicio = servicioSeleccionado,
                horario = horarioSeleccionado?.let { h ->
                    Pair(
                        h.horaInicio.format(DateTimeFormatter.ofPattern("HH:mm")),
                        h.horaFin.format(DateTimeFormatter.ofPattern("HH:mm"))
                    )
                },
                errorMessage = errorMessage.takeIf { it.isNotEmpty() }
            )
        }
    }
}



@Composable
fun HorarioItem(
    horaInicio: String,
    horaFin: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    fun extraerHoraMinuto(horaCompleta: String): String {
        return if (horaCompleta.length >= 5) {
            horaCompleta.substring(0, 5)
        } else {
            horaCompleta
        }
    }

    val horaInicioFormateada = extraerHoraMinuto(horaInicio)
    val horaFinFormateada = extraerHoraMinuto(horaFin)

    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    else MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Text(
        text = "$horaInicioFormateada - $horaFinFormateada",
        color = textColor,
        modifier = modifier
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}



@Composable
fun LazyRowConFlechas(
    horarios: List<Horario>,
    horarioSeleccionado: Horario?,
    onHorarioClick: (Horario) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val canScrollForward by remember {
        derivedStateOf { listState.firstVisibleItemIndex < horarios.lastIndex }
    }
    val canScrollBackward by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIos,
            contentDescription = "Anterior",
            modifier = Modifier
                .size(32.dp)
                .clickable(enabled = canScrollBackward) {
                    coroutineScope.launch {
                        val prevIndex = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                        Log.d("LazyRowConFlechas", "Scroll atrás a índice $prevIndex")
                        listState.animateScrollToItem(prevIndex)
                    }
                }
                .padding(8.dp),
            tint = if (canScrollBackward) MaterialTheme.colorScheme.primary else Color.Gray
        )

        Spacer(modifier = Modifier.width(4.dp))

        LazyRow(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(
                horarios,
                key = { it.horaInicio + it.horaFin } // Key basada en las horas
            ) { horario ->

                val estaSeleccionado = horarioSeleccionado?.let {
                    it.horaInicio == horario.horaInicio && it.horaFin == horario.horaFin
                } ?: false

                Log.d(
                    "LazyRowConFlechas",
                    "Renderizando Horario (${horario.horaInicio} - ${horario.horaFin}) - Seleccionado: $estaSeleccionado"
                )

                HorarioItem(
                    horaInicio = horario.horaInicio,
                    horaFin = horario.horaFin,
                    isSelected = estaSeleccionado,
                    onClick = {
                        Log.d("LazyRowConFlechas", "Click en horario (${horario.horaInicio} - ${horario.horaFin})")
                        onHorarioClick(horario)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Siguiente",
            modifier = Modifier
                .size(32.dp)
                .clickable(enabled = canScrollForward) {
                    coroutineScope.launch {
                        val nextIndex = (listState.firstVisibleItemIndex + 1).coerceAtMost(horarios.lastIndex)
                        Log.d("LazyRowConFlechas", "Scroll adelante a índice $nextIndex")
                        listState.animateScrollToItem(nextIndex)
                    }
                }
                .padding(8.dp),
            tint = if (canScrollForward) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}


@Composable
fun ServicioReservaItem(
    servicio: Servicio,
    horario: Pair<String, String>? = null,
    errorMessage: String? = null
) {
    CustomBox(
        borderTop = true,
        borderBottom = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = servicio.nombre,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${servicio.precio}€",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Text(
                        text = horario?.let { "${it.first} - ${it.second}" } ?: "Sin horario asignado",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
        }
    }
}



@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewReservaForm() {
    FrontendappTheme {
        ReservaForm(
            servicioViewModel = FakeServicioViewModel(),
            reservasViewModel = FakeReservasViewModel(),
            horariosViewModel = FakeHorariosViewModel(),
            navController = rememberNavController(),
            negocioId = 1
        )
    }
}