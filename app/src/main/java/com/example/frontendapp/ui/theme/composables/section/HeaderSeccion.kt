package com.example.frontendapp.ui.theme.composables.section

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.*
import com.example.frontendapp.ui.theme.composables.CustomSeachBar


data class IconConfig(
    val isVisible: Boolean = false,
    val onClick: () -> Unit= {},
    val icon: ImageVector=Icons.Default.ArrowBackIosNew,
    val iconSize:Dp = 24.dp
)
@Composable
fun HeaderSeccion(
    titulo: String = "Servicios",
    modifier: Modifier = Modifier,
    onSearchChange: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    hasActiveFilters: Boolean = false,
    searchQuery: String = "",
    iconConfig: IconConfig = IconConfig()
) {
    var query by remember { mutableStateOf(searchQuery) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val headerHeight = screenHeight * 0.20f

    LaunchedEffect(searchQuery) {
        query = searchQuery
    }
    //Caja genral(sin color)
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ICONO alineado a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (iconConfig.isVisible) {
                    IconButton(
                        onClick = iconConfig.onClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.onSecondary)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = iconConfig.icon,
                            contentDescription = "Icono de navegación",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(iconConfig.iconSize)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            // TÍTULO + FILTROS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                if (hasActiveFilters) {
                    Surface(
                        color = ThemeColors.warning,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(start = 8.dp)
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
        }

        // BARRA DE BÚSQUEDA superpuesta (sin Box, solo offset negativo)
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(68.dp)
                .offset(y = (-34).dp) // superposición con el header
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
                iconConfig = IconConfig(
                    isVisible = true,
                    icon = Icons.AutoMirrored.Filled.Logout
                )
            )
        }
    }
}
