package com.torfstack.docr.model

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.torfstack.docr.DocrFileManager
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.persistence.ImageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CategoryDetailViewModel(context: Context, categoryId: String) :
    ViewModel(viewModelScope = CoroutineScope(Dispatchers.IO)) {

    val category: LiveData<CategoryEntity> =
        DocrDatabase.getInstance(context)
            .dao()
            .getCategoryById(categoryId)

    val images: LiveData<List<ImageEntity>> =
        DocrDatabase.getInstance(context)
            .dao()
            .getImagesForCategory(categoryId)

    init {
        Log.i("CategoryDetailViewModel", "initialized view model")
    }

    suspend fun deleteCategory(context: Context, category: CategoryEntity) {
        DocrDatabase.getInstance(context)
            .dao()
            .deleteCategory(category)
        DocrFileManager().removeCategory(context, category)
    }

    suspend fun updateCategory(context: Context, category: CategoryEntity) {
        DocrDatabase.getInstance(context)
            .dao()
            .updateCategory(category)
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
            DocrFileManager().insertToCache(context, image)
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

    class Factory(private val context: Context, private val categoryId: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryDetailViewModel(
                context,
                categoryId,
            ) as T
        }
    }
}