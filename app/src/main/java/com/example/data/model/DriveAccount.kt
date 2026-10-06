package com.example.data.model

data class LinkedDrive(
    val id: String,
    val email: String,
    val isPrimary: Boolean = false,
    val totalBytes: Long = 2_199_023_255_552L, // 2 TB
    val usedBytes: Long = 92_771_293_593L, // 86.4 GB
    val label: String = "Primary Drive",
    val linkedDate: String = "24 Sep 2026",
    val status: String = "Connected"
) {
    val usedGB: Double get() = usedBytes.toDouble() / (1024 * 1024 * 1024)
    val totalTB: Double get() = totalBytes.toDouble() / (1024L * 1024L * 1024L * 1024L)
    val usagePercentage: Float get() = (usedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
    val formattedUsed: String get() = String.format(java.util.Locale.US, "%.1f GB", usedGB)
    val formattedTotal: String get() = String.format(java.util.Locale.US, "%.0f TB", totalTB)
    val isFull: Boolean get() = usagePercentage >= 0.95f
}

data class DriveAccount(
    val currentAccountEmail: String = "storageaccount@gmail.com",
    val previousAccountEmail: String = "backup.photoviews@gmail.com",
    val isConnected: Boolean = true,
    val totalStorageBytes: Long = 2_199_023_255_552L, // 2 TB
    val usedStorageBytes: Long = 92_771_293_593L, // 86.4 GB
    val lastSyncTime: String = "Today, 10:24 AM",
    val lastAccountChangeDate: String = "24 Sep 2026",
    val autoSyncEnabled: Boolean = true,
    val wifiOnly: Boolean = false,
    val linkedDrives: List<LinkedDrive> = listOf(
        LinkedDrive(
            id = "drive_1",
            email = "storageaccount@gmail.com",
            isPrimary = true,
            totalBytes = 2_199_023_255_552L,
            usedBytes = 92_771_293_593L,
            label = "Primary Storage Drive",
            linkedDate = "24 Sep 2026",
            status = "Active"
        ),
        LinkedDrive(
            id = "drive_2",
            email = "photoviews.archive@gmail.com",
            isPrimary = false,
            totalBytes = 2_199_023_255_552L,
            usedBytes = 28_400_000_000L,
            label = "Secondary Backup Drive",
            linkedDate = "26 Sep 2026",
            status = "Standby"
        )
    )
) {
    val usedGB: Double
        get() = usedStorageBytes.toDouble() / (1024 * 1024 * 1024)

    val totalTB: Double
        get() = totalStorageBytes.toDouble() / (1024L * 1024L * 1024L * 1024L)

    val usagePercentage: Float
        get() = (usedStorageBytes.toFloat() / totalStorageBytes.toFloat()).coerceIn(0f, 1f)

    val formattedUsed: String
        get() = String.format(java.util.Locale.US, "%.1f GB", usedGB)

    val formattedTotal: String
        get() = String.format(java.util.Locale.US, "%.0f TB", totalTB)

    val isStorageFull: Boolean
        get() = usagePercentage >= 0.90f
}
