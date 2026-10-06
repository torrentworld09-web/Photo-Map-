package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.DriveAccount
import com.example.data.model.GridLayoutMode
import com.example.data.model.ImageQuality
import com.example.data.model.UserRole
import java.security.MessageDigest
import java.util.UUID

class SecurityPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("photoviews_secure_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SALT = "salt_v1"
        private const val KEY_OWNER_ID = "owner_id"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_ACTIVE_ROLE = "active_role"
        private const val KEY_GRID_LAYOUT = "grid_layout"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_IMAGE_QUALITY = "image_quality"
        private const val KEY_AUTO_OPTIMIZE = "auto_optimize"
        private const val KEY_ORIGINAL_PRESERVE = "original_preserve"

        // Drive Storage Account
        private const val KEY_DRIVE_CURRENT_EMAIL = "drive_current_email"
        private const val KEY_DRIVE_PREV_EMAIL = "drive_prev_email"
        private const val KEY_DRIVE_CONNECTED = "drive_connected"
        private const val KEY_DRIVE_TOTAL_STORAGE = "drive_total_storage"
        private const val KEY_DRIVE_USED_STORAGE = "drive_used_storage"
        private const val KEY_DRIVE_LAST_SYNC = "drive_last_sync"
        private const val KEY_DRIVE_LAST_CHANGE_DATE = "drive_last_change_date"
        private const val KEY_DRIVE_AUTO_SYNC = "drive_auto_sync"
        private const val KEY_DRIVE_WIFI_ONLY = "drive_wifi_only"
        private const val KEY_DRIVE_LINKED_JSON = "drive_linked_json"

        // Camera settings
        private const val KEY_SAVE_LOCATION = "camera_save_location"
        private const val KEY_SAVE_PLUS_CODE = "camera_save_plus_code"
        private const val KEY_SAVE_DATETIME = "camera_save_datetime"
        private const val KEY_SAVE_CONDITIONS = "camera_save_conditions"

        // Recent searches
        private const val KEY_RECENT_SEARCHES = "recent_searches_json"

        // App Icon
        private const val KEY_ACTIVE_APP_ICON_ID = "active_app_icon_drive_id"
        private const val KEY_ACTIVE_LAUNCHER_ALIAS = "active_launcher_alias"

        // Default initial credentials hashed on first boot
        private const val DEFAULT_OWNER_ID = "Boogeymancartoons"
        private const val DEFAULT_INIT_PWD = "8825759388"
    }

    init {
        // Ensure salt and initial hash are configured
        if (!prefs.contains(KEY_SALT)) {
            val salt = UUID.randomUUID().toString()
            val initialHash = hashPassword(DEFAULT_INIT_PWD, salt)
            val initialSearches = org.json.JSONArray(listOf("Mountain", "Coimbatore", "9876543210", "Land Survey"))
            prefs.edit()
                .putString(KEY_SALT, salt)
                .putString(KEY_OWNER_ID, DEFAULT_OWNER_ID)
                .putString(KEY_PASSWORD_HASH, initialHash)
                .putString(KEY_ACTIVE_ROLE, UserRole.USER.name)
                .putString(KEY_GRID_LAYOUT, GridLayoutMode.MEDIUM_GRID.name)
                .putString(KEY_THEME_MODE, AppThemeMode.GLASS_LIGHT.name)
                .putString(KEY_IMAGE_QUALITY, ImageQuality.HIGH.name)
                .putBoolean(KEY_AUTO_OPTIMIZE, true)
                .putBoolean(KEY_ORIGINAL_PRESERVE, true)
                .putString(KEY_DRIVE_CURRENT_EMAIL, "storageaccount@gmail.com")
                .putString(KEY_DRIVE_PREV_EMAIL, "backup.photoviews@gmail.com")
                .putBoolean(KEY_DRIVE_CONNECTED, true)
                .putLong(KEY_DRIVE_TOTAL_STORAGE, 2_199_023_255_552L)
                .putLong(KEY_DRIVE_USED_STORAGE, 92_771_293_593L)
                .putString(KEY_DRIVE_LAST_SYNC, "Today, 10:24 AM")
                .putString(KEY_DRIVE_LAST_CHANGE_DATE, "24 Sep 2026")
                .putBoolean(KEY_DRIVE_AUTO_SYNC, true)
                .putBoolean(KEY_DRIVE_WIFI_ONLY, false)
                .putBoolean(KEY_SAVE_LOCATION, true)
                .putBoolean(KEY_SAVE_PLUS_CODE, true)
                .putBoolean(KEY_SAVE_DATETIME, true)
                .putBoolean(KEY_SAVE_CONDITIONS, true)
                .putString(KEY_RECENT_SEARCHES, initialSearches.toString())
                .apply()
        }
    }

    fun getRecentSearches(): List<String> {
        val json = prefs.getString(KEY_RECENT_SEARCHES, null)
            ?: return listOf("Mountain", "Coimbatore", "9876543210", "Land Survey")
        return try {
            val arr = org.json.JSONArray(json)
            val list = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                val item = arr.optString(i)
                if (item.isNotBlank()) list.add(item)
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        val current = getRecentSearches().filterNot { it.equals(trimmed, ignoreCase = true) }
        val updated = (listOf(trimmed) + current).take(15)
        val arr = org.json.JSONArray()
        updated.forEach { arr.put(it) }
        prefs.edit().putString(KEY_RECENT_SEARCHES, arr.toString()).apply()
    }

    fun removeRecentSearch(query: String) {
        val trimmed = query.trim()
        val current = getRecentSearches().filterNot { it.equals(trimmed, ignoreCase = true) }
        val arr = org.json.JSONArray()
        current.forEach { arr.put(it) }
        prefs.edit().putString(KEY_RECENT_SEARCHES, arr.toString()).apply()
    }

    fun clearRecentSearches() {
        val arr = org.json.JSONArray()
        prefs.edit().putString(KEY_RECENT_SEARCHES, arr.toString()).apply()
    }

    private fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest((salt + password).toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyOwner(ownerIdInput: String, passwordInput: String): Boolean {
        val currentOwnerId = prefs.getString(KEY_OWNER_ID, DEFAULT_OWNER_ID) ?: DEFAULT_OWNER_ID
        if (ownerIdInput.trim() != currentOwnerId.trim()) return false

        val salt = prefs.getString(KEY_SALT, "") ?: ""
        val storedHash = prefs.getString(KEY_PASSWORD_HASH, "") ?: ""
        val computedHash = hashPassword(passwordInput.trim(), salt)
        return storedHash.isNotEmpty() && storedHash == computedHash
    }

    fun verifyCurrentPassword(passwordInput: String): Boolean {
        val salt = prefs.getString(KEY_SALT, "") ?: ""
        val storedHash = prefs.getString(KEY_PASSWORD_HASH, "") ?: ""
        val computedHash = hashPassword(passwordInput.trim(), salt)
        return storedHash.isNotEmpty() && storedHash == computedHash
    }

    fun getCurrentOwnerId(): String {
        return prefs.getString(KEY_OWNER_ID, DEFAULT_OWNER_ID) ?: DEFAULT_OWNER_ID
    }

    fun updateOwnerCredentials(newOwnerId: String, newPasswordInput: String) {
        val newSalt = UUID.randomUUID().toString()
        val newHash = hashPassword(newPasswordInput.trim(), newSalt)
        prefs.edit()
            .putString(KEY_SALT, newSalt)
            .putString(KEY_OWNER_ID, newOwnerId.trim())
            .putString(KEY_PASSWORD_HASH, newHash)
            .apply()
    }

    var activeRole: UserRole
        get() {
            val name = prefs.getString(KEY_ACTIVE_ROLE, UserRole.USER.name) ?: UserRole.USER.name
            return try { UserRole.valueOf(name) } catch (e: Exception) { UserRole.USER }
        }
        set(value) = prefs.edit().putString(KEY_ACTIVE_ROLE, value.name).apply()

    var gridLayoutMode: GridLayoutMode
        get() {
            val name = prefs.getString(KEY_GRID_LAYOUT, GridLayoutMode.MEDIUM_GRID.name)
            return try { GridLayoutMode.valueOf(name ?: GridLayoutMode.MEDIUM_GRID.name) } catch (e: Exception) { GridLayoutMode.MEDIUM_GRID }
        }
        set(value) = prefs.edit().putString(KEY_GRID_LAYOUT, value.name).apply()

    var themeMode: AppThemeMode
        get() {
            val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.GLASS_DARK.name)
            return try { AppThemeMode.valueOf(name ?: AppThemeMode.GLASS_DARK.name) } catch (e: Exception) { AppThemeMode.GLASS_DARK }
        }
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value.name).apply()

    var imageQuality: ImageQuality
        get() {
            val name = prefs.getString(KEY_IMAGE_QUALITY, ImageQuality.HIGH.name)
            return try { ImageQuality.valueOf(name ?: ImageQuality.HIGH.name) } catch (e: Exception) { ImageQuality.HIGH }
        }
        set(value) = prefs.edit().putString(KEY_IMAGE_QUALITY, value.name).apply()

    var autoOptimize: Boolean
        get() = prefs.getBoolean(KEY_AUTO_OPTIMIZE, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_OPTIMIZE, value).apply()

    var originalPreservation: Boolean
        get() = prefs.getBoolean(KEY_ORIGINAL_PRESERVE, true)
        set(value) = prefs.edit().putBoolean(KEY_ORIGINAL_PRESERVE, value).apply()

    // Camera
    var saveLocation: Boolean
        get() = prefs.getBoolean(KEY_SAVE_LOCATION, true)
        set(value) = prefs.edit().putBoolean(KEY_SAVE_LOCATION, value).apply()

    var savePlusCode: Boolean
        get() = prefs.getBoolean(KEY_SAVE_PLUS_CODE, true)
        set(value) = prefs.edit().putBoolean(KEY_SAVE_PLUS_CODE, value).apply()

    var saveDateTime: Boolean
        get() = prefs.getBoolean(KEY_SAVE_DATETIME, true)
        set(value) = prefs.edit().putBoolean(KEY_SAVE_DATETIME, value).apply()

    var saveConditions: Boolean
        get() = prefs.getBoolean(KEY_SAVE_CONDITIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_SAVE_CONDITIONS, value).apply()

    // Active App Icon in Google Drive
    var activeAppIconDriveId: String
        get() = prefs.getString(KEY_ACTIVE_APP_ICON_ID, "drive_icon_default") ?: "drive_icon_default"
        set(value) = prefs.edit().putString(KEY_ACTIVE_APP_ICON_ID, value).apply()

    // Active Android Launcher Alias
    var activeLauncherAlias: String
        get() = prefs.getString(KEY_ACTIVE_LAUNCHER_ALIAS, "com.example.MainActivityDefault") ?: "com.example.MainActivityDefault"
        set(value) = prefs.edit().putString(KEY_ACTIVE_LAUNCHER_ALIAS, value).apply()

    // Drive Storage Account
    fun getDriveAccount(): DriveAccount {
        val linkedJson = prefs.getString(KEY_DRIVE_LINKED_JSON, null)
        val linkedDrivesList = if (!linkedJson.isNullOrBlank()) {
            try {
                val array = org.json.JSONArray(linkedJson)
                val list = mutableListOf<com.example.data.model.LinkedDrive>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        com.example.data.model.LinkedDrive(
                            id = obj.optString("id", "drive_$i"),
                            email = obj.optString("email", ""),
                            isPrimary = obj.optBoolean("isPrimary", false),
                            totalBytes = obj.optLong("totalBytes", 2_199_023_255_552L),
                            usedBytes = obj.optLong("usedBytes", 0L),
                            label = obj.optString("label", "Storage Drive"),
                            linkedDate = obj.optString("linkedDate", "24 Sep 2026"),
                            status = obj.optString("status", "Connected")
                        )
                    )
                }
                list
            } catch (e: Exception) {
                null
            }
        } else null

        val defaultAccount = DriveAccount()
        return DriveAccount(
            currentAccountEmail = prefs.getString(KEY_DRIVE_CURRENT_EMAIL, "storageaccount@gmail.com") ?: "storageaccount@gmail.com",
            previousAccountEmail = prefs.getString(KEY_DRIVE_PREV_EMAIL, "backup.photoviews@gmail.com") ?: "backup.photoviews@gmail.com",
            isConnected = prefs.getBoolean(KEY_DRIVE_CONNECTED, true),
            totalStorageBytes = prefs.getLong(KEY_DRIVE_TOTAL_STORAGE, 2_199_023_255_552L),
            usedStorageBytes = prefs.getLong(KEY_DRIVE_USED_STORAGE, 92_771_293_593L),
            lastSyncTime = prefs.getString(KEY_DRIVE_LAST_SYNC, "Today, 10:24 AM") ?: "Today, 10:24 AM",
            lastAccountChangeDate = prefs.getString(KEY_DRIVE_LAST_CHANGE_DATE, "24 Sep 2026") ?: "24 Sep 2026",
            autoSyncEnabled = prefs.getBoolean(KEY_DRIVE_AUTO_SYNC, true),
            wifiOnly = prefs.getBoolean(KEY_DRIVE_WIFI_ONLY, false),
            linkedDrives = linkedDrivesList ?: defaultAccount.linkedDrives
        )
    }

    fun updateDriveAccount(account: DriveAccount) {
        val jsonArray = org.json.JSONArray()
        account.linkedDrives.forEach { drive ->
            val obj = org.json.JSONObject().apply {
                put("id", drive.id)
                put("email", drive.email)
                put("isPrimary", drive.isPrimary)
                put("totalBytes", drive.totalBytes)
                put("usedBytes", drive.usedBytes)
                put("label", drive.label)
                put("linkedDate", drive.linkedDate)
                put("status", drive.status)
            }
            jsonArray.put(obj)
        }

        prefs.edit()
            .putString(KEY_DRIVE_CURRENT_EMAIL, account.currentAccountEmail)
            .putString(KEY_DRIVE_PREV_EMAIL, account.previousAccountEmail)
            .putBoolean(KEY_DRIVE_CONNECTED, account.isConnected)
            .putLong(KEY_DRIVE_TOTAL_STORAGE, account.totalStorageBytes)
            .putLong(KEY_DRIVE_USED_STORAGE, account.usedStorageBytes)
            .putString(KEY_DRIVE_LAST_SYNC, account.lastSyncTime)
            .putString(KEY_DRIVE_LAST_CHANGE_DATE, account.lastAccountChangeDate)
            .putBoolean(KEY_DRIVE_AUTO_SYNC, account.autoSyncEnabled)
            .putBoolean(KEY_DRIVE_WIFI_ONLY, account.wifiOnly)
            .putString(KEY_DRIVE_LINKED_JSON, jsonArray.toString())
            .apply()
    }
}
