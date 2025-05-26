package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.composables.modal.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.animatedContent.NegocioTabsWithContentBottom
import com.example.frontendapp.ui.theme.composables.list.ListaReservas
import com.example.frontendapp.ui.theme.composables.list.ServicioList
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun ClienteMainScreen(
    navController: NavController,
    reservasViewModel: ReservasViewModel,
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(ContentType.SERVICIOS) }

    // Interceptar botón atrás
    BackHandler {
        showDialog = true
    }

    // Diálogo de logout
    LogoutConfirmationDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirmLogout = {
            showDialog = false
            RetrofitInstance.setToken("")
            RetrofitInstance.setRoles(emptyList())
            navController.navigate(NavigationItem.LOGIN.route) {
                popUpTo(0)
            }
        },
        imageRes = R.mipmap.detener
    )
    Scaffold { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            NegocioTabsWithContentBottom(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            ) { currentTab ->
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                        (slideInHorizontally(
                            animationSpec = tween(durationMillis = 500),
                            initialOffsetX = { fullWidth -> direction * fullWidth }
                        ) + fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.9f)) togetherWith
                                (slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 500),
                                    targetOffsetX = { fullWidth -> -direction * fullWidth }
                                ) + fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 1.1f))
                    },
                    label = "ClienteTabsAnimation"
                ) { targetContent ->
                    when (targetContent) {
                        ContentType.RESERVAS -> Text("Vista de Servicios", style = MaterialTheme.typography.bodyLarge)
                        ContentType.SERVICIOS -> Text("Vista de Servicios", style = MaterialTheme.typography.bodyLarge)
                        ContentType.SUBSCRIPTOR -> Text("Vista de Suscriptores", style = MaterialTheme.typography.bodyLarge)
                        else -> Text("Selecciona una sección", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ClienteMainScreenPreview() {
    val controller = rememberNavController()
    ClienteMainScreen(controller, FakeReservasViewModel())
}