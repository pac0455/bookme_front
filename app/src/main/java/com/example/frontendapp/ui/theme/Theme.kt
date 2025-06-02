package com.example.frontendapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


// Colores complementarios para un sistema completo
object AppColors {
    // Verdes principales
    val GreenPrimary = Principal
    val GreenLight = Principal_variacion1
    val GreenSoft = Principal_variacion2
    val GreenDark = Principal_variacion3
    val GreenMuted = Principal_variacion4
    val GreenAccent = Principal_variacion5
    val GreenBackground = Principal_variacion6
    val GreenSecondary = Secundario

    // Colores de estado (manteniendo armonía con tu paleta)
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFE57373)
    val Info = Color(0xFF42A5F5)

    // Neutros para modo claro
    val NeutralWhite = Color(0xFFFFFFFF)
    val NeutralLight = Color(0xFFF8F9FA)
    val NeutralSoft = Color(0xFFF1F3F4)
    val NeutralMedium = Color(0xFFE8EAED)
    val NeutralDark = Color(0xFF5F6368)
    val NeutralBlack = Color(0xFF202124)

    // Neutros para modo oscuro
    val DarkSurface = Color(0xFF1A1C1E)
    val DarkSurfaceVariant = Color(0xFF2D3135)
    val DarkBackground = Color(0xFF121416)
    val DarkOnSurface = Color(0xFFE3E3E3)
    val DarkOnBackground = Color(0xFFE8E8E8)
}

// Esquema de colores para modo claro
val MyLightColorScheme = lightColorScheme(
    // Colores principales
    primary = AppColors.GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AppColors.GreenBackground,
    onPrimaryContainer = AppColors.GreenSecondary,

    // Colores secundarios
    secondary = AppColors.GreenSecondary,
    onSecondary = Color.White,
    secondaryContainer = AppColors.GreenSoft,
    onSecondaryContainer = AppColors.GreenSecondary,

    // Colores terciarios
    tertiary = AppColors.GreenAccent,
    onTertiary = Color.White,
    tertiaryContainer = AppColors.GreenLight,
    onTertiaryContainer = AppColors.GreenDark,

    // Fondos
    background = AppColors.NeutralLight,
    onBackground = AppColors.NeutralBlack,
    surface = AppColors.NeutralWhite,
    onSurface = AppColors.NeutralBlack,

    // Variantes de superficie
    surfaceVariant = AppColors.GreenBackground,
    onSurfaceVariant = AppColors.GreenSecondary,
    surfaceTint = AppColors.GreenPrimary,

    // Contornos
    outline = AppColors.NeutralMedium,
    outlineVariant = AppColors.GreenSoft,

    // Estados de error
    error = AppColors.Error,
    onError = Color.White,
    errorContainer = AppColors.Error.copy(alpha = 0.1f),
    onErrorContainer = AppColors.Error,

    // Contenedores inversos
    inverseSurface = AppColors.NeutralBlack,
    inverseOnSurface = AppColors.NeutralWhite,
    inversePrimary = AppColors.GreenLight,

    // Scrim
    scrim = Color.Black.copy(alpha = 0.32f)
)

// Esquema de colores para modo oscuro
val MyDarkColorScheme = darkColorScheme(
    // Colores principales
    primary = AppColors.GreenLight,
    onPrimary = AppColors.GreenSecondary,
    primaryContainer = AppColors.GreenDark,
    onPrimaryContainer = AppColors.GreenBackground,

    // Colores secundarios
    secondary = AppColors.GreenAccent,
    onSecondary = AppColors.GreenSecondary,
    secondaryContainer = AppColors.GreenSecondary,
    onSecondaryContainer = AppColors.GreenLight,

    // Colores terciarios
    tertiary = AppColors.GreenSoft,
    onTertiary = AppColors.GreenSecondary,
    tertiaryContainer = AppColors.GreenMuted,
    onTertiaryContainer = AppColors.GreenBackground,

    // Fondos
    background = AppColors.DarkBackground,
    onBackground = AppColors.DarkOnBackground,
    surface = AppColors.DarkSurface,
    onSurface = AppColors.DarkOnSurface,

    // Variantes de superficie
    surfaceVariant = AppColors.DarkSurfaceVariant,
    onSurfaceVariant = AppColors.GreenLight,
    surfaceTint = AppColors.GreenAccent,

    // Contornos
    outline = AppColors.GreenMuted,
    outlineVariant = AppColors.GreenDark,

    // Estados de error
    error = AppColors.Error.copy(alpha = 0.8f),
    onError = Color.White,
    errorContainer = AppColors.Error.copy(alpha = 0.2f),
    onErrorContainer = AppColors.Error.copy(alpha = 0.9f),

    // Contenedores inversos
    inverseSurface = AppColors.NeutralWhite,
    inverseOnSurface = AppColors.NeutralBlack,
    inversePrimary = AppColors.GreenPrimary,

    // Scrim
    scrim = Color.Black.copy(alpha = 0.5f)
)

@Composable
fun FrontendappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Cambiado a false para usar tu paleta personalizada
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MyDarkColorScheme
        else -> MyLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Extensiones útiles para acceder a colores personalizados
object ThemeColors {
    val success: Color
        @Composable get() = AppColors.Success

    val warning: Color
        @Composable get() = AppColors.Warning
    val error: Color
        @Composable get() = AppColors.Error

    val info: Color
        @Composable get() = AppColors.Info

    val greenPrimary: Color
        @Composable get() = AppColors.GreenPrimary

    val greenSecondary: Color
        @Composable get() = AppColors.GreenSecondary

    val greenBackground: Color
        @Composable get() = AppColors.GreenBackground
}

// Ejemplo de uso de los colores personalizados
@Composable
fun ExampleUsage() {
    // Usando colores del tema estándar
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Usando colores personalizados
    val successColor = ThemeColors.success
    val warningColor = ThemeColors.warning
}