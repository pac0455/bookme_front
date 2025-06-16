package com.example.frontendapp.ui.theme.screens

import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.composables.Btn.ActionButton
import com.example.frontendapp.ui.theme.composables.list.NegocioList
import com.example.frontendapp.ui.theme.composables.modals.ErrorModal
import com.example.frontendapp.ui.theme.composables.modals.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegociosScreen(
    navController: NavController,
    negocioViewModel: NegocioViewModel
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val user by remember { mutableStateOf(RetrofitInstance.getUsuario()) }
    val modalState = remember { mutableStateOf(ResusableModalDTO()) }
    var show by remember { mutableStateOf(false) }
    fun showModal(title: String, msg: String) {
        modalState.value = ResusableModalDTO(
            title = title,
            msg = msg
        )
    }


    BackHandler {
        showLogoutDialog = true
    }

    ErrorModal(
        isVisible = show,
        title = modalState.value.title,
        onConfirm = {show = false},
        onDismiss = {show = false},
        message = modalState.value.title
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {
                    Text(
                        "Gestión de Negocios",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Panel de acciones rápidas
            QuickActionsPanel(
                onAddNegocio = {
                    if(user?.Bloqueado == true){
                        show=true
                        showModal(
                            title = "Usuario bloqueado",
                            msg = "Su usuario esta bloqueado por lo tanto no se pueden añadir negocios",
                        )
                    }else{
                        negocioViewModel.startNewNegocio()
                        navController.navigate(NavigationItem.NEGOCIO_FORM_SCREEN.route)
                    }
                },
                onActualizar = {
                    negocioViewModel.getNegociosByUserId()
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationItem.PREFERENCES_SCREEN.route)
                }
            )

            // Cabecera de la sección de negocios
            NegociosHeader()

            // Lista de negocios
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                NegocioList(
                    bussinesMainViewModel = negocioViewModel,
                    navController = navController
                )
            }
        }

        // Diálogo de confirmación de cierre de sesión
        LogoutConfirmationDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirmLogout = {
                showLogoutDialog = false
                RetrofitInstance.setToken("")
                RetrofitInstance.setRoles(listOf())
                navController.navigate(NavigationItem.LOGIN.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            showDialog = showLogoutDialog
        )
    }
}

@Composable
fun QuickActionsPanel(
    onAddNegocio: () -> Unit,
    onActualizar: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Acciones rápidas",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.AddBusiness,
                    text = "Nuevo Negocio",
                    contentDescription = "Agregar nuevo negocio",
                    onClick = onAddNegocio,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

                ActionButton(
                    icon = Icons.Default.Refresh,
                    text = "Actualizar",
                    contentDescription = "Actualizar información",
                    onClick = onActualizar,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )

                ActionButton(
                    icon = Icons.Default.Settings,
                    text = "Configuración",
                    contentDescription = "Ir a configuración",
                    onClick = onNavigateToSettings,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )

            }
        }
    }
}




@Composable
fun NegociosHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = "Negocios",
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Mis Negocios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MAinBussinesingPreview() {
    FrontendappTheme {
        val fakeViewModel = remember { FakeNegocioViewModel() }

        NegociosScreen(navController = rememberNavController(), negocioViewModel = fakeViewModel)
    }
}