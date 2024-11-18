package com.s2i.inpayment.ui.components.camera.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb

fun DrawScope.drawBlockingOverlay() {
    val borderWidth = 4f
    val overlayColor = Color.Black.copy(alpha = 0.5f)
    val outlineColor = Color.White

    // Draw semi-transparent overlay
    drawRect(
        color = overlayColor,
        size = size
    )

    // Draw clear rectangle (block for NIK and Name)
    // Rectangle dimensions
    val rectWidth = size.width * 0.8f // 80% of screen width
    val rectHeight = size.height * 0.2f // 20% of screen height
    val rectLeft = (size.width - rectWidth) / 2 // Center horizontally
    val rectTop = (size.height - rectHeight) / 2 // Center vertically

    // Fill the rectangle with transparent color
    drawRect(
        color = Color.Transparent,
        topLeft = Offset(rectLeft, rectTop),
        size = Size(rectWidth, rectHeight)
    )

    // Draw the border
    drawRect(
        color = outlineColor,
        topLeft = Offset(rectLeft, rectTop),
        size = Size(rectWidth, rectHeight),
        style = Stroke(width = borderWidth)
    )
}
