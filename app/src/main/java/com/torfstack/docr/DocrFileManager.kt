package com.torfstack.docr

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.torfstack.docr.crypto.DocrCrypto
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.ImageEntity

class DocrFileManager : FileProvider(R.xml.paths) {

    fun insertToCache(context: Context, image: ImageEntity): Uri {
        val bytes = getFromFiles(context, image)
        context.cacheDir.resolve("${image.uid}.jpg").writeBytes(bytes)
        return getUriForFile(
            context,
            context.packageName + ".fileprovider",
            context.cacheDir.resolve("${image.uid}.jpg").absoluteFile
        )
    }

    fun clearCache(context: Context) {
        context.cacheDir.deleteRecursively()
    }

    fun insertToFiles(
        context: Context,
        bytes: ByteArray,
        category: String,
        name: String
    ): String {
        val file = context.filesDir.resolve("${category}/$name.jpg")
        file.parentFile?.mkdirs()
        file.writeBytes(DocrCrypto.encrypt(bytes))
        return file.absolutePath
    }

    fun insertDownscaledToFiles(
        context: Context,
        bytes: ByteArray,
        category: String,
        name: String
    ): String {
        val file = context.filesDir.resolve("${category}/$name-downscaled.jpg")
        file.parentFile?.mkdirs()
        file.writeBytes(DocrCrypto.encrypt(bytes))
        return file.absolutePath
    }

    fun insertThumbnailToFiles(
        context: Context,
        bytes: ByteArray,
        category: String,
    ): String {
        val thumbnailFile = context.filesDir.resolve("${category}/thumbnail.jpg")
        thumbnailFile.parentFile?.mkdirs()
        thumbnailFile.writeBytes(DocrCrypto.encrypt(bytes))
        return thumbnailFile.absolutePath
    }

    fun getFromFiles(context: Context, image: ImageEntity): ByteArray {
        return DocrCrypto.decrypt(
            context.filesDir.resolve("${image.category}/${image.uid}.jpg").readBytes()
        )
    }

    fun getDownscaledFromFiles(context: Context, image: ImageEntity): ByteArray {
        return DocrCrypto.decrypt(
            context.filesDir.resolve("${image.category}/${image.uid}-downscaled.jpg").readBytes()
        )
    }

    fun getThumbnailFromFiles(context: Context, category: CategoryEntity): ByteArray {
        return DocrCrypto.decrypt(
            context.filesDir.resolve("${category.uid}/thumbnail.jpg").readBytes()
        )
    }

    fun removeCategory(context: Context, category: CategoryEntity) {
        context.filesDir.resolve(category.uid).deleteRecursively()
    }
}
