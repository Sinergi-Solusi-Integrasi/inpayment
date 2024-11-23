package com.s2i.inpayment.ui.components.camera.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.White40

@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    onCapture: () -> Unit = {},
    onFlashToggle: () -> Unit = {}
) {
    var isFlashOn by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 62.dp), // Tambahkan padding dari navigation bar
    ) {
        // Tombol Kamera di tengah bawah
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter // Posisi tombol di tengah bawah
        ) {
            // Tombol Kamera
            IconButton(
                onClick = { onCapture() },
                modifier = Modifier
                    .size(80.dp) // Pastikan ukuran tombol cukup besar
                    // Tambahkan padding di dalam tombol
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera), // Ganti dengan ikon kamera
                    contentDescription = "Capture",
                    tint = Color.White
                )
            }
        }

        // Tombol Flash di pojok kanan bawah
        IconButton(
            onClick = {
                isFlashOn = !isFlashOn
                onFlashToggle()
            },
            modifier = Modifier
                .size(56.dp) // Ukuran tombol flash
                .align(Alignment.BottomEnd) // Posisi di pojok kanan bawah
                .padding(end = 16.dp, bottom = 32.dp) // Tambahkan padding untuk posisi tombol flash
        ) {
            Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = if (isFlashOn) "Turn Off Flash" else "Flash On",
                tint = White40
            )
        }
    }
}




