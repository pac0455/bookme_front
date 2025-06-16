package com.example.frontendapp.ui.theme.screens

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import android.Manifest
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.LiveHelp
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.modals.AboutModal
import com.example.frontendapp.ui.theme.composables.modals.DeveloperInfo
import com.example.frontendapp.ui.theme.composables.modals.getDefaultDeveloperInfo
import com.example.frontendapp.utils.UbicacionHelper


@Composable
fun PreferenciasScreenMejorada(
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToHelp: () -> Unit,
    onBack: () -> Unit
) {
    var showAboutModal by remember { mutableStateOf(false) }
    val  developerInfo by remember {
        mutableStateOf(
            getDefaultDeveloperInfo()
        )
    }
    AboutModal(
        isVisible = showAboutModal,
        onDismiss = { showAboutModal = false },
        developerInfo = developerInfo
    )
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
        HeaderSection(onBack)


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

            HorizontalDivider(
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
        // Configuración de privacidad
        ConfigurationCard(
            title = "Centro de ayuda",
            icon = Icons.AutoMirrored.Filled.HelpOutline
        ) {

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            PreferenceItem(
                icon = Icons.AutoMirrored.Filled.LiveHelp,
                title = "Ir a pagina de ayuda",
                subtitle = "Tiene dudas sobre la app?",
                onClick = onNavigateToHelp
            )
            PreferenceItem(
                icon = Icons.Default.Info,
                title = "Acerca de...",
                subtitle = "Conoce más sobre el autor",
                onClick = { showAboutModal = true }
            )
        }
    }
}

@Composable
private fun HeaderSection(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(32.dp)
                .clickable { onBack() }
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
fun LocationPermissionItem() {
    val context = LocalContext.current

    PreferenceItem(
        icon = Icons.Default.LocationOn,
        title = "Permisos de ubicación",
        subtitle = "Gestiona el acceso a tu ubicación",
        onClick = {
            UbicacionHelper.openAppSettings(context)
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreferenciasScreenMejoradaPreview() {
    FrontendappTheme {
        PreferenciasScreenMejorada(
            onNavigateToHelp = {},
            onBack = {}
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
    FrontendappTheme {
        PreferenciasScreenMejorada(
            onNavigateToHelp = {},
            onBack = {}
        )
    }
}
