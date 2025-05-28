package com.example.frontendapp.ui.theme.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.FrontendappTheme

@Composable
fun CustomMultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String? = null, // Añadir el parámetro para el mensaje de error
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    maxLines: Int = 5,
    enabled: Boolean = true
) {
    val isError = errorMessage == null
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            label = { Text(label) },
            singleLine = false,
            minLines = minLines,
            maxLines = maxLines,
            enabled = enabled,
            visualTransformation = VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if(isError) Color.Black else Color.Red ,
                unfocusedIndicatorColor = if(isError) Color.Black else Color.Red,
                disabledIndicatorColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )

        // Mostrar el mensaje de error si existe
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp) // Ajustar el padding según sea necesario
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomMultilineTextFieldPreview() {
    val textState = remember { mutableStateOf("") }

    FrontendappTheme {
        Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            CustomMultilineTextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = "Descripción"
            )
        }
    }
}

