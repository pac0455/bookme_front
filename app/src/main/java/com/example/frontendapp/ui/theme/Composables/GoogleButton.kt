package com.example.frontendapp.ui.theme.Composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R
import com.example.frontendapp.auth.GoogleAuthUiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GoogleButton(
    context: Context = LocalContext.current,
    onGoogleTokenReceived: (String) -> Unit,
    onError: (Throwable) -> Unit
) {
    Button(
        onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                GoogleAuthUiClient.launchSignIn(
                    context = context,
                    onSuccess = { idToken ->
                        Log.d("GoogleButton", "Token recibido: $idToken")
                        onGoogleTokenReceived(idToken)  // <- ahora sí está definido
                    },
                    onError = { error ->
                        Log.e("GoogleButton", "Error al hacer login con Google", error)
                        onError(error)  // <- ahora sí está definido
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_googleiconcolor),
            contentDescription = "Iniciar sesión con Google",
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Google", color = Color.Black)
    }
}