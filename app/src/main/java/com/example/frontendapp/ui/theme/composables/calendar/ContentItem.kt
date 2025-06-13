package com.example.frontendapp.ui.theme.composables.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.UI.CalendarUiState

@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    isToday: Boolean,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier
) {
    // Colores animados
    val backgroundColor by animateColorAsState(
        targetValue = when {
            date.isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    val textColor by animateColorAsState(
        targetValue = when {
            date.isSelected -> MaterialTheme.colorScheme.onPrimary
            !date.isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            isToday -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300)
    )

    val dotColor by animateColorAsState(
        targetValue = when {
            date.isSelected -> MaterialTheme.colorScheme.onPrimary
            date.isEnabled -> MaterialTheme.colorScheme.secondary
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300)
    )

    val elevation by animateDpAsState(
        targetValue = if (date.isSelected) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val scale by animateFloatAsState(
        targetValue = if (date.isSelected) 1.1f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .padding(4.dp)
            .scale(scale)
            .clip(CircleShape)
            .shadow(elevation, CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = if (date.isEnabled) LocalIndication.current else null,
                enabled = date.isEnabled,
                onClick = { onClickListener(date) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isToday || date.isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = textColor
                )
            )

            // Indicador de disponibilidad
            if (date.isEnabled) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .background(dotColor, CircleShape)
                )
            }

        }
    }
}



