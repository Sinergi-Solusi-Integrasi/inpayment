package com.s2i.inpayment.ui.components.camera.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    onCapture: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Flash functionality */

            }
        ) {
            // Flash Icon
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Flash",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Button(
            onClick = { /* Capture functionality */ },
            shape = CircleShape,
            modifier = Modifier.size(64.dp)
        ) {
            Text("") // Circular Button
        }
    }
}