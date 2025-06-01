package com.example.frontendapp.ui.theme.composables.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.Principal_variacion6
import com.example.frontendapp.ui.theme.composables.CustomSeachBar

@Composable
fun HeaderSeccion(
    titulo: String = "Servicios",
    modifier: Modifier = Modifier,
    mostrarVerTodo: Boolean = false,
    onVerTodoClick: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val headerHeight = screenHeight * 0.25f
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado visual
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .clip(RoundedCornerShape(bottomEnd = 40.dp, bottomStart = 40.dp))
                .background(Principal_variacion3)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // Barra de búsqueda
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(68.dp)
                .offset(y = (-32).dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(Principal_variacion6, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomSeachBar(
                query = query,
                backgroundColor = Color.Transparent,
                onQueryChange = { query = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .focusRequester(focusRequester)
            )
            IconButton(
                onClick = { /* TODO: acción filtro */ },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .shadow(1.dp, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "Filtro",
                    tint = Color.Black.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeaderSeccion() {
    MaterialTheme {
        HeaderSeccion(mostrarVerTodo = true, onVerTodoClick = { /* Acción aquí */ })
    }
}