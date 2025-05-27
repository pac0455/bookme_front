package com.example.frontendapp.data.model.UI

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.frontendapp.ui.theme.Principal_variacion3

data class GridButtonItem(
    val text: String,
    val icon: ImageVector,
    val containerColor: Color = Principal_variacion3,
    val onClick: () -> Unit
)