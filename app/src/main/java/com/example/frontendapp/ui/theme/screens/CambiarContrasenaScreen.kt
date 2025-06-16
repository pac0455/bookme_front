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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.data.model.UI.UpdatePasswordDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.CustomTextField
import com.example.frontendapp.ui.theme.composables.modals.ModalConfig
import com.example.frontendapp.ui.theme.composables.modals.ModalType
import com.example.frontendapp.ui.theme.composables.modals.ReusableModal
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel


@Composable
fun CambiarContrasenaScreen(
    usuarioViewModel: UsuarioViewModel,
    navController: NavController
) {
    var contrasenaActual by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Estados de visibilidad de contraseñas
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    val passwordLengthError = stringResource(id = R.string.password_length_error)
    val passwordsDoNotMatchError = stringResource(id = R.string.passwords_do_not_match_error)


    ReusableModal(
        isVisible = isVisible,
        config = ModalConfig(
            type = if(isError) ModalType.ERROR else ModalType.SUCCESS,
            title = if(isError) stringResource(id = R.string.modal_error_title) else stringResource(id = R.string.modal_success_title),
            message = if(isError)
                stringResource(id = R.string.modal_error_message)
            else stringResource(id = R.string.modal_success_message),
            confirmText = if(isError) stringResource(id = R.string.modal_confirm_error_button) else stringResource(id = R.string.modal_confirm_success_button)
        ),
        onConfirm = { isVisible = false },
        onDismiss = { isVisible = false }
    )


    // Validaciones
    val isFormValid = contrasenaActual.isNotBlank() &&
            nuevaContrasena.isNotBlank() &&
            confirmarContrasena.isNotBlank() &&
            nuevaContrasena == confirmarContrasena &&
            nuevaContrasena.length >= 6

    val buttonColor by animateColorAsState(
        targetValue = if (isFormValid) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        animationSpec = tween(300),
        label = "button_color"
    )

    // Validar en tiempo real
    LaunchedEffect(nuevaContrasena, confirmarContrasena) {
        error = when {
            nuevaContrasena.isNotBlank() && nuevaContrasena.length < 6 ->
                passwordLengthError // Usa el string cargado
            confirmarContrasena.isNotBlank() && nuevaContrasena != confirmarContrasena ->
                passwordsDoNotMatchError // Usa el string cargado
            else -> null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBarSection(onBack = {
            navController.popBackStack()
        })

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header con icono
            SecurityHeaderSection()

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
                        text = stringResource(id = R.string.change_password_screen_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Contraseña actual
                    CustomTextField(
                        value = contrasenaActual,
                        onValueChange = { contrasenaActual = it },
                        label = stringResource(id = R.string.current_password_label) ,
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true
                    )

                    // Nueva contraseña
                    OutlinedTextField(
                        value = nuevaContrasena,
                        onValueChange = { nuevaContrasena = it },
                        label = { Text(stringResource(id = R.string.new_password_label)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                Icon(
                                    imageVector = if (showNewPassword) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (showNewPassword) stringResource(id = R.string.hide_password_content_description) else stringResource(id = R.string.show_password_content_description),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (showNewPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        isError = error != null && nuevaContrasena.isNotBlank()
                    )

                    // Confirmar contraseña
                    OutlinedTextField(
                        value = confirmarContrasena,
                        onValueChange = { confirmarContrasena = it },
                        label = { Text(stringResource(id = R.string.confirm_new_password_label)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (nuevaContrasena == confirmarContrasena && confirmarContrasena.isNotBlank())
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (showConfirmPassword) stringResource(id = R.string.hide_password_content_description) else stringResource(id = R.string.show_password_content_description),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        isError = error != null && confirmarContrasena.isNotBlank()
                    )

                    // Mensaje de error
                    if (error != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Consejos de seguridad
                    SecurityTipsSection()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón cambiar
            Button(
                onClick = {
                    if (isFormValid) {
                        usuarioViewModel.updatePassword(
                            usuario = UpdatePasswordDTO(
                                newPassword = nuevaContrasena,
                                oldPassword = contrasenaActual,
                                userId = RetrofitInstance.getUserId()
                            ),
                            onSuccess = {
                                isVisible=true
                                isError=false
                                isLoading=false
                            },
                            onLoading = {isLoading=true},
                            onError = {
                                isLoading=false
                                isVisible=true
                                isError=true
                                Log.d("CambiarContrasenaScreen", it)
                            }
                        )
                    }
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.change_password_button),
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
            onClick = onBack,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.back_button_content_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(id = R.string.security_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun SecurityHeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.update_password_header),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(id = R.string.update_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SecurityTipsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.TipsAndUpdates,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.security_tips_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val tips = listOf(
            stringResource(id = R.string.tip_min_characters),
            stringResource(id = R.string.tip_combine_characters),
            stringResource(id = R.string.tip_avoid_personal_info),
            stringResource(id = R.string.tip_no_reuse_password)
        )

        tips.forEach { tip ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun CambiarContrasenaScreenMejoradaPreview() {
    FrontendappTheme {
        CambiarContrasenaScreen(
            navController = rememberNavController(),
            usuarioViewModel = UsuarioViewModel(AuthRepo(RetrofitInstance.userApi))
        )
    }
}
