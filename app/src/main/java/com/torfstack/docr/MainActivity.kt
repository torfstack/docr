package com.torfstack.docr

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.torfstack.docr.document.captureImageAndScan
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.persistence.Database
import com.torfstack.docr.persistence.Image
import com.torfstack.docr.ui.components.Category
import com.torfstack.docr.ui.theme.DocRTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Start()
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun Start(modifier: Modifier = Modifier) {
        val lensFacing = CameraSelector.LENS_FACING_BACK
        val lifecycleOwner = LocalLifecycleOwner.current
        val preview = androidx.camera.core.Preview.Builder().build()
        val previewView = remember {
            PreviewView(this)
        }
        val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val imageCapture = remember {
            ImageCapture.Builder().build()
        }
        LaunchedEffect(lensFacing) {
            val cameraProvider = getCameraProvider()
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
                        GlobalScope.launch {
                            val imageUri = pages[0].imageUri // do something with the image
                            val imageBytes =
                                contentResolver.openInputStream(imageUri)?.use { inputStream ->
                                    readAllBytes(inputStream)
                                }
                            val imageId = UUID.randomUUID().toString()
                            imageBytes?.let {
                                Room.databaseBuilder(
                                    this@MainActivity,
                                    Database::class.java,
                                    "database"
                                )
                                    .build()
                                    .imageDao()
                                    .insertImage(Image(imageId, it))
                            }
                            val categoryId = UUID.randomUUID().toString()
                            Room.databaseBuilder(
                                this@MainActivity,
                                Database::class.java,
                                "database"
                            )
                                .build()
                                .categoryDao()
                                .insertCategory(
                                    CategoryEntity(
                                        categoryId,
                                        "Category",
                                        "Description",
                                        created = System.currentTimeMillis(),
                                        lastUpdated = System.currentTimeMillis(),
                                        image = imageId
                                    )
                                )
                        }
                    }
                    gmsResult?.pdf?.let { pdf ->
                        // not yet active, activate in DocumentScan.kt
                        val pdfUri = pdf.uri // do something with the PDF
                    }
                }
            }

        val categoryViewModel: CategoryViewModel by viewModels()
        DocRTheme {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val categories = categoryViewModel.uiState.collectAsState()
                categories.value.forEach {
                    Category(text = it.name)
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
                            onClick = { captureImageAndScan(scannerLauncher, this@MainActivity) },
                            modifier = modifier
                        ) {
                            Text(text = "New scan")
                        }
                    }
                }
            }
        }
    }
}

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categories = listOf<CategoryEntity>()

    private val _uiState = MutableStateFlow(categories)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val c =
                Room.databaseBuilder(
                    application.applicationContext,
                    Database::class.java,
                    "database"
                )
                    .build()
                    .categoryDao()
                    .getAllCategories()
            _uiState.update { c }
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private fun readAllBytes(inputStream: InputStream): ByteArray {
    val buf = ByteArrayOutputStream()
    var n: Int
    val buffer = ByteArray(8192)
    while (inputStream.read(buffer).also { n = it } > 0) {
        buf.write(buffer, 0, n)
    }
    return buf.toByteArray()
}