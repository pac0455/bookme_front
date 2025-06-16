package com.example.frontendapp.ui.theme.composables.tab.adminPanel

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.composables.Items.UsuarioCardExpandable
import com.example.frontendapp.ui.theme.composables.list.ErrorState
import com.example.frontendapp.ui.theme.composables.list.LoadingState
import com.example.frontendapp.ui.theme.composables.modals.*
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.composables.section.IconConfig
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun UsuariosAdminPanelContent(
    usuarioViewModel: UsuarioViewModel,
    onBack: () -> Unit
) {
    val isVisible = remember { mutableStateOf(false) }
    var isVisibleLogOut by remember { mutableStateOf(false) }
    val usuariosLista by usuarioViewModel.usuariosState.collectAsState()

    // Estados para filtros
    var filtrosActivos by remember { mutableStateOf(FiltrosUsuario()) }
    var busqueda by remember { mutableStateOf("") }

    val modalState = remember { mutableStateOf(ResusableModalDTO()) }

    fun showModal(title: String, msg: String, type: ModalType) {
        modalState.value = ResusableModalDTO(
            title = title,
            msg = msg,
            type = type,
            show = true
        )
    }

    LaunchedEffect(Unit) {
        usuarioViewModel.getAllUsuarios()
    }

    ReusableModal(
        isVisible = modalState.value.show,
        onDismiss = { modalState.value = modalState.value.copy(show = false) },
        onConfirm = { modalState.value = modalState.value.copy(show = false) },
        onCancel = { modalState.value = modalState.value.copy(show = false) },
        config = ModalConfig(
            title = modalState.value.title,
            message = modalState.value.msg,
            type = modalState.value.type,
        )
    )

    Scaffold(
        topBar = {
            HeaderSeccion(
                titulo = "Usuarios",
                searchQuery = busqueda,
                hasActiveFilters = filtrosActivos != FiltrosUsuario() || busqueda.isNotBlank(),
                onSearchChange = { busqueda = it },
                onFilterClick = { isVisible.value = true },
                iconConfig = IconConfig(
                    isVisible = true,
                    onClick = onBack
                ),
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            BackHandler { onBack() }

            when (usuariosLista) {
                is Resource.Success -> {
                    val usuarios = usuariosLista.data ?: emptyList()

                    // Aplicar filtros
                    val usuariosFiltrados = remember(usuarios, filtrosActivos, busqueda) {
                        var resultado = usuarios

                        // Filtro por búsqueda
                        if (busqueda.isNotBlank()) {
                            resultado = resultado.filter { usuario ->
                                usuario.username?.contains(busqueda, ignoreCase = true) == true ||
                                        usuario.email?.contains(busqueda, ignoreCase = true) == true ||
                                        usuario.phoneNumber?.contains(busqueda, ignoreCase = true) == true
                            }
                        }



                        // Filtro por estado
                        filtrosActivos.estado?.let { estado ->
                            resultado = when (estado) {
                                EstadoUsuario.ACTIVOS -> resultado.filter { !it.Bloqueado }
                                EstadoUsuario.BLOQUEADOS -> resultado.filter { it.Bloqueado }
                                EstadoUsuario.TODOS -> resultado
                            }
                        }

                        // Filtro por autentificación
                        if (filtrosActivos.soloAutentificados) {
                            resultado = resultado.filter { it.isAutentificado }
                        }

                        // Filtro por fecha de registro
                        filtrosActivos.fechaRegistro?.let { rango ->
                            val ahora = Date()
                            val fechaLimite = when (rango) {
                                RangoFecha.ULTIMA_SEMANA -> Date(ahora.time - TimeUnit.DAYS.toMillis(7))
                                RangoFecha.ULTIMO_MES -> Date(ahora.time - TimeUnit.DAYS.toMillis(30))
                                RangoFecha.ULTIMOS_3_MESES -> Date(ahora.time - TimeUnit.DAYS.toMillis(90))
                                RangoFecha.ULTIMO_ANO -> Date(ahora.time - TimeUnit.DAYS.toMillis(365))
                                RangoFecha.TODOS -> null
                            }

                            fechaLimite?.let { limite ->
                                resultado = resultado.filter { usuario ->
                                    usuario.fechaRegistro?.after(limite) == true
                                }
                            }
                        }

                        // Ordenamiento
                        when (filtrosActivos.ordenarPor) {
                            OrdenarUsuarioPor.USERNAME -> resultado.sortedBy { it.username }
                            OrdenarUsuarioPor.EMAIL -> resultado.sortedBy { it.email }
                            OrdenarUsuarioPor.FECHA_REGISTRO -> resultado.sortedByDescending { it.fechaRegistro }
                            OrdenarUsuarioPor.TELEFONO -> resultado.sortedBy { it.phoneNumber }
                        }
                    }

                    // Mostrar resumen de filtros activos
                    val hayFiltrosActivos = filtrosActivos != FiltrosUsuario() || busqueda.isNotBlank()
                    if (hayFiltrosActivos) {
                        FiltrosActivosResumenUsuarios(
                            filtros = filtrosActivos,
                            busqueda = busqueda,
                            totalResultados = usuariosFiltrados.size,
                            onLimpiarFiltros = {
                                filtrosActivos = FiltrosUsuario()
                                busqueda = ""
                            }
                        )
                    }

                    if (usuariosFiltrados.isEmpty()) {
                        if (hayFiltrosActivos) {
                            EmptyFilterResultsUsuarios()
                        } else {
                            EmptyListUsuarios()
                        }
                    } else {
                        LazyColumn {
                            items(usuariosFiltrados, key = { it.id!! }) { usuario ->
                                Log.d("usuariosLista", usuario.toString())
                                UsuarioCardExpandable(
                                    usuario = usuario,
                                    onBlockClick = { user ->
                                        usuarioViewModel.bloquearUsuario(
                                            user.id!!,
                                            onSuccess = { response ->
                                                showModal(
                                                    title = "Usuario bloqueado",
                                                    msg = response.message,
                                                    type = ModalType.SUCCESS
                                                )
                                                usuarioViewModel.getAllUsuarios()
                                            },
                                            onError = { errorMsg ->
                                                showModal(
                                                    title = "Error al bloquear",
                                                    msg = errorMsg,
                                                    type = ModalType.ERROR
                                                )
                                            }
                                        )
                                    },
                                    onDeleteClick = { user ->
                                        usuarioViewModel.eliminarUsuario(
                                            user.email!!,
                                            onSuccess = { response ->
                                                showModal(
                                                    title = "Usuario eliminado",
                                                    msg = response.message,
                                                    type = ModalType.SUCCESS
                                                )
                                                usuarioViewModel.getAllUsuarios()
                                            },
                                            onError = { errorMsg ->
                                                showModal(
                                                    title = "Error al eliminar",
                                                    msg = errorMsg,
                                                    type = ModalType.ERROR
                                                )
                                            },
                                        )
                                    },
                                    onBack = { isVisibleLogOut = true },
                                    onDesbloquear = { user ->
                                        usuarioViewModel.desbloquearUsuario(
                                            user.id!!,
                                            onSuccess = { response ->
                                                showModal(
                                                    title = "Usuario desbloqueado",
                                                    msg = response.message,
                                                    type = ModalType.SUCCESS
                                                )
                                                usuarioViewModel.getAllUsuarios()
                                            },
                                            onError = { errorMsg ->
                                                showModal(
                                                    title = "Error al desbloquear",
                                                    msg = errorMsg,
                                                    type = ModalType.ERROR
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Modal de filtros
                    FiltrosUsuarioModal(
                        isVisible = isVisible.value,
                        filtrosActuales = filtrosActivos,
                        onDismiss = { isVisible.value = false },
                        onAplicarFiltros = { nuevosFiltros ->
                            filtrosActivos = nuevosFiltros
                        },
                        onLimpiarFiltros = {
                            filtrosActivos = FiltrosUsuario()
                            busqueda = ""
                        }
                    )
                }

                is Resource.Error -> {
                    ErrorState(
                        message = "Error al cargar la lista de usuarios",
                        onRetry = { usuarioViewModel.getAllUsuarios() }
                    )
                }

                is Resource.Loading -> LoadingState(
                    msg = "Cargando usuarios"
                )

                else -> Unit
            }
        }
    }
}

// Componente para mostrar resumen de filtros activos de usuarios
@Composable
private fun FiltrosActivosResumenUsuarios(
    filtros: FiltrosUsuario,
    busqueda: String,
    totalResultados: Int,
    onLimpiarFiltros: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$totalResultados usuarios encontrados",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = onLimpiarFiltros,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar")
                }
            }

            // Mostrar filtros activos
            if (busqueda.isNotBlank() || filtros != FiltrosUsuario()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (busqueda.isNotBlank()) {
                        item {
                            FiltroChipUsuario(
                                texto = "\"$busqueda\"",
                                icono = Icons.Default.Search
                            )
                        }
                    }

//                    filtros.tipoUsuario?.let { tipo ->
//                        item {
//                            FiltroChipUsuario(
//                                texto = tipo.displayName,
//                                icono = tipo.icon
//                            )
//                        }
//                    }

                    filtros.estado?.let { estado ->
                        item {
                            FiltroChipUsuario(
                                texto = estado.displayName,
                                icono = estado.icon
                            )
                        }
                    }

                    if (filtros.soloAutentificados) {
                        item {
                            FiltroChipUsuario(
                                texto = "Solo autentificados",
                                icono = Icons.Default.Verified
                            )
                        }
                    }

                    filtros.fechaRegistro?.let { fecha ->
                        item {
                            FiltroChipUsuario(
                                texto = fecha.displayName,
                                icono = fecha.icon
                            )
                        }
                    }

                    if (filtros.ordenarPor != OrdenarUsuarioPor.USERNAME) {
                        item {
                            FiltroChipUsuario(
                                texto = "Por ${filtros.ordenarPor.displayName}",
                                icono = filtros.ordenarPor.icon
                            )
                        }
                    }
                }
            }
        }
    }
}

// Chip para mostrar filtros activos de usuarios
@Composable
private fun FiltroChipUsuario(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EmptyListUsuarios() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = "Lista vacía",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay usuarios registrados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EmptyFilterResultsUsuarios() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Sin resultados",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No se encontraron usuarios",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Intenta ajustar los filtros de búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}