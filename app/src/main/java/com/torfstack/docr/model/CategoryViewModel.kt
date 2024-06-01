package com.torfstack.docr.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CategoryViewModel(context: Context) :
    ViewModel(viewModelScope = CoroutineScope(Dispatchers.IO)) {

    val uiState: LiveData<List<CategoryEntity>> =
        DocrDatabase.getInstance(context)
            .dao()
            .getAllCategories()

    init {
        Log.i("CategoryViewModel", "initialized view model")
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CategoryViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
