package com.torfstack.docr.document

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning

fun captureImageAndScan(
    scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    context: Activity
) {
    val options = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(3)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .setScannerMode(SCANNER_MODE_FULL)
        .build()

    val scanner = GmsDocumentScanning.getClient(options)

    scanner.getStartScanIntent(context)
        .addOnSuccessListener { intentSender ->
            scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        }
        .addOnFailureListener {
        }
}

