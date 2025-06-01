package com.example.frontendapp.ui.theme.composables



import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CustomBoxPreview() {
    CustomBox(
        borderTop = true,
    ){
        Text("Ejemplo")
    }
}

@Composable
fun CustomBox(
    modifier: Modifier = Modifier,
    borderLeft: Boolean = false,
    borderRight: Boolean = false,
    borderTop: Boolean = false,
    borderBottom: Boolean = false,
    border: Boolean = false,
    strokeColor: Color = Color.Black,
    content: @Composable () -> Unit
) {
    val indicatorWidth = 1.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            // Solo un padding, aquí o abajo, no ambos
            //.padding(16.dp)
            .drawBehind {
                val strokeWidth = indicatorWidth.toPx()
                val drawTop = border || borderTop
                val drawBottom = border || borderBottom
                val drawLeft = border || borderLeft
                val drawRight = border || borderRight

                if (drawTop) {
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )
                }

                if (drawBottom) {
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }

                if (drawLeft) {
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )
                }

                if (drawRight) {
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
            }
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)  // Aquí está bien dejar padding para el contenido
                .align(Alignment.Center)
        ) {
            content()
        }
    }
}
