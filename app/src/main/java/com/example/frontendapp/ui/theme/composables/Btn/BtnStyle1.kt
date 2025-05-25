package com.example.frontendapp.ui.theme.composables.Btn


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion3


enum class IconPosition{
    START,END
}


@Composable
fun BtnStyle1(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    iconSize: Dp = 34.dp,
    onClick: () -> Unit,
    text: String = "Ejemplo",
    icon: ImageVector? = null,
    iconPosition: IconPosition? = IconPosition.START,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    containerColor: Color = Principal_variacion3,
    shape: Shape = RectangleShape
) {
    val horizontalArrangement = when (horizontalAlignment) {
        Alignment.Start -> Arrangement.Start
        Alignment.CenterHorizontally -> Arrangement.Center
        Alignment.End -> Arrangement.End
        else -> Arrangement.Center
    }

    val displayText = if (isLoading) "Cargando..." else text
    val displayIcon = if (isLoading) Icons.Default.HourglassEmpty else icon

    Button(
        modifier = modifier
            .padding(8.dp)
            .shadow(
                elevation = 20.dp,
                shape = shape,
                clip = false,
                spotColor = Color.Black
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        shape = shape,
        onClick = onClick,
        enabled = !isLoading // opcional: deshabilitar mientras carga
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (displayText.isEmpty()) Arrangement.Center else horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (displayIcon != null && (iconPosition == IconPosition.START || displayText.isEmpty())) {
                Icon(
                    imageVector = displayIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = if (displayText.isNotEmpty()) 8.dp else 0.dp)
                        .size(iconSize)
                )
            }

            if (displayText.isNotEmpty()) {
                Text(displayText)
            }

            if (displayIcon != null && iconPosition == IconPosition.END && displayText.isNotEmpty()) {
                Icon(
                    imageVector = displayIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(iconSize)
                )
            }
        }
    }
}








@Preview(showBackground = true)
@Composable
fun BtnPreview() {

    BtnStyle1(onClick = { /* acción */ }, text = "Registrarse", icon = Icons.Default.Settings)
}