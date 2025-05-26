package com.example.frontendapp.ui.theme.composables

import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    label: String,
    value: String,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = {
                if (!it.contains('\n') && !it.contains('\t')) {
                    onValueChange(it)
                }
            },
            label = { Text(label) },
            trailingIcon = {
                if (isPassword) {
                    val visibilityIcon =
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    val description =
                        if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = visibilityIcon,
                            contentDescription = description,
                            tint = Color.Black
                        )
                    }
                } else if (icon != null) {
                    Icon(
                        modifier = Modifier.scale(1.4f),
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.Black
                    )
                }
            },
            enabled = enabled,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .border(1.dp, if (errorMessage != null) Color.Red else Color.Transparent),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Black,
                containerColor = Color.Transparent
            ),
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 60.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreview() {
    FrontendappTheme {
        Surface(modifier = Modifier.fillMaxSize().background(Color.Gray)) {
            val example = remember { mutableStateOf("") }
            val errorMessage = remember { mutableStateOf("Este campo es obligatorio") } // Ejemplo de mensaje de error
            Box(
                modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                CustomTextField(
                    Modifier.fillMaxWidth(0.8f),
                    Icons.Default.AccountCircle,
                    "Nombre",
                    example.value,
                    onValueChange = { example.value = it },
                    isPassword = true,
                    keyboardOptions = KeyboardOptions.Default,
                    errorMessage = errorMessage.value // Pasar el mensaje de error
                )
            }
        }
    }
}
