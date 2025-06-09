package com.example.frontendapp.data.model.UI

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.frontendapp.ui.theme.ThemeColors

@Composable
fun EstadoNegocio.toEstadoNegocioUI(): EstadoNegocioUI {
    return when (this) {
        EstadoNegocio.BLOQUEADO -> EstadoNegocioUI(
            color = ThemeColors.error,
            backgroundColor = ThemeColors.error.copy(alpha = 0.1f),
            icon = Icons.Default.Block,
            label = "Bloqueado"
        )
        EstadoNegocio.ACTIVO -> EstadoNegocioUI(
            color = ThemeColors.success,
            backgroundColor = ThemeColors.success.copy(alpha = 0.1f),
            icon = Icons.Default.CheckCircle,
            label = "Activo"
        )
        EstadoNegocio.INACTIVO -> EstadoNegocioUI(
            color = ThemeColors.error,
            backgroundColor = ThemeColors.error.copy(alpha = 0.1f),
            icon = Icons.Default.Cancel,
            label = "Inactivo"
        )
        EstadoNegocio.SIN_ESPECICAR -> EstadoNegocioUI(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            label = "Sin especificar"
        )
    }
}

//Bloqueado
enum class EstadoNegocio{
    BLOQUEADO,
    INACTIVO,
    ACTIVO,
    SIN_ESPECICAR
}
data class EstadoNegocioUI(
    val color: Color,
    val backgroundColor: Color,
    val icon: ImageVector,
    val label: String
)
