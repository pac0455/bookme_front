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

// Data classes para filtros de usuarios actualizadas
data class FiltrosUsuario(
    val estado: EstadoUsuario? = null,
    val fechaRegistro: RangoFecha? = null,
    val ordenarPor: OrdenarUsuarioPor = OrdenarUsuarioPor.USERNAME,
    val soloAutentificados: Boolean = false
)

enum class OrdenarUsuarioPor(val displayName: String, val icon: ImageVector) {
    USERNAME("Nombre de usuario", Icons.Default.Person),
    EMAIL("Email", Icons.Default.Email),
    FECHA_REGISTRO("Fecha de registro", Icons.Default.DateRange),
    TELEFONO("Teléfono", Icons.Default.Phone)
}

enum class EstadoUsuario(val displayName: String, val icon: ImageVector) {
    TODOS("Todos los usuarios", Icons.Default.People),
    ACTIVOS("Solo activos", Icons.Default.CheckCircle),
    BLOQUEADOS("Solo bloqueados", Icons.Default.Block)
}

enum class TipoUsuario(val displayName: String, val icon: ImageVector) {
    TODOS("Todos los tipos", Icons.Default.People),
    NEGOCIOS("Solo negocios", Icons.Default.Business),
    CLIENTES("Solo clientes", Icons.Default.Person)
}

enum class RangoFecha(val displayName: String, val icon: ImageVector) {
    TODOS("Todos los períodos", Icons.Default.DateRange),
    ULTIMA_SEMANA("Última semana", Icons.Default.CalendarToday),
    ULTIMO_MES("Último mes", Icons.Default.CalendarMonth),
    ULTIMOS_3_MESES("Últimos 3 meses", Icons.Default.CalendarViewMonth),
    ULTIMO_ANO("Último año", Icons.Default.CalendarViewWeek)
}

@Composable
fun FiltrosUsuarioModal(
    isVisible: Boolean,
    filtrosActuales: FiltrosUsuario,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosUsuario) -> Unit,
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
            FiltrosUsuarioContent(
                filtrosActuales = filtrosActuales,
                onDismiss = onDismiss,
                onAplicarFiltros = onAplicarFiltros,
                onLimpiarFiltros = onLimpiarFiltros
            )
        }
    }
}

@Composable
private fun FiltrosUsuarioContent(
    filtrosActuales: FiltrosUsuario,
    onDismiss: () -> Unit,
    onAplicarFiltros: (FiltrosUsuario) -> Unit,
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
                    text = "Filtros de Usuarios",
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

                // Estado del usuario (Activo/Bloqueado)
                FiltroSeccionUsuario(
                    titulo = "Estado",
                    icono = Icons.Default.AccountCircle
                ) {
                    EstadoUsuarioFilter(
                        estadoSeleccionado = filtros.estado,
                        onEstadoChange = { estado ->
                            filtros = filtros.copy(estado = estado)
                        }
                    )
                }

                // Fecha de registro
                FiltroSeccionUsuario(
                    titulo = "Fecha de registro",
                    icono = Icons.Default.DateRange
                ) {
                    FechaRegistroFilter(
                        rangoSeleccionado = filtros.fechaRegistro,
                        onRangoChange = { rango ->
                            filtros = filtros.copy(fechaRegistro = rango)
                        }
                    )
                }

                // Solo autentificados
                FiltroSeccionUsuario(
                    titulo = "Autentificación",
                    icono = Icons.Default.Verified
                ) {
                    AutentificacionFilter(
                        soloAutentificados = filtros.soloAutentificados,
                        onSoloAutentificadosChange = { autentificados ->
                            filtros = filtros.copy(soloAutentificados = autentificados)
                        }
                    )
                }

                // Ordenar por
                FiltroSeccionUsuario(
                    titulo = "Ordenar por",
                    icono = Icons.Default.Sort
                ) {
                    OrdenarUsuarioPorFilter(
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
                        filtros = FiltrosUsuario()
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

// Componente para secciones de filtro de usuario
@Composable
private fun FiltroSeccionUsuario(
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

// Filtro de tipo de usuario
@Composable
private fun TipoUsuarioFilter(
    tipoSeleccionado: TipoUsuario?,
    onTipoChange: (TipoUsuario?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TipoUsuario.entries.forEach { opcion ->
            val isSelected = tipoSeleccionado == opcion

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onTipoChange(if (isSelected) null else opcion)
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
                        onTipoChange(if (isSelected) null else opcion)
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

// Filtro de estado de usuario
@Composable
private fun EstadoUsuarioFilter(
    estadoSeleccionado: EstadoUsuario?,
    onEstadoChange: (EstadoUsuario?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoUsuario.entries.forEach { opcion ->
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

// Filtro de fecha de registro
@Composable
private fun FechaRegistroFilter(
    rangoSeleccionado: RangoFecha?,
    onRangoChange: (RangoFecha?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RangoFecha.entries.forEach { opcion ->
            val isSelected = rangoSeleccionado == opcion

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onRangoChange(if (isSelected) null else opcion)
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
                        onRangoChange(if (isSelected) null else opcion)
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

// Filtro de autentificación
@Composable
private fun AutentificacionFilter(
    soloAutentificados: Boolean,
    onSoloAutentificadosChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSoloAutentificadosChange(!soloAutentificados) }
            .background(
                if (soloAutentificados)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = soloAutentificados,
            onCheckedChange = onSoloAutentificadosChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Solo usuarios autentificados",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (soloAutentificados)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Mostrar únicamente usuarios con email confirmado",
                style = MaterialTheme.typography.bodySmall,
                color = if (soloAutentificados)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Filtro de ordenamiento de usuarios
@Composable
private fun OrdenarUsuarioPorFilter(
    ordenSeleccionado: OrdenarUsuarioPor,
    onOrdenChange: (OrdenarUsuarioPor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OrdenarUsuarioPor.entries.forEach { opcion ->
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
fun FiltrosUsuarioModalPreview() {
    FrontendappTheme {
        FiltrosUsuarioModal(
            isVisible = true,
            filtrosActuales = FiltrosUsuario(
                estado = EstadoUsuario.ACTIVOS,
                soloAutentificados = true
            ),
            onDismiss = {},
            onAplicarFiltros = {},
            onLimpiarFiltros = {}
        )
    }
}