package com.example.frontendapp.ui.theme.composables

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.navigation.NavigationItem

@Composable
fun Link(
    text :String = "",
    destino: String,
    navController: NavController
){
    val context = LocalContext.current

    Button(
        onClick = {
            navController.navigate(destino)
        }
    ) {

    }
}
@Preview
@Composable
fun LinkPreview(){
    FrontendappTheme {
        Scaffold { innerpad ->
            Box(Modifier.padding(innerpad)){
                Link(
                    navController = rememberNavController(),
                    destino = NavigationItem.MAIN.route,
                    text = "asfdadsf"
                )
            }
        }
    }
}