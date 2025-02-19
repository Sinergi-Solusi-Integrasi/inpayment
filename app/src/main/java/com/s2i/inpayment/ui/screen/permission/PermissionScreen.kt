package com.s2i.inpayment.ui.screen.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Room
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
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
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.KYCOptions
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    navController: NavController
) {
    val context = LocalContext.current

    // Multiple Permissions
    val permissions = listOfNotNull(
        Manifest.permission.CAMERA,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        },
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else null
    ) // Remove null permissions


    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Check permissions
    // Monitor all permissions
    // Check if all permissions are granted
    val allPermissionsGranted by rememberUpdatedState(
        multiplePermissionsState.permissions.all { it.status.isGranted }
    )

    // Trigger navigation only after all permissions are granted
    LaunchedEffect(allPermissionsGranted) {
        if (allPermissionsGranted) {
            navController.navigate("home_screen") {
                popUpTo("permission_screen") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp), // Ensure spacing for button alignment
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.permission_icon),
                contentDescription = "Permission Illustration",
                modifier = Modifier
                    .fillMaxWidth(0.6f) // More compact size
                    .height(160.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Please allow permissions for a better experience.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            KYCOptions(
                title = "Camera Access",
                descriptions = "Required to capture photos.",
                leadingImageVector = Icons.Default.CameraAlt,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            KYCOptions(
                title = "Gallery and Storage",
                descriptions = "Required to access your media.",
                leadingImageVector = Icons.Default.PermMedia,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            KYCOptions(
                title = "Location Access",
                descriptions = "Required to provide location-based features.",
                leadingImageVector = Icons.Default.Room,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            KYCOptions(
                title = "Notification Access",
                descriptions = "Required to send notifications.",
                leadingImageVector = Icons.Default.CircleNotifications,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Why is this needed?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable { /* Explain permissions */ }
            )
        }

        Button(
            onClick = {
                showBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Izinkan")
        }
    }

    if (showBottomSheet) {
        ReusableBottomSheet(
            imageRes = R.drawable.permission_icon,
            message = "Camera, Location, Notifications, and Storage permissions are required to proceed. Please allow access.",
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
                        coroutineScope.launch {
                            multiplePermissionsState.launchMultiplePermissionRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Grant Permissions")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPermissionScreen() {
    MaterialTheme {
        PermissionScreen(navController = NavController(LocalContext.current))
    }
}
