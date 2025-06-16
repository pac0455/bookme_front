package com.example.frontendapp.ui.theme.composables.Items

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.UI.EstadoNegocio
import com.example.frontendapp.data.model.UI.toEstadoNegocioUI
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.Btn.ActionButton
import com.example.frontendapp.ui.theme.composables.modals.ServicioImagePicker
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel

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
    val estadoNegocio = remember(negocio.activo, negocio.bloqueado) {
        when {
            negocio.bloqueado -> EstadoNegocio.BLOQUEADO
            negocio.activo -> EstadoNegocio.ACTIVO
            !negocio.activo -> EstadoNegocio.INACTIVO
            else -> EstadoNegocio.SIN_ESPECICAR
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Cabecera principal del negocio
            NegocionHeader(
                negocio = negocio,
                logoUrl = logoUrl ?: "",
                expanded = expanded,
                onExpandToggle = {
                    //Si esta bloqueado que no le deje mostrar opciones
                    expanded = if(estadoNegocio == EstadoNegocio.BLOQUEADO) false else !expanded }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estado del negocio
            NegocioStatusSection(
                negocio = negocio,
                estadoNegocio = estadoNegocio
            )

            // Panel expandible con acciones
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(300)) +
                        expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) +
                        shrinkVertically(animationSpec = tween(300))
            ) {
                NegocioActionsSection(
                    negocio = negocio,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onViewClick = onCLickVer
                )
            }
        }
    }
}

@Composable
private fun NegocionHeader(
    negocio: Negocio,
    logoUrl: String,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Logo del negocio
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(70.dp)
        ) {
            ServicioImagePicker(
                imageUrl = logoUrl,
                imageUpdatedAt = negocio.logoUpdatedAt,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                size = 48.dp,
                shape = CircleShape,
                clickable = false,
                iconSize = 28.dp,
                iconAlignment = Alignment.Center,
                contentAlignment = Alignment.Center,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Información del negocio
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = negocio.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chip de categoría
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = negocio.categoria?.nombre ?: "Sin categoría",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dirección
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación del negocio",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = negocio.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Botón expandir/colapsar
        IconButton(
            onClick = onExpandToggle,
            modifier = Modifier.semantics {
                contentDescription = if (expanded) "Ocultar opciones del negocio" else "Mostrar opciones del negocio"
            }
        ) {
            AnimatedContent(
                targetState = expanded,
                transitionSpec = {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut())
                },
                label = "ExpandCollapseAnimation"
            ) { targetExpanded ->
                Icon(
                    imageVector = if (targetExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (targetExpanded) "Contraer opciones" else "Expandir opciones",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun NegocioStatusSection(
    negocio: Negocio,
    estadoNegocio: EstadoNegocio
) {


    val estadoUI = estadoNegocio.toEstadoNegocioUI()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = estadoUI.backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = estadoUI.icon,
                    contentDescription = "Estado del negocio: ${estadoUI.label.lowercase()}",
                    modifier = Modifier.size(20.dp),
                    tint = estadoUI.color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Estado del negocio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = estadoUI.color.copy(alpha = 0.2f)
            ) {
                Text(
                    text = estadoUI.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = estadoUI.color,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun NegocioActionsSection(
    negocio: Negocio,
    onEditClick: (Negocio) -> Unit,
    onDeleteClick: (Negocio) -> Unit,
    onViewClick: (Negocio) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Acciones disponibles",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón Editar
            ActionButton(
                icon = Icons.Default.Edit,
                text = "Editar",
                contentDescription = "Editar información del negocio ${negocio.nombre}",
                onClick = { onEditClick(negocio) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )

            // Botón Eliminar
            ActionButton(
                icon = Icons.Default.Delete,
                text = "Eliminar",
                contentDescription = "Eliminar negocio ${negocio.nombre}",
                onClick = { onDeleteClick(negocio) },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )

            // Botón Configurar
            ActionButton(
                icon = Icons.Default.Settings,
                text = "Configurar",
                contentDescription = "Configurar negocio ${negocio.nombre}",
                onClick = { onViewClick(negocio) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegocioListItemPreview() {
    FrontendappTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Negocio activo
            val negocioActivo = Negocio(
                nombre = "Gimnasio FitLife",
                descripcion = "Gimnasio completo con equipos modernos",
                direccion = "Av. Principal 123, Centro",
                latitud = 40.0,
                longitud = -3.0,
                categoria = Categoria(
                    id = 1,
                    nombre = "Gimnasio"
                ),
                id = 1,
            )
            val negocioBloqueado = Negocio(
                nombre = "Gimnasio FitLife",
                descripcion = "Gimnasio completo con equipos modernos",
                direccion = "Av. Principal 123, Centro",
                latitud = 40.0,
                longitud = -3.0,
                categoria = Categoria(
                    id = 1,
                    nombre = "Gimnasio"
                ),
                id = 1,
                bloqueado = true
            )


            NegocioListItem(
                negocio = negocioActivo,
                show = false,
                viewModel = FakeNegocioViewModel()
            )

            // Negocio inactivo expandido
            val negocioInactivo = Negocio(
                nombre = "Spa Relajación Total",
                descripcion = "Centro de relajación y bienestar",
                direccion = "Calle Tranquila 456, Zona Norte",
                latitud = 40.1,
                longitud = -3.1,
                categoriaId = 2,
                categoria = Categoria(nombre = "Spa"),
                activo = false
            )

            NegocioListItem(
                negocio = negocioInactivo,
                show = true,
                viewModel = FakeNegocioViewModel()
            )
            NegocioListItem(
                negocio = negocioBloqueado,
                show = false,
                viewModel = FakeNegocioViewModel()
            )
        }
    }
}