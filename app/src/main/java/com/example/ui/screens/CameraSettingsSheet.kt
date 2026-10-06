package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HdrOn
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraLensChoice
import com.example.data.model.CameraSettings
import com.example.data.model.FileNamingScheme
import com.example.data.model.FlashOption
import com.example.data.model.FocusMode
import com.example.data.model.GridType
import com.example.data.model.HdrOption
import com.example.data.model.OverlayPosition
import com.example.data.model.PhotoFormatChoice
import com.example.data.model.PhotoQualityPreset
import com.example.data.model.StabilizationMode
import com.example.data.model.VolumeButtonAction
import com.example.data.model.WatermarkPosition
import com.example.data.model.WatermarkType
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.BrandRed
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CameraSettingsSheet(
    settings: CameraSettings,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSettingsChanged: (CameraSettings) -> Unit
) {
    var currentSettings by remember(settings) { mutableStateOf(settings) }
    var activeCategory by remember { mutableStateOf(0) } // 0: Quality & Capture, 1: Overlays & GPS, 2: System & Save

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Camera Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Professional Capture & Overlay Controls",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Quick Category Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryTabPill(
                    label = "Lens & Capture",
                    selected = activeCategory == 0,
                    onClick = { activeCategory = 0 },
                    modifier = Modifier.weight(1f)
                )
                CategoryTabPill(
                    label = "Overlays & GPS",
                    selected = activeCategory == 1,
                    onClick = { activeCategory = 1 },
                    modifier = Modifier.weight(1f)
                )
                CategoryTabPill(
                    label = "Save & System",
                    selected = activeCategory == 2,
                    onClick = { activeCategory = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Scrollable Content for All 18 Sections
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                when (activeCategory) {
                    0 -> {
                        // 1. PHOTO QUALITY
                        SectionCard(title = "1. Photo Quality", icon = Icons.Default.HighQuality) {
                            Text("Quality Preset", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                PhotoQualityPreset.values().forEach { preset ->
                                    FilterChip(
                                        selected = currentSettings.qualityPreset == preset,
                                        onClick = {
                                            currentSettings = currentSettings.copy(qualityPreset = preset)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(preset.label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("JPEG Quality", fontSize = 13.sp)
                                Text("${currentSettings.jpegQuality}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                            }
                            Slider(
                                value = currentSettings.jpegQuality.toFloat(),
                                onValueChange = {
                                    currentSettings = currentSettings.copy(jpegQuality = it.roundToInt())
                                    onSettingsChanged(currentSettings)
                                },
                                valueRange = 50f..100f,
                                steps = 9
                            )

                            SettingsToggleRow(
                                label = "PNG Export Option",
                                subtitle = "Lossless compression for diagrams & charts",
                                checked = currentSettings.pngExport,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(pngExport = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )

                            SettingsToggleRow(
                                label = "Preserve Original Resolution",
                                subtitle = "Maintain uncropped sensor dimensions",
                                checked = currentSettings.preserveOriginalResolution,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(preserveOriginalResolution = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                        }

                        // 2. CAMERA LENS
                        SectionCard(title = "2. Camera Lens", icon = Icons.Default.Videocam) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                CameraLensChoice.values().forEach { lens ->
                                    FilterChip(
                                        selected = currentSettings.lensChoice == lens,
                                        onClick = {
                                            currentSettings = currentSettings.copy(lensChoice = lens)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(lens.label, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        // 3. PHOTO FORMAT
                        SectionCard(title = "3. Photo Format", icon = Icons.Default.Image) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PhotoFormatChoice.values().forEach { fmt ->
                                    FilterChip(
                                        selected = currentSettings.photoFormat == fmt,
                                        onClick = {
                                            currentSettings = currentSettings.copy(photoFormat = fmt)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(fmt.label, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        // 4. FLASH
                        SectionCard(title = "4. Flash", icon = Icons.Default.FlashOn) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FlashOption.values().forEach { flash ->
                                    FilterChip(
                                        selected = currentSettings.flashMode == flash,
                                        onClick = {
                                            currentSettings = currentSettings.copy(flashMode = flash)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(flash.label, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        // 5. HDR
                        SectionCard(title = "5. HDR", icon = Icons.Default.HdrOn) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                HdrOption.values().forEach { hdr ->
                                    FilterChip(
                                        selected = currentSettings.hdrMode == hdr,
                                        onClick = {
                                            currentSettings = currentSettings.copy(hdrMode = hdr)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(hdr.label, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        // 6. GRID & COMPOSITION
                        SectionCard(title = "6. Grid & Composition", icon = Icons.Default.GridOn) {
                            Text("Grid Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                GridType.values().forEach { grid ->
                                    FilterChip(
                                        selected = currentSettings.gridType == grid,
                                        onClick = {
                                            currentSettings = currentSettings.copy(gridType = grid)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(grid.label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            if (currentSettings.gridType != GridType.OFF) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Grid Opacity", fontSize = 13.sp)
                                    Text("${(currentSettings.gridOpacity * 100).roundToInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                                }
                                Slider(
                                    value = currentSettings.gridOpacity,
                                    onValueChange = {
                                        currentSettings = currentSettings.copy(gridOpacity = it)
                                        onSettingsChanged(currentSettings)
                                    },
                                    valueRange = 0.1f..1.0f
                                )
                            }
                        }

                        // 7. FOCUS & EXPOSURE
                        SectionCard(title = "7. Focus & Exposure", icon = Icons.Default.CenterFocusStrong) {
                            Text("Focus Mode", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FocusMode.values().forEach { focus ->
                                    FilterChip(
                                        selected = currentSettings.focusMode == focus,
                                        onClick = {
                                            currentSettings = currentSettings.copy(focusMode = focus)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(focus.label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Exposure Bias", fontSize = 13.sp)
                                Text(
                                    String.format(java.util.Locale.US, "%.1f EV", currentSettings.exposureSlider),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandAmber
                                )
                            }
                            Slider(
                                value = currentSettings.exposureSlider,
                                onValueChange = {
                                    currentSettings = currentSettings.copy(exposureSlider = it)
                                    onSettingsChanged(currentSettings)
                                },
                                valueRange = -2.0f..2.0f,
                                steps = 7
                            )

                            SettingsToggleRow(
                                label = "Focus Lock",
                                subtitle = "Lock focus distance on shutter hold",
                                checked = currentSettings.focusLock,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(focusLock = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )

                            SettingsToggleRow(
                                label = "Exposure Lock (AEL)",
                                subtitle = "Freeze exposure value across frames",
                                checked = currentSettings.exposureLock,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(exposureLock = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                        }

                        // 8. ZOOM
                        SectionCard(title = "8. Zoom", icon = Icons.Default.ZoomIn) {
                            SettingsToggleRow(
                                label = "Pinch to Zoom",
                                subtitle = "Two-finger pinch gesture in viewfinder",
                                checked = currentSettings.pinchToZoom,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(pinchToZoom = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Volume Button Zoom",
                                subtitle = "Vol+/- controls digital zoom levels",
                                checked = currentSettings.volumeButtonZoom,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(volumeButtonZoom = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Show Zoom Slider",
                                subtitle = "Vertical slider on right edge",
                                checked = currentSettings.showZoomSlider,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(showZoomSlider = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Show Zoom Level Badge",
                                subtitle = "Display active ratio (e.g. 1.0x, 2.0x)",
                                checked = currentSettings.showZoomLevel,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(showZoomLevel = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Smooth Digital Zoom",
                                subtitle = "Interpolate zoom transitions smoothly",
                                checked = currentSettings.smoothDigitalZoom,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(smoothDigitalZoom = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                        }

                        // 9. TIMER
                        SectionCard(title = "9. Timer", icon = Icons.Default.Timer) {
                            val timerOptions = listOf(0 to "Off", 3 to "3s", 5 to "5s", 10 to "10s", 15 to "Custom (15s)")
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                timerOptions.forEach { (sec, label) ->
                                    FilterChip(
                                        selected = currentSettings.timerSeconds == sec,
                                        onClick = {
                                            currentSettings = currentSettings.copy(timerSeconds = sec)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(label, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        // 10. STABILIZATION
                        SectionCard(title = "10. Stabilization", icon = Icons.Default.ScreenRotation) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                StabilizationMode.values().forEach { stab ->
                                    FilterChip(
                                        selected = currentSettings.stabilizationMode == stab,
                                        onClick = {
                                            currentSettings = currentSettings.copy(stabilizationMode = stab)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(stab.label, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // 11. LOCATION / GPS
                        SectionCard(title = "11. Location / GPS", icon = Icons.Default.LocationOn) {
                            SettingsToggleRow("Save GPS Location", "Embed coordinates in metadata", currentSettings.saveGpsLocation) {
                                currentSettings = currentSettings.copy(saveGpsLocation = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Save Latitude & Longitude", "Accurate WGS84 coordinates", currentSettings.saveLatLng) {
                                currentSettings = currentSettings.copy(saveLatLng = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Reverse Geocoded Address", "Fetch street & locality names", currentSettings.reverseGeocodedAddress) {
                                currentSettings = currentSettings.copy(reverseGeocodedAddress = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Plus Code (Open Location Code)", "Global code for precise location", currentSettings.savePlusCode) {
                                currentSettings = currentSettings.copy(savePlusCode = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Altitude", "Record height above sea level", currentSettings.saveAltitude) {
                                currentSettings = currentSettings.copy(saveAltitude = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Compass Direction", "Heading angle & Cardinal point (N/S/E/W)", currentSettings.saveCompassDirection) {
                                currentSettings = currentSettings.copy(saveCompassDirection = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("GPS Accuracy", "Record horizontal confidence in meters", currentSettings.saveGpsAccuracy) {
                                currentSettings = currentSettings.copy(saveGpsAccuracy = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Location Timestamp", "Precise satellite time stamp", currentSettings.saveLocationTimestamp) {
                                currentSettings = currentSettings.copy(saveLocationTimestamp = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Show GPS Status Indicator", "Live green satellite status badge", currentSettings.showGpsStatusIndicator) {
                                currentSettings = currentSettings.copy(showGpsStatusIndicator = it); onSettingsChanged(currentSettings)
                            }
                        }

                        // 12. PHOTO INFORMATION OVERLAY
                        SectionCard(title = "12. Photo Information Overlay", icon = Icons.Default.Layers) {
                            Text("Independent Overlay Toggles", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                            SettingsToggleRow("Location Name", null, currentSettings.overlayLocationName) {
                                currentSettings = currentSettings.copy(overlayLocationName = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Address", null, currentSettings.overlayAddress) {
                                currentSettings = currentSettings.copy(overlayAddress = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Map Badge", null, currentSettings.overlayMap) {
                                currentSettings = currentSettings.copy(overlayMap = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Plus Code", null, currentSettings.overlayPlusCode) {
                                currentSettings = currentSettings.copy(overlayPlusCode = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Date", null, currentSettings.overlayDate) {
                                currentSettings = currentSettings.copy(overlayDate = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Time", null, currentSettings.overlayTime) {
                                currentSettings = currentSettings.copy(overlayTime = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Weather Condition", null, currentSettings.overlayWeather) {
                                currentSettings = currentSettings.copy(overlayWeather = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Temperature", null, currentSettings.overlayTemperature) {
                                currentSettings = currentSettings.copy(overlayTemperature = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Wind", null, currentSettings.overlayWind) {
                                currentSettings = currentSettings.copy(overlayWind = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Humidity", null, currentSettings.overlayHumidity) {
                                currentSettings = currentSettings.copy(overlayHumidity = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Altitude", null, currentSettings.overlayAltitude) {
                                currentSettings = currentSettings.copy(overlayAltitude = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Coordinates (Lat/Lng)", null, currentSettings.overlayCoordinates) {
                                currentSettings = currentSettings.copy(overlayCoordinates = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Compass Heading", null, currentSettings.overlayCompass) {
                                currentSettings = currentSettings.copy(overlayCompass = it); onSettingsChanged(currentSettings)
                            }
                            SettingsToggleRow("Custom Note", null, currentSettings.overlayCustomNote) {
                                currentSettings = currentSettings.copy(overlayCustomNote = it); onSettingsChanged(currentSettings)
                            }
                            if (currentSettings.overlayCustomNote) {
                                OutlinedTextField(
                                    value = currentSettings.customNoteText,
                                    onValueChange = {
                                        currentSettings = currentSettings.copy(customNoteText = it)
                                        onSettingsChanged(currentSettings)
                                    },
                                    label = { Text("Custom Note Text") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            SettingsToggleRow("App Logo", null, currentSettings.overlayLogo) {
                                currentSettings = currentSettings.copy(overlayLogo = it); onSettingsChanged(currentSettings)
                            }
                        }

                        // 13. OVERLAY POSITION & STYLE
                        SectionCard(title = "13. Overlay Position", icon = Icons.Default.AspectRatio) {
                            Text("Position", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OverlayPosition.values().forEach { pos ->
                                    FilterChip(
                                        selected = currentSettings.overlayPosition == pos,
                                        onClick = {
                                            currentSettings = currentSettings.copy(overlayPosition = pos)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(pos.label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Overlay Opacity", fontSize = 13.sp)
                                Text("${(currentSettings.overlayOpacity * 100).roundToInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                            }
                            Slider(
                                value = currentSettings.overlayOpacity,
                                onValueChange = {
                                    currentSettings = currentSettings.copy(overlayOpacity = it)
                                    onSettingsChanged(currentSettings)
                                },
                                valueRange = 0.2f..1.0f
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Overlay Text Size", fontSize = 13.sp)
                                Text("${currentSettings.overlayTextSizeSp.roundToInt()} sp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                            }
                            Slider(
                                value = currentSettings.overlayTextSizeSp,
                                onValueChange = {
                                    currentSettings = currentSettings.copy(overlayTextSizeSp = it)
                                    onSettingsChanged(currentSettings)
                                },
                                valueRange = 10f..18f,
                                steps = 7
                            )
                        }

                        // 14. WATERMARK
                        SectionCard(title = "14. Watermark", icon = Icons.Default.TextFields) {
                            Text("Watermark Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                WatermarkType.values().forEach { wm ->
                                    FilterChip(
                                        selected = currentSettings.watermarkType == wm,
                                        onClick = {
                                            currentSettings = currentSettings.copy(watermarkType = wm)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(wm.label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            if (currentSettings.watermarkType == WatermarkType.CUSTOM_TEXT) {
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = currentSettings.watermarkCustomText,
                                    onValueChange = {
                                        currentSettings = currentSettings.copy(watermarkCustomText = it)
                                        onSettingsChanged(currentSettings)
                                    },
                                    label = { Text("Watermark Text") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (currentSettings.watermarkType != WatermarkType.NONE) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Position", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    WatermarkPosition.values().forEach { pos ->
                                        FilterChip(
                                            selected = currentSettings.watermarkPosition == pos,
                                            onClick = {
                                                currentSettings = currentSettings.copy(watermarkPosition = pos)
                                                onSettingsChanged(currentSettings)
                                            },
                                            label = { Text(pos.label, fontSize = 12.sp) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Watermark Opacity", fontSize = 13.sp)
                                    Text("${(currentSettings.watermarkOpacity * 100).roundToInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                                }
                                Slider(
                                    value = currentSettings.watermarkOpacity,
                                    onValueChange = {
                                        currentSettings = currentSettings.copy(watermarkOpacity = it)
                                        onSettingsChanged(currentSettings)
                                    },
                                    valueRange = 0.2f..1.0f
                                )
                            }
                        }
                    }

                    2 -> {
                        // 15. FILE NAMING
                        SectionCard(title = "15. File Naming", icon = Icons.Default.TextFields) {
                            Text("Auto Naming Scheme", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FileNamingScheme.values().forEach { scheme ->
                                    FilterChip(
                                        selected = currentSettings.fileNamingScheme == scheme,
                                        onClick = {
                                            currentSettings = currentSettings.copy(fileNamingScheme = scheme)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(scheme.label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            if (currentSettings.fileNamingScheme == FileNamingScheme.CUSTOM) {
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = currentSettings.customFilePrefix,
                                    onValueChange = {
                                        currentSettings = currentSettings.copy(customFilePrefix = it)
                                        onSettingsChanged(currentSettings)
                                    },
                                    label = { Text("Custom File Prefix") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Preview: Chennai_Survey_20260927_143000.jpg",
                                    fontSize = 11.sp,
                                    color = BrandBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // 16. SAVE OPTIONS
                        SectionCard(title = "16. Save Options", icon = Icons.Default.Save) {
                            SettingsToggleRow(
                                label = "Save Original Photo",
                                subtitle = "Keep raw unedited picture from sensor",
                                checked = currentSettings.saveOriginalPhoto,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(saveOriginalPhoto = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Save Processed Photo",
                                subtitle = "Save photo with stamped GPS telemetry overlay",
                                checked = currentSettings.saveProcessedPhoto,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(saveProcessedPhoto = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Save Both Original + Processed",
                                subtitle = "Produces two versions side by side",
                                checked = currentSettings.saveBoth,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(saveBoth = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Gallery Visibility",
                                subtitle = "Make photos visible in Android MediaStore Gallery",
                                checked = currentSettings.galleryVisibility,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(galleryVisibility = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                        }

                        // 17. SOUND & CAPTURE
                        SectionCard(title = "17. Sound & Capture", icon = Icons.Default.VolumeUp) {
                            SettingsToggleRow(
                                label = "Camera Shutter Sound",
                                subtitle = "Play acoustic shutter sound on capture",
                                checked = currentSettings.shutterSound,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(shutterSound = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Volume Key Function", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                VolumeButtonAction.values().forEach { action ->
                                    FilterChip(
                                        selected = currentSettings.volumeButtonAction == action,
                                        onClick = {
                                            currentSettings = currentSettings.copy(volumeButtonAction = action)
                                            onSettingsChanged(currentSettings)
                                        },
                                        label = { Text(action.label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            SettingsToggleRow(
                                label = "Vibration Feedback",
                                subtitle = "Haptic pulse when shutter triggers",
                                checked = currentSettings.vibrationFeedback,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(vibrationFeedback = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )

                            SettingsToggleRow(
                                label = "Quick Capture",
                                subtitle = "Minimize capture lag for instant snapping",
                                checked = currentSettings.quickCapture,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(quickCapture = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                        }

                        // 18. SCREEN & VIEWFINDER
                        SectionCard(title = "18. Screen & Viewfinder", icon = Icons.Default.ScreenRotation) {
                            SettingsToggleRow(
                                label = "Full Screen Preview",
                                subtitle = "Edge-to-edge camera frame fill",
                                checked = currentSettings.fullScreenPreview,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(fullScreenPreview = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                            SettingsToggleRow(
                                label = "Keep Screen On",
                                subtitle = "Prevent display timeout while camera is active",
                                checked = currentSettings.keepScreenOn,
                                onCheckedChange = {
                                    currentSettings = currentSettings.copy(keepScreenOn = it)
                                    onSettingsChanged(currentSettings)
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply & Close Settings", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CategoryTabPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) BrandBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = BrandBlue, checkedTrackColor = BrandBlue.copy(alpha = 0.3f))
        )
    }
}
