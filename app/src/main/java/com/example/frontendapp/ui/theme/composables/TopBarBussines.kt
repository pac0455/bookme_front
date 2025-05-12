package com.example.frontendapp.ui.theme.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.Principal_variacion3

@Composable
fun TopBarBussines() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(Principal_variacion3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                contentScale = ContentScale.Inside,
                painter = painterResource(id = R.drawable.default_bussines_picture), // Asegúrate de tener esta imagen en res/drawable
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)

                    .border(1.dp, MaterialTheme.colorScheme.onPrimary)

            )
        }

        Column(Modifier.weight(2f)) {
            Text(text = "Bivenido", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
            Text(text = "Usuario", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
@Preview
@Composable
fun TopBarBussinessPrevew(){
    FrontendappTheme {
        Scaffold(
            topBar = { TopBarBussines() }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {

            }

        }

    }
}