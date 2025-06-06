package com.example.frontendapp.data.model.UI

import androidx.compose.ui.graphics.vector.ImageVector

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
enum class ModalType {
    SUCCESS, ERROR, WARNING, INFO, CONFIRMATION, ACTION
}