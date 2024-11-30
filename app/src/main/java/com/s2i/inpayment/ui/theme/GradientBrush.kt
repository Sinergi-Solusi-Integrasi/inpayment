package com.s2i.inpayment.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.TileMode

fun gradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(DarkTeal40, BrightYellow40),
        start = Offset(200f, 300f),
        end = Offset(1800f, 100f)
    )
}

// Gradient untuk Income (Hijau)
fun inComeGradient(size: IntSize): Brush {
    val center = Offset(size.width / 2f, size.height / 2f)  // Menentukan pusat dari container
    val radius = size.width.coerceAtLeast(size.height) / 2f  // Menggunakan dimensi terbesar sebagai radius

    return Brush.radialGradient(
        colors = listOf(
            GreenTeal21,  // Warna Hijau
            White30       // Warna Putih (transisi ke transparan)
        ),
        center = center,
        radius = radius
    )
}

// Gradient untuk Expense (Merah)
fun exComeGradient(size: IntSize): Brush {
    val center = Offset(size.width / 2f, size.height / 2f)  // Menentukan pusat dari container
    val radius = size.width.coerceAtLeast(size.height) / 2f  // Menggunakan dimensi terbesar sebagai radius

    return Brush.radialGradient(
        colors = listOf(
            Red500,    // Warna Merah
            White30    // Warna Putih (transisi ke transparan)
        ),
        center = center,
        radius = radius
    )
}




