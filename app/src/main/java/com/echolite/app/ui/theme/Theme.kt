package com.echolite.app.ui.theme

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

val DarkColors = darkColorScheme(
    background = DeepCharcoal,
    surface = DarkGray,
    primary = ElectricBlue,
    secondary = NeonGreen,
    onBackground = OffWhite,
    onSurface = LightGray,
    error = CrimsonRed
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EchoLiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> DarkColors
    }

    val originalDensity = LocalDensity.current

    val limitedDensity = originalDensity.run {
        Density(density, fontScale.coerceIn(1f, 1.2f))
    }


    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
        LocalDensity provides limitedDensity,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}