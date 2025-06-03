package com.example.frontendapp.ui.theme.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.utils.UbicacionHelper

data class UserPreferences(
    val darkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "es"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreenMejorada(
    preferences: UserPreferences,
    onDarkThemeChanged: (Boolean) -> Unit,
    onLanguageChanged: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        HeaderSection()

        // Configuración de apariencia
        ConfigurationCard(
            title = "Apariencia",
            icon = Icons.Default.Palette
        ) {
            PreferenceItem(
                icon = if (preferences.darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                title = "Tema oscuro",
                subtitle = if (preferences.darkTheme) "Activado" else "Desactivado",
                trailing = {
                    Switch(
                        checked = preferences.darkTheme,
                        onCheckedChange = onDarkThemeChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            LanguageSelector(
                selectedLanguage = preferences.language,
                onLanguageChanged = onLanguageChanged
            )
        }

        // Configuración de cuenta
        ConfigurationCard(
            title = "Cuenta",
            icon = Icons.Default.Person
        ) {
            PreferenceItem(
                icon = Icons.Default.Edit,
                title = "Editar perfil",
                subtitle = "Actualiza tu información personal",
                onClick = onNavigateToEditProfile
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            PreferenceItem(
                icon = Icons.Default.Lock,
                title = "Cambiar contraseña",
                subtitle = "Actualiza tu contraseña de acceso",
                onClick = onNavigateToChangePassword
            )
        }

        // Configuración de privacidad
        ConfigurationCard(
            title = "Privacidad y permisos",
            icon = Icons.Default.Security
        ) {

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            LocationPermissionItem()
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Preferencias",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ConfigurationCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Header de la card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            content()
        }
    }
}

@Composable
private fun PreferenceItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (onClick != null) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else Color.Transparent,
        animationSpec = tween(200),
        label = "background_color"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        trailing?.invoke()

        if (onClick != null && trailing == null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LanguageSelector(
    selectedLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    val idiomas = listOf(
        "es" to "Español",
        "en" to "English",
        "fr" to "Français"
    )
    var expanded by remember { mutableStateOf(false) }

    PreferenceItem(
        icon = Icons.Default.Language,
        title = "Idioma",
        subtitle = idiomas.find { it.first == selectedLanguage }?.second ?: selectedLanguage,
        onClick = { expanded = true },
        trailing = {
            Box {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    idiomas.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = name,
                                        color = if (code == selectedLanguage)
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (code == selectedLanguage) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onLanguageChanged(code)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun LocationPermissionItem() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    PreferenceItem(
        icon = Icons.Default.LocationOn,
        title = "Permisos de ubicación",
        subtitle = "Gestiona el acceso a tu ubicación",
        onClick = {
            activity?.let {
                UbicacionHelper.forzarSolicitudPermisoUbicacionDesde(
                    activity = it,
                    onConcedido = {
                        Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
                    },
                    onRechazado = {
                        Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreferenciasScreenMejoradaPreview() {
    var preferences by remember { mutableStateOf(UserPreferences()) }

    FrontendappTheme {
        PreferenciasScreenMejorada(
            preferences = preferences,
            onDarkThemeChanged = { preferences = preferences.copy(darkTheme = it) },
            onLanguageChanged = { preferences = preferences.copy(language = it) }
        )
    }
}

@Preview(
    name = "Preferencias Mejorada - Modo Oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreferenciasScreenMejoradaDarkPreview() {
    var preferences by remember { mutableStateOf(UserPreferences(darkTheme = true)) }

    FrontendappTheme {
        PreferenciasScreenMejorada(
            preferences = preferences,
            onDarkThemeChanged = { preferences = preferences.copy(darkTheme = it) },
            onLanguageChanged = { preferences = preferences.copy(language = it) }
        )
    }
}
