package com.example.frontendapp.ui.theme.composables.Btn

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme

@Composable
fun QuickActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    label: String
) {
    // Colores más distintivos del tema Material 3
    val backgroundColor = if (isSelected)
        MaterialTheme.colorScheme.primary // Color principal más visible
    else
        MaterialTheme.colorScheme.surfaceVariant

    val iconTint = if (isSelected)
        MaterialTheme.colorScheme.onPrimary // Contraste perfecto con primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    val textColor = if (isSelected)
        MaterialTheme.colorScheme.primary // Texto en color principal cuando seleccionado
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    // Desplazamiento animado cuando está seleccionado
    val offsetY by animateDpAsState(
        targetValue = if (isSelected) (-4).dp else 0.dp,
        label = "ElevaciónAnimada"
    )

    // Sombra más pronunciada cuando está seleccionado
    val shadowElevation = if (isSelected) 12.dp else 8.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(83.dp)
    ) {
        Column(
            modifier = Modifier
                .width(83.dp)
                .height(80.dp)
                .offset { IntOffset(0, offsetY.roundToPx()) }
                .shadow(shadowElevation, RoundedCornerShape(16.dp)) // Sombra variable
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .clickable { onClick() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(androidx.compose.ui.graphics.Color.Transparent)
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Texto descriptivo debajo del botón
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = if (isSelected)
                MaterialTheme.typography.labelMedium // Texto más prominente cuando seleccionado
            else
                MaterialTheme.typography.labelSmall,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(83.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuickActionButton() {
    FrontendappTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Home,
                    onClick = {},
                    isSelected = false,
                    label = "Inicio"
                )
                QuickActionButton(
                    icon = Icons.Default.CalendarMonth,
                    onClick = {},
                    isSelected = true,
                    label = "Servicios"
                )
            }
        }
    }
}

// Preview adicional para mostrar diferentes estados
@Preview(showBackground = true, name = "Estados Múltiples")
@Composable
fun PreviewQuickActionButtonStates() {
    FrontendappTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Estados del Botón",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Home,
                        onClick = {},
                        isSelected = false,
                        label = "Normal"
                    )
                    QuickActionButton(
                        icon = Icons.Default.CalendarMonth,
                        onClick = {},
                        isSelected = true,
                        label = "Seleccionado"
                    )
                }
            }
        }
    }
}