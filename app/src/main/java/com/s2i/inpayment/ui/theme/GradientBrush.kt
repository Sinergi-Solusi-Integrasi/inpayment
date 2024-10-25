package com.s2i.inpayment.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

fun gradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(DarkTeal40, BrightYellow40),
        start = Offset(200f, 300f),
        end = Offset(1800f, 100f)
    )
}




