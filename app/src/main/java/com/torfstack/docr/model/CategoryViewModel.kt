package com.torfstack.docr.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    val uiState: LiveData<List<CategoryEntity>> =
        DocrDatabase.getInstance(application.applicationContext)
            .liveDataDao()
            .getAllCategories()

    init {
        Log.i("CategoryViewModel", "initialized view model")
    }
}
