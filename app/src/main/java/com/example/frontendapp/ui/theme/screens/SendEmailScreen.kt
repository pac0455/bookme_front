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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.UI.ERol
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.model.Usuario.ConfirmMailDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.*
import com.example.frontendapp.ui.theme.composables.modals.ModalConfig
import com.example.frontendapp.ui.theme.composables.modals.ModalType
import com.example.frontendapp.ui.theme.composables.modals.ReusableModal
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeUsuarioViewModel
import kotlinx.coroutines.delay



private val TAG="SendMailScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMailScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val user by remember { mutableStateOf(RetrofitInstance.getUsuario()) }
    val verificationCode = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }
    val showSuccess = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val isResending = remember { mutableStateOf(false) }
    val modalState = remember { mutableStateOf(ResusableModalDTO()) }

    fun showModal(title: String,
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

    LaunchedEffect(Unit) {
        val userId = RetrofitInstance.getUserId()
        val user = RetrofitInstance.getUsuario()
        Log.d(TAG, "Enviando código para usuarioId: $userId")
        Log.d(TAG, "Usuario es: $user")

        usuarioViewModel.enviarCodigoAutenticacion(
            usuarioId = userId,
            onSuccess = {Log.d(TAG, "enviado correctamente")},
            onError =  {Log.d(TAG, it)}
            )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "Verificación de Email",
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
                                imageVector = Icons.Default.MailOutline,
                                contentDescription = "Email",
                                modifier = Modifier.size(40.dp),
                                tint = AppColors.GreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título principal
                    Text(
                        text = "Verifica tu email",
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
                            text = user?.email ?: "user@gmail.com",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.GreenSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Text(
                        text = "Tiempo de uso: 10 minutos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de código de verificación
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Código de verificación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = verificationCode.value,
                        onValueChange = {
                            if (it.length <= 6) {
                                verificationCode.value = it
                                showError.value = false
                            }
                        },
                        label = { Text("Ingresa el código de 6 dígitos") },
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
                        isError = showError.value,
                        supportingText = if (showError.value) {
                            {
                                Text(
                                    "Código incorrecto o expirado",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Botón principal - Verificar
                ModernButton(
                    onClick = {
                        if (verificationCode.value.isNotEmpty()) {

//                             Simular verificación
                             usuarioViewModel.confirmCode(
                                 model = ConfirmMailDTO(
                                     Id = user?.id!!,
                                     CODE = verificationCode.value
                                 ),
                                 onSuccess = {
                                     isLoading.value = false
                                     showModal(
                                         msg = "Correo autentificado correctamente",
                                         title = "EXITO!",
                                         type = ModalType.SUCCESS,
                                            onConfirm = {
                                            modalState.value = modalState.value.copy(show = false)
                                            //Navegar a la pantalla de login
                                            val roles= RetrofitInstance.getRoles()
                                            when {
                                                roles.contains(ERol.CLIENTE.toString()) -> navController.navigate(NavigationItem.CLIENTE_MAIN_SCREEN.route)
                                                roles.contains(ERol.NEGOCIO.toString()) -> navController.navigate(NavigationItem.BUSSINES_MAIN.route)
                                            }
                                        }
                                     )
                                 },
                                 onError = {
                                     isLoading.value = false
                                     Log.d(TAG, it)
                                     showModal(
                                         title = "Error de Verificación",
                                         msg = "Comprueba que el codigo coincide exactamente con el enviado",
                                         type = ModalType.ERROR,
                                     )
                                 },
                                 onLoading = {
                                     isLoading.value = true
                                 }
                             )
                        }
                    },
                    text = "Verificar código",
                    icon = Icons.Default.Verified,
                    isLoading = isLoading.value,
                    enabled = verificationCode.value.length == 6,
                    isPrimary = true
                )

                // Botón secundario - Reenviar
                val context = LocalContext.current

                ModernButton(
                    onClick = {

                         usuarioViewModel.enviarCodigoAutenticacion(
                             usuarioId = RetrofitInstance.getUserId(),
                             onLoading = {isResending.value = true},
                             onSuccess = {
                                 isResending.value = false
                                 Toast.makeText(context, "Correro reenviado, mire la bandeja de entrada", Toast.LENGTH_LONG).show()

                             },
                             onError = {
                                 isResending.value = false
                                 Toast.makeText(context, "Error inesperado", Toast.LENGTH_LONG).show()
                             }
                         )
                    },
                    text = "Reenviar código",
                    icon = Icons.AutoMirrored.Filled.Send,
                    isLoading = isResending.value,
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
                    message = "¡Verificación exitosa!",
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
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AppColors.GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "El código expira en 10 minutos. Si no lo recibes, revisa tu carpeta de spam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.GreenSecondary,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                    )
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
fun SendMailScreenPreview() {
    FrontendappTheme {
        SendMailScreen(
            navController = rememberNavController(),
            usuarioViewModel = FakeUsuarioViewModel()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SuccessMessagePreview() {
    FrontendappTheme {
        SuccessMessage(
            onDismiss = {},
            message = "sfdasfsdafksdafksdaf"
        )
    }
}