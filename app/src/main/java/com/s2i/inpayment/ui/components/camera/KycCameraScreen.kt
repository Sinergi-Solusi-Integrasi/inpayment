package com.s2i.inpayment.ui.components.camera

import android.Manifest
import androidx.camera.view.CameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.camera.controls.CameraControls
import com.s2i.inpayment.ui.components.camera.controls.KycCameraPreview
import com.s2i.inpayment.ui.components.camera.controls.drawBlockingOverlay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun KycCameraScreen(navController: NavController) {
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
        KycCameraContent(navController)
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


@Composable
private fun KycCameraContent(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize()) {
        KycCameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
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
        CameraControls(modifier = Modifier.align(Alignment.BottomCenter))
    }
}