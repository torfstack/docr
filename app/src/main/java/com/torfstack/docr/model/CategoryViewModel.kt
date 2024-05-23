package com.torfstack.docr.model

import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.torfstack.docr.DocrFileProvider
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    val uiState: LiveData<List<CategoryEntity>> =
        DocrDatabase.getInstance(application.applicationContext)
            .dao()
            .getAllCategories()

    init {
        Log.i("CategoryViewModel", "initialized view model")
    }

    suspend fun deleteCategory(context: Context, category: CategoryEntity) {
        DocrDatabase.getInstance(context)
            .dao()
            .deleteCategory(category)
    }

    suspend fun shareCategory(context: Context, category: CategoryEntity) {
        val images = DocrDatabase.getInstance(context)
            .dao()
            .getImagesForCategory(category.uid)

        if (images.isEmpty()) {
            Log.i("Share", "No images found for category ${category.uid}")
            return
        }

        val share = if (images.size > 1) {
            Intent(Intent.ACTION_SEND_MULTIPLE)
        } else {
            Intent(Intent.ACTION_SEND)
        }
        share.setType("image/jpeg")

        val uris = images.map { image ->
            DocrFileProvider().insertToCache(context, image)
        }

        if (uris.size > 1) {
            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            share.clipData = ClipData.newRawUri(null, uris[0])
            share.clipData!!.addItem(ClipData.Item(uris[1]))
        } else {
            share.putExtra(Intent.EXTRA_STREAM, uris[0])
            share.clipData = ClipData.newRawUri(null, uris[0])
        }
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        ContextCompat.startActivity(context, Intent.createChooser(share, "Share Image"), null)
    }
}
