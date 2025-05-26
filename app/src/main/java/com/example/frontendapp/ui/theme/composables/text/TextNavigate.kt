package com.example.frontendapp.ui.theme.composables.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TextNavigate(
    texto: String,
    navController: NavController,
    destino: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = texto,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.Center,
        modifier = modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(destino)
            }
    )
}
@Preview(showBackground = true)
@Composable
fun TextNavigatePreview() {
    val navController = rememberNavController()
    TextNavigate(
        texto = "¿No tienes cuenta? Registrarse",
        navController = navController,
        destino = "register"
    )
}