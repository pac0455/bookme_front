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

// Data classes para filtros de negocios admin específicos
data class FiltrosNegocioAdmin(
    val categorias: Set<String> = emptySet(),
    val ratingMinimo: Float? = null,
    val reviewCountMinimo: Int? = null,
    val estado: EstadoNegocioAdmin? = null,
    val estadoOperacion: EstadoOperacionNegocio? = null,
    val ordenarPor: OrdenarNegocioPor = OrdenarNegocioPor.NOMBRE
)

enum class OrdenarNegocioPor(val displayName: String, val icon: ImageVector) {
    NOMBRE("Nombre", Icons.Default.SortByAlpha),
    RATING("Valoración", Icons.Default.Star),
    REVIEWS("Número de reseñas", Icons.Default.Reviews),
    CATEGORIA("Categoría", Icons.Default.Category)
}

enum class EstadoNegocioAdmin(val displayName: String, val icon: ImageVector) {
    TODOS("Todos los estados", Icons.Default.Business),
    ACTIVOS("Solo activos", Icons.Default.CheckCircle),
    INACTIVOS("Solo inactivos", Icons.Default.Cancel),
    BLOQUEADOS("Solo bloqueados", Icons.Default.Block),
    NO_BLOQUEADOS("Solo no bloqueados", Icons.Default.CheckCircleOutline)
}

enum class EstadoOperacionNegocio(val displayName: String, val icon: ImageVector) {
    TODOS("Todos", Icons.Default.Schedule),
    ABIERTOS("Solo abiertos", Icons.Default.LockOpen),
    CERRADOS("Solo cerrados", Icons.Default.Lock)
}

enum class RangoRating(val displayName: String, val value: Float?) {
    TODOS("Todas las valoraciones", null),
    BUENO("3+ estrellas", 3f),
    MUY_BUENO("4+ estrellas", 4f),
    EXCELENTE("4.5+ estrellas", 4.5f),
    PERFECTO("5 estrellas", 5f)
}

enum class RangoReviews(val displayName: String, val value: Int?) {
    TODOS("Todas las cantidades", null),
    MINIMO_5("5+ reseñas", 5),
    MINIMO_10("10+ reseñas", 10),
    MINIMO_25("25+ reseñas", 25),
    MINIMO_50("50+ reseñas", 50),
    MINIMO_100("100+ reseñas", 100)
}

@Composable
fun FiltrosNegocioAdminModal(
    isVisible: Boolean,
    filtrosActuales: FiltrosNegocioAdmin,
    categoriasDisponibles: List<String>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosNegocioAdmin) -> Unit,
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
            FiltrosNegocioAdminContent(
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
private fun FiltrosNegocioAdminContent(
    filtrosActuales: FiltrosNegocioAdmin,
    categoriasDisponibles: List<String>,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosNegocioAdmin) -> Unit,
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
            // Header del modal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros de Negocios",
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

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Categorías
                if (categoriasDisponibles.isNotEmpty()) {
                    FiltroSeccionNegocioAdmin(
                        titulo = "Categorías",
                        icono = Icons.Default.Category
                    ) {
                        CategoriasNegocioFilter(
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
                }

                // Estado del negocio (activo/inactivo/bloqueado)
                FiltroSeccionNegocioAdmin(
                    titulo = "Estado del negocio",
                    icono = Icons.Default.Business
                ) {
                    EstadoNegocioAdminFilter(
                        estadoSeleccionado = filtros.estado,
                        onEstadoChange = { estado ->
                            filtros = filtros.copy(estado = estado)
                        }
                    )
                }

                // Estado de operación (abierto/cerrado)
                FiltroSeccionNegocioAdmin(
                    titulo = "Estado de operación",
                    icono = Icons.Default.Schedule
                ) {
                    EstadoOperacionFilter(
                        estadoSeleccionado = filtros.estadoOperacion,
                        onEstadoChange = { estado ->
                            filtros = filtros.copy(estadoOperacion = estado)
                        }
                    )
                }

                // Rating mínimo
                FiltroSeccionNegocioAdmin(
                    titulo = "Valoración mínima",
                    icono = Icons.Default.Star
                ) {
                    RatingNegocioFilter(
                        ratingSeleccionado = filtros.ratingMinimo,
                        onRatingChange = { rating ->
                            filtros = filtros.copy(ratingMinimo = rating)
                        }
                    )
                }

                // Número mínimo de reseñas
                FiltroSeccionNegocioAdmin(
                    titulo = "Número mínimo de reseñas",
                    icono = Icons.Default.Reviews
                ) {
                    ReviewCountFilter(
                        reviewCountSeleccionado = filtros.reviewCountMinimo,
                        onReviewCountChange = { count ->
                            filtros = filtros.copy(reviewCountMinimo = count)
                        }
                    )
                }

                // Ordenar por
                FiltroSeccionNegocioAdmin(
                    titulo = "Ordenar por",
                    icono = Icons.Default.Sort
                ) {
                    OrdenarNegocioPorFilter(
                        ordenSeleccionado = filtros.ordenarPor,
                        onOrdenChange = { orden ->
                            filtros = filtros.copy(ordenarPor = orden)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onLimpiarFiltros()
                        filtros = FiltrosNegocioAdmin()
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

// Componente para secciones de filtro de negocio admin
@Composable
private fun FiltroSeccionNegocioAdmin(
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

// Filtro de categorías de negocio
@Composable
private fun CategoriasNegocioFilter(
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

// Filtro de estado de negocio admin
@Composable
private fun EstadoNegocioAdminFilter(
    estadoSeleccionado: EstadoNegocioAdmin?,
    onEstadoChange: (EstadoNegocioAdmin?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoNegocioAdmin.entries.forEach { opcion ->
            val isSelected = estadoSeleccionado == opcion

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onEstadoChange(if (isSelected) null else opcion)
                    }
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
                    onClick = {
                        onEstadoChange(if (isSelected) null else opcion)
                    },
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

// Filtro de estado de operación
@Composable
private fun EstadoOperacionFilter(
    estadoSeleccionado: EstadoOperacionNegocio?,
    onEstadoChange: (EstadoOperacionNegocio?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoOperacionNegocio.entries.forEach { opcion ->
            val isSelected = estadoSeleccionado == opcion

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onEstadoChange(if (isSelected) null else opcion)
                    }
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
                    onClick = {
                        onEstadoChange(if (isSelected) null else opcion)
                    },
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

// Filtro de rating de negocio
@Composable
private fun RatingNegocioFilter(
    ratingSeleccionado: Float?,
    onRatingChange: (Float?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RangoRating.entries.forEach { opcion ->
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

// Filtro de número de reseñas
@Composable
private fun ReviewCountFilter(
    reviewCountSeleccionado: Int?,
    onReviewCountChange: (Int?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RangoReviews.entries.forEach { opcion ->
            val isSelected = reviewCountSeleccionado == opcion.value

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onReviewCountChange(opcion.value) }
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
                    onClick = { onReviewCountChange(opcion.value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Reviews,
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

// Filtro de ordenamiento de negocios
@Composable
private fun OrdenarNegocioPorFilter(
    ordenSeleccionado: OrdenarNegocioPor,
    onOrdenChange: (OrdenarNegocioPor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OrdenarNegocioPor.entries.forEach { opcion ->
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
fun FiltrosNegocioAdminModalPreview() {
    FrontendappTheme {
        FiltrosNegocioAdminModal(
            isVisible = true,
            filtrosActuales = FiltrosNegocioAdmin(
                categorias = setOf("Restaurante", "Belleza"),
                estado = EstadoNegocioAdmin.ACTIVOS,
                ratingMinimo = 4f
            ),
            categoriasDisponibles = listOf("Restaurante", "Belleza", "Salud", "Tecnología", "Educación"),
            onDismiss = {},
            onAplicarFiltros = {},
            onLimpiarFiltros = {}
        )
    }
}