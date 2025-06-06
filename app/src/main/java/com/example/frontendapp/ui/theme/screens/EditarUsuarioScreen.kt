package com.example.frontendapp.ui.theme.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Usuario.UpdateNombreDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.modals.ModalConfig
import com.example.frontendapp.ui.theme.composables.modals.ModalType
import com.example.frontendapp.ui.theme.composables.modals.ReusableModal
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel


@Composable
fun EditarUsuarioScreenMejorada(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
) {
    val usuarioUI by usuarioViewModel.usuarioUI.collectAsState()
    var nombre by remember { mutableStateOf(usuarioUI.userName) }
    var isLoading by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    // Detectar cambios
    LaunchedEffect(nombre) { hasChanges = nombre != usuarioUI.userName && nombre.isNotBlank() }

    val saveButtonColor by animateColorAsState(
        targetValue = if (hasChanges) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        animationSpec = tween(300),
        label = "save_button_color"
    )
    //Modal
    ReusableModal(
        isVisible = isVisible,
        config = ModalConfig(
            type = if(isError) ModalType.ERROR else ModalType.SUCCESS,
            title = if(isError) "Error" else "Exito",
            message = if(isError)
             "Error inesperado al actualiza el nombre de usuario"
             else "Nombre actulizado correctamente",
            confirmText = if(isError) "Entendido" else "Aceptar"
        ),
        onConfirm = { isVisible = false },
        onDismiss = { isVisible = false }
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBarSection(onBack = {
            navController.popBackStack()
        })

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Avatar y título
            ProfileHeaderSection()

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Información personal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Campo nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Nota informativa
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "El correo electrónico no se puede modificar por seguridad",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón guardar
            Button(
                onClick = {
                    if (hasChanges) {

                        usuarioViewModel.updateNombre(
                            usuario = UpdateNombreDTO(
                                userName = nombre,
                                id = RetrofitInstance.getUserId()
                            ),
                            onSuccess = { updatedUser ->
                                Log.d("EditarUsuairo", updatedUser.toString())
                                isVisible = true
                                isError = false
                            },
                            onError = {
                                Log.d("EditarUsuario", it)
                                isVisible = true
                                isError = true
                            },
                            onLoading = {
                                isLoading = true
                            }
                        )

                    }
                },
                enabled = hasChanges && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = saveButtonColor,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar cambios",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBarSection(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Editar perfil",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ProfileHeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Actualiza tu información",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun EditarUsuarioScreenMejoradaPreview() {
    FrontendappTheme {
        EditarUsuarioScreenMejorada(
            navController = rememberNavController(),
            usuarioViewModel = UsuarioViewModel(AuthRepo(RetrofitInstance.userApi))
        )
    }
}