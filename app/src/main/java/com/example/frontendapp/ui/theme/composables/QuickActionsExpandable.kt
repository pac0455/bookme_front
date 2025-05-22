package com.example.frontendapp.ui.theme.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion6
import com.example.frontendapp.ui.theme.screens.ContentType

@Composable
fun QuickActionsExpandable(
    selectedContent: ContentType?,
    onContentSelected: (ContentType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    //Lista de iconos que se va a recorrer
    val allActions = listOf(
        Pair(ContentType.RESERVAS, Icons.Default.Notifications),
        Pair(ContentType.CALENDARIO, Icons.Default.CalendarMonth),
        Pair(ContentType.SUBSCRIPTOR, Icons.Filled.Stars),
        Pair(ContentType.GALLERIA, Icons.Filled.PhotoAlbum),

        )

    // Caja contenedora única con fondo opaco
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .offset(y = (-32).dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(Principal_variacion6, RoundedCornerShape(24.dp))
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Primera fila (siempre visible)
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            allActions.take(3).forEach { (contentType, icon) ->
                QuickActionButton(
                    onClick = { onContentSelected(contentType) },
                    icon = icon,
                    isSelected = contentType == selectedContent
                )
            }
        }

        // Botones adicionales dentro del mismo contenedor
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
            exit = fadeOut(tween(300)) + slideOutVertically(tween(300))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(32.dp)) // antes estaba en 16.dp
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 0.dp, max = 400.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allActions.drop(3)) { (contentType, icon) ->
                        QuickActionButton(
                            onClick = { onContentSelected(contentType) },
                            icon = icon,
                            isSelected = contentType == selectedContent
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        // Botón Ver más / Ver menos
        TextButton(onClick = { expanded = !expanded }) {
            Text(if (expanded) "Ver menos" else "Ver más")
        }
    }
}