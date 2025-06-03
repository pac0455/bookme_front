package com.example.frontendapp.ui.theme.composables.list

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.Items.NegocioListItem
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel

@Composable
fun NegocioList(
    navController: NavController,
    bussinesMainViewModel: NegocioViewModel
) {
    val negociosState by bussinesMainViewModel.negociosByUserIdState.collectAsState()
    val deleteState by bussinesMainViewModel.deleteNegocioState.collectAsState()

    // Cargar negocios al entrar por primera vez
    LaunchedEffect(Unit) {
        bussinesMainViewModel.getNegociosByUserId()
    }

    // Muestra resultado de eliminación
    LaunchedEffect(deleteState) {
        if (deleteState is Resource.Success) {
            bussinesMainViewModel.getNegociosByUserId()
        }
    }

    AnimatedContent(
        targetState = negociosState,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "NegocioListStateAnimation"
    ) { state ->
        when (state) {
            is Resource.Loading -> {
                LoadingStateNegocioList()
            }
            is Resource.Error -> {
                ErrorStateNegocioList(
                    message = state.message ?: "Error desconocido",
                    onRetry = { bussinesMainViewModel.getNegociosByUserId() }
                )
            }
            is Resource.Success -> {
                val negocios = state.data.orEmpty()
                if (negocios.isEmpty()) {
                    EmptyStateNegocioList()
                } else {
                    NegocioListContent(
                        negocios = negocios,
                        viewModel = bussinesMainViewModel,
                        navController = navController
                    )
                }
            }
            else -> {
                // Estado inicial o desconocido
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun NegocioListContent(
    negocios: List<Negocio>,
    viewModel: NegocioViewModel,
    navController: NavController
) {

    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(negocios) { negocio ->
            Log.d("NegocioListContent",negocio.toString())
            NegocioListItem(
                viewModel = viewModel,
                negocio = negocio,
                onCLickVer = {
                    navController.navigate(NavigationItem.NEGOCIO_CONFIG.createRoute(negocio.id))
                },
                onEditClick = {
                    viewModel.loadNegocioById(it.id)
                    navController.navigate(NavigationItem.NEGOCIO_FORM_SCREEN.route)
                },
                onDeleteClick = {
                    viewModel.deleteNegocio(negocio.id)
                }
            )
        }
    }
}

@Composable
fun LoadingStateNegocioList() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cargando negocios...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorStateNegocioList(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ThemeColors.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Error al cargar los negocios",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Reintentar")
        }
    }
}

@Composable
fun EmptyStateNegocioList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Storefront,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No tienes negocios registrados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crea tu primer negocio usando el botón 'Nuevo Negocio'",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AddBusiness,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Añade tu primer negocio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}