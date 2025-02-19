package com.s2i.inpayment.ui.components.camera.controls

import android.content.Context
import android.graphics.ImageFormat
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.s2i.inpayment.ui.components.ml.TextRecognitionAnalyzer

@Composable
fun KycCameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier,
    onCameraControlAvailable: (CameraControl) -> Unit,
    onImageCaptureAvailable: (ImageCapture) -> Unit,
    textAnalyzer: TextRecognitionAnalyzer
) {
    val localCameraProviderFuture = ProcessCameraProvider.getInstance(context)
    DisposableEffect(Unit) {
        // Cleanup when this composable is removed

        onDispose {
            try {
                val cameraProvider = localCameraProviderFuture.get()
                cameraProvider.unbindAll()
                Log.d("CameraX", "Camera use cases unbound in onDispose")
            } catch (e: Exception) {
                Log.e("CameraX", "Error unbinding camera use cases: ${e.message}")
            }
        }
    }
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    val imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(previewView.display.rotation)
                        .build()

                    // ImageAnalysis for custom processing (optional)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetRotation(previewView.display.rotation)
                        .build()

                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx), textAnalyzer)


                    preview.surfaceProvider = previewView.surfaceProvider
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    // Bind Use Cases
                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture,
                            imageAnalysis
                        )
                        onCameraControlAvailable(camera.cameraControl)
                        onImageCaptureAvailable(imageCapture)
                    } catch (exc: IllegalStateException) {
                        Log.e("CameraX", "IllegalStateException when binding use cases: ${exc.message}")
                    } catch (exc: Exception) {
                        Log.e("CameraX", "Unexpected error: ${exc.message}")
                    }
                } catch (exc: Exception) {
                    Log.e("CameraX", "Failed to bind camera use cases", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = modifier,
    )
}
