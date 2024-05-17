package com.torfstack.docr.views

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.torfstack.docr.model.CategoryViewModel
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.ui.theme.Typography
import com.torfstack.docr.util.toImageBitmap
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailView(
    navController: NavController,
    categoryId: String,
    viewModel: CategoryViewModel,
) {
    val context = LocalContext.current
    val category by viewModel.uiState.observeAsState()
    category?.find { it.uid == categoryId }?.let {
        DocRTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Document") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { shareDocument(context, it) }) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                ) {
                    TextField(
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 64.dp, top = 16.dp, bottom = 16.dp),
                        label = { Text("ID") },
                        value = it.uid,
                        onValueChange = {},
                        textStyle = Typography.bodySmall,
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = "Category ID")
                        }
                    )

                    Card {

                    }
                    var nameText by remember { mutableStateOf(it.name) }
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        label = { Text("Name") },
                        value = nameText,
                        onValueChange = {
                            nameText = it
                        })

                    var descriptionText by remember { mutableStateOf(it.description) }
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        label = { Text("Description") },
                        value = descriptionText,
                        onValueChange = {
                            descriptionText = it
                        })

                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp),
                        bitmap = it.thumbnail.toImageBitmap(),
                        contentDescription = "Category Image"
                    )

                    val didChange by remember {
                        derivedStateOf {
                            nameText != it.name || descriptionText != it.description
                        }
                    }
                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        enabled = didChange,
                        onClick = {
                            viewModel.viewModelScope.launch {
                                val newCategory = it.copy(
                                    name = nameText,
                                    description = descriptionText,
                                    lastUpdated = System.currentTimeMillis()
                                )
                                DocrDatabase.getInstance(context)
                                    .dao()
                                    .updateCategory(newCategory)
                            }
                            navController.navigate(Screen.Home.route)
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Save")
                        Text(
                            fontSize = Typography.bodyLarge.fontSize,
                            fontWeight = Typography.bodyLarge.fontWeight,
                            text = "Save"
                        )
                    }
                }
            }
        }
    }
}

fun shareDocument(context: Context, entity: CategoryEntity) {

}
