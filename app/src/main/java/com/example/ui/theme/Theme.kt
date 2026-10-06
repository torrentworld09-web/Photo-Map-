package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.data.model.AppThemeMode

private fun buildGlassColorScheme(glassTheme: GlassThemeState): androidx.compose.material3.ColorScheme {
    val isDark = glassTheme.isDark
    val primary = glassTheme.primaryAccent
    val secondary = glassTheme.secondaryAccent
    val background = glassTheme.backgroundColor
    val onBackground = if (isDark) Color(0xFFF8FAFC) else glassTheme.primaryText
    val surface = if (isDark) {
        Color(0xFF0F172A).copy(alpha = glassTheme.cardTransparency)
    } else {
        Color.White.copy(alpha = glassTheme.cardTransparency)
    }
    val onSurface = if (isDark) Color(0xFFF8FAFC) else glassTheme.primaryText
    val surfaceVariant = if (isDark) {
        Color(0xFF141D2E) // Dark slate surface variant instead of bright whitish
    } else {
        Color(0xFFE2E8F0)
    }
    val onSurfaceVariant = glassTheme.secondaryText
    val outline = glassTheme.cardBorderColor

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.25f),
            onPrimaryContainer = Color.White,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.25f),
            onSecondaryContainer = Color.White,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.15f),
            onPrimaryContainer = primary,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondary.copy(alpha = 0.15f),
            onSecondaryContainer = secondary,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline
        )
    }
}

@Composable
fun PhotoViewsTheme(
    glassTheme: GlassThemeState = GlassThemePresets.PremiumDarkGlass,
    themeMode: AppThemeMode? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val activeTheme = if (themeMode != null) {
        when (themeMode) {
            AppThemeMode.GLASS_LIGHT -> GlassThemePresets.LightGlass
            AppThemeMode.GLASS_DARK -> GlassThemePresets.PremiumDarkGlass
            AppThemeMode.SYSTEM -> if (isSystemInDarkTheme()) GlassThemePresets.PremiumDarkGlass else GlassThemePresets.LightGlass
        }
    } else {
        glassTheme
    }

    val isDark = activeTheme.isDark
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> buildGlassColorScheme(activeTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalGlassTheme provides activeTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
