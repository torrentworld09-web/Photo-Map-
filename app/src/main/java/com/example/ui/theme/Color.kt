package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Primary & Accents
val BrandBlue = Color(0xFF1E88E5)
val BrandBlueDark = Color(0xFF1565C0)
val BrandCyan = Color(0xFF00ACC1)
val BrandTeal = Color(0xFF26A69A)
val BrandOrange = Color(0xFFFF9800)
val BrandPurple = Color(0xFF7E57C2)
val BrandAmber = Color(0xFFFFB300)
val BrandGreen = Color(0xFF43A047)
val BrandRed = Color(0xFFE53935)

// Light Glass Theme Palette
val GlassWhite = Color(0xCCFFFFFF) // 80% opacity
val GlassWhiteMedium = Color(0xE6FFFFFF) // 90% opacity
val GlassWhiteSolid = Color(0xF7FFFFFF) // 97% opacity
val GlassWhiteBorder = Color(0x66FFFFFF) // border highlight
val GlassSurfaceLight = Color(0xFFF3F7FA)
val GlassSurfaceLightGradientEnd = Color(0xFFE5EEF8)
val GlassTextPrimary = Color(0xFF1E293B)
val GlassTextSecondary = Color(0xFF64748B)
val GlassTextTertiary = Color(0xFF94A3B8)
val GlassDivider = Color(0x1F000000)

// Dark Glass Theme Palette
val GlassDark = Color(0xCC111827) // 80% opacity
val GlassDarkMedium = Color(0xE61F2937)
val GlassDarkSolid = Color(0xF7111827)
val GlassDarkBorder = Color(0x33FFFFFF)
val GlassSurfaceDark = Color(0xFF0B0F17)
val GlassSurfaceDarkGradientEnd = Color(0xFF161F30)
val GlassTextPrimaryDark = Color(0xFFF8FAFC)
val GlassTextSecondaryDark = Color(0xFF94A3B8)
val GlassTextTertiaryDark = Color(0xFF64748B)

// Accent Gradients
val GlassHeaderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE0EAFC), Color(0xFFCFDEF3))
)

val GlassAccentBlueGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF2563EB), Color(0xFF38BDF8))
)

val GlassAccentSunsetGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFF97316), Color(0xFFEC4899))
)

val GlassAccentGreenGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF10B981), Color(0xFF34D399))
)
