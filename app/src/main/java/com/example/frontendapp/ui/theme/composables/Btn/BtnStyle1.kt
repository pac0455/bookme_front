package com.example.frontendapp.ui.theme.composables.Btn

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme

enum class IconPosition {
    START, END, TOP, BOTTOM
}

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    ERROR,
    SUCCESS,
    WARNING
}

@Composable
fun BtnStyle1(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    iconSize: Dp = 20.dp,
    onClick: () -> Unit,
    text: String = "Ejemplo",
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.START,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    elevation: Dp = 4.dp
) {
    // Determinar colores basados en el variant y el tema
    val (finalContainerColor, finalContentColor) = when (variant) {
        ButtonVariant.PRIMARY -> Pair(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        ButtonVariant.SECONDARY -> Pair(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )
        ButtonVariant.TERTIARY -> Pair(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary
        )
        ButtonVariant.ERROR -> Pair(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        )
        ButtonVariant.SUCCESS -> Pair(
            Color(0xFF4CAF50),
            Color.White
        )
        ButtonVariant.WARNING -> Pair(
            Color(0xFFFF9800),
            Color.White
        )
    }.let { (container, content) ->
        // Si se proporcionan colores personalizados, usarlos
        Pair(
            if (containerColor == MaterialTheme.colorScheme.primary) container else containerColor,
            if (contentColor == MaterialTheme.colorScheme.onPrimary) content else contentColor
        )
    }

    // Estados de animación
    val isClickable = enabled && !isLoading

    val animatedContainerColor by animateColorAsState(
        targetValue = if (isClickable) finalContainerColor else finalContainerColor.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 200),
        label = "ContainerColorAnimation"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (isClickable) finalContentColor else finalContentColor.copy(alpha = 0.7f),
        animationSpec = tween(durationMillis = 200),
        label = "ContentColorAnimation"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isClickable) 1f else 0.7f,
        animationSpec = tween(durationMillis = 200),
        label = "AlphaAnimation"
    )

    // Contenido del botón
    val displayText = if (isLoading) "Cargando..." else text
    val displayIcon = if (isLoading) Icons.Default.HourglassEmpty else icon

    Button(
        onClick = onClick,
        modifier = modifier
            .alpha(animatedAlpha)
            .shadow(
                elevation = if (isClickable) elevation else elevation / 2,
                shape = shape,
                clip = false
            ),
        shape = shape,
        enabled = isClickable,
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedContainerColor,
            contentColor = animatedContentColor,
            disabledContainerColor = animatedContainerColor,
            disabledContentColor = animatedContentColor
        ),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    ) {
        ButtonContent(
            text = displayText,
            icon = displayIcon,
            iconPosition = iconPosition,
            iconSize = iconSize,
            horizontalAlignment = horizontalAlignment,
            isLoading = isLoading
        )
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    iconPosition: IconPosition,
    iconSize: Dp,
    horizontalAlignment: Alignment.Horizontal,
    isLoading: Boolean
) {
    when (iconPosition) {
        IconPosition.TOP, IconPosition.BOTTOM -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = Arrangement.Center
            ) {
                if (icon != null && iconPosition == IconPosition.TOP) {
                    ButtonIcon(
                        icon = icon,
                        iconSize = iconSize,
                        isLoading = isLoading,
                        modifier = Modifier.padding(bottom = if (text.isNotEmpty()) 4.dp else 0.dp)
                    )
                }

                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (icon != null && iconPosition == IconPosition.BOTTOM) {
                    ButtonIcon(
                        icon = icon,
                        iconSize = iconSize,
                        isLoading = isLoading,
                        modifier = Modifier.padding(top = if (text.isNotEmpty()) 4.dp else 0.dp)
                    )
                }
            }
        }

        IconPosition.START, IconPosition.END -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = when (horizontalAlignment) {
                    Alignment.Start -> Arrangement.Start
                    Alignment.End -> Arrangement.End
                    else -> Arrangement.Center
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null && (iconPosition == IconPosition.START || text.isEmpty())) {
                    ButtonIcon(
                        icon = icon,
                        iconSize = iconSize,
                        isLoading = isLoading,
                        modifier = Modifier.padding(end = if (text.isNotEmpty()) 8.dp else 0.dp)
                    )
                }

                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (icon != null && iconPosition == IconPosition.END && text.isNotEmpty()) {
                    ButtonIcon(
                        icon = icon,
                        iconSize = iconSize,
                        isLoading = isLoading,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonIcon(
    icon: ImageVector,
    iconSize: Dp,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = modifier.size(iconSize),
            strokeWidth = 2.dp,
            color = LocalContentColor.current
        )
    } else {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = modifier.size(iconSize)
        )
    }
}

// Variantes predefinidas del botón
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.START,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    BtnStyle1(
        onClick = onClick,
        text = text,
        icon = icon,
        iconPosition = iconPosition,
        isLoading = isLoading,
        enabled = enabled,
        variant = ButtonVariant.PRIMARY,
        modifier = modifier
    )
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.START,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    BtnStyle1(
        onClick = onClick,
        text = text,
        icon = icon,
        iconPosition = iconPosition,
        isLoading = isLoading,
        enabled = enabled,
        variant = ButtonVariant.SECONDARY,
        modifier = modifier
    )
}

@Composable
fun ErrorButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.START,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    BtnStyle1(
        onClick = onClick,
        text = text,
        icon = icon,
        iconPosition = iconPosition,
        isLoading = isLoading,
        enabled = enabled,
        variant = ButtonVariant.ERROR,
        modifier = modifier
    )
}

@Composable
fun SuccessButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.START,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    BtnStyle1(
        onClick = onClick,
        text = text,
        icon = icon,
        iconPosition = iconPosition,
        isLoading = isLoading,
        enabled = enabled,
        variant = ButtonVariant.SUCCESS,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun BtnPreview() {
    FrontendappTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón primario normal
            BtnStyle1(
                onClick = { },
                text = "Botón Primario",
                icon = Icons.Default.Settings
            )

            // Botón secundario
            BtnStyle1(
                onClick = { },
                text = "Botón Secundario",
                icon = Icons.Default.Settings,
                variant = ButtonVariant.SECONDARY
            )

            // Botón de error
            BtnStyle1(
                onClick = { },
                text = "Botón Error",
                icon = Icons.Default.Settings,
                variant = ButtonVariant.ERROR
            )

            // Botón de éxito
            BtnStyle1(
                onClick = { },
                text = "Botón Éxito",
                icon = Icons.Default.Settings,
                variant = ButtonVariant.SUCCESS
            )

            // Botón cargando
            BtnStyle1(
                onClick = { },
                text = "Cargando",
                icon = Icons.Default.Settings,
                isLoading = true
            )

            // Botón deshabilitado
            BtnStyle1(
                onClick = { },
                text = "Deshabilitado",
                icon = Icons.Default.Settings,
                enabled = false
            )

            // Botón con icono arriba
            BtnStyle1(
                onClick = { },
                text = "Icono Arriba",
                icon = Icons.Default.Settings,
                iconPosition = IconPosition.TOP
            )

            // Usando variantes predefinidas
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    onClick = { },
                    text = "Primario",
                    modifier = Modifier.weight(1f)
                )

                SecondaryButton(
                    onClick = { },
                    text = "Secundario",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}