package com.s2i.inpayment.ui.components.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraControl
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.s2i.common.utils.convert.bitmapToBase64WithFormat
import com.s2i.common.utils.convert.correctImageOrientation
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.camera.controls.CameraControls
import com.s2i.inpayment.ui.components.camera.controls.KycCameraPreview
import com.s2i.inpayment.ui.components.camera.controls.drawBlockingOverlay
import com.s2i.inpayment.ui.components.ml.TextRecognitionAnalyzer
import com.s2i.inpayment.ui.components.ml.extractNIK
import com.s2i.inpayment.ui.components.ml.extractName
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun KycCameraScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Initialize the coroutine scope

    LaunchedEffect(cameraPermissionState.status.isGranted) {
        // Handle permission logic
        showBottomSheet = !cameraPermissionState.status.isGranted
    }

    Spacer(modifier = Modifier.height(32.dp))
    if (cameraPermissionState.status.isGranted) {
        // Permission granted
        KycCameraContent(navController, authViewModel)
    }

    if (showBottomSheet) {
        ReusableBottomSheet(
            imageRes = R.drawable.ic_id_card, // Your camera icon or relevant resource
            message = "Camera permission is required to proceed. Please allow access.",
            sheetState = bottomSheetState,
            onDismiss = {
                coroutineScope.launch { bottomSheetState.hide() }
                showBottomSheet = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        cameraPermissionState.launchPermissionRequest()
                        showBottomSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Izinkan Akses Kamera")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        showBottomSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Batal")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KycCameraContent(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var photoFilePath by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraControl: CameraControl? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var detectedText: String by remember { mutableStateOf("No text detected yet..") }
    var extractedName by remember { mutableStateOf("") }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(500)
            Log.d("LoadingStatus", "Loading started")
        } else {
            Log.d("LoadingStatus", "Loading ended")
        }
    }


    fun onTextUpdated(updatedText: String) {
        detectedText = updatedText
    }

    val textAnalyzer = remember {
        TextRecognitionAnalyzer { nik, name ->
            detectedText = nik
            extractedName = name
            Log.d("OCR Result", "NIK: $nik, Name: $name")
            Toast.makeText(context, "NIK: $nik, Name: $name", Toast.LENGTH_SHORT).show()
        }
    }

    startTextRecognition(
        context = context,
        cameraController = LifecycleCameraController(context),
        lifecycleOwner = lifecycleOwner,
        previewView = PreviewView(context),
        onDetectedTextUpdated = { nik, name -> // Memastikan callback sesuai
            detectedText = nik
            extractedName = name
            Log.d("Detected NIK & Name", "NIK: $nik, Name: $name")
            Toast.makeText(context, "NIK: $nik, Name: $name", Toast.LENGTH_SHORT).show()
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        KycCameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            textAnalyzer = textAnalyzer,
            onCameraControlAvailable = { control ->
                cameraControl = control
            },
            onImageCaptureAvailable = { capture ->
                imageCapture = capture
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay with block outline for NIK and Name
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBlockingOverlay()
        }
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
            onCapture = {
                imageCapture?.let { capture ->
                    val photoFile = File(
                        context.externalCacheDir,
                        "in_idCard_photo_${System.currentTimeMillis()}.jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                Log.d("Image Saved","Photo saved at ${photoFile.absolutePath} ")

                                // Simpan path file ke state
                                photoFilePath = photoFile.absolutePath
                                // Convert the image to Bitmap and Base64
                                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                                // Correct orientation
                                val correctedBitmap = correctImageOrientation(context, photoFilePath)
                                if (correctedBitmap == null) {
                                    Log.e("ImageProcessing", "❌ Bitmap null, tidak bisa diproses")
                                    return
                                }

                                capturedPhoto = correctedBitmap

                                val (base64Data, ext, mimeType) = bitmapToBase64WithFormat(
                                    correctedBitmap,
                                    Bitmap.CompressFormat.JPEG
                                )
                                authViewModel.updateIdentityData(
                                    base64Data = base64Data,
                                    bitmap = correctedBitmap,
                                    format = Bitmap.CompressFormat.JPEG,
                                    mimeType = mimeType,
                                    ext = ext
                                )
                                Log.d("ImageCapture", "Base64 Data: $base64Data, Extension: $ext, MIME Type: $mimeType\"")
                                // Perform text recognition
                                val inputImage = InputImage.fromBitmap(correctedBitmap, 0)
                                textRecognizer.process(inputImage)
                                    .addOnSuccessListener { visionText ->
                                        val nik = extractNIK(visionText.text)
                                        val name = extractName(visionText.text)

                                        detectedText = nik
                                        extractedName = name

                                        Log.d("OCR Result", "Detected NIK: $nik, Name: $name")
                                        Toast.makeText(
                                            context,
                                            "Detected NIK: $nik, Name: $name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("TextRecognitionError", "Error: ${e.message}")
                                        Toast.makeText(
                                            context,
                                            "Failed to recognize text: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                Log.d("ImageCapture", "Processing completed for saved image.")
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Toast.makeText(
                                    context,
                                    "Failed to capture image: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            },
            onFlashToggle = {
                isFlashOn = !isFlashOn
                cameraControl?.enableTorch(isFlashOn)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        if (capturedPhoto != null) {
            Log.d("ReusableBottomSheet", "Displaying bottom sheet with captured image")
            ReusableBottomSheet(
                message = "Captured Image",
                sheetState = sheetState,
                onDismiss = {
                    Log.d("ReusableBottomSheet", "Bottom sheet dismissed")
                    coroutineScope.launch {
                        if (!isLoading) {
                            sheetState.hide()
                            capturedPhoto = null
                        } else {
                            capturedPhoto = null
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    capturedPhoto?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            capturedPhoto?.let { bitmap ->
                                isLoading = true // Start loading when button is clicked
                                coroutineScope.launch {
                                    delay(1000) // Simulating data processing/loading
                                    val (base64Data, ext, mimeType) = bitmapToBase64WithFormat(
                                        bitmap,
                                        Bitmap.CompressFormat.JPEG
                                    )

                                    val safeDetectedText = detectedText.ifEmpty { "Unknown_NIK" }
                                    val safeExtractedName = extractedName.ifEmpty { "Unknown_Name" }
                                    delay(2000)
                                    if (safeDetectedText.isNotEmpty() && safeExtractedName.isNotEmpty()) {
                                        sheetState.hide()
                                        if (bitmap != null && mimeType != null && ext != null) {
                                            navController.navigate("register_screen/$safeDetectedText/$safeExtractedName?filePath=$photoFilePath")
                                            Log.d("Navigation", "register_screen/$safeDetectedText/$safeExtractedName?filePath=$photoFilePath")
                                        } else {
                                            Log.e("NavigationError", "One or more parameters are invalid!")
                                        }
                                    } else {
                                        Log.e("NavigationError", "One or more parameters are invalid!")
                                    }
                                    isLoading = false // Stop loading after data is processed
                                }
                            } ?: Log.e("NavigationError", "Captured photo is null!")
                        },
                        enabled = !isLoading && !photoFilePath.isNullOrBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Selanjutnya")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        coroutineScope.launch { sheetState.hide() }
                        capturedPhoto = null
                    }) {
                        Text("Foto Ulang")
                    }
                }
            }
        }
    }
}

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (String, String) -> Unit,
) {
    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}