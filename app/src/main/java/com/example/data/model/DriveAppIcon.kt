package com.example.data.model

import java.util.Locale

data class DriveAppIcon(
    val driveFileId: String,
    val name: String,
    val folderPath: String = "Icon/",
    val localCacheUri: String = "",
    val uploadDateTime: String,
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val fileSizeBytes: Long = 142_850L,
    val primaryColorHex: String = "#3B82F6",
    val secondaryColorHex: String = "#1D4ED8",
    val styleKey: String = "classic",
    val isDefault: Boolean = false
) {
    val formattedFileSize: String
        get() {
            val kb = fileSizeBytes.toDouble() / 1024.0
            return if (kb >= 1024.0) {
                String.format(Locale.US, "%.1f MB", kb / 1024.0)
            } else {
                String.format(Locale.US, "%.0f KB", kb)
            }
        }

    val fullDrivePath: String
        get() = "${folderPath.trimEnd('/')}/$name"
}
