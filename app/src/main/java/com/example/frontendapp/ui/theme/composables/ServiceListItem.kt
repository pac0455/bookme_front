package com.example.frontendapp.ui.theme.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R


@Composable
fun ServiceListItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), // Opcional: ocupa todo el espacio
        contentAlignment = Alignment.Center  // Opcional: centra el contenido
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp)) // 🔁 Bordes redondeados
                .background(Color.White)         // 🎨 Fondo (requerido si usas clip)
                .border(1.dp, Color.Black, RoundedCornerShape(12.dp)) // 🔲 Borde opcional

            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Service Image",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(8.dp) // espacio entre imagen y texto
            ) {
                val price = 100
                Text(
                    text = "Título del servicio",
                    modifier = Modifier.weight(2f),
                    maxLines = 1
                )
                Text(
                    text = "Precio: $price€",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun ServiceListItemPreview() {
    Scaffold {innerPadding ->
    var myPadding = innerPadding
        myPadding = PaddingValues(top = 8.dp, start = 1.dp)
    Box(modifier = Modifier.padding(myPadding)) {
            ServiceListItem()
        }
    }

}