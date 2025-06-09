package com.example.frontendapp.ui.theme.composables.Items

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.Negocio.NegocioResponseAdminDTO
import com.example.frontendapp.data.model.UI.EstadoNegocio
import com.example.frontendapp.data.model.UI.toEstadoNegocioUI
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.Btn.ActionButton
import com.example.frontendapp.ui.theme.composables.modals.ServicioImagePicker
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel

@Composable
fun NegocioListAdminItem(
    negocio: NegocioResponseAdminDTO,
    onDelete: (Int) -> Unit = {},
    onBloquear: (Int) -> Unit = {},
    onDesBloquear: (Int) -> Unit = {},
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
            NegocioAdminHeader(
                negocio = negocio,
                logoUrl = logoUrl ?: "",
                expanded = expanded,
                onExpandToggle = { expanded = !expanded }
            )

            Spacer(modifier = Modifier.height(16.dp))
            NegocioAdminStatusSection(
                negocio = negocio,
            )


            // Panel expandible con acciones
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(300)) +
                        expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) +
                        shrinkVertically(animationSpec = tween(300))
            ) {
                // Estado del negocio
                NegocioAdminActionsSection(
                    negocio = negocio,
                    onBloquear = onBloquear,
                    onDeleteClick = onDelete,
                    onDesBloquear = onDesBloquear
                )
            }
        }
    }
}

@Composable
private fun NegocioAdminHeader(
    negocio: NegocioResponseAdminDTO,
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
private fun NegocioAdminStatusSection(negocio: NegocioResponseAdminDTO) {
    val estadoNegocio: EstadoNegocio = remember(negocio.bloqueado, negocio.isActive) {
        when {
            negocio.bloqueado -> EstadoNegocio.BLOQUEADO
            negocio.isActive -> EstadoNegocio.ACTIVO
            !negocio.isActive -> EstadoNegocio.INACTIVO
            else -> EstadoNegocio.SIN_ESPECICAR
        }
    }

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
                    contentDescription = "Estado del negocio: ${estadoUI.label}",
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
private fun NegocioAdminActionsSection(
    negocio: NegocioResponseAdminDTO,
    onDeleteClick: (Int) -> Unit,
    onBloquear: (Int) -> Unit,
    onDesBloquear: (Int) -> Unit
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
            // Botón Eliminar
            ActionButton(
                icon = Icons.Default.Delete,
                text = "Eliminar",
                contentDescription = "Eliminar negocio ${negocio.nombre}",
                onClick = { onDeleteClick(negocio.id) },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )

            // Botón bloquear/desbloquear
            if(!negocio.bloqueado){
                ActionButton(
                    icon =   Icons.Default.Lock,
                    text = "Bloquear",
                    contentDescription = "Bloquear negocio ${negocio.nombre}",
                    onClick = { onBloquear(negocio.id) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }else{
                ActionButton(
                    icon =   Icons.Default.LockOpen,
                    text = "Desbloquear",
                    contentDescription = "Desbloquear negocio ${negocio.nombre}",
                    onClick = { onDesBloquear(negocio.id) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}



@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview
fun NegocioListAdminItemPreview() {
    FrontendappTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Negocio activo
            val negocioActivo = NegocioResponseAdminDTO(
                id = 1,
                nombre = "Gimnasio FitLife",
                descripcion = "Gimnasio completo con equipos modernos",
                direccion = "Av. Principal 123, Centro",
                latitud = 40.0,
                longitud = -3.0,
                categoria = Categoria(id = 10, nombre = "Gimnasio"),
                rating = 4.5f,
                reviewCount = 120,
                isActive = true,
                isOpen = true,
                bloqueado = false
            )

            // Negocio inactivo expandido
            val negocioInactivo = NegocioResponseAdminDTO(
                id = 2,
                nombre = "Spa Relajación Total",
                descripcion = "Centro de relajación y bienestar",
                direccion = "Calle Tranquila 456, Zona Norte",
                latitud = 40.1,
                longitud = -3.1,
                categoria = Categoria(id = 20, nombre = "Spa"),
                rating = 4.8f,
                reviewCount = 80,
                isActive = false,
                isOpen = true,
                bloqueado = true
            )

            LazyColumn {
                items(listOf(negocioActivo, negocioInactivo)) { negocio ->
                    NegocioListAdminItem(
                        negocio = negocio,
                        onBloquear = {},
                        viewModel = FakeNegocioViewModel(),
                        show = true,
                        onDelete = {}
                    )
                }
            }
        }
    }
}
