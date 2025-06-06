package com.example.frontendapp.ui.theme.composables.section

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.*
import com.example.frontendapp.ui.theme.composables.CustomSeachBar

@Composable
fun HeaderSeccion(
    titulo: String = "Servicios",
    modifier: Modifier = Modifier,
    onSearchChange: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    hasActiveFilters: Boolean = false,
    searchQuery: String = ""
) {
    var query by remember { mutableStateOf(searchQuery) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val headerHeight = screenHeight * 0.20f

    //  Actualizar query cuando cambie searchQuery externo
    LaunchedEffect(searchQuery) {
        query = searchQuery

    }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Encabezado visual con colores del tema
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .clip(RoundedCornerShape(bottomEnd = 40.dp, bottomStart = 40.dp))
                .background(
                    MaterialTheme.colorScheme.primary
                )
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // ✅ NUEVO: Indicador de filtros activos
            if (hasActiveFilters) {
                Surface(
                    color = ThemeColors.warning,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = "Filtros activos",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // ✅ MEJORADO: Barra de búsqueda con colores del tema
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(68.dp)
                .offset(y = (-32).dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomSeachBar(
                query = query,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                onQueryChange = {
                    query = it
                    onSearchChange(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            // Botón de filtro con animación
            FilterButton(
                onClick = onFilterClick,
                hasActiveFilters = hasActiveFilters
            )
        }
    }
}

//  Componente de botón de filtro animado
@Composable
private fun FilterButton(
    onClick: () -> Unit,
    hasActiveFilters: Boolean
) {
    val animatedColor by animateColorAsState(
        targetValue = if (hasActiveFilters)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "filter_button_color"
    )

    val animatedIconColor by animateColorAsState(
        targetValue = if (hasActiveFilters)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "filter_icon_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (hasActiveFilters) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "filter_button_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(
                color = animatedColor,
                shape = RoundedCornerShape(16.dp)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Icon(
            imageVector = if (hasActiveFilters) Icons.Default.FilterAltOff else Icons.Default.FilterAlt,
            contentDescription = if (hasActiveFilters) "Limpiar filtros" else "Aplicar filtros",
            tint = animatedIconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeaderSeccion() {
    FrontendappTheme {
        Column {
            HeaderSeccion(
                titulo = "Negocios",
                hasActiveFilters = false,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HeaderSeccion(
                titulo = "Servicios",
                hasActiveFilters = true,
            )
        }
    }
}
