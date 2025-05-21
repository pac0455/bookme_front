package com.example.frontendapp.ui.theme.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion2
import com.example.frontendapp.ui.theme.Principal_variacion6


@Composable
fun QuickActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    val backgroundColor = if (isSelected) Principal_variacion6 else Principal_variacion2
    val iconTint = if (isSelected) Color.Blue else Color.Black

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            Modifier
                .width(83.dp)
                .height(80.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))  // sombra tipo tarjeta
                .clip(RoundedCornerShape(16.dp))          // esquinas redondeadas
                .background(backgroundColor)                  // fondo tipo tarjeta
                .clickable { onClick() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp) // Tamaño del contenedor
                    .background(Color.Transparent) // Fondo del contenedor
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.fillMaxSize() // Tamaño del icono
                )
            }
        }
    }
}
