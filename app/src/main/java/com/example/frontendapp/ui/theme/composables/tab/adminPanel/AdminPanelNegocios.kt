package com.example.frontendapp.ui.theme.composables.tab.adminPanel

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.Items.NegocioListAdminItem
import com.example.frontendapp.ui.theme.composables.loadPages.TripleOrbitLoadingAnimation
import com.example.frontendapp.ui.theme.composables.modals.*
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.composables.section.IconConfig
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel

private val TAG = "NegociosPanelAdminContent"

@Composable
fun NegociosPanelAdminContent(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    onBack: () -> Unit,
) {
    val isVisible = remember { mutableStateOf(false) }
    var isVisibleLogOut by remember { mutableStateOf(false) }
    val negociosListaState by negocioViewModel.negociosForAdmin.collectAsState()

    // Estados para filtros - ACTUALIZADO para usar FiltrosNegocioAdmin
    var filtrosActivos by remember { mutableStateOf(FiltrosNegocioAdmin()) }
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

    LaunchedEffect(Unit) {
        negocioViewModel.getNegociosForAdmin()
    }

    Column(
        Modifier.systemBarsPadding()
    ) {
        HeaderSeccion(
            titulo = "Bienvenido Administrador",
            searchQuery = busqueda,
            hasActiveFilters = filtrosActivos != FiltrosNegocioAdmin() || busqueda.isNotBlank(),
            onSearchChange = { busqueda = it },
            onFilterClick = { isVisible.value = true },
            iconConfig = IconConfig(
                isVisible = true,
                onClick = onBack,
            )
        )

        when (negociosListaState) {
            is Resource.Success -> {
                val listaNegocios = negociosListaState.data?.data ?: emptyList()

                // Obtener categorías disponibles
                val categoriasDisponibles = remember(listaNegocios) {
                    listaNegocios.map { it.categoria.nombre }.distinct()
                }

                // Aplicar filtros - ACTUALIZADO para NegocioResponseAdminDTO
                val negociosFiltrados = remember(listaNegocios, filtrosActivos, busqueda) {
                    var resultado = listaNegocios

                    // Filtro por búsqueda
                    if (busqueda.isNotBlank()) {
                        resultado = resultado.filter { negocio ->
                            negocio.nombre.contains(busqueda, ignoreCase = true) ||
                                    negocio.descripcion.contains(busqueda, ignoreCase = true) ||
                                    negocio.categoria.nombre.contains(busqueda, ignoreCase = true) ||
                                    negocio.direccion.contains(busqueda, ignoreCase = true)
                        }
                    }

                    // Filtro por categorías
                    if (filtrosActivos.categorias.isNotEmpty()) {
                        resultado = resultado.filter { negocio ->
                            negocio.categoria.nombre in filtrosActivos.categorias
                        }
                    }

                    // Filtro por rating mínimo
                    filtrosActivos.ratingMinimo?.let { minRating ->
                        resultado = resultado.filter { negocio ->
                            negocio.rating >= minRating
                        }
                    }

                    // Filtro por número mínimo de reseñas
                    filtrosActivos.reviewCountMinimo?.let { minReviews ->
                        resultado = resultado.filter { negocio ->
                            negocio.reviewCount >= minReviews
                        }
                    }

                    // Filtro por estado del negocio
                    filtrosActivos.estado?.let { estado ->
                        resultado = when (estado) {
                            EstadoNegocioAdmin.ACTIVOS -> resultado.filter { it.isActive && !it.bloqueado }
                            EstadoNegocioAdmin.INACTIVOS -> resultado.filter { !it.isActive }
                            EstadoNegocioAdmin.BLOQUEADOS -> resultado.filter { it.bloqueado }
                            EstadoNegocioAdmin.NO_BLOQUEADOS -> resultado.filter { !it.bloqueado }
                            EstadoNegocioAdmin.TODOS -> resultado
                        }
                    }

                    // Filtro por estado de operación
                    filtrosActivos.estadoOperacion?.let { operacion ->
                        resultado = when (operacion) {
                            EstadoOperacionNegocio.ABIERTOS -> resultado.filter { it.isOpen }
                            EstadoOperacionNegocio.CERRADOS -> resultado.filter { !it.isOpen }
                            EstadoOperacionNegocio.TODOS -> resultado
                        }
                    }

                    // Ordenamiento
                    when (filtrosActivos.ordenarPor) {
                        OrdenarNegocioPor.NOMBRE -> resultado.sortedBy { it.nombre }
                        OrdenarNegocioPor.RATING -> resultado.sortedByDescending { it.rating }
                        OrdenarNegocioPor.REVIEWS -> resultado.sortedByDescending { it.reviewCount }
                        OrdenarNegocioPor.CATEGORIA -> resultado.sortedBy { it.categoria.nombre }
                    }
                }

                // Mostrar resumen de filtros activos
                val hayFiltrosActivos = filtrosActivos != FiltrosNegocioAdmin() || busqueda.isNotBlank()
                if (hayFiltrosActivos) {
                    FiltrosActivosResumenNegociosAdmin(
                        filtros = filtrosActivos,
                        busqueda = busqueda,
                        totalResultados = negociosFiltrados.size,
                        onLimpiarFiltros = {
                            filtrosActivos = FiltrosNegocioAdmin()
                            busqueda = ""
                        }
                    )
                }

                if (negociosFiltrados.isEmpty()) {
                    if (hayFiltrosActivos) {
                        EmptyFilterResults()
                    } else {
                        EmptyList()
                    }
                } else {
                    LazyColumn {
                        items(negociosFiltrados, key = { it.id }) { negocio ->
                            NegocioListAdminItem(
                                negocio = negocio,
                                viewModel = negocioViewModel,
                                onDelete = {
                                    negocioViewModel.deleteNegocio(
                                        negocio.id,
                                        onSuccess = {
                                            showModal("Éxito", "Negocio eliminado con éxito", ModalType.SUCCESS)
                                            negocioViewModel.getNegociosForAdmin()
                                        },
                                        onError = {
                                            showModal("Error", it, ModalType.ERROR)
                                        }
                                    )
                                },
                                onBloquear = {
                                    negocioViewModel.bloquearNegocio(
                                        negocio.id,
                                        onSuccess = {
                                            showModal("Éxito", "Negocio bloqueado con éxito", ModalType.SUCCESS)
                                            negocioViewModel.getNegociosForAdmin()
                                        },
                                        onError = {
                                            showModal("Error", it, ModalType.ERROR)
                                        }
                                    )
                                },
                                onDesBloquear = {
                                    negocioViewModel.desbloquearNegocio(
                                        negocio.id,
                                        onSuccess = {
                                            showModal("Éxito", "Negocio desbloqueado con éxito", ModalType.SUCCESS)
                                            negocioViewModel.getNegociosForAdmin()
                                        },
                                        onError = {
                                            showModal("Error", it, ModalType.ERROR)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                // Modal de filtros - ACTUALIZADO para usar FiltrosNegocioAdminModal
                FiltrosNegocioAdminModal(
                    isVisible = isVisible.value,
                    filtrosActuales = filtrosActivos,
                    categoriasDisponibles = categoriasDisponibles,
                    onDismiss = { isVisible.value = false },
                    onAplicarFiltros = { nuevosFiltros ->
                        filtrosActivos = nuevosFiltros
                    },
                    onLimpiarFiltros = {
                        filtrosActivos = FiltrosNegocioAdmin()
                        busqueda = ""
                    }
                )
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al cargar los negocios",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { negocioViewModel.getNegociosForAdmin() }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            is Resource.Loading -> {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    TripleOrbitLoadingAnimation()
                }
            }

            is Resource.None -> {}
        }
    }
}

// Componente para mostrar resumen de filtros activos en admin - ACTUALIZADO
@Composable
private fun FiltrosActivosResumenNegociosAdmin(
    filtros: FiltrosNegocioAdmin,
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
                    text = "$totalResultados negocios encontrados",
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
            if (busqueda.isNotBlank() || filtros != FiltrosNegocioAdmin()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (busqueda.isNotBlank()) {
                        item {
                            FiltroChipNegocioAdmin(
                                texto = "\"$busqueda\"",
                                icono = Icons.Default.Search
                            )
                        }
                    }

                    if (filtros.categorias.isNotEmpty()) {
                        items(filtros.categorias.toList()) { categoria ->
                            FiltroChipNegocioAdmin(
                                texto = categoria,
                                icono = Icons.Default.Category
                            )
                        }
                    }

                    filtros.estado?.let { estado ->
                        item {
                            FiltroChipNegocioAdmin(
                                texto = estado.displayName,
                                icono = estado.icon
                            )
                        }
                    }

                    filtros.estadoOperacion?.let { operacion ->
                        item {
                            FiltroChipNegocioAdmin(
                                texto = operacion.displayName,
                                icono = operacion.icon
                            )
                        }
                    }

                    filtros.ratingMinimo?.let { rating ->
                        item {
                            FiltroChipNegocioAdmin(
                                texto = "${rating}+ ⭐",
                                icono = Icons.Default.Star
                            )
                        }
                    }

                    filtros.reviewCountMinimo?.let { reviews ->
                        item {
                            FiltroChipNegocioAdmin(
                                texto = "${reviews}+ reseñas",
                                icono = Icons.Default.Reviews
                            )
                        }
                    }

                    if (filtros.ordenarPor != OrdenarNegocioPor.NOMBRE) {
                        item {
                            FiltroChipNegocioAdmin(
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

// Chip para mostrar filtros activos en admin - ACTUALIZADO
@Composable
private fun FiltroChipNegocioAdmin(
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
fun EmptyList() {
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
                imageVector = Icons.Default.Storefront,
                contentDescription = "Lista vacía",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay negocios registrados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EmptyFilterResults() {
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
                text = "No se encontraron negocios",
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NegociosPanelAdminContentPreview() {
    val navController = rememberNavController()
    FrontendappTheme {
        NegociosPanelAdminContent(
            navController = navController,
            negocioViewModel = FakeNegocioViewModel(),
            onBack = {}
        )
    }
}