package com.s2i.inpayment.ui.components.camera.controls

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil3.compose.rememberAsyncImagePainter
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.White40

@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    selectedImages: List<String> = emptyList(),
    firstSelectedImage: String? = null,
    onCapture: () -> Unit = {},
    onFlashToggle: () -> Unit = {},
    onGalleryClick: (() -> Unit)? = null,
    onNextClick: (() -> Unit)? = null,
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

        // Gallery button in the bottom left (if provided)
        onGalleryClick?.let {
            IconButton(
                onClick = { it.invoke() },
                modifier = Modifier
                    .size(72.dp) // Button size
                    .align(Alignment.BottomStart) // Position in the bottom left
                    .padding(start = 16.dp, bottom = 32.dp) // Add padding for placement
            ) {
                if (firstSelectedImage != null) {
                    Image(
                        painter = rememberAsyncImagePainter(firstSelectedImage),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                    Log.d("CameraControls", "First Selected Image: $firstSelectedImage")
                } else {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Open Gallery",
                        tint = White40
                    )
                    Log.d("CameraControls", "No Selected Image, showing default icon.")
                }

                // Menampilkan badge
                if (selectedImages.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedImages.size.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Tombol Flash di pojok kanan bawah
//        IconButton(
//            onClick = {
//                isFlashOn = !isFlashOn
//                onFlashToggle()
//            },
//            modifier = Modifier
//                .size(56.dp) // Ukuran tombol flash
//                .align(Alignment.BottomEnd) // Posisi di pojok kanan bawah
//                .padding(end = 16.dp, bottom = 32.dp) // Tambahkan padding untuk posisi tombol flash
//        ) {
//            Icon(
//                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
//                contentDescription = if (isFlashOn) "Turn Off Flash" else "Flash On",
//                tint = White40
//            )
//        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 32.dp) // Tambahkan padding untuk posisi sejajar
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp) // Jarak antara Flash dan Next
            ) {
                // Tombol Flash
                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        onFlashToggle()
                    },
                    modifier = Modifier.size(32.dp) // Ukuran tombol flash
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = if (isFlashOn) "Turn Off Flash" else "Flash On",
                        tint = White40
                    )
                }

                // Tombol Next (hanya muncul jika ada gambar yang dipilih)
                if (selectedImages.isNotEmpty() && onNextClick != null) {
                    IconButton(
                        onClick = { onNextClick() },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Next",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}




