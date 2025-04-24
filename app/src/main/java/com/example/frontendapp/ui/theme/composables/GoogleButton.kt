package com.example.frontendapp.ui.theme.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R
import com.example.frontendapp.auth.GoogleAuthUiClient
import com.example.frontendapp.ui.theme.FrontendappTheme


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Composable
fun GoogleButton(
    context: Context = LocalContext.current,
    onGoogleTokenReceived: (String)->Unit,
    onError: (Throwable)->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            // Sombra en el Box contenedor
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false,
                ambientColor = Color.LightGray.copy(alpha = 0.4f),
                spotColor = Color.LightGray.copy(alpha = 0.6f)
            )
    ) {
        Button(
            onClick = {
                onClick(context)
            },
            modifier = Modifier
                .matchParentSize(), // ocupa todo el Box
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp), // Sin sombra interna
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_googleiconcolor),
                contentDescription = "Iniciar sesión con Google",
                tint = Color.Unspecified
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
        GoogleButton(onGoogleTokenReceived = {}, onError = {})
    }
}
