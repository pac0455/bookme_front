package com.example.frontendapp.ui.theme.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R
import com.example.frontendapp.GoogleAuthUiClient
import com.example.frontendapp.ui.theme.FrontendappTheme


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Composable
fun GoogleButton(
    context: Context = LocalContext.current,
    widthFraction: Float = 0.8f // Por defecto, 80% del ancho del Box
) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .border(1.dp, Color.Black, RectangleShape)
            .fillMaxWidth(widthFraction) // Ancho del Box




            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                spotColor = Color.Black
            )
    ) {
        Button(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    GoogleAuthUiClient.launchSignIn(
                        context = context,
                        onSuccess = { idToken -> onGoogleTokenReceived(idToken) },
                        onError = { error -> onError(Exception("algo ha fallado")) }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth(1f) // Ancho del botón al porcentaje especificado
                .height(40.dp), // Altura específica del botón

            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp),

        ) {
            Icon(
                modifier = Modifier.padding(6.dp),
                painter = painterResource(id = R.drawable.ic_googleiconcolor),
                contentDescription = "Iniciar sesión con Google",
                tint = Color.Unspecified,

            )
            Spacer(Modifier.width(8.dp))
            Text("Google", color = Color.Black)
        }
    }
}

fun onClick(context: Context){
    CoroutineScope(Dispatchers.Main).launch {
        GoogleAuthUiClient.launchSignIn(
            context = context,
            onSuccess = { idToken ->
                onGoogleTokenReceived(idToken)  // <- ahora sí está definido
            },
            onError = { error ->
                onError(error)  // <- ahora sí está definido
            }
        )
    }
}

fun onGoogleTokenReceived(idToken: String) {
    Log.d("GoogleAuth", "Token recibido correctamente: $idToken")
}

fun onError(error: Throwable) {
    Log.e("GoogleAuth", "Error al iniciar sesión con Google", error)
}

@Preview(showBackground = true, backgroundColor = 0xFFE0E0E0)
@Composable
fun GoogleButtonPreview() {
    FrontendappTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Red,

        ) {
            Box(
                modifier =Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                GoogleButton(LocalContext.current)
            }


        }
    }
}


//Prueba
@Composable
fun BOxWithShadow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        //sombra basica
        //modifier = modifier
        /*.shadow(
            elevation = 50.dp, // 👈 sombra potente
            shape = RoundedCornerShape(16.dp),
            clip = false, // deja que la sombra sobresalga,
            spotColor = Color.Blue
        )*/
        modifier = modifier
            .graphicsLayer {
                // Aumenta el blur y spread visual
                shadowElevation = 32f // más grande que el .shadow normal
                shape = RoundedCornerShape(0.dp)
                clip = false // que sobresalga
            }
            .drawBehind {
                // Sombra personalizada tipo "spread"
                val shadowColor = Color.Black.copy(alpha = 0.4f)
                val cornerRadius = 16.dp.toPx()
                val shadowOffset = 8.dp.toPx()

                drawRect(
                    color = shadowColor,
                    topLeft = androidx.compose.ui.geometry.Offset(shadowOffset, shadowOffset),
                    size = size,
                )
            }
            .width(50.dp)
            .height(50.dp)
            .background(Color.White, shape = RectangleShape)
    ) {
        content()
    }
}
