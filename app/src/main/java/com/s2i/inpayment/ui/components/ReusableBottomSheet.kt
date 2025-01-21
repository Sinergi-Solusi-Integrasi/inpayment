package com.s2i.inpayment.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableBottomSheet(
    imageRes: Int ? = null, // resource gambar id
    message: String ? = null, //pesan yang di terima dari api atau dari kita
    sheetState: SheetState, // state untuk bottom sheet
    onDismiss: (() -> Unit) ? = null, // Callback to dismiss the bottom sheet atau aksi menutup bottomsheet
    content: (@Composable () -> Unit)? = null
) {
    ModalBottomSheet(
        onDismissRequest = {
            Log.d("ReusableBottomSheet", "onDismissRequest called")
            onDismiss?.invoke()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                imageRes?.let {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Image resources",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Pesan opsional
                message?.let {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                // Add the content here
                content?.invoke()
            }
            // Close icon positioned at the top-right corner
            onDismiss?.let {
                IconButton(
                    onClick = {
                        Log.d("ReusableBottomSheet", "Close button clicked")
                        it.invoke()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        }
    }
}