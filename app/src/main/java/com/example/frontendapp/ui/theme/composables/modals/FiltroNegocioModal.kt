package com.example.frontendapp.ui.theme.composables.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.frontendapp.ui.theme.*

//  NUEVO: Data classes para filtros
data class FiltrosNegocio(
    val categorias: Set<String> = emptySet(),
    val distanciaMaxima: Float? = null,
    val ratingMinimo: Float? = null,
    val soloAbiertos: Boolean = false,
    val ordenarPor: OrdenarPor = OrdenarPor.RELEVANCIA,
    val precioMaximo: Float? = null
)

enum class OrdenarPor(val displayName: String, val icon: ImageVector) {
    RELEVANCIA("Relevancia", Icons.Default.Star),
    DISTANCIA("Distancia", Icons.Default.LocationOn),
    RATING("Valoración", Icons.Default.ThumbUp),
    PRECIO("Precio", Icons.Default.AttachMoney),
    NOMBRE("Nombre", Icons.Default.SortByAlpha)
}

enum class DistanciaOption(val displayName: String, val value: Float?) {
    TODAS("Todas las distancias", null),
    CERCA("Menos de 1 km", 1f),
    MEDIO("Menos de 5 km", 5f),
    LEJOS("Menos de 10 km", 10f)
}

enum class RatingOption(val displayName: String, val value: Float?) {
    TODAS("Todas las valoraciones", null),
    BUENO("4+ estrellas", 4f),
    MUY_BUENO("4.5+ estrellas", 4.5f),
    EXCELENTE("5 estrellas", 5f)
}

@Composable
fun FiltrosNegocioModal(
    isVisible: Boolean,
    filtrosActuales: FiltrosNegocio,
    categoriasDisponibles: List<String>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosNegocio) -> Unit,
    onLimpiarFiltros: () -> Unit
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
            FiltrosContent(
                filtrosActuales = filtrosActuales,
                categoriasDisponibles = categoriasDisponibles,
                onDismiss = onDismiss,
                onAplicarFiltros = onAplicarFiltros,
                onLimpiarFiltros = onLimpiarFiltros
            )
        }
    }
}

@Composable
private fun FiltrosContent(
    filtrosActuales: FiltrosNegocio,
    categoriasDisponibles: List<String>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosNegocio) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    var filtros by remember { mutableStateOf(filtrosActuales) }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            //  Header del modal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //  Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Categorías
                FiltroSeccion(
                    titulo = "Categorías",
                    icono = Icons.Default.Category
                ) {
                    CategoriasFilter(
                        categoriasDisponibles = categoriasDisponibles,
                        categoriasSeleccionadas = filtros.categorias,
                        onCategoriaToggle = { categoria ->
                            filtros = if (categoria in filtros.categorias) {
                                filtros.copy(categorias = filtros.categorias - categoria)
                            } else {
                                filtros.copy(categorias = filtros.categorias + categoria)
                            }
                        }
                    )
                }

                // Distancia
                FiltroSeccion(
                    titulo = "Distancia",
                    icono = Icons.Default.LocationOn
                ) {
                    DistanciaFilter(
                        distanciaSeleccionada = filtros.distanciaMaxima,
                        onDistanciaChange = { distancia ->
                            filtros = filtros.copy(distanciaMaxima = distancia)
                        }
                    )
                }

                // Rating
                FiltroSeccion(
                    titulo = "Valoración mínima",
                    icono = Icons.Default.Star
                ) {
                    RatingFilter(
                        ratingSeleccionado = filtros.ratingMinimo,
                        onRatingChange = { rating ->
                            filtros = filtros.copy(ratingMinimo = rating)
                        }
                    )
                }

                // Estado (Abierto/Cerrado)
                FiltroSeccion(
                    titulo = "Estado",
                    icono = Icons.Default.Schedule
                ) {
                    EstadoFilter(
                        soloAbiertos = filtros.soloAbiertos,
                        onSoloAbiertosChange = { soloAbiertos ->
                            filtros = filtros.copy(soloAbiertos = soloAbiertos)
                        }
                    )
                }

                // Ordenar por
                FiltroSeccion(
                    titulo = "Ordenar por",
                    icono = Icons.Default.Sort
                ) {
                    OrdenarPorFilter(
                        ordenSeleccionado = filtros.ordenarPor,
                        onOrdenChange = { orden ->
                            filtros = filtros.copy(ordenarPor = orden)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //  Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onLimpiarFiltros()
                        filtros = FiltrosNegocio()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Limpiar")
                }

                Button(
                    onClick = {
                        onAplicarFiltros(filtros)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aplicar")
                }
            }
        }
    }
}

//  Componente para secciones de filtro
@Composable
private fun FiltroSeccion(
    titulo: String,
    icono: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

//  Filtro de categorías
@Composable
private fun CategoriasFilter(
    categoriasDisponibles: List<String>,
    categoriasSeleccionadas: Set<String>,
    onCategoriaToggle: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categoriasDisponibles) { categoria ->
            val isSelected = categoria in categoriasSeleccionadas

            FilterChip(
                onClick = { onCategoriaToggle(categoria) },
                label = { Text(categoria) },
                selected = isSelected,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

//  Filtro de distancia
@Composable
private fun DistanciaFilter(
    distanciaSeleccionada: Float?,
    onDistanciaChange: (Float?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DistanciaOption.values().forEach { opcion ->
            val isSelected = distanciaSeleccionada == opcion.value

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDistanciaChange(opcion.value) }
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            Color.Transparent
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onDistanciaChange(opcion.value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = opcion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

//  Filtro de rating
@Composable
private fun RatingFilter(
    ratingSeleccionado: Float?,
    onRatingChange: (Float?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RatingOption.values().forEach { opcion ->
            val isSelected = ratingSeleccionado == opcion.value

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onRatingChange(opcion.value) }
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            Color.Transparent
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onRatingChange(opcion.value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = opcion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                if (opcion.value != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < (opcion.value ?: 0f))
                                ThemeColors.warning
                            else
                                MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

//  Filtro de estado
@Composable
private fun EstadoFilter(
    soloAbiertos: Boolean,
    onSoloAbiertosChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSoloAbiertosChange(!soloAbiertos) }
            .background(
                if (soloAbiertos)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = soloAbiertos,
            onCheckedChange = onSoloAbiertosChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Solo negocios abiertos",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (soloAbiertos)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Mostrar únicamente los que están abiertos ahora",
                style = MaterialTheme.typography.bodySmall,
                color = if (soloAbiertos)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

//  Filtro de ordenamiento
@Composable
private fun OrdenarPorFilter(
    ordenSeleccionado: OrdenarPor,
    onOrdenChange: (OrdenarPor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OrdenarPor.values().forEach { opcion ->
            val isSelected = ordenSeleccionado == opcion

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOrdenChange(opcion) }
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            Color.Transparent
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onOrdenChange(opcion) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = opcion.icon,
                    contentDescription = null,
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = opcion.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FiltrosNegocioModalPreview() {
    FrontendappTheme {
        FiltrosNegocioModal(
            isVisible = true,
            filtrosActuales = FiltrosNegocio(
                categorias = setOf("Restaurante", "Belleza"),
                soloAbiertos = true,
                ratingMinimo = 4f
            ),
            categoriasDisponibles = listOf("Restaurante", "Belleza", "Salud", "Tecnología", "Educación"),
            onDismiss = {},
            onAplicarFiltros = {},
            onLimpiarFiltros = {}
        )
    }
}
