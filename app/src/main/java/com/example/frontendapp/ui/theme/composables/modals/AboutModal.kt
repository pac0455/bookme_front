package com.example.frontendapp.ui.theme.composables.modals

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.frontendapp.ui.theme.*

data class DeveloperInfo(
    val name: String,
    val role: String,
    val description: String,
    val email: String,
    val github: String? = null,
    val linkedin: String? = null,
    val website: String? = null,
    val version: String,
    val buildDate: String
)

@Composable
fun AboutModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    developerInfo: DeveloperInfo = getDefaultDeveloperInfo()
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            AboutModalContent(
                developerInfo = developerInfo,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun AboutModalContent(
    developerInfo: DeveloperInfo,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                AppColors.GreenPrimary,
                                AppColors.GreenSoft
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botón cerrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White
                            )
                        }
                    }

                    // Avatar del desarrollador
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre y rol
                    Text(
                        text = developerInfo.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = developerInfo.role,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Descripción
                AboutSection(
                    title = "Acerca del desarrollador",
                    icon = Icons.Default.Info
                ) {
                    Text(
                        text = developerInfo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }

                // Información de contacto
                AboutSection(
                    title = "Contacto",
                    icon = Icons.Default.ContactMail
                ) {
                    ContactInfoItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = developerInfo.email
                    )

                    developerInfo.github?.let { github ->
                        ContactInfoItem(
                            icon = Icons.Default.Code,
                            label = "GitHub",
                            value = github
                        )
                    }

                    developerInfo.linkedin?.let { linkedin ->
                        ContactInfoItem(
                            icon = Icons.Default.Work,
                            label = "LinkedIn",
                            value = linkedin
                        )
                    }

                    developerInfo.website?.let { website ->
                        ContactInfoItem(
                            icon = Icons.Default.Language,
                            label = "Website",
                            value = website
                        )
                    }
                }

                // Información de la app
                AboutSection(
                    title = "Información de la aplicación",
                    icon = Icons.Default.Apps
                ) {
                    AppInfoItem(
                        label = "Versión",
                        value = developerInfo.version
                    )

                    AppInfoItem(
                        label = "Fecha de compilación",
                        value = developerInfo.buildDate
                    )

                    AppInfoItem(
                        label = "Plataforma",
                        value = "Android (Jetpack Compose)"
                    )
                }

                // Tecnologías utilizadas
                AboutSection(
                    title = "Tecnologías utilizadas",
                    icon = Icons.Default.Build
                ) {
                    TechnologyChip("Kotlin")
                    TechnologyChip("Jetpack Compose")
                    TechnologyChip("Material Design 3")
                    TechnologyChip("Android Architecture Components")
                }

                // Agradecimientos
                AboutSection(
                    title = "Agradecimientos",
                    icon = Icons.Default.Favorite
                ) {
                    Text(
                        text = "Gracias por usar esta aplicación. Tu feedback es muy valioso para seguir mejorando.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Surface(
                color = AppColors.GreenBackground,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.GreenPrimary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun ContactInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.GreenPrimary,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AppInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TechnologyChip(technology: String) {
    Surface(
        color = AppColors.GreenBackground.copy(alpha = 0.7f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
    ) {
        Text(
            text = technology,
            style = MaterialTheme.typography.labelMedium,
            color = AppColors.GreenSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// Función para obtener información por defecto del desarrollador
fun getDefaultDeveloperInfo(): DeveloperInfo {
    return DeveloperInfo(
        name = "Francisco Hidalgo Alcaide",
        role = "Desarrollador Android",
        description = "Desarrollador apasionado por crear aplicaciones móviles innovadoras y funcionales. " +
                "Especializado en Android con Kotlin y Jetpack Compose, siempre buscando las mejores " +
                "prácticas y tecnologías emergentes para ofrecer experiencias de usuario excepcionales.",
        email = "franhidalc@gmail.com",
        github = "https://github.com/pac0455",
        linkedin = "https://www.linkedin.com/in/francisco-hidalgo-alcaide-239054271/",
        website = "",
        version = "1.0.0",
        buildDate = "Junio 2025"
    )
}

@Preview(showBackground = true)
@Composable
fun AboutModalPreview() {
    FrontendappTheme {
        AboutModal(
            isVisible = true,
            onDismiss = {}
        )
    }
}
