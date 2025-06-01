package com.example.frontendapp.ui.theme.composables.calendar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.UI.CalendarUiState

@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val elevation by animateDpAsState(
        targetValue = if (date.isSelected) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val scale by animateFloatAsState(
        targetValue = if (date.isSelected) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .background(Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = if (date.isEnabled) rememberRipple() else null,
                enabled = date.isEnabled,
                onClick = {
                    onClickListener(date)
                }
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(scale)
                .background(
                    color = if (date.isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = if (date.isEnabled && !date.isSelected) 2.dp else 0.dp,
                    color = if (date.isEnabled && !date.isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                )
                .shadow(elevation, CircleShape)
        ) {
            Text(
                text = date.dayOfMonth,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = when {
                        !date.isEnabled -> Color.LightGray
                        date.isSelected -> MaterialTheme.colorScheme.onSecondary
                        else -> MaterialTheme.colorScheme.onBackground
                    }
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}




