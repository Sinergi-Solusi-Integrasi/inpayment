package com.s2i.inpayment.ui.components.camera

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.ReusableBottomSheetScaffold
import com.s2i.inpayment.ui.components.camera.controls.CameraContent
import com.s2i.inpayment.ui.components.gallery.GalleryContent
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
fun CameraScreen(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope() // Initialize the coroutine scope
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showGallerySheet by remember { mutableStateOf(false) } // State untuk gallery sheet
    var totalDragAmount by remember { mutableStateOf(0f) }

    val savedImages by vehiclesViewModel.vehicleUrisState.collectAsState()
    val cameraCapturedImages = remember { mutableStateListOf<String>() } // Gambar Hasil camera
    val gallerySelectedImages = remember { mutableStateListOf<String>() } // Gambar yang dipilih dari galeri


    val combinedImages by remember {
        derivedStateOf {
            (cameraCapturedImages + gallerySelectedImages + savedImages).distinct()
        }
    }

    // ✅ Saat kembali ke CameraScreen, masukkan kembali savedImages
    LaunchedEffect(savedImages) {
        cameraCapturedImages.clear()
        gallerySelectedImages.clear()
        cameraCapturedImages.addAll(savedImages) // Pastikan gambar sebelumnya tetap ada

        // Sinkronisasi dengan ViewModel
        vehiclesViewModel.setVehicleUri(cameraCapturedImages)
    }


    LaunchedEffect(cameraPermissionState.status.isGranted) {
        // Handle permission logic
        showBottomSheet = !cameraPermissionState.status.isGranted
    }

    // GallerySheet
    ReusableBottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (showGallerySheet) {
                Column(
                    modifier = Modifier
                        .systemBarsPadding()
                        .padding(16.dp)
                ) {
                    GalleryContent(
                        context = context,
                        preSelectedImages = combinedImages,
                        onImageSelected = { selectedImages ->
                            // Handle selected images
                            gallerySelectedImages.clear()

                            selectedImages.toList().forEach { images ->
                                if (!cameraCapturedImages.contains(images)) {
                                    gallerySelectedImages.add(images)
                                }
                            }

                            vehiclesViewModel.setVehicleUri(selectedImages)

                            Log.d("CameraScreen", "Updated Camera Captured Images: $cameraCapturedImages")
                            Log.d("CameraScreen", "Updated Gallery Images: $gallerySelectedImages")



                            // **Jika tidak ada gambar yang tersisa, tutup bottom sheet**

                            if (gallerySelectedImages.isEmpty()) {
                                coroutineScope.launch {
                                    try {
                                        if (scaffoldState.bottomSheetState.isVisible) {
                                            scaffoldState.bottomSheetState.partialExpand()
                                            showGallerySheet = false
                                        }
                                    } catch (e: IllegalStateException) {
                                        Log.e("CameraScreen", "Error hiding bottom sheet: ${e.message}")
                                    }
                                }
                            }
                            println("Selected Images: $selectedImages")
                            Log.d("CameraScreen", "Selected Images: $selectedImages")
                        },
                        onDismiss = {
                            coroutineScope.launch {
                                try {
                                    if (scaffoldState.bottomSheetState.isVisible) { // Pastikan hanya menutup jika sedang terbuka
                                        Log.d("CameraScreen", "✅ Sebelum modal ditutup: ${vehiclesViewModel.vehicleUrisState.value}")
                                        Log.d("CameraScreen", "✅ Setelah modal ditutup: ${vehiclesViewModel.vehicleUrisState.value}")
                                        scaffoldState.bottomSheetState.partialExpand()
                                        showGallerySheet = false
                                    }
                                } catch (e: IllegalStateException) {
                                    Log.e("CameraScreen", "Error hiding bottom sheet: ${e.message}")
                                }
                            }


                        },
                        navController = navController
                    )
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, dragAmount ->
                                totalDragAmount += dragAmount
                                // Handle vertical drag (optional, if needed)

                                if (totalDragAmount < 0 && !showGallerySheet){
                                    println("Dragging vertically: $totalDragAmount")
                                    Log.d("CameraScreen", "Dragging vertically: $totalDragAmount")
                                }
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    try {
                                        if (totalDragAmount < 60) { // Buka penuh jika drag lebih dari -200px
                                            if (scaffoldState.bottomSheetState.isVisible) { // Cegah ekspansi ulang
                                                scaffoldState.bottomSheetState.expand()
                                                showGallerySheet = true
                                            }
                                        } else { // Tutup kembali jika kurang dari threshold
                                            if (!scaffoldState.bottomSheetState.isVisible) { // Cegah hide jika sudah tersembunyi
                                                scaffoldState.bottomSheetState.partialExpand()
                                                showGallerySheet = false
                                            }
                                        }
                                        totalDragAmount = 0f
                                    } catch (e: IllegalStateException) {
                                        Log.e("CameraScreen", "Error in onDragEnd: ${e.message}")
                                    }
                                }
                            }
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .systemBarsPadding()
                ) {
                    if (cameraPermissionState.status.isGranted) {
                        // Tampilkan kamera dengan tombol untuk membuka galeri
                        CameraContent(
                            navController = navController,
                            selectedImages = combinedImages,
                            onCapture = { capturedImagePath ->
                                if (cameraCapturedImages.size < 4 && !cameraCapturedImages.contains(capturedImagePath)) {
                                    cameraCapturedImages.add(capturedImagePath)
                                    // Update badge ke galeri
                                    gallerySelectedImages.clear()
                                    gallerySelectedImages.addAll(cameraCapturedImages)
                                    vehiclesViewModel.setVehicleUri(cameraCapturedImages)
                                    Log.d("CameraScreen", "Updated Captured Images: $cameraCapturedImages")
                                } else {
                                    Log.d("CameraScreen", "Capture ignored: maximum or duplicate reached.")
                                }
                            },
                            onGalleryClick = {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.expand() // Buka modal sheet
                                    showGallerySheet = true
                                }
                            },
                        )
                    }

                    // Gunakan ReusableBottomSheet untuk izin kamera
                    if (showBottomSheet) {
                        ReusableBottomSheet(
                            imageRes = R.drawable.camera_vector,
                            message = "Camera permission is required to proceed. Please allow access.",
                            sheetState = bottomSheetState,
                            onDismiss = {
                                coroutineScope.launch {
                                    bottomSheetState.partialExpand()
                                    showBottomSheet = false
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .systemBarsPadding()
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
            }
        }
    )
}