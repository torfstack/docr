package com.torfstack.docr

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.torfstack.docr.document.captureImageAndScan
import com.torfstack.docr.ui.theme.DocRTheme
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
                        pages.forEach { page ->
                            val imageUri = page.imageUri // do something with the image
                        }
                    }
                    gmsResult?.pdf?.let { pdf ->
                        val pdfUri = pdf.uri // do something with the PDF
                    }
                }
            }

        DocRTheme {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
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

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }
