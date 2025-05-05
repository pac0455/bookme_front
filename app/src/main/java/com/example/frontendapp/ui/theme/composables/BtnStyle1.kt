package com.example.frontendapp.ui.theme.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.R;


enum class IconPosition{
    START,END
}


@Composable
fun BtnStyle1(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String = "Ejemplo",
    icon: ImageVector? = null,
    iconPosition: IconPosition? = IconPosition.START,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    val horizontalArrangement = when (horizontalAlignment) {
        Alignment.Start -> Arrangement.Start
        Alignment.CenterHorizontally -> Arrangement.Center
        Alignment.End -> Arrangement.End
        else -> Arrangement.Center
    }

    Button(
        modifier = modifier
            .padding(8.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                spotColor = Color.Black
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Principal_variacion3,
            contentColor = Color.White
        ),
        shape = RectangleShape,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // 👈 aquí está el cambio clave
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null && iconPosition == IconPosition.START) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Text(text)

            if (icon != null && iconPosition == IconPosition.END) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
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