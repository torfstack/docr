package com.torfstack.docr.views

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.room.Room
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.torfstack.docr.document.captureImageAndScan
import com.torfstack.docr.model.CategoryViewModel
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.DocrDatabase
import com.torfstack.docr.persistence.ImageEntity
import com.torfstack.docr.ui.components.Category
import com.torfstack.docr.ui.theme.DocRTheme
import com.torfstack.docr.util.findActivity
import com.torfstack.docr.util.getCameraProvider
import com.torfstack.docr.util.thumbnail
import com.torfstack.docr.util.toByteArray
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CategoryView(navController: NavHostController, viewModel: CategoryViewModel) {
    val activity = LocalContext.current.findActivity()!!
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = androidx.camera.core.Preview.Builder().build()
    val previewView = remember {
        PreviewView(activity)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }
    LaunchedEffect(lensFacing) {
        val cameraProvider = activity.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    val scannerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val gmsResult =
                    GmsDocumentScanningResult.fromActivityResultIntent(result.data) // get the result
                gmsResult?.pages?.let { pages ->
                    activity.lifecycleScope.launch {
                        val database = Room.databaseBuilder(
                            activity,
                            DocrDatabase::class.java,
                            "database"
                        ).build()

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
                        viewModel.addCategory(newCategory)
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
                Category(navController = navController, category = it)
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
                        Text(text = "New scan")
                    }
                }
            }
        }
    }
}

