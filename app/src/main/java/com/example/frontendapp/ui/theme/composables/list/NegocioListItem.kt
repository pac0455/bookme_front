package com.example.frontendapp.ui.theme.composables.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.modals.ServicioImagePicker
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel

@Composable
fun NegocioListItem(
    negocio: Negocio,
    onEditClick: (Negocio) -> Unit = {},
    onDeleteClick: (Negocio) -> Unit = {},
    onCLickVer: (Negocio) -> Unit = {},
    show: Boolean = false,
    viewModel: NegocioViewModel,
) {
    var expanded by remember { mutableStateOf(show) }
    val logoUrl = viewModel.getNegocioImageUrl(negocio.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Logo o imagen del negocio
                ServicioImagePicker(
                    imageUrl = logoUrl,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    size = 48.dp,
                    shape = CircleShape,
                    clickable = false,
                    iconSize = 24.dp,
                    iconAlignment = Alignment.Center,
                    contentAlignment = Alignment.Center,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Información del negocio
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = negocio.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = negocio.categoria?.nombre!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = negocio.direccion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Botón para expandir/colapsar
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.semantics {
                        contentDescription = if (expanded) "Cerrar opciones" else "Abrir opciones"
                    }
                ) {
                    AnimatedContent(
                        targetState = expanded,
                        transitionSpec = {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut())
                        },
                        label = "ExpandCollapseAnimation"
                    ) { targetExpanded ->
                        Icon(
                            imageVector = if (targetExpanded) Icons.Default.Close else Icons.Default.ArrowDropDown,
                            contentDescription = if (targetExpanded) "Cerrar" else "Más opciones",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Estado del negocio
            val estadoColor = if (negocio.activo)
                ThemeColors.success else ThemeColors.error

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (negocio.activo)
                        ThemeColors.success.copy(alpha = 0.1f)
                    else
                        ThemeColors.error.copy(alpha = 0.1f)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (negocio.activo)
                        ThemeColors.success.copy(alpha = 0.2f)
                    else
                        ThemeColors.error.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (negocio.activo) "Activo" else "Inactivo",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = estadoColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Panel expandible con acciones
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut() + shrinkVertically(
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Botón Editar
                        FilledTonalButton(
                            onClick = { onEditClick(negocio) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar")
                        }

                        // Botón Eliminar
                        FilledTonalButton(
                            onClick = { onDeleteClick(negocio) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar")
                        }

                        // Botón Ver/Configurar
                        FilledTonalButton(
                            onClick = { onCLickVer(negocio) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Configurar")
                        }
                    }
                }
            }
        }
    }
}