package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.model.pago.MetodoPagoDto
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.AppColors
import com.example.frontendapp.ui.theme.ThemeColors
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
import java.time.LocalDate
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

    // Estado para el método de pago seleccionado
    var metodoPagoSeleccionado by remember { mutableStateOf(MetodoPagoDto.TARJETA) }

    // Estado para mostrar/ocultar la sección de pago
    var mostrarSeccionPago by remember { mutableStateOf(false) }

    // Estado para el mensaje de error (inicialmente vacío)
    var errorMessage by remember { mutableStateOf("") }
    val errorPagoMessage by remember { mutableStateOf("") }

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reserva",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BtnStyle1(
                modifier = Modifier.navigationBarsPadding(),
                isLoading = isLoading,
                text = "Confirmar Reserva",
                onClick = {
                    // Validar selección de horario
                    if (horarioSeleccionado == null) {
                        errorMessage = "Debe seleccionar una hora de inicio"
                        return@BtnStyle1
                    } else {
                        errorMessage = ""
                        reservasViewModel.addServicio(
                            horaInicio = horarioSeleccionado!!.horaInicio.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                            horaFin = horarioSeleccionado!!.horaFin.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                            servicio = servicioSeleccionado
                        )
                    }

                    // Procesar la reserva con el pago
                    if(errorMessage.isEmpty()){
                        reservasViewModel.setNegocioId(negocioId)
                        reservasViewModel.setUserId(RetrofitInstance.getUserId())

                        reservasViewModel.addReserva(
                            monto = servicioSeleccionado.precio,
                            metodoPago = metodoPagoSeleccionado,
                            onSucces = {
                                isLoading = false
                                Toast.makeText(context,"Reserva y pago procesados con éxito", Toast.LENGTH_SHORT).show()
                                navController.navigate(NavigationItem.CLIENTE_MAIN_SCREEN.route)
                            },
                            onError = {
                                isLoading = false
                                Log.d("ReservaForm", it)
                                Toast.makeText(context,"La reserva no se ha podido completar", Toast.LENGTH_SHORT).show()
                            },
                            onLoading = {
                                isLoading = true
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Calendar(
                    initialDate = fechaSeleccionada,
                    fechasConHorarios = diasDisponibles,
                    onDateSelected = { fechaSeleccionada = it },
                    onMonthChanged = { horariosViewModel.setMesActual(it) }
                )
            }

            item {
                when (estadoHorariosDisponibles) {
                    is Resource.Loading -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    is Resource.Success -> {
                        val horarios = (estadoHorariosDisponibles as Resource.Success<List<Horario>>).data
                        if (horarios.isNullOrEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AppColors.GreenBackground
                                )
                            ) {
                                Text(
                                    "No hay horarios disponibles para este día",
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    "Horarios disponibles:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                LazyRowConFlechas(
                                    horarios = horarios,
                                    horarioSeleccionado = horarioSeleccionado,
                                    onHorarioClick = { horarioSeleccionado = it }
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AppColors.Error.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = "Error: ${(estadoHorariosDisponibles as Resource.Error<List<Horario>>).message ?: "Desconocido"}",
                                color = AppColors.Error,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is Resource.None -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                "No hay servicio disponible",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
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

            // Sección de pago mejorada
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { mostrarSeccionPago = !mostrarSeccionPago }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Método de pago",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (mostrarSeccionPago) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (mostrarSeccionPago) "Ocultar" else "Mostrar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(
                            visible = mostrarSeccionPago,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                MetodoPagoDto.entries.forEach { metodo ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = (metodo == metodoPagoSeleccionado),
                                                onClick = { metodoPagoSeleccionado = metodo }
                                            )
                                            .padding(vertical = 12.dp)
                                            .background(
                                                if (metodo == metodoPagoSeleccionado)
                                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = (metodo == metodoPagoSeleccionado),
                                            onClick = { metodoPagoSeleccionado = metodo },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = MaterialTheme.colorScheme.primary,
                                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            metodo.displayName,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }

                                if (metodoPagoSeleccionado == MetodoPagoDto.TARJETA) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Simulación visual de tarjeta mejorada
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(70.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                    colors = listOf(
                                                        AppColors.GreenPrimary,
                                                        AppColors.GreenAccent
                                                    )
                                                )
                                            )
                                            .padding(16.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CreditCard,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                "•••• •••• •••• 1234",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                if (errorPagoMessage.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = errorPagoMessage,
                                        color = AppColors.Error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Resumen de pago mejorado
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Resumen de pago",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        PaymentSummaryRow(
                            label = "Servicio",
                            value = servicioSeleccionado.nombre
                        )

                        PaymentSummaryRow(
                            label = "Precio",
                            value = "${servicioSeleccionado.precio}€"
                        )

                        PaymentSummaryRow(
                            label = "Método de pago",
                            value = metodoPagoSeleccionado.displayName
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total a pagar",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "${servicioSeleccionado.precio}€",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Espacio adicional al final
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PaymentSummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
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

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSurfaceVariant
    )

    Text(
        text = "$horaInicioFormateada - $horaFinFormateada",
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
        IconButton(
            onClick = {
                coroutineScope.launch {
                    val prevIndex = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                    listState.animateScrollToItem(prevIndex)
                }
            },
            enabled = canScrollBackward
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = "Anterior",
                tint = if (canScrollBackward)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }

        LazyRow(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                horarios,
                key = { it.horaInicio + it.horaFin }
            ) { horario ->
                val estaSeleccionado = horarioSeleccionado?.let {
                    it.horaInicio == horario.horaInicio && it.horaFin == horario.horaFin
                } ?: false

                HorarioItem(
                    horaInicio = horario.horaInicio,
                    horaFin = horario.horaFin,
                    isSelected = estaSeleccionado,
                    onClick = {
                        onHorarioClick(horario)
                    }
                )
            }
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    val nextIndex = (listState.firstVisibleItemIndex + 1).coerceAtMost(horarios.lastIndex)
                    listState.animateScrollToItem(nextIndex)
                }
            },
            enabled = canScrollForward
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Siguiente",
                tint = if (canScrollForward)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun ServicioReservaItem(
    servicio: Servicio,
    horario: Pair<String, String>? = null,
    errorMessage: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${servicio.precio}€",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = horario?.let { "${it.first} - ${it.second}" } ?: "Sin horario asignado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = AppColors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewReservaFormDark() {
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