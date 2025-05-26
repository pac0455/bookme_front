package com.example.frontendapp.ui.theme.composables.list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Horario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.frontendapp.ui.theme.FrontendappTheme

@SuppressLint("SuspiciousIndentation")
@Composable
fun HorarioList(
    horarios: List<Horario>,
    horariosMarcados: SnapshotStateList<Horario>,
    onEditar: (Horario) -> Unit,
    onEliminar: (List<Horario>) -> Unit
) {
    val isInSelectionMode = horariosMarcados.isNotEmpty()
    if (horarios.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay horarios añadidos",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
    else {
        val multipleSelection = horariosMarcados.size > 1
        val groupedHorarios = horarios.groupBy { it.diaSemana }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp) // Ajusta la altura máxima visible
                .padding(vertical = 8.dp)
        ) {
            groupedHorarios.forEach { (dia, lista) ->
                item {
                    Text(
                        text = dia,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(lista) { horario ->
                    val isMarked = horariosMarcados.contains(horario)
                    val bgColor = if (isMarked) Color(0xFFD0EBFF) else Color(0xFFF5F5F5)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = isMarked,
                                    onCheckedChange = {
                                        if (it) horariosMarcados.add(horario)
                                        else horariosMarcados.remove(horario)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${horario.horaInicio} - ${horario.horaFin}")
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (horariosMarcados.count() <= 1) {
                                    IconButton(onClick = { onEditar(horario) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        val aEliminar = if (isMarked) horariosMarcados.toList() else listOf(horario)
                                        onEliminar(aEliminar)
                                    },
                                ) {
                                    val darkRed = Color.Red.copy(alpha = 1f).darken(0.2f)
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = darkRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Color.darken(factor: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    hsv[2] *= (1f - factor).coerceIn(0f, 1f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}


@Preview(showBackground = true)
@Composable
fun PreviewHorarioList() {
    val horarios = remember {
        mutableStateListOf(
            Horario(id = 1, idNegocio = 1, diaSemana = "Lunes", horaInicio = "08:00", horaFin = "12:00"),
            Horario(id = 2, idNegocio = 1, diaSemana = "Lunes", horaInicio = "16:00", horaFin = "20:00"),
            Horario(id = 3, idNegocio = 1, diaSemana = "Martes", horaInicio = "09:00", horaFin = "13:00")
        )
    }

    val horariosMarcados = remember {
        mutableStateListOf(
            // ✅ Marcamos 2 para simular selección múltiple
            Horario(id = 1, idNegocio = 1, diaSemana = "Lunes", horaInicio = "08:00", horaFin = "12:00"),
            Horario(id = 2, idNegocio = 1, diaSemana = "Lunes", horaInicio = "16:00", horaFin = "20:00")
        )
    }

    FrontendappTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            HorarioList(
                horarios = horarios,
                horariosMarcados = horariosMarcados,
                onEditar = { /* acción de prueba */ },
                onEliminar = { horariosAEliminar ->
                    horarios.removeAll(horariosAEliminar)
                    horariosMarcados.removeAll(horariosAEliminar)
                }
            )
        }
    }
}






