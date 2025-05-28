package com.example.frontendapp.data.model.UI

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val index: Int=0, // orden del tab (si se desea control explícito)
    val title: String, // nombre visible o descriptivo del tab
    val unSelectedIcon: ImageVector, // ícono cuando NO está seleccionado
    val selectedIcon: ImageVector,   // ícono cuando está seleccionado
    val content: @Composable () -> Unit, // contenido que se muestra al seleccionar el tab
)

