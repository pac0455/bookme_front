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

//// Colores personalizados modo claro
//val MyLightColorScheme = lightColorScheme(
//    primary = Principal,
//    onPrimary = Color.White,
//    secondary = Secundario,
//    onSecondary = Color.White,
//    background = Color.White,
//    onBackground = Color.Black,
//    surface = Color.White,
//    onSurface = Color.Black,
//    surfaceVariant = Principal_variacion1,
//    onSurfaceVariant = Color(0xFF444444)
//)
//
//// Colores personalizados modo oscuro
//val MyDarkColorScheme = darkColorScheme(
//    primary = Principal_variacion3,
//    onPrimary = Color.Black,
//    secondary = Secundario,
//    onSecondary = Color.White,
//    background = Color(0xFF121212),
//    onBackground = Color.White,
//    surface = Color(0xFF1E1E1E),
//    onSurface = Color.White,
//    surfaceVariant = Color(0xFF2C2C2C),
//    onSurfaceVariant = Color(0xFFAAAAAA),
//)

// Claro
val MyLightColorScheme = lightColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color.White,
    secondary = Color(0xFFFF6F61),
    onSecondary = Color.White,
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE3E7EB),
    onSurfaceVariant = Color(0xFF5F6368)
)

// Oscuro
val MyDarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B6BB8),
    onPrimary = Color(0xFFE1E6F0),
    secondary = Color(0xFFE06450),
    onSecondary = Color(0xFFE1E6F0),
    background = Color(0xFF121A26),
    onBackground = Color(0xFFE1E6F0),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFE1E6F0),
    surfaceVariant = Color(0xFF2C3E50),
    onSurfaceVariant = Color(0xFFA0A7B7)
)


@Composable
fun FrontendappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
