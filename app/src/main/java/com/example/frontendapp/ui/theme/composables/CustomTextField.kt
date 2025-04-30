package com.example.frontendapp.ui.theme.composables



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    icon: ImageVector,
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
    ) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = { Icon(
            modifier = Modifier.scale(1.4f),
            imageVector = icon,
            tint = Color.Black,
            contentDescription = label) },

        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier
            .fillMaxWidth(1f)
            .background(Color.Transparent),
        colors = TextFieldDefaults.textFieldColors(

            unfocusedIndicatorColor = Color.Black,
            containerColor = Color.Transparent
        ),

    )
}
@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreview(){
    FrontendappTheme {
        Surface(modifier = Modifier.fillMaxSize().background(Color.Gray)) {
            val example = remember { mutableStateOf("") }
            Box(
                modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.5f),
                contentAlignment = Alignment.Center
            ){
                CustomTextField(
                    Modifier.fillMaxWidth(0.8f),
                    Icons.Default.AccountCircle,
                    "Nombre",
                    example.value,
                    onValueChange =  { example.value = it },
                )
            }


        }
    }
}