package com.annywalker.ipet.core.designsystem.theme

import android.app.Activity
import android.os.Build
import android.view.WindowInsetsController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001E3C),
    secondary = Color(0xFF7B61FF),
    onSecondary = Color.White,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF666666),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003355),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFB39DDB),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurface = Color(0xFDFDFDFD),
    onSurfaceVariant = Color(0xFFCCCCCC),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun IPetTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            SetStatusBarColor(LightColorScheme.onPrimary)
            content()
        }
    )
}

@Composable
fun SetStatusBarColor(
    color: Color,
    darkIcons: Boolean = true
) {
    val view = LocalView.current
    val activity = view.context as? Activity ?: return

    SideEffect {
        val window = activity.window
        window.statusBarColor = color.toArgb()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                if (darkIcons) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = darkIcons
        }
    }
}