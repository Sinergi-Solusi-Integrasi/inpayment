package com.s2i.inpayment.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)

private val LightColorScheme = lightColorScheme(
    primary = DarkTeal40,
    onPrimary = Color.White, // For text/icons on primary color
    primaryContainer = DarkTeal40.copy(alpha = 0.1f), // Lighter variant for primary backgrounds
    onPrimaryContainer = DarkTeal40,

    secondary = DarkTeal21,
    onSecondary = Color.White, // Text/icons on secondary color
    secondaryContainer = DarkTeal21.copy(alpha = 0.1f), // Backgrounds using secondary color
    onSecondaryContainer = DarkTeal21,

    background = Color(0xFFF6F6F8), // Subtle light gray for the background
    onBackground = DarkTeal40, // Dark teal for better readability on background

    surface = Color.White, // Surface color for cards or sheets
    onSurface = DarkTeal21, // Text/icons on surface color
    surfaceVariant = Color(0xFFF0F0F5), // Slightly darker shade for contrast within surfaces

    tertiary = BrightYellow40,
    onTertiary = Color.Black, // Good contrast on yellow backgrounds

    /* Other default colors to override


    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun InPaymentTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    //only use one theme its light theme
    val colorScheme = LightColorScheme
    val view = LocalView.current
    val context = LocalContext.current

    // dynamicColor android
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    if (!view.isInEditMode) {
        val activity = context as Activity
        activity.window.statusBarColor = Color.Transparent.toArgb()
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}