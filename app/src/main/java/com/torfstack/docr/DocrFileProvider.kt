package com.torfstack.docr

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.torfstack.docr.persistence.ImageEntity

const val AUTHORITY = "com.torfstack.docr.fileprovider"

class DocrFileProvider : FileProvider(R.xml.paths) {

    fun insertToCache(context: Context, image: ImageEntity): Uri {
        context.cacheDir.resolve("${image.uid}.jpg").writeBytes(image.data)
        return getUriForFile(
            context,
            AUTHORITY,
            context.cacheDir.resolve("${image.uid}.jpg").absoluteFile
        )
    }

    fun clearCache(context: Context) {
        context.cacheDir.deleteRecursively()
    }
}
