package com.s2i.inpayment.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

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
//fun backgroundGradientBrush(
//    center: Offset = Offset(500f, 500f),
//    radius: Float = 1000f
//): Brush {
//    return Brush.radialGradient(
//        colors = listOf(
//            Color(0xFF0057B8), // Biru Visa di tengah
//            Color(0xFF1A1F71), // Biru tua Navy
//            Color(0xFF0C1445)  // Sangat gelap di tepi
//        ),
//        center = center,
//        radius = radius
//    )
//}

fun backgroundGradientBrush(
    start: Offset = Offset(0f, 0f),
    end: Offset = Offset(1000f, 1000f)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFF005C29), // Hijau tua
            Color(0xFF008242), // Hijau medium
            Color(0xFF00A650), // Hijau cerah
            Color(0xFFC1E1C1)  // Hijau sangat muda (mendekati putih)
        ),
        start = start,
        end = end
    )
}

fun appleFrostedGlassGradient(
    start: Offset = Offset(0f, 500f),
    end: Offset = Offset(1000f, 500f)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFDCEDC8), // Hijau pastel sangat muda
            Color(0xFFE8F5E9), // Putih kehijauan sangat lembut
            Color(0xFFF1F8E9), // Putih dengan hint hijau
            Color(0xFFE8F5E9), // Putih kehijauan sangat lembut
            Color(0xFFDCEDC8)  // Hijau pastel sangat muda
        ),
        start = start,
        end = end
    )
}

fun appleCardGradient(
    start: Offset = Offset(0f, 0f),
    end: Offset = Offset(1000f, 1000f)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFF8FAF8), // Putih dengan sentuhan hijau sangat halus
            Color(0xFFEFF5F0), // Putih kehijauan
            Color(0xFFE7F1E8), // Hijau sangat muda
            Color(0xFFDEECDF)  // Hijau pastel sangat muda
        ),
        start = start,
        end = end
    )
}