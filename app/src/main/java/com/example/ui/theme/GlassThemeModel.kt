package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.json.JSONObject

/**
 * Complete Theme Model & Design Tokens for Dynamic Glass Theming System.
 * Every card, dialog, toolbar, navigation component and screen adapts
 * instantly in real-time when these tokens change.
 */
@Immutable
data class GlassThemeState(
    val id: String = "preset_premium_dark",
    val name: String = "Premium Dark Glass",
    val isDark: Boolean = true,
    val isCustom: Boolean = false,

    // Background & Gradients
    val backgroundColorVal: Long = 0xFF0A0F1DL,
    val backgroundGradientEnabled: Boolean = true,
    val backgroundGradientEndColorVal: Long = 0xFF142038L,

    // Glass Properties
    val glassTransparency: Float = 0.78f,        // 0.1f (very sheer) to 0.98f (opaque)
    val glassBlurIntensity: Float = 14f,         // 0f to 24f (render blur/scrim token)
    val glassBorderOpacity: Float = 0.28f,       // 0.05f to 0.9f
    val borderThickness: Float = 1.0f,           // 0.5f to 4.0f dp
    val cornerRadius: Float = 18f,               // 4f to 32f dp
    val shadowIntensity: Float = 4.0f,           // 0f to 16f dp
    val glowIntensity: Float = 0.45f,            // 0.0f to 1.0f

    // Accent & Text Colors
    val primaryAccentVal: Long = 0xFF1E88E5L,
    val secondaryAccentVal: Long = 0xFF00ACC1L,
    val iconColorVal: Long = 0xFF90CAF9L,
    val primaryTextVal: Long = 0xFFF8FAFCL,
    val secondaryTextVal: Long = 0xFF94A3B8L,

    // Transparencies for specific zones
    val bottomNavTransparency: Float = 0.88f,
    val cardTransparency: Float = 0.80f,

    // Buttons & Wallpaper
    val buttonStyle: String = "Gradient",        // "Glass", "Filled", "Gradient", "Outlined"
    val buttonGlow: Boolean = true,
    val appBackgroundImage: String = "mesh_gradient", // "none", "mesh_gradient", "aurora", "cosmic", "abstract_fluid"
    val wallpaperBlur: Float = 6.0f,             // 0f to 20f
    val wallpaperBrightness: Float = 0.9f        // 0.2f to 1.5f
) {
    // Composable-friendly Color accessors
    val backgroundColor: Color get() = Color(backgroundColorVal)
    val backgroundGradientEndColor: Color get() = Color(backgroundGradientEndColorVal)
    val primaryAccent: Color get() = Color(primaryAccentVal)
    val secondaryAccent: Color get() = Color(secondaryAccentVal)
    val iconColor: Color get() = Color(iconColorVal)
    val primaryText: Color get() = Color(primaryTextVal)
    val secondaryText: Color get() = Color(secondaryTextVal)

    // Derived Card & Panel Surface Colors with computed alpha
    val cardSurfaceColor: Color
        get() = if (isDark) {
            Color(0xFF0F172A).copy(alpha = cardTransparency)
        } else {
            Color.White.copy(alpha = cardTransparency)
        }

    val cardBorderColor: Color
        get() = if (isDark) {
            Color(0xFF94A3B8).copy(alpha = (glassBorderOpacity * 0.75f).coerceIn(0.08f, 0.45f))
        } else {
            Color.White.copy(alpha = (glassBorderOpacity + 0.2f).coerceAtMost(1f))
        }

    val bottomNavSurfaceColor: Color
        get() = if (isDark) {
            Color(0xFF070B14).copy(alpha = bottomNavTransparency)
        } else {
            Color.White.copy(alpha = bottomNavTransparency)
        }

    val backgroundBrush: Brush
        get() = if (backgroundGradientEnabled) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(backgroundColorVal),
                    Color(backgroundGradientEndColorVal)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(backgroundColorVal),
                    Color(backgroundColorVal)
                )
            )
        }

    fun toJsonString(): String {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("isDark", isDark)
        json.put("isCustom", isCustom)
        json.put("backgroundColorVal", backgroundColorVal)
        json.put("backgroundGradientEnabled", backgroundGradientEnabled)
        json.put("backgroundGradientEndColorVal", backgroundGradientEndColorVal)
        json.put("glassTransparency", glassTransparency.toDouble())
        json.put("glassBlurIntensity", glassBlurIntensity.toDouble())
        json.put("glassBorderOpacity", glassBorderOpacity.toDouble())
        json.put("borderThickness", borderThickness.toDouble())
        json.put("cornerRadius", cornerRadius.toDouble())
        json.put("shadowIntensity", shadowIntensity.toDouble())
        json.put("glowIntensity", glowIntensity.toDouble())
        json.put("primaryAccentVal", primaryAccentVal)
        json.put("secondaryAccentVal", secondaryAccentVal)
        json.put("iconColorVal", iconColorVal)
        json.put("primaryTextVal", primaryTextVal)
        json.put("secondaryTextVal", secondaryTextVal)
        json.put("bottomNavTransparency", bottomNavTransparency.toDouble())
        json.put("cardTransparency", cardTransparency.toDouble())
        json.put("buttonStyle", buttonStyle)
        json.put("buttonGlow", buttonGlow)
        json.put("appBackgroundImage", appBackgroundImage)
        json.put("wallpaperBlur", wallpaperBlur.toDouble())
        json.put("wallpaperBrightness", wallpaperBrightness.toDouble())
        return json.toString(2)
    }

    companion object {
        fun fromJsonString(jsonStr: String): GlassThemeState? {
            return try {
                val json = JSONObject(jsonStr)
                GlassThemeState(
                    id = json.optString("id", "custom_${System.currentTimeMillis()}"),
                    name = json.optString("name", "Custom Glass Theme"),
                    isDark = json.optBoolean("isDark", true),
                    isCustom = json.optBoolean("isCustom", true),
                    backgroundColorVal = json.optLong("backgroundColorVal", 0xFF0A0F1DL),
                    backgroundGradientEnabled = json.optBoolean("backgroundGradientEnabled", true),
                    backgroundGradientEndColorVal = json.optLong("backgroundGradientEndColorVal", 0xFF142038L),
                    glassTransparency = json.optDouble("glassTransparency", 0.78).toFloat(),
                    glassBlurIntensity = json.optDouble("glassBlurIntensity", 14.0).toFloat(),
                    glassBorderOpacity = json.optDouble("glassBorderOpacity", 0.28).toFloat(),
                    borderThickness = json.optDouble("borderThickness", 1.0).toFloat(),
                    cornerRadius = json.optDouble("cornerRadius", 18.0).toFloat(),
                    shadowIntensity = json.optDouble("shadowIntensity", 4.0).toFloat(),
                    glowIntensity = json.optDouble("glowIntensity", 0.45).toFloat(),
                    primaryAccentVal = json.optLong("primaryAccentVal", 0xFF1E88E5L),
                    secondaryAccentVal = json.optLong("secondaryAccentVal", 0xFF00ACC1L),
                    iconColorVal = json.optLong("iconColorVal", 0xFF90CAF9L),
                    primaryTextVal = json.optLong("primaryTextVal", 0xFFF8FAFCL),
                    secondaryTextVal = json.optLong("secondaryTextVal", 0xFF94A3B8L),
                    bottomNavTransparency = json.optDouble("bottomNavTransparency", 0.88).toFloat(),
                    cardTransparency = json.optDouble("cardTransparency", 0.80).toFloat(),
                    buttonStyle = json.optString("buttonStyle", "Gradient"),
                    buttonGlow = json.optBoolean("buttonGlow", true),
                    appBackgroundImage = json.optString("appBackgroundImage", "mesh_gradient"),
                    wallpaperBlur = json.optDouble("wallpaperBlur", 6.0).toFloat(),
                    wallpaperBrightness = json.optDouble("wallpaperBrightness", 0.9).toFloat()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * 10 Built-in Beautiful Hand-Crafted Glass Presets
 */
object GlassThemePresets {
    val PremiumDarkGlass = GlassThemeState(
        id = "preset_premium_dark",
        name = "Premium Dark Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF050811L,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF0D1322L,
        glassTransparency = 0.82f,
        glassBlurIntensity = 14f,
        glassBorderOpacity = 0.22f,
        borderThickness = 1.0f,
        cornerRadius = 18f,
        shadowIntensity = 4.0f,
        glowIntensity = 0.45f,
        primaryAccentVal = 0xFF1E88E5L,
        secondaryAccentVal = 0xFF00ACC1L,
        iconColorVal = 0xFF90CAF9L,
        primaryTextVal = 0xFFF8FAFCL,
        secondaryTextVal = 0xFF94A3B8L,
        bottomNavTransparency = 0.92f,
        cardTransparency = 0.82f,
        buttonStyle = "Gradient",
        buttonGlow = true,
        appBackgroundImage = "mesh_gradient"
    )

    val LightGlass = GlassThemeState(
        id = "preset_light_glass",
        name = "Light Glass",
        isDark = false,
        isCustom = false,
        backgroundColorVal = 0xFFF1F5F9L,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFFE2E8F0L,
        glassTransparency = 0.82f,
        glassBlurIntensity = 12f,
        glassBorderOpacity = 0.45f,
        borderThickness = 1.0f,
        cornerRadius = 18f,
        shadowIntensity = 3.0f,
        glowIntensity = 0.20f,
        primaryAccentVal = 0xFF1976D2L,
        secondaryAccentVal = 0xFF0288D1L,
        iconColorVal = 0xFF1976D2L,
        primaryTextVal = 0xFF0F172AL,
        secondaryTextVal = 0xFF64748BL,
        bottomNavTransparency = 0.92f,
        cardTransparency = 0.85f,
        buttonStyle = "Filled",
        buttonGlow = false,
        appBackgroundImage = "none"
    )

    val CrystalGlass = GlassThemeState(
        id = "preset_crystal_glass",
        name = "Crystal Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF041026L,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF0A2244L,
        glassTransparency = 0.55f, // Highly transparent
        glassBlurIntensity = 18f,
        glassBorderOpacity = 0.48f, // Specular crystalline edges
        borderThickness = 1.2f,
        cornerRadius = 22f,
        shadowIntensity = 6.0f,
        glowIntensity = 0.65f,
        primaryAccentVal = 0xFF00E5FFL,
        secondaryAccentVal = 0xFF2979FFL,
        iconColorVal = 0xFF80D8FFL,
        primaryTextVal = 0xFFFFFFFFL,
        secondaryTextVal = 0xFFB0BEC5L,
        bottomNavTransparency = 0.75f,
        cardTransparency = 0.60f,
        buttonStyle = "Glass",
        buttonGlow = true,
        appBackgroundImage = "abstract_fluid"
    )

    val FrostedGlass = GlassThemeState(
        id = "preset_frosted_glass",
        name = "Frosted Glass",
        isDark = false,
        isCustom = false,
        backgroundColorVal = 0xFFEBF1F6L,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFFD8E2EBL,
        glassTransparency = 0.88f, // Dense silky frosted matte
        glassBlurIntensity = 20f,
        glassBorderOpacity = 0.35f,
        borderThickness = 1.0f,
        cornerRadius = 20f,
        shadowIntensity = 4.0f,
        glowIntensity = 0.15f,
        primaryAccentVal = 0xFF334155L,
        secondaryAccentVal = 0xFF475569L,
        iconColorVal = 0xFF334155L,
        primaryTextVal = 0xFF0F172AL,
        secondaryTextVal = 0xFF475569L,
        bottomNavTransparency = 0.95f,
        cardTransparency = 0.88f,
        buttonStyle = "Glass",
        buttonGlow = false,
        appBackgroundImage = "none"
    )

    val LiquidGlass = GlassThemeState(
        id = "preset_liquid_glass",
        name = "Liquid Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF031A1CL,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF06353AL,
        glassTransparency = 0.65f,
        glassBlurIntensity = 16f,
        glassBorderOpacity = 0.38f,
        borderThickness = 1.0f,
        cornerRadius = 24f,
        shadowIntensity = 5.0f,
        glowIntensity = 0.55f,
        primaryAccentVal = 0xFF00B4D8L,
        secondaryAccentVal = 0xFF48CAE4L,
        iconColorVal = 0xFF90E0EFL,
        primaryTextVal = 0xFFE0F7FAL,
        secondaryTextVal = 0xFF80DEEAL,
        bottomNavTransparency = 0.82f,
        cardTransparency = 0.70f,
        buttonStyle = "Gradient",
        buttonGlow = true,
        appBackgroundImage = "aurora"
    )

    val AuroraGlass = GlassThemeState(
        id = "preset_aurora_glass",
        name = "Aurora Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF0C0721L,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF1E0B3BL,
        glassTransparency = 0.68f,
        glassBlurIntensity = 16f,
        glassBorderOpacity = 0.40f,
        borderThickness = 1.2f,
        cornerRadius = 20f,
        shadowIntensity = 6.0f,
        glowIntensity = 0.70f,
        primaryAccentVal = 0xFF10B981L, // Northern lights emerald
        secondaryAccentVal = 0xFFA855F7L, // Aurora magenta
        iconColorVal = 0xFF34D399L,
        primaryTextVal = 0xFFF3E8FFL,
        secondaryTextVal = 0xFFC084FCL,
        bottomNavTransparency = 0.80f,
        cardTransparency = 0.72f,
        buttonStyle = "Gradient",
        buttonGlow = true,
        appBackgroundImage = "aurora"
    )

    val NeonGlass = GlassThemeState(
        id = "preset_neon_glass",
        name = "Neon Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF050508L, // Cyber black
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF110B1FL,
        glassTransparency = 0.60f,
        glassBlurIntensity = 12f,
        glassBorderOpacity = 0.65f, // Vivid glowing neon lines
        borderThickness = 1.5f,
        cornerRadius = 16f,
        shadowIntensity = 8.0f,
        glowIntensity = 0.85f,
        primaryAccentVal = 0xFF00F0FFL, // Electric cyan
        secondaryAccentVal = 0xFFFF007FL, // Electric pink
        iconColorVal = 0xFF00F0FFL,
        primaryTextVal = 0xFFFFFFFFL,
        secondaryTextVal = 0xFFE2E8F0L,
        bottomNavTransparency = 0.85f,
        cardTransparency = 0.68f,
        buttonStyle = "Gradient",
        buttonGlow = true,
        appBackgroundImage = "cosmic"
    )

    val MidnightGlass = GlassThemeState(
        id = "preset_midnight_glass",
        name = "Midnight Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF030712L, // Obsidian cosmic space
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF0C132CL,
        glassTransparency = 0.75f,
        glassBlurIntensity = 14f,
        glassBorderOpacity = 0.28f,
        borderThickness = 1.0f,
        cornerRadius = 18f,
        shadowIntensity = 5.0f,
        glowIntensity = 0.45f,
        primaryAccentVal = 0xFF6366F1L, // Deep Indigo
        secondaryAccentVal = 0xFF818CF8L,
        iconColorVal = 0xFFA5B4FCL,
        primaryTextVal = 0xFFF9FAFBL,
        secondaryTextVal = 0xFF9CA3AFL,
        bottomNavTransparency = 0.90f,
        cardTransparency = 0.78f,
        buttonStyle = "Glass",
        buttonGlow = true,
        appBackgroundImage = "cosmic"
    )

    val MinimalGlass = GlassThemeState(
        id = "preset_minimal_glass",
        name = "Minimal Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF121214L,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF1A1A1EL,
        glassTransparency = 0.82f,
        glassBlurIntensity = 10f,
        glassBorderOpacity = 0.18f,
        borderThickness = 0.8f,
        cornerRadius = 14f,
        shadowIntensity = 2.0f,
        glowIntensity = 0.0f,
        primaryAccentVal = 0xFFE4E4E7L, // Cool platinum white
        secondaryAccentVal = 0xFFA1A1AAL,
        iconColorVal = 0xFFD4D4D8L,
        primaryTextVal = 0xFFFAFAFAL,
        secondaryTextVal = 0xFFA1A1AAL,
        bottomNavTransparency = 0.92f,
        cardTransparency = 0.82f,
        buttonStyle = "Outlined",
        buttonGlow = false,
        appBackgroundImage = "none"
    )

    val TransparentGlass = GlassThemeState(
        id = "preset_transparent_glass",
        name = "Transparent Glass",
        isDark = true,
        isCustom = false,
        backgroundColorVal = 0xFF080D18L,
        backgroundGradientEnabled = true,
        backgroundGradientEndColorVal = 0xFF101B30L,
        glassTransparency = 0.38f, // Ultra-sheer transparency
        glassBlurIntensity = 15f,
        glassBorderOpacity = 0.42f,
        borderThickness = 0.8f,
        cornerRadius = 20f,
        shadowIntensity = 4.0f,
        glowIntensity = 0.35f,
        primaryAccentVal = 0xFF38BDF8L,
        secondaryAccentVal = 0xFF818CF8L,
        iconColorVal = 0xFF7DD3FCL,
        primaryTextVal = 0xFFF1F5F9L,
        secondaryTextVal = 0xFF94A3B8L,
        bottomNavTransparency = 0.65f,
        cardTransparency = 0.45f,
        buttonStyle = "Glass",
        buttonGlow = true,
        appBackgroundImage = "mesh_gradient"
    )

    val allPresets: List<GlassThemeState> = listOf(
        PremiumDarkGlass,
        LightGlass,
        CrystalGlass,
        FrostedGlass,
        LiquidGlass,
        AuroraGlass,
        NeonGlass,
        MidnightGlass,
        MinimalGlass,
        TransparentGlass
    )

    fun findPresetById(id: String): GlassThemeState {
        return allPresets.firstOrNull { it.id == id } ?: PremiumDarkGlass
    }
}

/**
 * CompositionLocal providing active theme design tokens to every Composable in the app hierarchy.
 */
val LocalGlassTheme = staticCompositionLocalOf { GlassThemePresets.PremiumDarkGlass }
