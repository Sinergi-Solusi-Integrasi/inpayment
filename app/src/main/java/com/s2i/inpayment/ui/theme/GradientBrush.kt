package com.s2i.inpayment.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind

fun gradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(DarkTeal40, BrightYellow40),
        start = Offset(200f, 300f),
        end = Offset(1800f, 100f)
    )
}

// Gradient Brush Card Intro
fun gradientBrushCards(): Brush {
    return Brush.linearGradient(
        colors = listOf(GreenTealLight20, GreenSkyLight21),
        start = Offset(0f, 0f), // Koordinat awal
        end = Offset(0f, 1000f) // Koordinat akhir untuk orientasi vertikal
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

// Add this to your com.s2i.inpayment.ui.theme file

// Gradient Brush for background with football field effect
fun triColorGradientBrushs(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            DarkGreen10,           // Darker green at top
            DarkTeal29,          // Dark teal in middle
            GreenTeal45.copy(alpha = 0.7f)  // Lighter green at bottom with transparency
        ),
        startY = 0f,
        endY = 1000f
    )
}

// Gradient for the balance card with road background
fun backgroundsGradientBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            DarkGreen,          // Matching the drop indicator color at top
            GreenTeal45.copy(alpha = 0.8f),  // Fading to lighter green
            MediumTeal.copy(alpha = 0.6f)   // More transparent at bottom
        ),
        startY = 0f,
        endY = 500f
    )
}

// For the triangular gradient effect shown in the image
fun triGradientBrussh(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            DarkGreen,         // Top color
            MediumTeal.copy(alpha = 0.7f),  // Middle color
            BrightTeal.copy(alpha = 0.4f)  // Bottom color with transparency
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, 1000f),
        tileMode = TileMode.Clamp
    )
}

fun Modifier.linearGradientBackground(
    colors: List<Color>,
    angle: Float = 90f // dalam derajat, 0 = horizontal ke kanan, 90 = vertikal ke bawah
) = composed {
    val angleRad = Math.toRadians(angle.toDouble()).toFloat()
    val cosAngle = kotlin.math.cos(angleRad)
    val sinAngle = kotlin.math.sin(angleRad)

    this.drawBehind {
        val width = size.width
        val height = size.height

        // Hitung titik awal dan akhir berdasarkan sudut
        val endX = width * cosAngle
        val endY = height * sinAngle

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = Offset(endX, endY)
            ),
            size = size
        )
    }
}





