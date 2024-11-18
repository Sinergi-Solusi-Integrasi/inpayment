package com.s2i.inpayment.ui.screen.kyc

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.KYCOptions
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun KYCIntroScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Check permissions
    LaunchedEffect(cameraPermissionState.status.isGranted, storagePermissionState.status.isGranted) {
        showBottomSheet = !cameraPermissionState.status.isGranted && storagePermissionState.status.isGranted
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Let’s verify KYC",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Illustration
            Image(
                painter = painterResource(id = R.drawable.ic_kyc_placeholder), // Replace with your image
                contentDescription = "KYC Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            // Subtitle
            Text(
                text = "Please submit the following documents to verify your profile.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Option 1
            KYCOptions(
                title = "Take a picture of your valid ID",
                descriptions = "To check your personal information is correct",
                leadingIcon = painterResource(id = R.drawable.ic_id_card), // Icon untuk sisi kiri
                trailingImageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, // Icon untuk sisi kanan
                onClick = {
                    if (cameraPermissionState.status.isGranted && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || storagePermissionState.status.isGranted)) {
                        navController.navigate("kyc_camera_screen")
                    } else {
                        showBottomSheet = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Option 2
            KYCOptions(
                title = "Take a selfie of yourself",
                descriptions = "To match your face to your ID photo",
                leadingImageVector = Icons.Default.Camera, // Icon untuk sisi kiri
                trailingImageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, // Icon untuk sisi kanan
                onClick = { /* Navigate to selfie upload */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Why is this needed?
            Text(
                text = "Why is this needed?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable { /* Show explanation */ }
            )
        }
    }
    if (showBottomSheet) {
        ReusableBottomSheet(
            imageRes = R.drawable.ic_id_card,
            message = "Camera and Storage permissions are required to proceed. Please allow access.",
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
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            storagePermissionState.launchPermissionRequest()
                        }
                        showBottomSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Grant Permissions")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showBottomSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Dismiss")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewKYCIntroScreen() {
    MaterialTheme {
        KYCIntroScreen(navController = NavController(LocalContext.current))
    }
}