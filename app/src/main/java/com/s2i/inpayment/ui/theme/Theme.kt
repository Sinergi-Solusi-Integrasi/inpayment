package com.s2i.inpayment.ui.theme

import android.app.Activity
import android.content.Context
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
private val LightColorScheme = lightColorScheme(
    primary = VeryDarkBlue,
    onPrimary = Color.White, // For text/icons on primary color
    primaryContainer = VeryDarkBlue.copy(alpha = 0.1f), // Lighter variant for primary backgrounds
    onPrimaryContainer = VeryDarkBlue,

    secondary = VeryDarkBlue,
    onSecondary = Color.White, // Text/icons on secondary color
    secondaryContainer = VeryDarkBlue.copy(alpha = 0.1f), // Backgrounds using secondary color
    onSecondaryContainer = VeryDarkBlue,

//  background = Color(0xFFF6F6F8),
    background = Color(0xFFFFFFFF), // Subtle light gray for the background
    onBackground = VeryDarkBlue, // Dark teal for better readability on background

    surface = Color.White, // Surface color for cards or sheets
    onSurface = VeryDarkBlue, // Text/icons on surface color
    surfaceVariant = Color(0xFFF0F0F5), // Slightly darker shade for contrast within surfaces

    tertiary = BrightYellow40,
    onTertiary = Color.Black, // Good contrast on yellow backgrounds
)

@Composable
fun InPaymentTheme(
    content: @Composable () -> Unit
) {
    //only use one theme its light theme
    val colorScheme = LightColorScheme
    val view = LocalView.current
    val context = LocalContext.current

    // Force the app to light mode regardless of the system setting
    if (!view.isInEditMode) {
        // Make sure the activity follows the light mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as android.app.UiModeManager
            uiModeManager?.nightMode = android.app.UiModeManager.MODE_NIGHT_NO // Force light mode
        }

        // Make status and navigation bars transparent and light themed
        val activity = context as Activity
        activity.window.statusBarColor = Color.Transparent.toArgb()
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        activity.window.navigationBarColor = Color.Transparent.toArgb() // Make navigation bar transparent
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightNavigationBars = true // Light navigation bars
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}