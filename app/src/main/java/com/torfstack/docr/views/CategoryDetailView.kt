package com.torfstack.docr.views

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.util.toImageBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun CategoryDetailView(
    navController: NavController,
    categoryId: String,
    viewModel: CategoryDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(context) {
        viewModel.loadCategory(categoryId)
    }
    val category by viewModel.uiState.collectAsStateWithLifecycle()
    category?.let {
        DocRTheme {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {
                var nameText by remember { mutableStateOf(it.name) }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Name") },
                    value = nameText,
                    onValueChange = { nameText = it })

                var descriptionText by remember { mutableStateOf(it.description) }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Description") },
                    value = descriptionText,
                    onValueChange = { descriptionText = it })

                TextField(
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = "ID: ${it.uid}",
                    onValueChange = {}
                )

                Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    bitmap = it.thumbnail.toImageBitmap(),
                    contentDescription = "Category Image"
                )

                ElevatedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    enabled = false,
                    onClick = { /*TODO*/ }
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

class CategoryDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState: MutableStateFlow<CategoryEntity?> = MutableStateFlow(null)
    val uiState: StateFlow<CategoryEntity?> = _uiState.asStateFlow()

    // TODO: Use Flow<CategoryEntity> instead?
    fun loadCategory(categoryId: String) {
        viewModelScope.launch {
            val category = DocrDatabase.getInstance(this@CategoryDetailViewModel.getApplication())
                .dao().getCategoryById(categoryId)
            _uiState.update { category }
        }
    }
}
