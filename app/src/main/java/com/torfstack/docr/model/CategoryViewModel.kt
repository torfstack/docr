package com.torfstack.docr.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(listOf<CategoryEntity>())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                DocrDatabase.getInstance(application.applicationContext)
                    .dao()
                    .getAllCategories()
            }
        }
    }

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            _uiState.update {
                it + category
            }
        }
    }
}
