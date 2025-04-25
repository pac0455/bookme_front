package com.example.frontendapp.ui.theme.composables


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion3




@Composable
fun BtnStyle1(
    onClick: () -> Unit,
    text: String = "Ejemplo"
) {
    Button(
        modifier = Modifier
            .padding(8.dp)
            .shadow(
                elevation = 20.dp, // 👈 sombra potente
                shape = RoundedCornerShape(16.dp),
                clip = false, // deja que la sombra sobresalga,
                spotColor = Color.Black
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Principal_variacion3, // Azul Material
            contentColor = Color.White
        ),
        shape = RectangleShape,

        onClick = onClick) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun BtnPreview() {
    BtnStyle1(onClick = { /* acción */ }, text = "Registrarse")
}