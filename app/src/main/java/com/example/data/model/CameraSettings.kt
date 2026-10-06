package com.example.data.model

enum class PhotoQualityPreset(val label: String) {
    ORIGINAL_MAX("Original / Max Quality"),
    HIGH("High Quality (1080p+)"),
    MEDIUM("Medium Quality (720p)"),
    CUSTOM("Custom Resolution")
}

enum class CameraLensChoice(val label: String) {
    AUTO("Auto Lens"),
    BACK("Back Camera"),
    FRONT("Front Camera"),
    ULTRA_WIDE("Ultra-Wide (0.5x)"),
    WIDE("Wide (1x)"),
    TELEPHOTO("Telephoto (3x/5x)")
}

enum class PhotoFormatChoice(val label: String, val extension: String) {
    JPG("JPEG / JPG", ".jpg"),
    PNG("PNG (Lossless)", ".png"),
    HEIF("HEIF / HEIC (High Efficiency)", ".heic")
}

enum class FlashOption(val label: String) {
    AUTO("Auto"),
    ON("On"),
    OFF("Off"),
    TORCH("Torch / Continuous")
}

enum class HdrOption(val label: String) {
    AUTO("Auto HDR"),
    ON("HDR On"),
    OFF("HDR Off")
}

enum class GridType(val label: String) {
    OFF("None"),
    RULE_OF_THIRDS("Rule of Thirds (3x3)"),
    SQUARE_GRID("Square Grid (4x4)"),
    DIAGONAL_GRID("Diagonal Grid"),
    CENTER_CROSSHAIR("Center Crosshair"),
    HORIZON_LEVEL("Horizon Level"),
    GOLDEN_RATIO("Golden Ratio / Phi")
}

enum class FocusMode(val label: String) {
    AUTO("Auto Focus"),
    TOUCH_TO_FOCUS("Touch to Focus"),
    FOCUS_LOCK("Focus Lock"),
    MANUAL("Manual Focus")
}

enum class StabilizationMode(val label: String) {
    AUTO("Auto"),
    STANDARD("Standard"),
    ELECTRONIC("Electronic (EIS)"),
    OPTICAL("Optical (OIS)")
}

enum class OverlayPosition(val label: String) {
    BOTTOM("Bottom"),
    TOP("Top"),
    LEFT("Left"),
    RIGHT("Right"),
    CUSTOM("Custom Float")
}

enum class WatermarkType(val label: String) {
    NONE("None"),
    APP_LOGO("App Logo"),
    CUSTOM_LOGO("Custom Logo"),
    CUSTOM_TEXT("Custom Text"),
    DATE_TIME("Date & Time"),
    GPS("GPS Coordinates")
}

enum class WatermarkPosition(val label: String) {
    BOTTOM_RIGHT("Bottom Right"),
    BOTTOM_LEFT("Bottom Left"),
    TOP_RIGHT("Top Right"),
    TOP_LEFT("Top Left")
}

enum class FileNamingScheme(val label: String) {
    LOCATION_DATE_TIME("Location_Name_Date_Time"),
    DATE_TIME("Date_Time"),
    GPS_COORDS("GPS_Lat_Lng"),
    SEQUENTIAL("Photo_0001"),
    CUSTOM("Custom Prefix")
}

enum class VolumeButtonAction(val label: String) {
    CAPTURE("Shutter Capture"),
    ZOOM("Zoom In / Out"),
    NONE("None / System Volume")
}

data class CameraSettings(
    // 1. Photo Quality
    val qualityPreset: PhotoQualityPreset = PhotoQualityPreset.ORIGINAL_MAX,
    val jpegQuality: Int = 95,
    val pngExport: Boolean = false,
    val preserveOriginalResolution: Boolean = true,
    val customResolution: String = "4000x3000",

    // 2. Camera Lens
    val lensChoice: CameraLensChoice = CameraLensChoice.BACK,

    // 3. Photo Format
    val photoFormat: PhotoFormatChoice = PhotoFormatChoice.JPG,

    // 4. Flash
    val flashMode: FlashOption = FlashOption.AUTO,

    // 5. HDR
    val hdrMode: HdrOption = HdrOption.AUTO,

    // 6. Grid & Composition
    val gridType: GridType = GridType.RULE_OF_THIRDS,
    val gridOpacity: Float = 0.5f,

    // 7. Focus & Exposure
    val focusMode: FocusMode = FocusMode.TOUCH_TO_FOCUS,
    val focusLock: Boolean = false,
    val exposureSlider: Float = 0f, // -2f to +2f
    val exposureLock: Boolean = false,
    val autoExposure: Boolean = true,

    // 8. Zoom
    val pinchToZoom: Boolean = true,
    val volumeButtonZoom: Boolean = false,
    val showZoomSlider: Boolean = true,
    val showZoomLevel: Boolean = true,
    val smoothDigitalZoom: Boolean = true,
    val currentZoom: Float = 1.0f,

    // 9. Timer
    val timerSeconds: Int = 0, // 0 = off, 3, 5, 10, custom

    // 10. Stabilization
    val stabilizationMode: StabilizationMode = StabilizationMode.AUTO,

    // 11. Location / GPS
    val saveGpsLocation: Boolean = true,
    val saveLatLng: Boolean = true,
    val reverseGeocodedAddress: Boolean = true,
    val savePlusCode: Boolean = true,
    val saveAltitude: Boolean = true,
    val saveCompassDirection: Boolean = true,
    val saveGpsAccuracy: Boolean = true,
    val saveLocationTimestamp: Boolean = true,
    val showGpsStatusIndicator: Boolean = true,

    // 12. Photo Information Overlay toggles
    val overlayLocationName: Boolean = true,
    val overlayAddress: Boolean = true,
    val overlayMap: Boolean = true,
    val overlayPlusCode: Boolean = true,
    val overlayDate: Boolean = true,
    val overlayTime: Boolean = true,
    val overlayWeather: Boolean = true,
    val overlayTemperature: Boolean = true,
    val overlayWind: Boolean = true,
    val overlayHumidity: Boolean = true,
    val overlayAltitude: Boolean = true,
    val overlayCoordinates: Boolean = true,
    val overlayCompass: Boolean = true,
    val overlayCustomNote: Boolean = false,
    val customNoteText: String = "Survey site #104",
    val overlayLogo: Boolean = true,

    // 13. Overlay Position & Sizing
    val overlayPosition: OverlayPosition = OverlayPosition.BOTTOM,
    val overlayOpacity: Float = 0.85f,
    val overlayTextSizeSp: Float = 12f,

    // 14. Watermark
    val watermarkType: WatermarkType = WatermarkType.NONE,
    val watermarkCustomText: String = "PhotoViews Pro",
    val watermarkPosition: WatermarkPosition = WatermarkPosition.BOTTOM_RIGHT,
    val watermarkOpacity: Float = 0.75f,

    // 15. File Naming
    val fileNamingScheme: FileNamingScheme = FileNamingScheme.LOCATION_DATE_TIME,
    val customFilePrefix: String = "PhotoViews",
    val sequentialNumber: Int = 101,

    // 16. Save Options
    val saveOriginalPhoto: Boolean = true,
    val saveProcessedPhoto: Boolean = true,
    val saveBoth: Boolean = false,
    val autoSaveToFolder: String? = null,
    val galleryVisibility: Boolean = true,

    // 17. Sound & Capture
    val shutterSound: Boolean = true,
    val volumeButtonAction: VolumeButtonAction = VolumeButtonAction.CAPTURE,
    val vibrationFeedback: Boolean = true,
    val quickCapture: Boolean = false,

    // 18. Screen & Viewfinder
    val fullScreenPreview: Boolean = true,
    val keepScreenOn: Boolean = true
)
