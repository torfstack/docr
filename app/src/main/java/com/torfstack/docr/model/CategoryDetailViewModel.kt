package com.torfstack.docr.model

import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.torfstack.docr.DocrFileProvider
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.persistence.ImageEntity

class CategoryDetailViewModel(application: Application, categoryId: String) :
    AndroidViewModel(application) {

    val category: LiveData<CategoryEntity> =
        DocrDatabase.getInstance(application.applicationContext)
            .liveDataDao()
            .getCategoryById(categoryId)

    val images: LiveData<List<ImageEntity>> =
        DocrDatabase.getInstance(application.applicationContext)
            .liveDataDao()
            .getImagesForCategory(categoryId)

    suspend fun deleteCategory(context: Context, category: CategoryEntity) {
        DocrDatabase.getInstance(context)
            .dao()
            .deleteCategory(category)
    }

    suspend fun shareCategory(context: Context, category: CategoryEntity) {
        val images = images.value ?: emptyList()

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
            share.clipData = ClipData.newRawUri(null, uris[1])
            share.clipData!!.addItem(ClipData.Item(uris[0]))
        } else {
            share.putExtra(Intent.EXTRA_STREAM, uris[0])
            share.clipData = ClipData.newRawUri(null, uris[0])
        }
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        ContextCompat.startActivity(context, Intent.createChooser(share, "Share Image"), null)
    }

    class Factory(private val application: Application, private val categoryId: String) :
        ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryDetailViewModel(
                application,
                categoryId,
            ) as T
        }
    }
}