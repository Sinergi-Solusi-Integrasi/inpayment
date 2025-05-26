package com.s2i.inpayment.ui.screen.kyc

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun KYCIntroScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val storagePermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(
        cameraPermissionState.status.isGranted,
        storagePermissionState.status.isGranted
    ) {
        showBottomSheet =
            !cameraPermissionState.status.isGranted && storagePermissionState.status.isGranted
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightTeal20)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Let's verify KYC",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        ),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.kyc1),
                contentDescription = "KYC Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Please submit the following documents to verify your profile.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            KYCOptions(
                title = "Take a picture of your valid ID",
                descriptions = "To check your personal information is correct",
                leadingIcon = painterResource(id = R.drawable.ic_id_card),
                trailingImageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                onClick = {
                    if (cameraPermissionState.status.isGranted &&
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || storagePermissionState.status.isGranted)
                    ) {
                        navController.navigate("kyc_camera_screen")
                    } else {
                        showBottomSheet = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Why is this needed?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable { /* Show explanation */ }
            )
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Grant Permissions")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Dismiss")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewKYCIntroScreen() {
    MaterialTheme {
        // Perlu dummy NavController untuk preview
        KYCIntroScreen(navController = NavController(LocalContext.current))
    }
}
