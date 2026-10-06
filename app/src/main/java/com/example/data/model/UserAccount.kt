package com.example.data.model

data class UserAccount(
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole = UserRole.USER,
    val isActive: Boolean = true,
    val lastLogin: String = "2 hours ago",
    val lastSync: String = "Today, 10:24 AM",
    val postCount: Int = 12,
    val photoCount: Int = 48,
    val storageBytes: Long = 2_400_000_000L
) {
    val formattedStorage: String
        get() {
            val gb = storageBytes.toDouble() / (1024 * 1024 * 1024)
            return if (gb >= 1.0) {
                String.format(java.util.Locale.US, "%.1f GB", gb)
            } else {
                val mb = storageBytes.toDouble() / (1024 * 1024)
                String.format(java.util.Locale.US, "%.1f MB", mb)
            }
        }
}
