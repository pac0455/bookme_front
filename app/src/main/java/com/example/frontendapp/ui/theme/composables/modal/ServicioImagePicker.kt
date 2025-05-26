package com.example.frontendapp.ui.theme.composables.modal

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.animation.Transformation
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest


@Composable
fun ServicioImagePicker(
    id: Int? = null,
    showIconEdit: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    iconEditSize: Dp = 24.dp,
    imageUrl: String? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    iconEdit: ImageVector = Icons.Default.Edit,
    icon: ImageVector = Icons.Default.PhotoCamera,
    iconSize: Dp = 48.dp,
    iconAlignment: Alignment = Alignment.Center,
    contentAlignment: Alignment = Alignment.Center,
    backgroundColor: Color = Color.LightGray,
    clickable: Boolean = false,
    borderColor: Color = Color.Transparent,  // Nuevo: color borde
    borderWidth: Dp = 0.dp,                  // Nuevo: grosor borde
    onSuccess: (() -> Unit)? = null,
    onImageSelected: (Uri?) -> Unit = {},
    imagenUriExterna: Uri? = null,
) {
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

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
            onSuccess?.invoke()
        }
    }

    val imageDto = remember(id, imageUrl) {
        ImageDto(
            url = when {
                !imageUrl.isNullOrBlank() -> imageUrl
                id != null -> "http://192.168.18.3:5000/api/servicio/$id/imagen"
                else -> null
            },
            cacheKey = id?.toString() + "_" + System.currentTimeMillis()
        )
    }

    val imageRequest = remember(imageDto) {
        ImageRequest.Builder(context)
            .data(imageDto.url)
            .memoryCacheKey(imageDto.cacheKey)
            .build()
    }

    val painter = rememberAsyncImagePainter(imageRequest)
    val painterState = painter.state

    LaunchedEffect(painterState) {
        if (painterState is AsyncImagePainter.State.Success) {
            Log.d("ServicioImagePicker", "Imagen remota cargada correctamente")
            onSuccess?.invoke()
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .let { mod ->
                if (clickable) mod.clickable {
                    Log.d("ServicioImagePicker", "Click para seleccionar imagen local")
                    launcher.launch("image/*")
                } else mod
            }
    ) {
        // Caja para la imagen con borde y clip
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(backgroundColor)
                .then(
                    if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape)
                    else Modifier
                ),

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
                imageDto.url != null -> {
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
                        tint = Color.Black,
                        modifier = Modifier
                            .size(iconSize)
                            .align(iconAlignment)
                    )
                }
            }
        }

        // Icono superpuesto arriba derecha sin clip
        Box(
            modifier = Modifier
                .size(size)
                .align(Alignment.TopEnd),
            contentAlignment = Alignment.TopEnd
        ) {
            if (showIconEdit) {
                Icon(
                    imageVector = iconEdit,
                    contentDescription = "Editar imagen",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(iconEditSize)
                        .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(50))
                        .padding(4.dp)
                )
            }
        }
    }
}
@Preview(showBackground = true, name = "Custom Style")
@Composable
fun ServicioImagePickerPreview_CustomStyle() {
    ServicioImagePicker(
        modifier = Modifier.size(200.dp),
        shape = RoundedCornerShape(100.dp),
        backgroundColor = Color(0xFFE3F2FD),
        iconSize = 64.dp,
        icon = Icons.Default.Edit,
        clickable = true,
        showIconEdit = true,
        borderColor = Color.White,
        borderWidth = 2.dp
    )
}
data class ImageDto(
    val url: String?,
    val cacheKey: String
)