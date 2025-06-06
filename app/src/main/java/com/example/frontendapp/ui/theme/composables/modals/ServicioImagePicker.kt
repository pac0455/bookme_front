package com.example.frontendapp.ui.theme.composables.modals

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File

@Composable
fun ServicioImagePicker(
    id: Int? = null,
    showIconEdit: Boolean = false,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    imageModifier: Modifier = Modifier.fillMaxSize(),
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
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    onSuccess: (() -> Unit)? = null,
    onImageSelected: (Uri?) -> Unit = {},
    imagenUriExterna: Uri? = null,
) {
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var showErrorIcon by remember { mutableStateOf(false) }
    // Almacenamos el URI temporal fuera para recuperarlo después
    var photoUri: Uri? = null

    LaunchedEffect(imageUrl) {
        if (imageUrl != null) {
            Log.d("ServicioImagePicker", "Nueva imageUrl detectada: $imageUrl")
            imagenUri = null
            showErrorIcon = false
        }
    }

    // Launcher para manejar el resultado del Intent Chooser
    val chooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uri = data?.data ?: photoUri
            Log.d("ServicioImagePicker", "Imagen seleccionada: ${uri?.toString()}")
            imagenUri = uri
            onImageSelected(uri)
            if (uri != null) onSuccess?.invoke()
        }
    }
    // Modificador para el elemento clickeable
    val clickableModifier = if (clickable) {
        modifier.clickable {
            // Crear archivo temporal seguro
            val photoFile = File.createTempFile("temp_photo", ".jpg", context.cacheDir)

            // Obtener Uri segura con FileProvider
            photoUri = FileProvider.getUriForFile(
                context,
                "com.example.frontendapp.fileprovider", // ¡Este valor debe coincidir exactamente!
                photoFile
            )

            // Intent de cámara con destino
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            // Intent de galería
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            // Chooser
            val chooserIntent = Intent.createChooser(galleryIntent, "Selecciona una fuente de imagen").apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
            }

            // Lanzar chooser
            chooserLauncher.launch(chooserIntent)
        }
    } else {
        modifier
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

    val imageRequest by produceState<ImageRequest?>(initialValue = null, imageDto) {
        value = imageDto.url?.let {
            ImageRequest.Builder(context)
                .data(it)
                //Concateno algo al cache key para que su unica cache no sea la ruta ya que entonces no detectara
                //cambios
                .memoryCacheKey(imageDto.cacheKey) // previene caché en memoria
                .diskCacheKey(imageDto.cacheKey)   // previene caché en disco
                .crossfade(true)
                .build()
        }
    }

    val painter = rememberAsyncImagePainter(
        model = imageRequest
    )

    val painterState = painter.state

    LaunchedEffect(painterState) {
        when (painterState) {
            is AsyncImagePainter.State.Success -> {
                Log.d("ServicioImagePicker", "Imagen remota cargada correctamente")
                showErrorIcon = false
                onSuccess?.invoke()
            }
            is AsyncImagePainter.State.Error -> {
                Log.e("ServicioImagePicker", "Error al cargar imagen: ${imageDto.url}")
                showErrorIcon = true
            }
            else -> Unit
        }
    }

    Box(
        modifier = clickableModifier
            .size(size)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(backgroundColor)
                .then(
                    if (borderWidth > 0.dp) Modifier.border(
                        borderWidth,
                        borderColor,
                        shape
                    ) else Modifier
                ),
            contentAlignment = contentAlignment
        ) {
            when {
                imagenUriExterna != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(imagenUriExterna),
                        contentDescription = "Imagen seleccionada externa",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                imagenUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(imagenUri),
                        contentDescription = "Imagen seleccionada local",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                !imageDto.url.isNullOrBlank() -> {
                    SubcomposeAsyncImage(
                        model = imageRequest,
                        contentDescription = "Imagen remota",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = Color.Gray
                                )
                            }
                        },
                        error = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Error al cargar imagen",
                                    tint = Color.Black,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                        },
                        success = {
                            onSuccess?.invoke()
                            Box(
                                modifier = imageModifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                            ) {
                                Image(
                                    painter = it.painter,
                                    contentDescription = "Imagen remota",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = contentScale
                                )
                            }
                        }
                    )
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

        if (showIconEdit) {
            Box(
                modifier = Modifier
                    .then(if (size != Dp.Unspecified) Modifier.size(size) else Modifier)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.TopEnd
            ) {
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
        borderWidth = 2.dp,
    )
}

data class ImageDto(
    val url: String?,
    val cacheKey: String
)
