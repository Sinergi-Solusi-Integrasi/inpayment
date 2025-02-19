package com.s2i.inpayment.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

fun triGradientBrush(): Brush{
    return Brush.linearGradient(
        colors = listOf(
            DarkTeal40,
            BrightYellow40,
            BrightCerulean21
        ),
        start = Offset(0f, 0f),
        end = Offset(500f, 500f)
    )
}