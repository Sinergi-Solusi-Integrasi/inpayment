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

/**
 * Creates a linear gradient brush with three colors matching the design
 */
fun triColorGradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            DarkGreen,
            MediumTeal,
            BrightTeal
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)  // Vertical gradient
    )
}

/**
 * Creates a linear gradient brush for the background with custom start and end points
 */
fun backgroundGradientBrush(start: Offset = Offset(0f, 0f), end: Offset = Offset(0f, 1000f)): Brush {
    return Brush.linearGradient(
        colors = listOf(
            DarkGreen,
            MediumTeal,
            BrightTeal
        ),
        start = start,
        end = end
    )
}