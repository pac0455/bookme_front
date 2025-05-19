package com.example.frontendapp.ui.theme.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion2
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.Principal_variacion4

@Composable
fun DaySelector(
    diasVisuales: List<String>,
    diasInternos: List<String>,
    diasSeleccionados: SnapshotStateList<String>,
    onClearTimeSelection: () -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(diasVisuales.size) { index ->
            val diaReal = diasInternos[index]
            val diaVisible = diasVisuales[index]
            val isSelected = diasSeleccionados.contains(diaReal)

            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) Principal_variacion4 else Principal_variacion3,
                label = "dayColor"
            )

            val offsetY by animateDpAsState(
                targetValue = if (isSelected) (-15).dp else 0.dp,
                label = "verticalOffset"
            )

            BtnStyle1(
                modifier = Modifier
                    .offset { IntOffset(x = 0, y = offsetY.roundToPx()) }
                    .size(70.dp),

                onClick = {
                    if (isSelected) diasSeleccionados.remove(diaReal)
                    else diasSeleccionados.add(diaReal)
                    onClearTimeSelection()
                },
                text = diaVisible,
                containerColor = animatedColor,
                shape = RoundedCornerShape(50.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    // Fila con botón de "Todos"
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            val todosSeleccionados = diasSeleccionados.size == diasInternos.size
            val animatedColor by animateColorAsState(
                targetValue = if (todosSeleccionados) Principal_variacion4 else Principal_variacion3,
                label = "colorTodos"
            )
            BtnStyle1(
                text = "Todos",
                containerColor = animatedColor,
                onClick = {
                    if (todosSeleccionados) {
                        diasSeleccionados.clear()
                    } else {
                        diasSeleccionados.clear()
                        diasSeleccionados.addAll(diasInternos)
                    }
                    onClearTimeSelection()
                }
            )
        }

    }

}
@Preview(showBackground = true)
@Composable
fun PreviewDaySelector() {
    val diasVisuales = listOf("L", "M", "X", "J", "V", "S", "D")
    val diasInternos = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val diasSeleccionados = remember { mutableStateListOf<String>() }

    FrontendappTheme  {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DaySelector(
                diasVisuales = diasVisuales,
                diasInternos = diasInternos,
                diasSeleccionados = diasSeleccionados,
                onClearTimeSelection = {}
            )
        }
    }
}