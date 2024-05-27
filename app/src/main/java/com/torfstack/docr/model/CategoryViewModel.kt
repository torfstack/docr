package com.torfstack.docr.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase

class CategoryViewModel(context: Context) : ViewModel() {
    val uiState: LiveData<List<CategoryEntity>> =
        DocrDatabase.getInstance(context)
            .dao()
            .getAllCategories()

    init {
        Log.i("CategoryViewModel", "initialized view model")
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryViewModel(context) as T
        }
    }
}
