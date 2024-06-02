package com.torfstack.docr.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.torfstack.docr.model.CategoryDetailViewModel
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailView(
    navController: NavController,
    viewModel: CategoryDetailViewModel,
) {
    val context = LocalContext.current
    val category by viewModel.category.observeAsState()
    val images by viewModel.images.observeAsState()

    val openDeleteDialog = remember { mutableStateOf(false) }

    category?.let {
        var nameText by remember { mutableStateOf(it.name) }
        var descriptionText by remember { mutableStateOf(it.description) }

        val didChange by remember {
            derivedStateOf {
                nameText != it.name || descriptionText != it.description
            }
        }

        DocRTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = it.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        val newCategory = it.copy(
                                            name = nameText.trim(),
                                            description = descriptionText.trim(),
                                            lastUpdated = System.currentTimeMillis(),
                                        )
                                        viewModel.updateCategory(context, newCategory)
                                    }
                                    navController.popBackStack()
                                },
                                enabled = didChange
                            )
                            {
                                if (didChange) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = MaterialTheme.shapes.small,
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Done,
                                            contentDescription = "Save",
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                } else {
                                    Icon(
                                        Icons.Default.Done,
                                        contentDescription = "Save",
                                    )
                                }
                            }
                            IconButton(onClick = {
                                viewModel.viewModelScope.launch {
                                    openDeleteDialog.value = true
                                }
                            })
                            {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                            IconButton(onClick = {
                                viewModel.viewModelScope.launch {
                                    viewModel.shareCategory(context, it)
                                }
                            })
                            {
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
                        .verticalScroll(
                            enabled = true,
                            state = rememberScrollState(0)
                        )
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
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
                            enabled = false,
                            leadingIcon = {
                                Icon(Icons.Default.Star, contentDescription = "Category ID")
                            }
                        )

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                            label = { Text("Title") },
                            value = nameText,
                            onValueChange = {
                                nameText = it
                            })
                    }

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Images",
                            style = Typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 96.dp, end = 96.dp)
                        ) {
                            images?.forEach {
                                Image(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(8.dp)
                                        .clip(MaterialTheme.shapes.small),
                                    bitmap = it.downscaledData(context),
                                    contentDescription = "Category Image"
                                )
                            }
                        }
                    }
                }
            }
            when {
                openDeleteDialog.value -> {
                    DeleteDialog(it, viewModel, openDeleteDialog, navController)
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(
    entity: CategoryEntity,
    viewModel: CategoryDetailViewModel,
    open: MutableState<Boolean>,
    navController: NavController
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { open.value = false },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.viewModelScope.launch {
                        open.value = false
                        viewModel.deleteCategory(context, entity)
                    }
                    navController.popBackStack()
                },
            ) {
                Text("Delete")
            }
        },
        title = { Text("Delete document") },
        text = { Text("Are you sure you want to delete this document?") },
        icon = { Icon(Icons.Default.Delete, contentDescription = "Delete icon") },
    )
}