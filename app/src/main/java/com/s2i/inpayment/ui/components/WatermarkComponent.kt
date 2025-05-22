// File: WatermarkComponent.kt
package com.s2i.inpayment.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s2i.inpayment.ui.theme.DarkGreen

@Composable
fun BablasWatermark(
    modifier: Modifier = Modifier,
    text: String = "BABLAS",
    alpha: Float = 0.06f
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp) // Padding agar tidak terpotong
            .rotate(-35f)
    ) {
        // Baris 1
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 0.dp, y = 0.dp)
        )
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 80.dp, y = 0.dp)
        )
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 160.dp, y = 0.dp)
        )

        // Baris 2
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 0.dp, y = 120.dp)
        )
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 80.dp, y = 120.dp)
        )
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 160.dp, y = 120.dp)
        )

        // Baris 3
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 0.dp, y = 240.dp)
        )
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 80.dp, y = 240.dp)
        )
        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen.copy(alpha = 0.1f),
            modifier = Modifier.offset(x = 160.dp, y = 240.dp)
        )
    }
}