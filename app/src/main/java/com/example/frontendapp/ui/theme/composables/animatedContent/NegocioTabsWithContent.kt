package com.example.frontendapp.ui.theme.composables.animatedContent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.screens.ContentType

@Composable
fun NegocioTabsWithContentBottom(
    selectedTab: ContentType,
    onTabSelected: (ContentType) -> Unit,
    content: @Composable (ContentType) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Contenido principal
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            content(selectedTab)
        }

        // Tab bar abajo
        TabRow(
            modifier = Modifier.navigationBarsPadding(),
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Principal_variacion3,
            contentColor = Color.White
        ) {
            Tab(
                selected = selectedTab == ContentType.SERVICIOS,
                onClick = { onTabSelected(ContentType.SERVICIOS) },
                text = { Text("Servicios") },
                icon = { Icon(Icons.Default.Storefront, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == ContentType.RESERVAS,
                onClick = { onTabSelected(ContentType.RESERVAS) },
                text = { Text("Reservas") },
                icon = { Icon(Icons.Default.List, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == ContentType.SUBSCRIPTOR,
                onClick = { onTabSelected(ContentType.SUBSCRIPTOR) },
                text = { Text("Suscriptores") },
                icon = { Icon(Icons.Default.Star, contentDescription = null) }
            )
        }
    }
}

