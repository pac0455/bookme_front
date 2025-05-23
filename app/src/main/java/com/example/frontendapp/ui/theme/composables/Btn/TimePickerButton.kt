package com.example.frontendapp.ui.theme.composables.Btn

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.frontendapp.ui.theme.composables.BtnStyle1
import java.util.Calendar

@Composable
fun TimePickerButton(
    label: String,
    selectedTime: String?,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    BtnStyle1(
        text = selectedTime ?: label,
        onClick = {
            showTimePicker(context, onTimeSelected)
        }
    )
}

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
