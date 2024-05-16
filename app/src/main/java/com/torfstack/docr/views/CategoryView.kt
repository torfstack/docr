package com.torfstack.docr.views

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.torfstack.docr.document.captureImageAndScan
import com.torfstack.docr.model.CategoryViewModel
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.persistence.ImageEntity
import com.torfstack.docr.ui.components.Category
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.ui.theme.Typography
import com.torfstack.docr.util.findActivity
import com.torfstack.docr.util.thumbnail
import com.torfstack.docr.util.toByteArray
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CategoryView(navController: NavHostController, viewModel: CategoryViewModel) {
    val activity = LocalContext.current.findActivity()!!
    val uiState by viewModel.uiState.observeAsState(initial = emptyList())

    val scannerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val gmsResult =
                    GmsDocumentScanningResult.fromActivityResultIntent(result.data) // get the result
                gmsResult?.pages?.let { pages ->
                    viewModel.viewModelScope.launch {
                        val database = DocrDatabase.getInstance(activity)

                        val imageUri = pages[0].imageUri
                        val imageBytes =
                            activity.contentResolver.openInputStream(imageUri)?.use { s ->
                                thumbnail(s.toByteArray())
                            }

                        val categoryId = UUID.randomUUID().toString()
                        val newCategory = CategoryEntity(
                            categoryId,
                            "Category",
                            "Description",
                            created = System.currentTimeMillis(),
                            lastUpdated = System.currentTimeMillis(),
                            thumbnail = imageBytes ?: byteArrayOf()
                        )
                        val imageId = UUID.randomUUID().toString()
                        val newImage =
                            ImageEntity(imageId, imageBytes ?: byteArrayOf(), categoryId)
                        database
                            .dao()
                            .insertCategoryWithImages(
                                newCategory,
                                listOf(newImage)
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
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            val categories = uiState
            categories.forEach {
                Category(category = it) {
                    navController.navigate(Screen.Category.withArgs(it.uid))
                }
            }
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 128.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Button(
                        onClick = { captureImageAndScan(scannerLauncher, activity) },
                    ) {
                        Text(
                            fontSize = Typography.bodyLarge.fontSize,
                            fontWeight = Typography.bodyLarge.fontWeight,
                            text = "New scan"
                        )
                    }
                }
            }
        }
    }
}

