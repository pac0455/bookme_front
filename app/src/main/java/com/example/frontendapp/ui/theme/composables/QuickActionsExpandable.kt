package com.example.frontendapp.ui.theme.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.Btn.QuickActionButton
import com.example.frontendapp.ui.theme.screens.ContentType



@Composable
fun QuickActionsExpandable(
    selectedContent: ContentType?,
    onContentSelected: (ContentType) -> Unit,
    expanded: Boolean = false,
    onExpandedChange: ((Boolean) -> Unit)? = null
) {
    var expandedState by remember { mutableStateOf(expanded) }

    // Sincronizamos el estado interno con el externo
    LaunchedEffect(expanded) {
        expandedState = expanded
    }

    val allActions = listOf(
        Triple(ContentType.RESERVAS, Icons.Default.Event, "Reservas"),             // Calendario/Evento
        Triple(ContentType.SERVICIOS, Icons.Default.Build, "Servicios"),           // Herramientas/Servicios
        Triple(ContentType.SUBSCRIPTOR, Icons.Default.People, "Reservas por semana"),  // Personas/Grupo para suscriptores
        Triple(ContentType.GALLERIA, Icons.Default.StarRate, "Valoraciones"),      // Estrella para valoraciones
    )


    // Cálculo más preciso de alturas
    val collapsedHeight = 220.dp
    val expandedHeight = run {
        val rows = kotlin.math.ceil(allActions.size / 3.0).toInt()
        // Ajustamos las medidas para que coincidan mejor con el QuickActionButton real
        val buttonHeightDp = 120.dp // 80dp botón + 40dp texto aproximadamente
        val spacingDp = 16.dp
        val paddingDp = 120.dp // Más espacio para el botón "Ver más"
        val buttonsHeight = buttonHeightDp * rows + spacingDp * (rows - 1)
        buttonsHeight + paddingDp
    }

    // Animación spring para la altura
    val animatedHeight by animateDpAsState(
        targetValue = if (expandedState) expandedHeight else collapsedHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = 1.dp
        ),
        label = "ContainerHeightAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .offset(y = (-32).dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(24.dp)
            )
            .height(animatedHeight)
            .clipToBounds(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Grid unificado que contiene todos los botones
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(allActions) { (contentType, icon, label) ->
                QuickActionButton(
                    onClick = { onContentSelected(contentType) },
                    icon = icon,
                    isSelected = contentType == selectedContent,
                    label = label
                )
            }
        }

        // Botón Ver más / Ver menos
        TextButton(
            onClick = {
                val newState = !expandedState
                expandedState = newState
                onExpandedChange?.invoke(newState)
            },
            modifier = Modifier.padding(bottom = 16.dp) // Más padding para mejor espaciado
        ) {
            Text(
                text = if (expandedState) "Ver menos" else "Ver más",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// Enum para el preview (si no existe en tu proyecto)
enum class ContentType {
    RESERVAS, SERVICIOS, SUBSCRIPTOR, GALLERIA
}

@Preview(showBackground = true, heightDp = 700) // Aumentamos la altura del preview
@Composable
fun PreviewQuickActionsExpandable() {
    FrontendappTheme {
        var selectedContent by remember { mutableStateOf<ContentType?>(ContentType.SERVICIOS) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Text(
                    text = "Panel de Control",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                QuickActionsExpandable(
                    selectedContent = selectedContent,
                    onContentSelected = { selectedContent = it },
                    expanded = false
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Sección seleccionada: ${selectedContent?.name ?: "Ninguna"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 700, name = "Modo Expandido")
@Composable
fun PreviewQuickActionsExpandableExpanded() {
    FrontendappTheme {
        var selectedContent by remember { mutableStateOf<ContentType?>(ContentType.RESERVAS) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Text(
                    text = "Panel de Control - Expandido",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                QuickActionsExpandable(
                    selectedContent = selectedContent,
                    onContentSelected = { selectedContent = it },
                    expanded = true
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 700, name = "Interactivo")
@Composable
fun PreviewQuickActionsExpandableInteractive() {
    FrontendappTheme {
        var selectedContent by remember { mutableStateOf<ContentType?>(ContentType.SERVICIOS) }
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Text(
                    text = "Panel de Control - Interactivo",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                QuickActionsExpandable(
                    selectedContent = selectedContent,
                    onContentSelected = { selectedContent = it },
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Estado: ${if (expanded) "Expandido" else "Contraído"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Sección: ${selectedContent?.name ?: "Ninguna"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}