package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlassTheme

/**
 * Reusable GlassCard adhering to Dynamic Glass Design Tokens.
 * Changing any token in Theme Customizer updates every card in the app instantly.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    elevation: Dp? = null,
    borderWidth: Dp? = null,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val glassTheme = LocalGlassTheme.current
    val effectiveShape = shape ?: RoundedCornerShape(glassTheme.cornerRadius.dp)
    val effectiveElevation = elevation ?: glassTheme.shadowIntensity.dp
    val effectiveBorderWidth = borderWidth ?: glassTheme.borderThickness.dp
    val effectiveBg = backgroundColor ?: glassTheme.cardSurfaceColor
    val effectiveBorder = borderColor ?: glassTheme.cardBorderColor

    val shadowColor = if (glassTheme.isDark) {
        Color(0x2F000000)
    } else {
        Color(0x18000000)
    }

    val cardModifier = if (onClick != null) {
        modifier
            .shadow(effectiveElevation, shape = effectiveShape, spotColor = shadowColor, ambientColor = shadowColor)
            .clip(effectiveShape)
            .clickable { onClick() }
    } else {
        modifier
            .shadow(effectiveElevation, shape = effectiveShape, spotColor = shadowColor, ambientColor = shadowColor)
            .clip(effectiveShape)
    }

    Surface(
        modifier = cardModifier,
        shape = effectiveShape,
        color = effectiveBg,
        border = BorderStroke(effectiveBorderWidth, effectiveBorder)
    ) {
        content()
    }
}

@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    textColor: Color? = null
) {
    val glassTheme = LocalGlassTheme.current
    val bg = containerColor ?: (if (glassTheme.isDark) Color(0x44000000) else Color(0x22000000))
    val fg = textColor ?: glassTheme.primaryText

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(glassTheme.cornerRadius.dp / 2))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = fg
        )
    }
}

/**
 * Universal dynamic button matching the theme's buttonStyle and buttonGlow
 */
@Composable
fun GlassThemedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val glassTheme = LocalGlassTheme.current
    val buttonShape = shape ?: RoundedCornerShape(glassTheme.cornerRadius.dp)
    val glowShadow = if (glassTheme.buttonGlow) {
        glassTheme.primaryAccent.copy(alpha = glassTheme.glowIntensity.coerceIn(0.2f, 0.7f))
    } else {
        Color.Transparent
    }

    when (glassTheme.buttonStyle) {
        "Gradient" -> {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(glassTheme.primaryAccent, glassTheme.secondaryAccent)
            )
            Box(
                modifier = modifier
                    .shadow(
                        if (glassTheme.buttonGlow) 6.dp else 2.dp,
                        shape = buttonShape,
                        spotColor = glowShadow,
                        ambientColor = glowShadow
                    )
                    .clip(buttonShape)
                    .background(gradientBrush)
                    .clickable(enabled = enabled, onClick = onClick)
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
        "Glass" -> {
            GlassCard(
                modifier = modifier,
                shape = buttonShape,
                backgroundColor = glassTheme.primaryAccent.copy(alpha = 0.22f),
                borderColor = glassTheme.primaryAccent.copy(alpha = 0.55f),
                onClick = onClick
            ) {
                Box(
                    modifier = Modifier.padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        content = content
                    )
                }
            }
        }
        "Outlined" -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = buttonShape,
                border = BorderStroke(glassTheme.borderThickness.dp, glassTheme.primaryAccent),
                contentPadding = contentPadding,
                content = content
            )
        }
        else -> { // "Filled"
            Button(
                onClick = onClick,
                modifier = modifier.shadow(
                    if (glassTheme.buttonGlow) 6.dp else 2.dp,
                    shape = buttonShape,
                    spotColor = glowShadow
                ),
                enabled = enabled,
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = glassTheme.primaryAccent,
                    contentColor = Color.White
                ),
                contentPadding = contentPadding,
                content = content
            )
        }
    }
}

/**
 * Universal dark glass circular icon button for top-bars, back buttons, search, and actions.
 * Completely eliminates washed-out light-white circles in dark theme.
 * Uses sleek dark translucent glass (Color(0x40000000)) and crisp glass rim.
 */
@Composable
fun GlassCircleIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    backgroundColor: Color? = null,
    borderColor: Color? = null
) {
    val glassTheme = LocalGlassTheme.current
    val isDark = glassTheme.isDark
    val bg = backgroundColor ?: if (isDark) {
        Color(0x40000000) // Deep dark translucent glass
    } else {
        Color(0x14000000) // Soft translucent light
    }
    val border = borderColor ?: if (isDark) {
        Color(0x33FFFFFF) // Subtle specular glass rim
    } else {
        Color(0x1A000000)
    }
    val iconTint = tint ?: if (isDark) Color.White else Color(0xFF0F172A)

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bg)
            .border(BorderStroke(1.dp, border), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
    }
}

