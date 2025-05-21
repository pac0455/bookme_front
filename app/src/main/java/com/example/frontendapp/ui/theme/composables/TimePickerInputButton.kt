package com.example.frontendapp.ui.theme.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion3
import  androidx.compose.runtime.setValue
import  androidx.compose.runtime.getValue


@Composable
fun TimePickerInputButton(
    label: String,
    selectedTime: String?,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable {
                showTimePicker(context) { time -> onTimeSelected(time) }
            }
            .padding(horizontal = 16.dp)
            .padding(bottom = 4.dp), // deja espacio para el borde
        contentAlignment = Alignment.CenterStart
    ) {
        // Fondo gris claro
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 2.dp)
                .background(Color.LightGray.copy(alpha = 0.1f))
        )

        // Texto
        Text(
            text = selectedTime ?: label,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )

        // Borde inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(2.dp)
                .background(Principal_variacion3)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimePickerInputButton() {
    var selectedTime by remember { mutableStateOf<String?>(null) }

    TimePickerInputButton(
        label = "Selecciona hora",
        selectedTime = selectedTime,
        onTimeSelected = { selectedTime = it },
        modifier = Modifier.padding(16.dp)
    )
}
