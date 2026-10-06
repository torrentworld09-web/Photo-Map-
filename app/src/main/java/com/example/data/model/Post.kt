package com.example.data.model

data class Post(
    val id: String, // e.g. "BG-20260927-000001"
    val title: String,
    val description: String = "",
    val mobile: String = "",
    val mapsUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String = "",
    val plusCode: String = "",
    val date: String = "",
    val time: String = "",
    val conditions: String = "Sunny",
    val folderId: String? = null,
    val folderName: String = "Unorganized",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val storageBytes: Long = 4_200_000L, // in bytes
    val cloudFileId: String? = null,
    val syncStatus: SyncState = SyncState.SYNCED,
    val photoUris: List<String> = emptyList(), // URI or image asset path
    val tags: List<String> = emptyList(),
    val isCameraPhoto: Boolean = false,
    val isImported: Boolean = false,
    val isDownloaded: Boolean = true
) {
    val primaryPhotoUri: String
        get() = photoUris.firstOrNull() ?: ""

    val formattedStorageSize: String
        get() {
            val mb = storageBytes.toDouble() / (1024 * 1024)
            return if (mb >= 1024) {
                String.format(java.util.Locale.US, "%.1f GB", mb / 1024)
            } else {
                String.format(java.util.Locale.US, "%.1f MB", mb)
            }
        }
}
