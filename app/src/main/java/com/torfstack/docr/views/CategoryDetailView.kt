package com.torfstack.docr.views

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.torfstack.docr.model.CategoryViewModel
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.util.toImageBitmap

@Composable
fun CategoryDetailView(
    categoryId: String,
    viewModel: CategoryViewModel,
) {
    val category by viewModel.uiState.collectAsStateWithLifecycle()
    category.find { it.uid == categoryId }?.let {
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
