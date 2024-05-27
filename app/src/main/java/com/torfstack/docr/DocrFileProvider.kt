package com.torfstack.docr

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.torfstack.docr.persistence.ImageEntity
import com.torfstack.docr.util.bytes

class DocrFileProvider : FileProvider(R.xml.paths) {

    fun insertToCache(context: Context, image: ImageEntity): Uri {
        context.cacheDir.resolve("${image.uid}.jpg").writeBytes(image.data.bytes())
        return getUriForFile(
            context,
            context.packageName + ".fileprovider",
            context.cacheDir.resolve("${image.uid}.jpg").absoluteFile
        )
    }

    fun clearCache(context: Context) {
        context.cacheDir.deleteRecursively()
    }
}
