package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraSettings
import com.example.data.model.GridType
import com.example.data.model.WatermarkPosition
import com.example.data.model.WatermarkType
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import java.util.Locale

@Composable
fun CameraGridOverlay(
    gridType: GridType,
    opacity: Float,
    modifier: Modifier = Modifier
) {
    if (gridType == GridType.OFF) return

    val gridColor = Color.White.copy(alpha = opacity)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        when (gridType) {
            GridType.RULE_OF_THIRDS -> {
                // 2 vertical lines, 2 horizontal lines
                val stroke = Stroke(width = 1.5.dp.toPx())
                drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = stroke.width)
            }

            GridType.SQUARE_GRID -> {
                // 4x4 grid
                val stroke = Stroke(width = 1.2.dp.toPx())
                for (i in 1..3) {
                    drawLine(gridColor, Offset(i * w / 4f, 0f), Offset(i * w / 4f, h), strokeWidth = stroke.width)
                    drawLine(gridColor, Offset(0f, i * h / 4f), Offset(w, i * h / 4f), strokeWidth = stroke.width)
                }
            }

            GridType.DIAGONAL_GRID -> {
                val stroke = Stroke(width = 1.5.dp.toPx())
                drawLine(gridColor, Offset(0f, 0f), Offset(w, h), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(w, 0f), Offset(0f, h), strokeWidth = stroke.width)
                // Thirds intersections
                drawLine(gridColor, Offset(w / 3f, 0f), Offset(w, 2f * h / 3f), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(0f, h / 3f), Offset(2f * w / 3f, h), strokeWidth = stroke.width)
            }

            GridType.CENTER_CROSSHAIR -> {
                val stroke = Stroke(width = 2.dp.toPx())
                val cx = w / 2f
                val cy = h / 2f
                val len = 24.dp.toPx()
                drawLine(gridColor, Offset(cx - len, cy), Offset(cx + len, cy), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(cx, cy - len), Offset(cx, cy + len), strokeWidth = stroke.width)
                drawCircle(gridColor, radius = 18.dp.toPx(), center = Offset(cx, cy), style = Stroke(1.5.dp.toPx()))
            }

            GridType.HORIZON_LEVEL -> {
                val stroke = Stroke(width = 2.dp.toPx())
                val cy = h / 2f
                // Center level line with notch
                drawLine(Color(0xFF10B981).copy(alpha = opacity), Offset(w * 0.15f, cy), Offset(w * 0.85f, cy), strokeWidth = stroke.width)
                drawCircle(Color(0xFF10B981).copy(alpha = opacity), radius = 6.dp.toPx(), center = Offset(w / 2f, cy))
            }

            GridType.GOLDEN_RATIO -> {
                val stroke = Stroke(width = 1.5.dp.toPx())
                val phi = 0.618f
                val x1 = w * (1f - phi)
                val x2 = w * phi
                val y1 = h * (1f - phi)
                val y2 = h * phi
                drawLine(gridColor, Offset(x1, 0f), Offset(x1, h), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(x2, 0f), Offset(x2, h), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(0f, y1), Offset(w, y1), strokeWidth = stroke.width)
                drawLine(gridColor, Offset(0f, y2), Offset(w, y2), strokeWidth = stroke.width)
            }

            GridType.OFF -> {}
        }
    }
}

@Composable
fun BoxScope.CameraWatermarkOverlay(
    settings: CameraSettings,
    latitude: Double,
    longitude: Double,
    dateStr: String,
    timeStr: String
) {
    if (settings.watermarkType == WatermarkType.NONE) return

    val alignment = when (settings.watermarkPosition) {
        WatermarkPosition.TOP_LEFT -> Alignment.TopStart
        WatermarkPosition.TOP_RIGHT -> Alignment.TopEnd
        WatermarkPosition.BOTTOM_LEFT -> Alignment.BottomStart
        WatermarkPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
    }

    Surface(
        modifier = Modifier
            .align(alignment)
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Black.copy(alpha = settings.watermarkOpacity * 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (settings.watermarkType) {
                WatermarkType.APP_LOGO, WatermarkType.CUSTOM_LOGO -> {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PhotoViews Pro",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                WatermarkType.CUSTOM_TEXT -> {
                    Text(
                        text = settings.watermarkCustomText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                WatermarkType.DATE_TIME -> {
                    Text(
                        text = "$dateStr • $timeStr",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                WatermarkType.GPS -> {
                    Text(
                        text = String.format(Locale.US, "GPS: %.4f, %.4f", latitude, longitude),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandAmber
                    )
                }
                WatermarkType.NONE -> {}
            }
        }
    }
}

@Composable
fun BoxScope.CameraZoomIndicator(
    currentZoom: Float,
    showSlider: Boolean,
    showLevel: Boolean,
    onZoomChange: (Float) -> Unit
) {
    if (showLevel) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.6f)
        ) {
            Text(
                text = String.format(Locale.US, "%.1fx", currentZoom),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }

    if (showSlider) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
                .height(180.dp)
                .width(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("5x", color = Color.White, fontSize = 9.sp)
                Slider(
                    value = currentZoom,
                    onValueChange = onZoomChange,
                    valueRange = 1f..5f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandBlue,
                        activeTrackColor = BrandBlue,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.height(100.dp)
                )
                Text("1x", color = Color.White, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun BoxScope.CameraTimerCountdownOverlay(secondsLeft: Int) {
    if (secondsLeft <= 0) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(BrandBlue.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$secondsLeft",
                fontSize = 54.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}
