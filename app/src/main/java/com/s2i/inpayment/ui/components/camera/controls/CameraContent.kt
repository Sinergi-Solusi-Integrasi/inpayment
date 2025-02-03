package com.s2i.inpayment.ui.components.camera.controls

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraControl
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.common.utils.convert.correctImageOrientation
import com.s2i.inpayment.BuildConfig
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ml.TextRecognitionAnalyzer
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraContent(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel = koinViewModel(),
    selectedImages: List<String>,
    onCapture: (String) -> Unit, // Callback untuk menangkap path gambar
    onGalleryClick: () -> Unit
) {
    val isLoading by remember { mutableStateOf(false) }

    var photoFilePath by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraControl: CameraControl? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    val firstSelectedImage = if (selectedImages.isNotEmpty()) selectedImages.firstOrNull() else null
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(500)
            Log.d("LoadingStatus", "Loading started")
        } else {
            Log.d("LoadingStatus", "Loading ended")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            onCameraControlAvailable = { control ->
                cameraControl = control
            },
            onImageCaptureAvailable = { capture ->
                imageCapture = capture
            },
            modifier = Modifier.fillMaxSize()
        )

//        Spacer(modifier = Modifier.height(32.dp))

        // Top Bar with Close and Help buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    WindowInsets.statusBars.asPaddingValues() // Tambahkan padding sesuai status bar
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                        cameraProvider.unbindAll()
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .size(40.dp) // Pastikan ukuran tombol cukup besar
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Warna lingkaran semi transparan
                            shape = CircleShape
                        )
                        .padding(8.dp) // Tambahkan padding di dalam tombol
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onPrimary, // Warna ikon
                        modifier = Modifier.size(24.dp) // Ukuran ikon
                    )
                }
                IconButton(onClick = { /* Help functionality */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_help), // Replace with your help icon
                        contentDescription = "Help",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Bottom Controls
        CameraControls(
            selectedImages = selectedImages,
            firstSelectedImage = firstSelectedImage,
            onCapture = {
                imageCapture?.let { capture ->
                    // Save the captured image to a file

                    //Save image to cache
//                    val photoFile = File(
//                        context.externalCacheDir,
//                        "in_idCard_photo_${System.currentTimeMillis()}.jpg"
//                    )

                    //Save image to external storage
//                    val photoFile = File(
//                        context.getExternalFilesDir("Pictures/INPayment"),
//                        "bablas_${System.currentTimeMillis()}.jpg"
//                    )
//
//                    val photoUri = FileProvider.getUriForFile(
//                        context,
//                        "${BuildConfig.APPLICATION_ID}.provider",
//                        photoFile
//                    )
//
//                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

//                    capture.takePicture(
//                        outputOptions,
//                        ContextCompat.getMainExecutor(context),
//                        object : ImageCapture.OnImageSavedCallback {
//                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                                Log.d("Image Saved","Photo saved at ${photoFile.absolutePath} ")
//
//                                // Simpan path file ke state
//                                photoFilePath = photoFile.absolutePath
//                                // Convert the image to Bitmap and Base64
//                                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
//                                // Correct orientation
//                                val correctedBitmap = correctImageOrientation(photoFile.absolutePath, bitmap)
//
//                                capturedPhoto = correctedBitmap
//
//                                // Perform text recognition
//                                val inputImage = InputImage.fromBitmap(correctedBitmap, 0)
//                                Log.d("ImageCapture", "Processing completed for saved image.")
//                            }
//
//                            override fun onError(exception: ImageCaptureException) {
//                                Toast.makeText(
//                                    context,
//                                    "Failed to capture image: ${exception.message}",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }
//                    )

                    val contentResolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "bablas_${System.currentTimeMillis()}.jpg")
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/INPayment") // Lokasi folder tujuan
                    }

                    val photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(
                        contentResolver,
                        photoUri,
                        contentValues
                    ).build()

                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                Log.d("ImageCapture", "Image saved successfully at: ${outputFileResults.savedUri}")
//                                Toast.makeText(context, "Image saved at: ${outputFileResults.savedUri}", Toast.LENGTH_SHORT).show()
                                // Kirimkan path gambar ke callback
                                val savedUri = outputFileResults.savedUri
                                if (savedUri != null) {
                                    Log.d("ImageCapture", "Image saved successfully at: $savedUri")
                                    onCapture(savedUri.toString())
                                    // Baca gambar sebagai bitmap jika diperlukan
                                    try {
                                        val inputStream =
                                            contentResolver.openInputStream(outputFileResults.savedUri!!)
                                        val bitmap = BitmapFactory.decodeStream(inputStream)
                                        if (bitmap != null) {
                                            capturedPhoto = bitmap
                                            Log.d("ImageCapture", "Bitmap processed successfully")
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "ImageCapture",
                                            "Error reading image: ${e.message}",
                                            e
                                        )
                                    }
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("ImageCapture", "Error saving image: ${exception.message}", exception)
                                Toast.makeText(context, "Error saving image: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                }
            },
            onGalleryClick = onGalleryClick,
            onFlashToggle = {
                isFlashOn = !isFlashOn
                cameraControl?.enableTorch(isFlashOn)
            },
            onNextClick = {
                // Action for Next button
                coroutineScope.launch {
                    vehiclesViewModel.setVehicleUri(selectedImages)
                    delay(500)
                    delay(200) // Beri jeda lagi sebelum navigasi
                    navController.navigate("image_vehicle_screen")
                    Log.d(
                        "CameraContent",
                        "\uD83D\uDEE0 setVehicleUri Dipanggil dengan: $selectedImages"
                    )

                }
                Log.d("CameraControls", "Next button clicked!")

            }
        )
    }
}