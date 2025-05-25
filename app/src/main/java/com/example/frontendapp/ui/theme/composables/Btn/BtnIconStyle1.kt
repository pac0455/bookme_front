package com.example.frontendapp.ui.theme.composables.Btn

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion3

@Composable
fun BtnIconRounded(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    iconSize: Dp = size / 2,
    containerColor: Color = Principal_variacion3,
    iconTint: Color = Color.White,
    shape: Shape = CircleShape
) {
    Button (
        onClick = onClick,
        modifier = modifier
            .size(size)
            .shadow(

                elevation = 10.dp,
                shape = shape,
                clip = false,
                spotColor = Color.Black
            ),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = iconTint
        ),
        contentPadding = PaddingValues(0.dp) // 🔍 evita padding interno innecesario
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = iconTint
        )
    }
}
