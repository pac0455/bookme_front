package com.example.frontendapp.ui.theme.composables.modal

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter




@Composable
fun ServicioImagePicker(
    id: Int? = null,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    imageUrl: String? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    icon: ImageVector = Icons.Default.PhotoCamera,
    iconSize: Dp = 48.dp,
    iconAlignment: Alignment = Alignment.Center,
    contentAlignment: Alignment = Alignment.Center,
    backgroundColor: Color = Color.LightGray,
    clickable: Boolean = false,
    onSuccess: (() -> Unit)? = null, // ✅ NUEVO
    onImageSelected: (Uri?) -> Unit = {},
    imagenUriExterna: Uri? = null,
) {
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(imageUrl) {
        if (imageUrl != null) {
            Log.d("ServicioImagePicker", "Nueva imageUrl detectada: $imageUrl")
            imagenUri = null
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("ServicioImagePicker", "Imagen seleccionada desde dispositivo: ${uri?.toString()}")
        imagenUri = uri
        onImageSelected(uri)
        if (uri != null) {
            onSuccess?.invoke() // ✅ Notificamos carga exitosa de imagen local
        }
    }

    val resolvedImageUrl = when {
        !imageUrl.isNullOrBlank() -> imageUrl
        id != null -> "http://192.168.18.3:5000/api/servicio/$id/imagen"
        else -> null
    }

    val painter = rememberAsyncImagePainter(model = resolvedImageUrl)
    val painterState = painter.state

    // Notificar éxito de carga remota
    LaunchedEffect(painterState) {
        if (painterState is AsyncImagePainter.State.Success) {
            Log.d("ServicioImagePicker", "Imagen remota cargada correctamente")
            onSuccess?.invoke()
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .let { mod ->
                if (clickable) mod.clickable {
                    Log.d("ServicioImagePicker", "Click para seleccionar imagen local")
                    launcher.launch("image/*")
                } else mod
            },
        contentAlignment = contentAlignment
    ) {
        when {
            imagenUriExterna != null -> {
                Image(
                    painter = rememberAsyncImagePainter(imagenUriExterna),
                    contentDescription = "Imagen seleccionada local",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            imagenUri != null -> {
                Image(
                    painter = rememberAsyncImagePainter(imagenUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            resolvedImageUrl != null -> {
                Image(
                    painter = painter,
                    contentDescription = "Imagen remota",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (painterState is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp),
                        color = Color.Gray
                    )
                }
            }
            else -> {
                Icon(
                    imageVector = icon,
                    contentDescription = "Seleccionar imagen",
                    tint = if (imagenUri != null) Color.Black else Color.Red,
                    modifier = Modifier
                        .size(iconSize)
                        .align(iconAlignment)
                )
            }
        }
    }
}