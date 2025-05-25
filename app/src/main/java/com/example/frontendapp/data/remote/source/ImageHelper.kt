package com.example.frontendapp.data.remote.source

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object ImageHelper {

    fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun prepareImagePart(context: Context, uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null

        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { inputStream.copyTo(it) }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("Url", file.name, requestFile)

    }

    fun fetchImageFromUrl(
        context: Context,
        url: String,
        onLoading: () -> Unit,
        onSuccess: (android.graphics.Bitmap) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                onLoading()

                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false) // Necesario para obtener Bitmap
                    .build()

                val result = imageLoader.execute(request)

                if (result is SuccessResult) {
                    val drawable = result.drawable
                    val bitmap = BitmapFactory.decodeStream(
                        context.contentResolver.openInputStream(Uri.parse(url))
                    ) ?: throw Exception("No se pudo convertir a bitmap")

                    CoroutineScope(Dispatchers.Main).launch {
                        onSuccess(bitmap)
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        onError("No se pudo obtener la imagen desde $url")
                    }
                }

            } catch (e: Exception) {
                Log.e("ImageHelper", "Error al cargar imagen", e)
                CoroutineScope(Dispatchers.Main).launch {
                    onError("Error al cargar imagen: ${e.localizedMessage}")
                }
            }
        }
    }
    fun prepareSingleImagePart(context: Context, imageUri: Uri): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(imageUri) ?: return null

        // Crear un archivo temporal para copiar la imagen
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

        // El nombre del campo debe ser "Url" para que el backend lo reciba como IFormFile Url
        return MultipartBody.Part.createFormData("Url", tempFile.name, requestBody)
    }

}
