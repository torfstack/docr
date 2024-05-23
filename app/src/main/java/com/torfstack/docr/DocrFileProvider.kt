package com.torfstack.docr

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.torfstack.docr.crypto.DocrCrypto
import com.torfstack.docr.persistence.ImageEntity

class DocrFileProvider : FileProvider(R.xml.paths) {

    fun insertToCache(context: Context, image: ImageEntity): Uri {
        context.cacheDir.resolve("${image.uid}.jpg").writeBytes(DocrCrypto.decrypt(image.data))
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
