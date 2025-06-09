package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.Items.UsuarioCardExpandable
import com.example.frontendapp.ui.theme.composables.list.ErrorState
import com.example.frontendapp.ui.theme.composables.list.LoadingState
import com.example.frontendapp.ui.theme.composables.modals.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.composables.modals.ModalConfig
import com.example.frontendapp.ui.theme.composables.modals.ModalType
import com.example.frontendapp.ui.theme.composables.modals.ReusableModal
import com.example.frontendapp.ui.theme.composables.navigation.TabAnimatedScaffold
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.composables.section.IconConfig
import com.example.frontendapp.ui.theme.composables.tab.adminPanel.EmptyList
import com.example.frontendapp.ui.theme.composables.tab.adminPanel.FilterPanelModal
import com.example.frontendapp.ui.theme.composables.tab.adminPanel.NegociosPanelAdminContent
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeUsuarioViewModel



@Composable
fun AdminPanel(
    usuarioViewModel: UsuarioViewModel,
    negocioViewModel: NegocioViewModel,
    navController: NavController
){
    //Modal filtro desactivado
    val modalState = remember { mutableStateOf(ResusableModalDTO()) }
    var isVisibleLogOut by remember { mutableStateOf(false) }

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
    BackHandler { isVisibleLogOut=true }

    //Modal logout
    LogoutConfirmationDialog(
        onDismiss = {isVisibleLogOut = false},
        showDialog = isVisibleLogOut,
        onConfirmLogout = {
            navController.navigate(NavigationItem.LOGIN.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    )

    val tabs = listOf(
        TabItem(
            index = 0,
            title = "Negocios",
            selectedIcon = Icons.Filled.Store,
            unSelectedIcon = Icons.Outlined.Storefront,
            content = {
                NegociosPanelAdminContent(
                    negocioViewModel = negocioViewModel,
                    navController = navController,
                    onBack = {isVisibleLogOut=true}
                )
            }
        ),
        TabItem(
            index = 0,
            title = "Usuarios",
            selectedIcon = Icons.Filled.Person,
            unSelectedIcon = Icons.Outlined.Person,
            content = {
                UsuariosAdminPanelContent(
                    usuarioViewModel = usuarioViewModel,
                    onBack = {isVisibleLogOut=true}
                )
            }
        )

    )
    TabAnimatedScaffold(tabs)
}


@Composable
fun UsuariosAdminPanelContent(
    usuarioViewModel: UsuarioViewModel,
    onBack: () -> Unit
) {

    val isVisible = remember { mutableStateOf(false) }
    var isVisibleLogOut by remember { mutableStateOf(false) }
    val usuariosLista by usuarioViewModel.usuariosState.collectAsState()

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
                iconConfig = IconConfig(
                    isVisible = true,
                    onClick = onBack
                ),

            )
        }
    ) { innerPadding ->

        // Contenedor principal con padding del scaffold
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            BackHandler { onBack() }

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

            when (usuariosLista) {
                is Resource.Success -> {
                    val usuarios = usuariosLista.data ?: emptyList()

                    if (usuarios.isEmpty()) {
                        EmptyList()
                    } else {
                        LazyColumn {
                            items(usuarios, key = { it.id!! }) {
                                Log.d("usuariosLista", it.toString())
                                UsuarioCardExpandable(
                                    usuario = it,
                                    onBlockClick = { usuario ->
                                        usuarioViewModel.bloquearUsuario(
                                            usuario.id!!,
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
                                    onDeleteClick = { usuario ->
                                        usuarioViewModel.eliminarUsuario(
                                            usuario.id!!,
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
                                    onBack = {  isVisibleLogOut=true },
                                    onDesbloquear = { usuario ->
                                        usuarioViewModel.desbloquearUsuario(
                                            usuario.id!!,
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

            // Modal filtro
            FilterPanelModal(
                isVisible = isVisible.value,
                onDismiss = { isVisible.value = false }
            )

        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun AdminPanelPreview(){
    FrontendappTheme {
        AdminPanel(
            usuarioViewModel = FakeUsuarioViewModel(),
            negocioViewModel = FakeNegocioViewModel(),
            navController = rememberNavController()
        )
    }
}