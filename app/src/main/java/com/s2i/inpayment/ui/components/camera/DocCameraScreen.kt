package com.s2i.inpayment.ui.components.camera

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraControl
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
import com.s2i.domain.entity.model.users.BlobImageModel
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.camera.controls.CameraContent
import com.s2i.inpayment.ui.components.camera.controls.CameraControls
import com.s2i.inpayment.ui.components.camera.controls.CameraPreview
import com.s2i.inpayment.ui.components.camera.controls.KycCameraPreview
import com.s2i.inpayment.ui.components.camera.controls.drawBlockingOverlay
import com.s2i.inpayment.ui.components.ml.TextRecognitionAnalyzer
import com.s2i.inpayment.ui.components.ml.extractBrand
import com.s2i.inpayment.ui.components.ml.extractColor
import com.s2i.inpayment.ui.components.ml.extractModel
import com.s2i.inpayment.ui.components.ml.extractNIK
import com.s2i.inpayment.ui.components.ml.extractName
import com.s2i.inpayment.ui.components.ml.extractPlateNumber
import com.s2i.inpayment.ui.components.ml.extractType
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DocCameraScreen(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel =  koinViewModel()
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Initialize the coroutine scope

    // Handle permission logic
    LaunchedEffect(cameraPermissionState.status.isGranted) {
        showBottomSheet = !cameraPermissionState.status.isGranted
    }

    if (cameraPermissionState.status.isGranted) {
        // Permission granted
        DocCameraContent(navController = navController, vehiclesViewModel)
    }

    if (showBottomSheet) {
        ReusableBottomSheet(
            imageRes = R.drawable.camera_vector, // Your camera icon or relevant resource
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
private fun DocCameraContent(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel =  koinViewModel()
) {
    var isImageCaptured by remember { mutableStateOf(false) } // ✅ Menyimpan status apakah gambar sudah diambil
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
    var isProcessing by remember { mutableStateOf(false) }
    var showNextButton by remember { mutableStateOf(false) }
    var base64Image by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(500)
            Log.d("LoadingStatus", "Loading started")
        } else {
            Log.d("LoadingStatus", "Loading ended")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (capturedPhoto == null) {
            // Tampilkan kamera sebelum foto diambil
            CameraPreview(
                context = context,
                lifecycleOwner = lifecycleOwner,
                onCameraControlAvailable = { cameraControl = it },
                onImageCaptureAvailable = { imageCapture = it },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Setelah foto diambil, tampilkan hasilnya
            Image(
                bitmap = capturedPhoto!!.asImageBitmap(),
                contentDescription = "Captured Photo",
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.Center
            )
        }

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
                        context.getExternalFilesDir("Pictures/INPayment"),
                        "doc_vehicle_${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                                val correctedBitmap = correctImageOrientation(context, photoFile.absolutePath)

                                if (correctedBitmap != null) {
                                    capturedPhoto = correctedBitmap
                                    showNextButton = true
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("Camera", "⚠️ Error capturing image: ${exception.message}")
                                Toast.makeText(context, "Gagal menangkap gambar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            },
            onFlashToggle = {
                isFlashOn = !isFlashOn
                cameraControl?.enableTorch(isFlashOn)
            },
            onNextClick = {
                if (capturedPhoto != null) {
                    isProcessing = true
                    coroutineScope.launch {
                        // Jalankan OCR setelah gambar diambil
                        val inputImage = InputImage.fromBitmap(capturedPhoto!!, 0)
                        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                        textRecognizer.process(inputImage)
                            .addOnSuccessListener { visionText ->
                                val plateNumber = extractPlateNumber(visionText.text)
                                val brand = extractBrand(visionText.text)
                                val type = extractType(visionText.text)
                                val model = extractModel(visionText.text)
                                val color = extractColor(visionText.text)

                                vehiclesViewModel.setVehicleOCRData(
                                    plateNumber, brand, type, model, color
                                )

                                Log.d("OCR", "✅ OCR Data Saved: $plateNumber, $brand, $type, $model, $color")

                                val (base64Data, ext, mimeType) = bitmapToBase64WithFormat(
                                    capturedPhoto!!,
                                    Bitmap.CompressFormat.JPEG
                                )
                                Log.d("Image Save", "Base64 Data: $base64Data, Extension: $ext, MIME Type: $mimeType")

                                // Simpan Gambar ke ViewModel
                                vehiclesViewModel.setDocImage(
                                    BlobImageModel(
                                        data = base64Data,
                                        ext = ext,
                                        mimeType = mimeType
                                    )
                                )


                                Log.d("Image Save", "✅ Image Saved to ViewModel successfully ")

                                // Navigasi setelah OCR berhasil
                                navController.navigate("doc_vehicle_screen")
                                isProcessing = false
                            }
                            .addOnFailureListener { e ->
                                Log.e("OCR", "❌ OCR Error: ${e.message}")
                                isProcessing = false
                            }
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedImages = if (capturedPhoto != null) listOf("Captured") else emptyList()
        )
    }
}