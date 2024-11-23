package com.s2i.inpayment.ui.components.ml

import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun extractNIK(text: String): String {
    val nikRegex = "\\b\\d{16}\\b".toRegex()
    return nikRegex.find(text)?.value.orEmpty()

}

fun extractName(text: String): String {
    val namaRegex = "\\bNama\\s*:\\s*([A-Za-z ]+)\\b".toRegex(RegexOption.IGNORE_CASE)
    return namaRegex.find(text)?.groupValues?.get(1)?.trim().orEmpty()
}

class TextRecognitionAnalyzer(
    private val onDetectedTextUpdated: (nik: String, name: String) -> Unit
) : ImageAnalysis.Analyzer {

    companion object{
        const val THROTTLE_TIMEOUT_MS = 1000L
    }

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        scope.launch {
            val mediaImage: Image =  imageProxy.image ?: run { imageProxy.close(); return@launch }
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            suspendCoroutine { continuation ->
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText: Text ->
                        Log.d("OCR Result", "Detected text: ${visionText.text}")
                        visionText.textBlocks.forEach { block ->
                            block.lines.forEach { line ->
                                Log.d("Detected Line", "Line: ${line.text}")
                                // Memfilter NIK dan Nama
                                val nik = extractNIK(visionText.text)
                                val name = extractName(visionText.text)
                                Log.d("Extracted Data", "NIK: $nik, Name: $name")
                                if (nik.isNotEmpty() && name.isNotEmpty()) {
                                    onDetectedTextUpdated(nik, name)
                                }
                            }
                        }
                    }
                    .addOnFailureListener{ e ->
                        Log.e("OCR Error", "Error processing image: ${e.message}", e)
                        onDetectedTextUpdated("", "")
                    }
                    .addOnCompleteListener {
                        continuation.resume(Unit)
                    }
            }
            delay(THROTTLE_TIMEOUT_MS)
        }.invokeOnCompletion { exception ->
            exception?.printStackTrace()
            imageProxy.close()

        }
    }

}