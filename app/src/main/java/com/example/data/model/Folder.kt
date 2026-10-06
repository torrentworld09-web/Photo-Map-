package com.example.data.model

data class Folder(
    val id: String,
    val name: String,
    val iconName: String = "folder",
    val colorHex: String = "#3B82F6",
    val postCount: Int = 0,
    val storageBytes: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val formattedStorageSize: String
        get() {
            val mb = storageBytes.toDouble() / (1024 * 1024)
            return if (mb >= 1024) {
                String.format(java.util.Locale.US, "%.1f GB", mb / 1024)
            } else {
                String.format(java.util.Locale.US, "%.0f MB", mb)
            }
        }
}
