package com.example.frontendapp.ui.theme.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.data.dto.GridButtonItem
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.composables.Btn.IconPosition
import com.example.frontendapp.ui.theme.composables.list.NegocioList
import com.example.frontendapp.ui.theme.composables.modal.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.composables.Btn.onClick


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioNegocioMainScreen(
    navController: NavController,
    viewModel: NegocioViewModel
) {
    val context = LocalContext.current
    var show by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Si no se permite la ubicación no se podrá ubicar tu posición", Toast.LENGTH_LONG).show()
        }
    }
    BackHandler {
        show = true
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Principal_variacion3,
                    titleContentColor = Color.White
                ),
                title = { Text("Registro de negocio") },
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        // Contenedor principal con scroll para evitar desbordamiento
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cabecera con botones
            GridButtons(
                modifier = Modifier

                    .fillMaxWidth()
                    .height(200.dp), // Limitar la altura de la cabecera
                padding = PaddingValues(16.dp),
                buttons = listOf(
                    GridButtonItem(
                        text = "Añadir",
                        icon = Icons.Default.AddBusiness,
                        onClick = {
                            viewModel.startNewNegocio()
                            navController.navigate(NavigationItem.NEGOCIO_FORM_SCREEN.route)
                        }
                    ),
                    GridButtonItem(
                        text = "Logout",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        containerColor = Color.Red,
                        onClick = {show=true}
                    )
                )
            )
            LogoutConfirmationDialog(
                onDismiss = {show=false},
                onConfirmLogout = {
                    show=false
                    RetrofitInstance.setToken("")
                    RetrofitInstance.setRoles(listOf())
                },
                showDialog = show,
                imageRes = R.mipmap.detener
            )
            NegociosHeader()

            // Lista de negocios
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // Asegura que la lista ocupe el espacio restante
            ) {
                NegocioList(
                    bussinesMainViewModel = viewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun NegociosHeader() {
    Column {
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
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mis Negocios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
    }
}


@Composable
fun GridButtons(
    modifier: Modifier,
    buttons: List<GridButtonItem>,
    width: Dp = 150.dp,
    height: Dp = 150.dp,
    padding: PaddingValues
) {
    Column(
        modifier = modifier.padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                items(buttons.size) { index ->
                    val button = buttons[index]
                    Box(
                        modifier = Modifier
                            .size(width, height)
                    ) {
                        BtnStyle1(
                            onClick = button.onClick,
                            icon = button.icon,
                            text = button.text,
                            containerColor = button.containerColor,
                            iconPosition = IconPosition.END,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MAinBussinesingPreview() {
    FrontendappTheme {
        val fakeViewModel = remember { FakeNegocioViewModel() }

        UsuarioNegocioMainScreen(navController = rememberNavController(), viewModel = fakeViewModel)
    }
}