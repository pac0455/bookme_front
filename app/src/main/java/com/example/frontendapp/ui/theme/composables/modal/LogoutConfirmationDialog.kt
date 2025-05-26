package com.example.frontendapp.ui.theme.composables.modal


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.Btn.IconPosition

@Composable
fun LogoutConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit,
    imageRes: Int,
) {
    if (showDialog) {
        AnimatedVisibility(visible = showDialog, enter = fadeIn(), exit = fadeOut()) {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    BtnStyle1(
                        containerColor = Color.Red.copy(0.6f),
                        text = "Sí, salir",
                        icon = Icons.Default.ExitToApp,
                        onClick = onConfirmLogout,
                        iconPosition = IconPosition.START
                    )
                },
                dismissButton = {
                    BtnStyle1(

                        text = "Cancelar",
                        icon = Icons.Default.Warning,
                        onClick = onDismiss,
                        iconPosition = IconPosition.START
                    )
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Estás seguro de que quieres salir?")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp)
                        )
                        Text("Se cerrará tu sesión actual.")
                    }
                },
                containerColor = Color.White,
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogoutConfirmationDialog() {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        LogoutConfirmationDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onConfirmLogout = {
                showDialog = false
                // Acción simulada en el preview
            },
            imageRes = R.mipmap.detener // Reemplaza con tu imagen
        )
    }
}

