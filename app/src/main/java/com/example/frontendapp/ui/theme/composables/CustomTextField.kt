package com.example.frontendapp.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    leadingIcon: ImageVector? = null,
    label: String,
    value: String,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    errorMessage: String? = null,
    placeholder: String? = null,
    maxLines: Int = 1,
    supportingText: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isError = errorMessage != null

    // Usar leadingIcon si se proporciona, sino usar icon para compatibilidad
    val actualLeadingIcon = leadingIcon ?: icon

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Filtrar caracteres no deseados
                if (!newValue.contains('\n') && !newValue.contains('\t')) {
                    onValueChange(newValue)
                }
            },
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
            placeholder = placeholder?.let { placeholderText ->
                {
                    Text(
                        text = placeholderText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            },
            leadingIcon = actualLeadingIcon?.let { iconVector ->
                {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = "Icono de $label",
                        tint = when {
                            isError -> MaterialTheme.colorScheme.error
                            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff else
                                Icons.Default.Visibility,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña" else
                                "Mostrar contraseña",
                            tint = when {
                                isError -> MaterialTheme.colorScheme.error
                                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            } else null,
            enabled = enabled,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
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
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error
            ),
            supportingText = if (isError || supportingText != null) {
                {
                    Text(
                        text = errorMessage ?: supportingText ?: "",
                        color = if (isError)
                            MaterialTheme.colorScheme.error else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else null
        )
    }
}

@Composable
fun CustomMultilineTextField(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    errorMessage: String? = null,
    placeholder: String? = null,
    minLines: Int = 3,
    maxLines: Int = 6,
    supportingText: String? = null
) {
    CustomTextField(
        modifier = modifier,
        leadingIcon = leadingIcon,
        label = label,
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        errorMessage = errorMessage,
        placeholder = placeholder,
        maxLines = maxLines,
        supportingText = supportingText
    )
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreview() {
    FrontendappTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var textValue by remember { mutableStateOf("") }
            var passwordValue by remember { mutableStateOf("") }
            var emailValue by remember { mutableStateOf("") }
            var descriptionValue by remember { mutableStateOf("") }

            // Campo de texto normal
            CustomTextField(
                leadingIcon = Icons.Default.Person,
                label = "Nombre completo",
                value = textValue,
                onValueChange = { textValue = it },
                placeholder = "Ingresa tu nombre"
            )

            // Campo de email con error
            CustomTextField(
                leadingIcon = Icons.Default.Email,
                label = "Correo electrónico",
                value = emailValue,
                onValueChange = { emailValue = it },
                errorMessage = "Formato de email inválido",
                placeholder = "ejemplo@correo.com"
            )

            // Campo de contraseña
            CustomTextField(
                leadingIcon = Icons.Default.Lock,
                label = "Contraseña",
                value = passwordValue,
                onValueChange = { passwordValue = it },
                isPassword = true,
                placeholder = "Mínimo 8 caracteres",
                supportingText = "Debe contener al menos 8 caracteres"
            )

            // Campo multilínea
            CustomMultilineTextField(
                leadingIcon = Icons.Default.Description,
                label = "Descripción",
                value = descriptionValue,
                onValueChange = { descriptionValue = it },
                placeholder = "Describe tu negocio...",
                supportingText = "Máximo 500 caracteres"
            )

            // Campo deshabilitado
            CustomTextField(
                leadingIcon = Icons.Default.LocationOn,
                label = "Ubicación",
                value = "Calle Principal 123",
                onValueChange = { },
                enabled = false,
                supportingText = "Selecciona en el mapa para cambiar"
            )
        }
    }
}