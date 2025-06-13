package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.modals.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.composables.modals.ModalConfig
import com.example.frontendapp.ui.theme.composables.modals.ReusableModal
import com.example.frontendapp.ui.theme.composables.navigation.TabAnimatedScaffold
import com.example.frontendapp.ui.theme.composables.tab.adminPanel.NegociosPanelAdminContent
import com.example.frontendapp.ui.theme.composables.tab.adminPanel.UsuariosAdminPanelContent
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