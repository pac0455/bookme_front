package com.example.frontendapp.ui.theme.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSelector(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    errorMessage: String? = null,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    placeholder: String = "Selecciona una opción"
) {
    var expanded by remember { mutableStateOf(false) }
    val isError = errorMessage != null

    // Animación para el icono de flecha
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "ArrowRotation"
    )

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (enabled) expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = selectedOption.ifEmpty { "" },
                onValueChange = { },
                readOnly = true,
                enabled = enabled,
                label = {
                    Text(
                        text = label,
                        color = when {
                            isError -> MaterialTheme.colorScheme.error
                            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = leadingIcon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Icono de $label",
                            tint = when {
                                isError -> MaterialTheme.colorScheme.error
                                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (enabled) expanded = !expanded
                        },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Contraer opciones" else "Expandir opciones",
                            modifier = Modifier.rotate(arrowRotation),
                            tint = when {
                                isError -> MaterialTheme.colorScheme.error
                                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .clickable(enabled = enabled) {
                        if (enabled) expanded = !expanded
                    },
                isError = isError,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isError)
                        MaterialTheme.colorScheme.error else
                        MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (isError)
                        MaterialTheme.colorScheme.error else
                        MaterialTheme.colorScheme.outline,
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedLabelColor = if (isError)
                        MaterialTheme.colorScheme.error else
                        MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = if (isError)
                        MaterialTheme.colorScheme.error else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        // Mensaje de error
        if (isError) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomSelectorPreview() {
    FrontendappTheme {
        val options = listOf("Gimnasio", "Spa", "Restaurante", "Peluquería", "Clínica")
        var selectedOption by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector normal
            CustomSelector(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                label = "Categoría del negocio",
                leadingIcon = Icons.Default.Category
            )

            // Selector con error
            CustomSelector(
                options = options,
                selectedOption = "",
                onOptionSelected = { },
                label = "Campo obligatorio",
                errorMessage = "Debes seleccionar una categoría",
                leadingIcon = Icons.Default.Error
            )

            // Selector deshabilitado
            CustomSelector(
                options = options,
                selectedOption = "Gimnasio",
                onOptionSelected = { },
                label = "Campo deshabilitado",
                enabled = false,
                leadingIcon = Icons.Default.Lock
            )
        }
    }
}