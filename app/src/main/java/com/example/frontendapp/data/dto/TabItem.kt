package com.example.frontendapp.data.dto

import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem (
    val title: String,
    val unSelectedIcon: ImageVector,
    val selectedIcon: ImageVector
)