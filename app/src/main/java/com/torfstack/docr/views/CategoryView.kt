package com.torfstack.docr.views

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.torfstack.docr.DocrFileManager
import com.torfstack.docr.document.captureImageAndScan
import com.torfstack.docr.model.CategoryViewModel
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.persistence.ImageEntity
import com.torfstack.docr.ui.components.Category
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.util.downscaled
import com.torfstack.docr.util.findActivity
import com.torfstack.docr.util.thumbnail
import com.torfstack.docr.util.toByteArray
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CategoryView(navController: NavHostController, viewModel: CategoryViewModel) {
    val activity = LocalContext.current.findActivity()!!
    val categories by viewModel.uiState.observeAsState(initial = emptyList())

    val scannerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val gmsResult =
                    GmsDocumentScanningResult.fromActivityResultIntent(result.data) // get the result
                gmsResult?.pages?.let { pages ->
                    viewModel.viewModelScope.launch {
                        val database = DocrDatabase.getInstance(activity)

                        val imageUri = pages[0].imageUri
                        val thumbnailBytes =
                            activity.contentResolver.openInputStream(imageUri)?.use { s ->
                                thumbnail(s.toByteArray())
                            } ?: return@launch

                        val categoryId = UUID.randomUUID().toString()

                        DocrFileManager().insertThumbnailToFiles(
                            activity,
                            thumbnailBytes,
                            categoryId,
                        )

                        val newCategory = CategoryEntity(
                            categoryId,
                            "Category",
                            "Description",
                            created = System.currentTimeMillis(),
                            lastUpdated = System.currentTimeMillis(),
                            version = 0
                        )

                        val images = mutableListOf<ImageEntity>()
                        pages.forEach { page ->
                            val imageId = UUID.randomUUID().toString()
                            activity.contentResolver.openInputStream(page.imageUri)?.use { s ->
                                val bytes = s.toByteArray()
                                val downScaled = downscaled(bytes)

                                DocrFileManager().insertToFiles(
                                    activity,
                                    bytes,
                                    categoryId,
                                    imageId,
                                )
                                DocrFileManager().insertDownscaledToFiles(
                                    activity,
                                    downScaled,
                                    categoryId,
                                    imageId,
                                )

                                images.add(
                                    ImageEntity(
                                        imageId,
                                        categoryId
                                    )
                                )
                            }
                        }

                        database
                            .dao()
                            .insertCategoryWithImages(
                                newCategory,
                                images
                            )
                    }
                }

                gmsResult?.pdf?.let { pdf ->
                    // not yet active, activate in DocumentScan.kt
                    val pdfUri = pdf.uri // do something with the PDF
                }
            }
        }

    DocRTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier
                            .padding(end = 32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ),
                        onClick = {
                            captureImageAndScan(scannerLauncher, activity)
                        }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Category"
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                categories.forEach {
                    Category(category = it) {
                        navController.navigate(
                            Screen.Category
                                .withArgs(it.uid, it.version)
                        )
                    }
                }
            }
        }
    }
}

