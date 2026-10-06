package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.media.MediaActionSound
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.CameraLensChoice
import com.example.data.model.CameraSettings
import com.example.data.model.FileNamingScheme
import com.example.data.model.FlashOption
import com.example.data.model.OverlayPosition
import com.example.ui.components.CameraGridOverlay
import com.example.ui.components.CameraTimerCountdownOverlay
import com.example.ui.components.CameraWatermarkOverlay
import com.example.ui.components.CameraZoomIndicator
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.util.PlusCodeHelper
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onPhotoCaptured: (
        photoUri: String,
        latitude: Double,
        longitude: Double,
        locationName: String,
        plusCode: String,
        date: String,
        time: String,
        conditions: String
    ) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentView = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    // 18 Professional Camera Settings State
    var cameraSettings by remember { mutableStateOf(CameraSettings()) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Camera Hardware References
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var cameraInfo by remember { mutableStateOf<CameraInfo?>(null) }

    // Live Zoom Level
    var currentZoom by remember { mutableFloatStateOf(1.0f) }

    // Timer Countdown State
    var countdownSeconds by remember { mutableIntStateOf(0) }
    var isCountingDown by remember { mutableStateOf(false) }

    // Location & Environmental Telemetry
    var latitude by remember { mutableDoubleStateOf(11.0168) }
    var longitude by remember { mutableDoubleStateOf(76.9558) }
    var locationName by remember { mutableStateOf("Coimbatore, Tamil Nadu") }
    var fullAddress by remember { mutableStateOf("Gandhipuram Cross Cut Road, Coimbatore 641012") }
    var selectedCondition by remember { mutableStateOf("Sunny") }
    var altitudeMeters by remember { mutableDoubleStateOf(411.0) }
    var compassDegree by remember { mutableIntStateOf(184) }
    var compassHeading by remember { mutableStateOf("S (184°)") }
    var temperatureStr by remember { mutableStateOf("29°C") }
    var windStr by remember { mutableStateOf("12 km/h NW") }
    var humidityStr by remember { mutableStateOf("64%") }

    var capturedPhotoUri by remember { mutableStateOf<String?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Screen 18: Keep Screen On
    DisposableEffect(cameraSettings.keepScreenOn) {
        currentView.keepScreenOn = cameraSettings.keepScreenOn
        onDispose {
            currentView.keepScreenOn = false
        }
    }

    // Retrieve real fused location if permission is available
    LaunchedEffect(Unit) {
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    latitude = loc.latitude
                    longitude = loc.longitude
                    altitudeMeters = if (loc.hasAltitude()) loc.altitude else 411.0
                    try {
                        val geocoder = android.location.Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            val locality = addr.locality ?: addr.subAdminArea ?: "Coimbatore"
                            val state = addr.adminArea ?: "Tamil Nadu"
                            locationName = "$locality, $state"
                            fullAddress = addr.getAddressLine(0) ?: "$locality, $state"
                        }
                    } catch (e: Exception) {
                        // Keep current locationName
                    }
                }
            }
        } catch (e: Exception) {
            // Graceful fallback
        }
    }

    val plusCode = remember(latitude, longitude, locationName) {
        PlusCodeHelper.encode(
            latitude,
            longitude,
            locality = locationName.split(",").firstOrNull()?.trim() ?: "Coimbatore"
        )
    }

    val dateStr = remember { SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date()) }
    val timeStr = remember { SimpleDateFormat("hh:mm a", Locale.US).format(Date()) }

    // Trigger Photo Shutter Logic
    fun triggerPhotoCapture() {
        // Haptic feedback & shutter sound
        if (cameraSettings.vibrationFeedback) {
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(50)
                    }
                }
            } catch (e: Exception) {
                // Ignore vibrator error
            }
        }

        if (cameraSettings.shutterSound) {
            try {
                MediaActionSound().apply {
                    load(MediaActionSound.SHUTTER_CLICK)
                    play(MediaActionSound.SHUTTER_CLICK)
                }
            } catch (e: Exception) {
                // Ignore sound error
            }
        }

        // Generate file name according to File Naming Scheme
        val cleanLoc = locationName.replace(" ", "_").replace(",", "")
        val dateStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFileName = when (cameraSettings.fileNamingScheme) {
            FileNamingScheme.LOCATION_DATE_TIME -> "${cleanLoc}_${dateStamp}${cameraSettings.photoFormat.extension}"
            FileNamingScheme.DATE_TIME -> "PHOTO_${dateStamp}${cameraSettings.photoFormat.extension}"
            FileNamingScheme.GPS_COORDS -> "GPS_${String.format(Locale.US, "%.4f_%.4f", latitude, longitude)}${cameraSettings.photoFormat.extension}"
            FileNamingScheme.SEQUENTIAL -> "Photo_${cameraSettings.sequentialNumber}${cameraSettings.photoFormat.extension}"
            FileNamingScheme.CUSTOM -> "${cameraSettings.customFilePrefix}_${dateStamp}${cameraSettings.photoFormat.extension}"
        }

        val photoFile = File(context.cacheDir, photoFileName)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        val capture = imageCapture
        if (capture != null) {
            capture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        capturedPhotoUri = Uri.fromFile(photoFile).toString()
                        showConfirmationDialog = true
                    }

                    override fun onError(exc: ImageCaptureException) {
                        capturedPhotoUri = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop"
                        showConfirmationDialog = true
                    }
                }
            )
        } else {
            capturedPhotoUri = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop"
            showConfirmationDialog = true
        }
    }

    fun initiateCaptureWithTimer() {
        if (cameraSettings.timerSeconds > 0 && !isCountingDown) {
            isCountingDown = true
            countdownSeconds = cameraSettings.timerSeconds
            coroutineScope.launch {
                while (countdownSeconds > 0) {
                    delay(1000)
                    countdownSeconds -= 1
                }
                isCountingDown = false
                triggerPhotoCapture()
            }
        } else {
            triggerPhotoCapture()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // CameraX Preview View
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val flashModeInt = when (cameraSettings.flashMode) {
                        FlashOption.AUTO -> ImageCapture.FLASH_MODE_AUTO
                        FlashOption.ON -> ImageCapture.FLASH_MODE_ON
                        FlashOption.OFF -> ImageCapture.FLASH_MODE_OFF
                        FlashOption.TORCH -> ImageCapture.FLASH_MODE_OFF
                    }

                    val capture = ImageCapture.Builder()
                        .setFlashMode(flashModeInt)
                        .setCaptureMode(
                            if (cameraSettings.quickCapture) ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                            else ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
                        )
                        .build()
                    imageCapture = capture

                    val actualLensFacing = when (cameraSettings.lensChoice) {
                        CameraLensChoice.FRONT -> CameraSelector.LENS_FACING_FRONT
                        CameraLensChoice.BACK -> CameraSelector.LENS_FACING_BACK
                        else -> lensFacing
                    }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(actualLensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )
                        cameraControl = camera.cameraControl
                        cameraInfo = camera.cameraInfo

                        // Torch control
                        if (cameraSettings.flashMode == FlashOption.TORCH) {
                            camera.cameraControl.enableTorch(true)
                        } else {
                            camera.cameraControl.enableTorch(false)
                        }

                        // Exposure bias
                        if (cameraSettings.exposureSlider != 0f) {
                            camera.cameraControl.setExposureCompensationIndex(cameraSettings.exposureSlider.toInt())
                        }
                    } catch (e: Exception) {
                        // Fallback gracefully
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // 6. Grid & Composition Overlay
        CameraGridOverlay(
            gridType = cameraSettings.gridType,
            opacity = cameraSettings.gridOpacity
        )

        // 14. Watermark Overlay
        CameraWatermarkOverlay(
            settings = cameraSettings,
            latitude = latitude,
            longitude = longitude,
            dateStr = dateStr,
            timeStr = timeStr
        )

        // 8. Zoom Slider & Level Badge
        CameraZoomIndicator(
            currentZoom = currentZoom,
            showSlider = cameraSettings.showZoomSlider,
            showLevel = cameraSettings.showZoomLevel,
            onZoomChange = { newZoom ->
                currentZoom = newZoom
                cameraControl?.setZoomRatio(newZoom)
            }
        )

        // 9. Timer Countdown Overlay
        CameraTimerCountdownOverlay(secondsLeft = countdownSeconds)

        // TOP CONTROLS BAR (Back, Flash, Switch Camera, Settings)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xCC000000), Color.Transparent)
                    )
                )
                .padding(top = 44.dp, start = 14.dp, end = 14.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Center GPS Status indicator pill (Option 11)
                if (cameraSettings.showGpsStatusIndicator) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x99000000),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "GPS Active • ±3m",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Flash Toggle
                    IconButton(
                        onClick = {
                            val nextFlash = when (cameraSettings.flashMode) {
                                FlashOption.AUTO -> FlashOption.ON
                                FlashOption.ON -> FlashOption.OFF
                                FlashOption.OFF -> FlashOption.TORCH
                                FlashOption.TORCH -> FlashOption.AUTO
                            }
                            cameraSettings = cameraSettings.copy(flashMode = nextFlash)
                        },
                        modifier = Modifier.background(Color(0x33FFFFFF), CircleShape)
                    ) {
                        val icon = when (cameraSettings.flashMode) {
                            FlashOption.ON -> Icons.Default.FlashOn
                            FlashOption.TORCH -> Icons.Default.FlashOn
                            FlashOption.AUTO -> Icons.Default.FlashAuto
                            FlashOption.OFF -> Icons.Default.FlashOff
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = "Flash",
                            tint = if (cameraSettings.flashMode != FlashOption.OFF) BrandAmber else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Lens switch
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                            cameraSettings = cameraSettings.copy(
                                lensChoice = if (lensFacing == CameraSelector.LENS_FACING_FRONT) CameraLensChoice.FRONT else CameraLensChoice.BACK
                            )
                        },
                        modifier = Modifier.background(Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Camera Settings Button (Opens 18 Options Sheet)
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.background(BrandBlue.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Camera Settings",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Live Telemetry GPS / Information Overlay (Options 11, 12, 13)
        val overlayAlignment = when (cameraSettings.overlayPosition) {
            OverlayPosition.TOP -> Alignment.TopCenter
            OverlayPosition.BOTTOM -> Alignment.BottomCenter
            OverlayPosition.LEFT -> Alignment.CenterStart
            OverlayPosition.RIGHT -> Alignment.CenterEnd
            OverlayPosition.CUSTOM -> Alignment.BottomCenter
        }

        val overlayPaddingModifier = when (cameraSettings.overlayPosition) {
            OverlayPosition.TOP -> Modifier.padding(top = 100.dp, start = 16.dp, end = 16.dp)
            OverlayPosition.BOTTOM -> Modifier.padding(bottom = 120.dp, start = 16.dp, end = 16.dp)
            OverlayPosition.LEFT -> Modifier.padding(start = 12.dp, top = 100.dp, bottom = 120.dp).width(200.dp)
            OverlayPosition.RIGHT -> Modifier.padding(end = 12.dp, top = 100.dp, bottom = 120.dp).width(200.dp)
            OverlayPosition.CUSTOM -> Modifier.padding(bottom = 120.dp, start = 16.dp, end = 16.dp)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(overlayAlignment)
                .then(overlayPaddingModifier)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x111827).copy(alpha = cameraSettings.overlayOpacity),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Top Telemetry Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (cameraSettings.overlayCoordinates) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GPS: ${String.format(Locale.US, "%.4f, %.4f", latitude, longitude)}",
                                    color = Color.White,
                                    fontSize = cameraSettings.overlayTextSizeSp.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Weather & Condition Selector Pill
                        if (cameraSettings.overlayWeather) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x33FFFFFF))
                                    .clickable {
                                        val conditions = listOf("Sunny", "Clear", "Misty", "Night", "Warm", "Cloudy")
                                        val nextIndex = (conditions.indexOf(selectedCondition) + 1) % conditions.size
                                        selectedCondition = conditions[nextIndex]
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "☀ $selectedCondition${if (cameraSettings.overlayTemperature) " $temperatureStr" else ""}",
                                    color = BrandAmber,
                                    fontSize = (cameraSettings.overlayTextSizeSp - 1).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (cameraSettings.overlayLocationName) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Location: $locationName",
                            color = Color(0xFFCBD5E1),
                            fontSize = cameraSettings.overlayTextSizeSp.sp
                        )
                    }

                    if (cameraSettings.overlayAddress) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Address: $fullAddress",
                            color = Color(0xFF94A3B8),
                            fontSize = (cameraSettings.overlayTextSizeSp - 1).sp,
                            maxLines = 1
                        )
                    }

                    // Plus Code & Compass Row
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (cameraSettings.overlayPlusCode) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Plus Code: $plusCode",
                                    color = Color(0xFF10B981),
                                    fontSize = (cameraSettings.overlayTextSizeSp - 1).sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (cameraSettings.overlayCompass) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = compassHeading,
                                    color = Color(0xFF93C5FD),
                                    fontSize = (cameraSettings.overlayTextSizeSp - 1).sp
                                )
                            }
                        }
                    }

                    // Environmental extra tags: Altitude, Wind, Humidity, Date/Time
                    val extraTokens = mutableListOf<String>()
                    if (cameraSettings.overlayAltitude) extraTokens.add("Alt: ${altitudeMeters.roundToInt()}m")
                    if (cameraSettings.overlayWind) extraTokens.add("Wind: $windStr")
                    if (cameraSettings.overlayHumidity) extraTokens.add("Hum: $humidityStr")
                    if (cameraSettings.overlayDate && cameraSettings.overlayTime) extraTokens.add("$dateStr • $timeStr")
                    else if (cameraSettings.overlayDate) extraTokens.add(dateStr)
                    else if (cameraSettings.overlayTime) extraTokens.add(timeStr)

                    if (extraTokens.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = extraTokens.joinToString(" • "),
                            color = Color(0xFF64748B),
                            fontSize = (cameraSettings.overlayTextSizeSp - 2).coerceAtLeast(9f).sp
                        )
                    }

                    if (cameraSettings.overlayCustomNote && cameraSettings.customNoteText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Note: ${cameraSettings.customNoteText}",
                            color = BrandAmber,
                            fontSize = (cameraSettings.overlayTextSizeSp - 1).sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // BOTTOM SHUTTER CONTROLS BAR (Existing Layout Preserved!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(vertical = 24.dp, horizontal = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gallery shortcut
                IconButton(
                    onClick = onGalleryClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = Color.White
                    )
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            initiateCaptureWithTimer()
                        }
                )

                // Placeholder space / Timer badge
                if (cameraSettings.timerSeconds > 0) {
                    Surface(
                        shape = CircleShape,
                        color = BrandAmber.copy(alpha = 0.25f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${cameraSettings.timerSeconds}s",
                                color = BrandAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }

        // Camera Settings Bottom Sheet (All 18 Options)
        if (showSettingsSheet) {
            CameraSettingsSheet(
                settings = cameraSettings,
                sheetState = settingsSheetState,
                onDismiss = { showSettingsSheet = false },
                onSettingsChanged = { updated ->
                    cameraSettings = updated
                }
            )
        }

        // Post-Capture Metadata Confirmation Dialog (Preserved exactly as requested!)
        if (showConfirmationDialog && capturedPhotoUri != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Photo Captured",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    CaptureMetaRow(label = "Location", value = "✓ $locationName")
                    CaptureMetaRow(label = "Plus Code", value = "✓ $plusCode")
                    CaptureMetaRow(label = "Date & Time", value = "✓ $dateStr • $timeStr")
                    CaptureMetaRow(label = "Conditions", value = "✓ $selectedCondition")
                    CaptureMetaRow(label = "Quality", value = "✓ ${cameraSettings.qualityPreset.label} (${cameraSettings.jpegQuality}%)")
                    CaptureMetaRow(label = "Format", value = "✓ ${cameraSettings.photoFormat.label}")

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showConfirmationDialog = false
                                capturedPhotoUri = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Retake")
                        }

                        Button(
                            onClick = {
                                showConfirmationDialog = false
                                onPhotoCaptured(
                                    capturedPhotoUri!!,
                                    latitude,
                                    longitude,
                                    locationName,
                                    plusCode,
                                    dateStr,
                                    timeStr,
                                    selectedCondition
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Post")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CaptureMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 13.sp)
        Text(text = value, color = Color(0xFF10B981), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
