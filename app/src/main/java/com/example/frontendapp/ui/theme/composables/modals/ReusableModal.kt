package com.example.frontendapp.ui.theme.composables.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.ThemeColors

// Enum para tipos de modal
enum class ModalType {
    SUCCESS, ERROR, WARNING, INFO, CONFIRMATION, ACTION
}

// Data class para configurar el modal
data class ModalConfig(
    val type: ModalType,
    val title: String,
    val message: String,
    val confirmText: String = "Aceptar",
    val cancelText: String = "Cancelar",
    val showCancelButton: Boolean = false,
    val icon: ImageVector? = null
)

// Modal base reutilizable
@Composable
fun ReusableModal(
    isVisible: Boolean,
    config: ModalConfig,
    onConfirm: () -> Unit,
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono del modal
                    val (iconVector, iconColor) = getModalIconAndColor(config.type, config.icon)

                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = iconColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Título
                    Text(
                        text = config.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mensaje
                    Text(
                        text = config.message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (config.showCancelButton) {
                            Arrangement.spacedBy(12.dp)
                        } else {
                            Arrangement.Center
                        }
                    ) {
                        if (config.showCancelButton) {
                            OutlinedButton(
                                onClick = {
                                    onCancel()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(config.cancelText)
                            }
                        }

                        Button(
                            onClick = {
                                onConfirm()
                                onDismiss()
                            },
                            modifier = if (config.showCancelButton) Modifier.weight(1f) else Modifier,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getButtonColor(config.type)
                            )
                        ) {
                            Text(
                                text = config.confirmText,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// Función para obtener icono y color según el tipo
@Composable
private fun getModalIconAndColor(type: ModalType, customIcon: ImageVector?): Pair<ImageVector, Color> {
    return when (type) {
        ModalType.SUCCESS -> Pair(
            customIcon ?: Icons.Default.CheckCircle,
            ThemeColors.success
        )
        ModalType.ERROR -> Pair(
            customIcon ?: Icons.Default.Error,
            ThemeColors.error
        )
        ModalType.WARNING -> Pair(
            customIcon ?: Icons.Default.Warning,
            ThemeColors.warning
        )
        ModalType.INFO -> Pair(
            customIcon ?: Icons.Default.Info,
            ThemeColors.info
        )
        ModalType.CONFIRMATION -> Pair(
            customIcon ?: Icons.Default.Help,
            ThemeColors.info
        )
        ModalType.ACTION -> Pair(
            customIcon ?: Icons.Default.Settings,
            ThemeColors.greenPrimary
        )
    }
}

// Función para obtener color del botón según el tipo
@Composable
private fun getButtonColor(type: ModalType): Color {
    return when (type) {
        ModalType.SUCCESS -> ThemeColors.success
        ModalType.ERROR -> ThemeColors.error
        ModalType.WARNING -> ThemeColors.warning
        ModalType.INFO -> ThemeColors.info
        ModalType.CONFIRMATION -> ThemeColors.greenPrimary
        ModalType.ACTION -> ThemeColors.greenPrimary
    }
}

// Modales específicos pre-configurados
@Composable
fun SuccessModal(
    isVisible: Boolean,
    title: String = "¡Éxito!",
    message: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit
) {
    ReusableModal(
        isVisible = isVisible,
        config = ModalConfig(
            type = ModalType.SUCCESS,
            title = title,
            message = message,
            confirmText = "Aceptar"
        ),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun ErrorModal(
    isVisible: Boolean,
    title: String = "Error",
    message: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit
) {
    ReusableModal(
        isVisible = isVisible,
        config = ModalConfig(
            type = ModalType.ERROR,
            title = title,
            message = message,
            confirmText = "Entendido"
        ),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun ConfirmationModal(
    isVisible: Boolean,
    title: String,
    message: String,
    confirmText: String = "Confirmar",
    cancelText: String = "Cancelar",
    onConfirm: () -> Unit,
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit
) {
    ReusableModal(
        isVisible = isVisible,
        config = ModalConfig(
            type = ModalType.CONFIRMATION,
            title = title,
            message = message,
            confirmText = confirmText,
            cancelText = cancelText,
            showCancelButton = true
        ),
        onConfirm = onConfirm,
        onCancel = onCancel,
        onDismiss = onDismiss
    )
}

// Modal de acciones (Editar, Borrar, Actualizar)
@Composable
fun ActionModal(
    isVisible: Boolean,
    title: String,
    actions: List<ActionItem>,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    actions.forEach { action ->
                        TextButton(
                            onClick = {
                                action.onClick()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = action.color
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = action.text,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

// Data class para items de acción
data class ActionItem(
    val text: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

// Ejemplos de uso
@Composable
fun ExampleUsage() {
    var showSuccessModal by remember { mutableStateOf(false) }
    var showErrorModal by remember { mutableStateOf(false) }
    var showConfirmationModal by remember { mutableStateOf(false) }
    var showActionModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { showSuccessModal = true }) {
            Text("Mostrar Modal de Éxito")
        }

        Button(onClick = { showErrorModal = true }) {
            Text("Mostrar Modal de Error")
        }

        Button(onClick = { showConfirmationModal = true }) {
            Text("Mostrar Modal de Confirmación")
        }

        Button(onClick = { showActionModal = true }) {
            Text("Mostrar Modal de Acciones")
        }
    }

    // Modal de éxito
    SuccessModal(
        isVisible = showSuccessModal,
        message = "Has eliminado el producto correctamente",
        onDismiss = { showSuccessModal = false }
    )

    // Modal de error
    ErrorModal(
        isVisible = showErrorModal,
        message = "No se pudo eliminar el producto. Inténtalo de nuevo.",
        onDismiss = { showErrorModal = false }
    )

    // Modal de confirmación
    ConfirmationModal(
        isVisible = showConfirmationModal,
        title = "Eliminar producto",
        message = "¿Estás seguro de que quieres eliminar este producto? Esta acción no se puede deshacer.",
        confirmText = "Eliminar",
        onConfirm = {
            // Lógica para eliminar
        },
        onDismiss = { showConfirmationModal = false }
    )

    // Modal de acciones
    ActionModal(
        isVisible = showActionModal,
        title = "Opciones del producto",
        actions = listOf(
            ActionItem(
                text = "Editar",
                icon = Icons.Default.Edit,
                color = ThemeColors.info
            ) {
                // Lógica para editar
            },
            ActionItem(
                text = "Eliminar",
                icon = Icons.Default.Delete,
                color = ThemeColors.error
            ) {
                // Lógica para eliminar
            },
            ActionItem(
                text = "Actualizar",
                icon = Icons.Default.Refresh,
                color = ThemeColors.success
            ) {
                // Lógica para actualizar
            }
        ),
        onDismiss = { showActionModal = false }
    )
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ModalsPreview() {
    FrontendappTheme {
        ModalsDemo()
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ModalsPreviewDark() {
    FrontendappTheme(darkTheme = true) {
        ModalsDemo()
    }
}

@Composable
fun ModalsDemo() {
    var showSuccessModal by remember { mutableStateOf(false) }
    var showErrorModal by remember { mutableStateOf(false) }
    var showWarningModal by remember { mutableStateOf(false) }
    var showInfoModal by remember { mutableStateOf(false) }
    var showConfirmationModal by remember { mutableStateOf(false) }
    var showActionModal by remember { mutableStateOf(false) }
    var showCustomModal by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Modales Reutilizables Demo",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Modales de Estado",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showSuccessModal = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ThemeColors.success
                                )
                            ) {
                                Text("Éxito", color = Color.White)
                            }

                            Button(
                                onClick = { showErrorModal = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ThemeColors.error
                                )
                            ) {
                                Text("Error", color = Color.White)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showWarningModal = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ThemeColors.warning
                                )
                            ) {
                                Text("Advertencia", color = Color.White)
                            }

                            Button(
                                onClick = { showInfoModal = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ThemeColors.info
                                )
                            ) {
                                Text("Info", color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Modales de Acción",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = { showConfirmationModal = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Modal de Confirmación")
                        }

                        Button(
                            onClick = { showActionModal = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Modal de Acciones")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Modal Personalizado",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = { showCustomModal = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeColors.greenPrimary
                            )
                        ) {
                            Text("Modal con Icono Personalizado")
                        }
                    }
                }
            }
        }

        // Modales
        SuccessModal(
            isVisible = showSuccessModal,
            title = "¡Producto Eliminado!",
            message = "Has eliminado el producto correctamente. Los cambios se han guardado.",
            onDismiss = { showSuccessModal = false }
        )

        ErrorModal(
            isVisible = showErrorModal,
            title = "Error al Eliminar",
            message = "No se pudo eliminar el carrito. Verifica tu conexión a internet e inténtalo de nuevo.",
            onDismiss = { showErrorModal = false }
        )

        ReusableModal(
            isVisible = showWarningModal,
            config = ModalConfig(
                type = ModalType.WARNING,
                title = "Advertencia",
                message = "El stock del producto está bajo. Solo quedan 3 unidades disponibles.",
                confirmText = "Entendido"
            ),
            onConfirm = { },
            onDismiss = { showWarningModal = false }
        )

        ReusableModal(
            isVisible = showInfoModal,
            config = ModalConfig(
                type = ModalType.INFO,
                title = "Información",
                message = "La actualización del catálogo se completará en aproximadamente 5 minutos.",
                confirmText = "OK"
            ),
            onConfirm = { },
            onDismiss = { showInfoModal = false }
        )

        ConfirmationModal(
            isVisible = showConfirmationModal,
            title = "Eliminar Producto",
            message = "¿Estás seguro de que quieres eliminar este producto del carrito? Esta acción no se puede deshacer.",
            confirmText = "Sí, Eliminar",
            cancelText = "Cancelar",
            onConfirm = {
                // Aquí iría la lógica de eliminación
            },
            onDismiss = { showConfirmationModal = false }
        )

        ActionModal(
            isVisible = showActionModal,
            title = "Opciones del Producto",
            actions = listOf(
                ActionItem(
                    text = "Editar Producto",
                    icon = Icons.Default.Edit,
                    color = ThemeColors.info
                ) {
                    // Lógica para editar
                },
                ActionItem(
                    text = "Duplicar Producto",
                    icon = Icons.Default.ContentCopy,
                    color = ThemeColors.greenPrimary
                ) {
                    // Lógica para duplicar
                },
                ActionItem(
                    text = "Compartir Producto",
                    icon = Icons.Default.Share,
                    color = ThemeColors.info
                ) {
                    // Lógica para compartir
                },
                ActionItem(
                    text = "Eliminar Producto",
                    icon = Icons.Default.Delete,
                    color = ThemeColors.error
                ) {
                    // Lógica para eliminar
                },
                ActionItem(
                    text = "Actualizar Stock",
                    icon = Icons.Default.Refresh,
                    color = ThemeColors.success
                ) {
                    // Lógica para actualizar
                }
            ),
            onDismiss = { showActionModal = false }
        )

        ReusableModal(
            isVisible = showCustomModal,
            config = ModalConfig(
                type = ModalType.SUCCESS,
                title = "¡Carrito Actualizado!",
                message = "Se han agregado 3 productos nuevos a tu carrito de compras.",
                confirmText = "Ver Carrito",
                icon = Icons.Default.ShoppingCart
            ),
            onConfirm = {
                // Navegar al carrito
            },
            onDismiss = { showCustomModal = false }
        )
    }
}

// Preview individual de cada modal
@Preview(showBackground = true)
@Composable
fun SuccessModalPreview() {
    FrontendappTheme {
        SuccessModal(
            isVisible = true,
            title = "¡Éxito!",
            message = "Has eliminado el producto correctamente",
            onDismiss = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorModalPreview() {
    FrontendappTheme {
        ErrorModal(
            isVisible = true,
            title = "Error",
            message = "No se pudo completar la operación. Inténtalo de nuevo.",
            onDismiss = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmationModalPreview() {
    FrontendappTheme {
        ConfirmationModal(
            isVisible = true,
            title = "Eliminar Producto",
            message = "¿Estás seguro de que quieres eliminar este producto?",
            onConfirm = { },
            onDismiss = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActionModalPreview() {
    FrontendappTheme {
        ActionModal(
            isVisible = true,
            title = "Opciones",
            actions = listOf(
                ActionItem(
                    text = "Editar",
                    icon = Icons.Default.Edit,
                    color = ThemeColors.info
                ) { },
                ActionItem(
                    text = "Eliminar",
                    icon = Icons.Default.Delete,
                    color = ThemeColors.error
                ) { },
                ActionItem(
                    text = "Actualizar",
                    icon = Icons.Default.Refresh,
                    color = ThemeColors.success
                ) { }
            ),
            onDismiss = { }
        )
    }
}

// Preview con diferentes estados
@Preview(showBackground = true, name = "Modo Claro")
@Composable
fun AllModalsLightPreview() {
    FrontendappTheme(darkTheme = false) {
        ModalsDemo()
    }
}

@Preview(showBackground = true, name = "Modo Oscuro", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AllModalsDarkPreview() {
    FrontendappTheme(darkTheme = true) {
        ModalsDemo()
    }
}