package com.pocketledger.app.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = PurpleBlue40,
    secondary = PurpleBlue60,
    tertiary = PurpleBlue80,
    surface = SurfaceLight,
)

private val DarkColors = darkColorScheme(
    primary = PurpleBlue80,
    secondary = PurpleBlue60,
    tertiary = PurpleBlue40,
    surface = SurfaceDark,
)

@Composable
fun PocketLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PocketLedgerTypography,
        shapes = PocketLedgerShapes,
        content = content,
    )
}


