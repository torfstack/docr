package com.torfstack.docr.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun InputStream.toByteArray(): ByteArray {
    val buf = ByteArrayOutputStream()
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var n: Int
    while (this.read(buffer).also { n = it } != -1) {
        buf.write(buffer, 0, n)
    }
    return buf.toByteArray()
}

fun ByteArray.toImageBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
    return bitmap.asImageBitmap()
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Bitmap.thumbnail(): Bitmap {
    val aspectRatio = width.toFloat() / height.toFloat()
    val thumbnailWidth = 128
    val thumbnailHeight = (thumbnailWidth / aspectRatio).toInt()
    return Bitmap.createScaledBitmap(this, thumbnailWidth, thumbnailHeight, false)
}

fun thumbnail(bytes: ByteArray): ByteArray {
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val thumbnail = bitmap.thumbnail()
    val stream = ByteArrayOutputStream()
    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

