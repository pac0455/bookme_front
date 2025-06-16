package com.example.frontendapp.ui.theme.screens


import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.*
import com.example.frontendapp.ui.theme.composables.modals.ModalConfig
import com.example.frontendapp.ui.theme.composables.modals.ModalType
import com.example.frontendapp.ui.theme.composables.modals.ReusableModal
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeUsuarioViewModel
import kotlinx.coroutines.delay

private val TAG = "PasswordResetScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    userEmail: String = "usuario@ejemplo.com"
) {
    val verificationCode = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val showNewPassword = remember { mutableStateOf(false) }
    val showConfirmPassword = remember { mutableStateOf(false) }
    val showError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val showSuccess = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val isResending = remember { mutableStateOf(false) }
    val modalState = remember { mutableStateOf(ResusableModalDTO()) }
    val timeLeft = remember { mutableStateOf(600) } // 10 minutos en segundos

    fun showModal(
        title: String,
        msg: String,
        type: ModalType,
        onConfirm: () -> Unit = {}
    ) {
        modalState.value = ResusableModalDTO(
            title = title,
            msg = msg,
            type = type,
            show = true,
            onConfirm = onConfirm
        )
    }

    fun validateForm(): Boolean {
        when {
            verificationCode.value.length != 6 -> {
                errorMessage.value = "El código debe tener 6 dígitos"
                showError.value = true
                return false
            }
            newPassword.value.length < 8 -> {
                errorMessage.value = "La contraseña debe tener al menos 8 caracteres"
                showError.value = true
                return false
            }
            newPassword.value != confirmPassword.value -> {
                errorMessage.value = "Las contraseñas no coinciden"
                showError.value = true
                return false
            }
            else -> {
                showError.value = false
                return true
            }
        }
    }

    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", mins, secs)
    }

    // Contador regresivo
    LaunchedEffect(timeLeft.value) {
        if (timeLeft.value > 0) {
            delay(1000)
            timeLeft.value = timeLeft.value - 1
        }
    }

    ReusableModal(
        isVisible = modalState.value.show,
        onDismiss = { modalState.value = modalState.value.copy(show = false) },
        onConfirm = modalState.value.onConfirm,
        onCancel = { modalState.value = modalState.value.copy(show = false) },
        config = ModalConfig(
            title = modalState.value.title,
            message = modalState.value.msg,
            type = modalState.value.type,
        )
    )

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "Restablecer Contraseña",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.GreenSecondary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate(NavigationItem.LOGIN.route) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                AppColors.GreenPrimary.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = AppColors.GreenSecondary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // Header Card con icono animado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = AppColors.GreenPrimary.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Icono principal con animación
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AppColors.GreenPrimary.copy(alpha = pulseAlpha * 0.3f),
                                        AppColors.GreenPrimary.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    AppColors.GreenPrimary.copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Seguridad",
                                modifier = Modifier.size(40.dp),
                                tint = AppColors.GreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título principal
                    Text(
                        text = "Crear nueva contraseña",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtítulo
                    Text(
                        text = "Hemos enviado un código de verificación a:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email del usuario
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.GreenBackground.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.GreenSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tiempo restante: ${formatTime(timeLeft.value)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Datos de restablecimiento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Campo de código de verificación
                    OutlinedTextField(
                        value = verificationCode.value,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                verificationCode.value = it
                                showError.value = false
                            }
                        },
                        label = { Text("Código de verificación") },
                        placeholder = { Text("000000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.GreenPrimary,
                            focusedLabelColor = AppColors.GreenPrimary,
                            cursorColor = AppColors.GreenPrimary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Pin,
                                contentDescription = null,
                                tint = AppColors.GreenPrimary
                            )
                        },
                        isError = showError.value && errorMessage.value.contains("código")
                    )

                    // Nueva contraseña
                    OutlinedTextField(
                        value = newPassword.value,
                        onValueChange = {
                            newPassword.value = it
                            showError.value = false
                        },
                        label = { Text("Nueva contraseña") },
                        placeholder = { Text("Mínimo 8 caracteres") },
                        visualTransformation = if (showNewPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.GreenPrimary,
                            focusedLabelColor = AppColors.GreenPrimary,
                            cursorColor = AppColors.GreenPrimary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AppColors.GreenPrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword.value = !showNewPassword.value }) {
                                Icon(
                                    imageVector = if (showNewPassword.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showNewPassword.value) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = AppColors.GreenPrimary
                                )
                            }
                        },
                        isError = showError.value && errorMessage.value.contains("contraseña") && !errorMessage.value.contains("coinciden")
                    )

                    // Confirmar contraseña
                    OutlinedTextField(
                        value = confirmPassword.value,
                        onValueChange = {
                            confirmPassword.value = it
                            showError.value = false
                        },
                        label = { Text("Confirmar contraseña") },
                        placeholder = { Text("Repite la contraseña") },
                        visualTransformation = if (showConfirmPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.GreenPrimary,
                            focusedLabelColor = AppColors.GreenPrimary,
                            cursorColor = AppColors.GreenPrimary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                tint = AppColors.GreenPrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword.value = !showConfirmPassword.value }) {
                                Icon(
                                    imageVector = if (showConfirmPassword.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showConfirmPassword.value) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = AppColors.GreenPrimary
                                )
                            }
                        },
                        isError = showError.value && errorMessage.value.contains("coinciden")
                    )

                    // Mensaje de error
                    if (showError.value) {
                        Text(
                            text = errorMessage.value,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Botón principal - Restablecer contraseña
                ModernButton(
                    onClick = {
                        if (validateForm()) {
//                            usuarioViewModel.resetPassword(
//                                model = ResetPasswordDTO(
//                                    email = userEmail,
//                                    code = verificationCode.value,
//                                    newPassword = newPassword.value
//                                ),
//                                onSuccess = {
//                                    isLoading.value = false
//                                    showModal(
//                                        msg = "Contraseña restablecida correctamente",
//                                        title = "¡ÉXITO!",
//                                        type = ModalType.SUCCESS,
//                                        onConfirm = {
//                                            modalState.value = modalState.value.copy(show = false)
//                                            navController.navigate(NavigationItem.LOGIN.route)
//                                        }
//                                    )
//                                },
//                                onError = {
//                                    isLoading.value = false
//                                    errorMessage.value = it
//                                    showError.value = true
//                                    Log.d(TAG, it)
//                                },
//                                onLoading = {
//                                    isLoading.value = true
//                                }
//                            )
                        }
                    },
                    text = "Restablecer contraseña",
                    icon = Icons.Default.Security,
                    isLoading = isLoading.value,
                    enabled = verificationCode.value.length == 6 &&
                            newPassword.value.isNotEmpty() &&
                            confirmPassword.value.isNotEmpty(),
                    isPrimary = true
                )

                // Botón secundario - Reenviar código
                val context = LocalContext.current

                ModernButton(
                    onClick = {
//                        usuarioViewModel.resendResetCode(
//                            email = userEmail,
//                            onLoading = { isResending.value = true },
//                            onSuccess = {
//                                isResending.value = false
//                                timeLeft.value = 600 // Reiniciar contador
//                                Toast.makeText(
//                                    context,
//                                    "Código reenviado, revisa tu bandeja de entrada",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            },
//                            onError = {
//                                isResending.value = false
//                                Toast.makeText(context, "Error inesperado", Toast.LENGTH_LONG).show()
//                            }
//                        )
                    },
                    text = "Reenviar código",
                    icon = Icons.AutoMirrored.Filled.Send,
                    isLoading = isResending.value,
                    enabled = timeLeft.value > 0,
                    isPrimary = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de éxito
            AnimatedVisibility(
                visible = showSuccess.value,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                SuccessMessage(
                    message = "¡Contraseña restablecida exitosamente!",
                    onDismiss = { showSuccess.value = false }
                )
            }

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.GreenBackground.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AppColors.GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Información importante:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.GreenSecondary
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "• El código expira en 10 minutos",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.GreenSecondary
                        )
                        Text(
                            text = "• La contraseña debe tener al menos 8 caracteres",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.GreenSecondary
                        )
                        Text(
                            text = "• Si no recibes el código, revisa tu carpeta de spam",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.GreenSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ModernButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = AppColors.GreenPrimary,
                contentColor = Color.White,
                disabledContainerColor = AppColors.GreenPrimary.copy(alpha = 0.3f)
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = AppColors.GreenPrimary,
                disabledContainerColor = Color.Transparent
            )
        },
        border = if (!isPrimary) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                AppColors.GreenPrimary.copy(alpha = if (enabled) 1f else 0.3f)
            )
        } else null,
        elevation = if (isPrimary) {
            ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        } else {
            ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = if (isPrimary) Color.White else AppColors.GreenPrimary
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SuccessMessage(
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000)
        onDismiss()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Success.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            AppColors.Success.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AppColors.Success,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = AppColors.Success,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = AppColors.Success,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PasswordResetScreenPreview() {
    FrontendappTheme {
        PasswordResetScreen(
            navController = rememberNavController(),
            usuarioViewModel = FakeUsuarioViewModel(),
            userEmail = "usuario@ejemplo.com"
        )
    }
}
