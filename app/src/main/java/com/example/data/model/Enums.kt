package com.example.data.model

enum class GridLayoutMode(val displayName: String, val columns: Int) {
    LARGE_GRID("Large Grid", 1),
    MEDIUM_GRID("Medium Grid", 2),
    SMALL_GRID("Small Grid", 3),
    COMPACT_GRID("Compact Grid", 4),
    TWO_COLUMN("Two-column Grid", 2),
    THREE_COLUMN("Three-column Grid", 3),
    FOUR_COLUMN("Four-column Grid", 4),
    MASONRY("Masonry Grid", 2),
    LIST_VIEW("List View", 1),
    LARGE_CARD_VIEW("Large Card View", 1),
    TELEGRAM_COMPACT("Telegram-style compact", 3)
}

enum class SyncState(val label: String) {
    LOCAL_ONLY("Local Only"),
    CLOUD_BACKED_UP("Cloud Backed Up"),
    SYNCED("Synced"),
    PENDING_UPLOAD("Pending Upload"),
    PENDING_RESTORE("Pending Restore"),
    DELETED_LOCALLY("Deleted Locally"),
    PERMANENTLY_DELETED("Permanently Deleted"),
    UPLOADING("Uploading..."),
    DOWNLOADING("Downloading..."),
    PENDING("Pending Sync"),
    FAILED("Sync Failed"),
    OFFLINE("Offline")
}

enum class SyncProgressStep(val stepNumber: Int, val title: String, val description: String) {
    PREPARING(1, "Preparing", "Scanning cloud storage and verifying account credentials..."),
    DOWNLOADING(2, "Downloading", "Retrieving cloud-backed-up photos and metadata..."),
    RESTORING(3, "Restoring", "Rebuilding local database, folders, and links..."),
    COMPLETE(4, "Sync Complete", "All cloud-backed-up data has been restored successfully.")
}

enum class ImageQuality(val displayName: String, val compressionQuality: Int, val maxDimension: Int) {
    MAXIMUM("Maximum Quality", 95, 3840),
    HIGH("High Quality (Default)", 85, 2560),
    BALANCED("Balanced", 75, 1920),
    STORAGE_SAVER("Storage Saver", 60, 1280)
}

enum class SortOption(val displayName: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    NAME_AZ("Name A-Z"),
    NAME_ZA("Name Z-A"),
    LARGEST_STORAGE("Largest Storage"),
    SMALLEST_STORAGE("Smallest Storage"),
    RECENTLY_MODIFIED("Recently Modified")
}

enum class AppThemeMode(val displayName: String) {
    GLASS_LIGHT("Light Glass"),
    GLASS_DARK("Dark Glass"),
    SYSTEM("System Default")
}

enum class UserRole {
    OWNER,
    USER
}
