package com.example.frontendapp.data.helper

import android.content.Context
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.request.CachePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageCacheUtils {

    /**
     * Limpia el cache de una imagen específica
     */
    @OptIn(ExperimentalCoilApi::class)
    suspend fun clearImageCache(context: Context, imageUrl: String) {
        withContext(Dispatchers.IO) {
            val imageLoader = ImageLoader(context)

            // Limpiar de memoria
            imageLoader.memoryCache?.remove(MemoryCache.Key(imageUrl))

            // Limpiar de disco
            imageLoader.diskCache?.remove(imageUrl)
        }
    }

    /**
     * Precarga una imagen en cache
     */
    suspend fun preloadImage(context: Context, imageUrl: String) {
        withContext(Dispatchers.IO) {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()

            imageLoader.execute(request)
        }
    }

    /**
     * Genera un cache key único basado en parámetros
     */
    fun generateCacheKey(
        id: Int?,
        version: String? = null,
        timestamp: Long? = null
    ): String {
        return buildString {
            append(id?.toString() ?: "no_id")
            if (version != null) {
                append("_v$version")
            }
            if (timestamp != null) {
                append("_t$timestamp")
            }
        }
    }

    /**
     * Verifica si una imagen está en cache
     */
    @OptIn(ExperimentalCoilApi::class)
    suspend fun isImageCached(context: Context, cacheKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            val imageLoader = ImageLoader(context)
            imageLoader.memoryCache?.get(MemoryCache.Key(cacheKey)) != null ||
                    imageLoader.diskCache?.get(cacheKey) != null
        }
    }
}