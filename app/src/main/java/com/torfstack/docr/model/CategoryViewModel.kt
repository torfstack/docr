package com.torfstack.docr.model

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.util.imageCollection

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
            val values = ContentValues().apply {
                val replaced = category.name.replace(" ", "_")
                put(MediaStore.Images.Media.DISPLAY_NAME, "$replaced.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, category.created / 1000)
            }
            val uri = context.contentResolver.insert(imageCollection(), values)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { os ->
                    os.write(image.data)
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            } ?: {
                Log.i("Share", "Uri is null")
            }
            uri
        }

        if (uris.size > 1) {
            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
        } else {
            share.putExtra(Intent.EXTRA_STREAM, uris[0])
        }

        ContextCompat.startActivity(context, Intent.createChooser(share, "Share Image"), null)
    }
}
