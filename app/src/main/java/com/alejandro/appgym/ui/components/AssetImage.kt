package com.alejandro.appgym.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.alejandro.appgym.ui.theme.Surface2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caché de imágenes en memoria limitada a 1/6 de la memoria disponible.
 * Evita añadir una librería externa de carga de imágenes.
 */
private object BitmapCache {
    private val maxKb = (Runtime.getRuntime().maxMemory() / 1024 / 6).toInt()
    val lru = object : LruCache<String, ImageBitmap>(maxKb) {
        override fun sizeOf(key: String, value: ImageBitmap): Int =
            (value.width * value.height * 4) / 1024
    }
}

private suspend fun loadAsset(context: Context, path: String): ImageBitmap? =
    withContext(Dispatchers.IO) {
        BitmapCache.lru.get(path)?.let { return@withContext it }
        runCatching {
            context.assets.open(path).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }.getOrNull()?.also { BitmapCache.lru.put(path, it) }
    }

/** Imagen cargada desde `assets/` con fundido de entrada. */
@Composable
fun AssetImage(
    asset: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Color = Surface2
) {
    val context = LocalContext.current
    var bitmap by remember(asset) { mutableStateOf<ImageBitmap?>(BitmapCache.lru.get(asset)) }

    LaunchedEffect(asset) {
        if (bitmap == null) bitmap = loadAsset(context, asset)
    }

    val alpha by animateFloatAsState(if (bitmap != null) 1f else 0f, label = "assetFade")

    Box(modifier.background(placeholder)) {
        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
            )
        }
    }
}
