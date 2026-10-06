package com.example.util

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.data.local.SecurityPrefs
import com.example.data.model.DriveAppIcon
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

data class LauncherAliasInfo(
    val aliasName: String,
    val simpleName: String,
    val styleKey: String,
    val driveFileId: String,
    val displayName: String,
    val isDefault: Boolean = false
)

data class ApplyLauncherResult(
    val success: Boolean,
    val appliedAlias: LauncherAliasInfo,
    val previousAlias: LauncherAliasInfo?,
    val message: String,
    val isLauncherImmediate: Boolean = true
)

object AppIconLauncherManager {
    private const val TAG = "AppIconLauncherManager"
    private const val PACKAGE_NAME = "com.example"

    val SUPPORTED_ALIASES = listOf(
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityDefault",
            simpleName = "MainActivityDefault",
            styleKey = "classic",
            driveFileId = "drive_icon_default",
            displayName = "Photo Views Classic",
            isDefault = true
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityNeon",
            simpleName = "MainActivityNeon",
            styleKey = "neon",
            driveFileId = "drive_icon_neon",
            displayName = "Neon Aurora Glass"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityObsidian",
            simpleName = "MainActivityObsidian",
            styleKey = "obsidian",
            driveFileId = "drive_icon_obsidian",
            displayName = "Obsidian Dark Glass"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivitySunset",
            simpleName = "MainActivitySunset",
            styleKey = "sunset",
            driveFileId = "drive_icon_sunset",
            displayName = "Sunset Horizon"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityCobalt",
            simpleName = "MainActivityCobalt",
            styleKey = "cobalt",
            driveFileId = "drive_icon_cobalt",
            displayName = "Midnight Cobalt"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityEmerald",
            simpleName = "MainActivityEmerald",
            styleKey = "emerald",
            driveFileId = "drive_icon_emerald",
            displayName = "Emerald Nature"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityCyberpunk",
            simpleName = "MainActivityCyberpunk",
            styleKey = "cyberpunk",
            driveFileId = "drive_icon_cyberpunk",
            displayName = "Cyberpunk Violet"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityGolden",
            simpleName = "MainActivityGolden",
            styleKey = "golden",
            driveFileId = "drive_icon_golden",
            displayName = "Golden Hour"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityRose",
            simpleName = "MainActivityRose",
            styleKey = "rose",
            driveFileId = "drive_icon_rose",
            displayName = "Rose Prism"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityTitanium",
            simpleName = "MainActivityTitanium",
            styleKey = "titanium",
            driveFileId = "drive_icon_titanium",
            displayName = "Titanium Minimal"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityGlacier",
            simpleName = "MainActivityGlacier",
            styleKey = "glacier",
            driveFileId = "drive_icon_glacier",
            displayName = "Glacier Frost"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityVolcanic",
            simpleName = "MainActivityVolcanic",
            styleKey = "volcanic",
            driveFileId = "drive_icon_volcanic",
            displayName = "Volcanic Lava"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityCustom1",
            simpleName = "MainActivityCustom1",
            styleKey = "custom1",
            driveFileId = "custom_icon_1",
            displayName = "Custom Icon 1"
        ),
        LauncherAliasInfo(
            aliasName = "$PACKAGE_NAME.MainActivityCustom2",
            simpleName = "MainActivityCustom2",
            styleKey = "custom2",
            driveFileId = "custom_icon_2",
            displayName = "Custom Icon 2"
        )
    )

    val defaultAlias: LauncherAliasInfo
        get() = SUPPORTED_ALIASES.first { it.isDefault }

    /**
     * Finds the most appropriate launcher alias for a given DriveAppIcon.
     */
    fun findAliasForIcon(icon: DriveAppIcon): LauncherAliasInfo {
        // 1. Direct file ID match
        SUPPORTED_ALIASES.find { it.driveFileId == icon.driveFileId }?.let { return it }

        // 2. Direct styleKey match
        SUPPORTED_ALIASES.find { it.styleKey.equals(icon.styleKey, ignoreCase = true) }?.let { return it }

        // 3. Name or tone heuristic
        val nameLower = icon.name.lowercase()
        SUPPORTED_ALIASES.find { nameLower.contains(it.styleKey) }?.let { return it }

        // 4. Custom uploaded icon fallback: map deterministically to Custom1 or Custom2
        val customAliases = SUPPORTED_ALIASES.filter { it.styleKey.startsWith("custom") }
        if (customAliases.isNotEmpty()) {
            val index = abs(icon.driveFileId.hashCode()) % customAliases.size
            return customAliases[index]
        }

        return defaultAlias
    }

    /**
     * Determines which alias is currently enabled in Android PackageManager.
     */
    fun getActiveLauncherAlias(context: Context): LauncherAliasInfo {
        val pm = context.packageManager
        for (alias in SUPPORTED_ALIASES) {
            val component = ComponentName(context, alias.aliasName)
            try {
                val state = pm.getComponentEnabledSetting(component)
                if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                    return alias
                }
                // If it is the default alias and state is DEFAULT, it is enabled by manifest definition
                if (alias.isDefault && (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT || state == 0)) {
                    // Check if any other is explicitly enabled
                    val hasExplicitOther = SUPPORTED_ALIASES.any { other ->
                        !other.isDefault && pm.getComponentEnabledSetting(ComponentName(context, other.aliasName)) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    }
                    if (!hasExplicitOther) {
                        return alias
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error checking component state for ${alias.aliasName}", e)
            }
        }
        return defaultAlias
    }

    /**
     * Real Android launcher icon switching system.
     * Enables target alias, disables all others, updates recent-apps branding,
     * and persists the configuration.
     */
    fun applyLauncherIcon(
        context: Context,
        icon: DriveAppIcon,
        activity: Activity? = null
    ): ApplyLauncherResult {
        val pm = context.packageManager
        val targetAlias = findAliasForIcon(icon)
        val previousAlias = getActiveLauncherAlias(context)

        Log.i(TAG, "Switching launcher icon from ${previousAlias.simpleName} to ${targetAlias.simpleName} for ${icon.name}")

        try {
            // 1. Enable target alias FIRST so there is always a valid launcher entry point
            val targetComponent = ComponentName(context, targetAlias.aliasName)
            pm.setComponentEnabledSetting(
                targetComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            // 2. Disable all other aliases to avoid duplicate launcher icons
            for (other in SUPPORTED_ALIASES) {
                if (other.aliasName != targetAlias.aliasName) {
                    val otherComponent = ComponentName(context, other.aliasName)
                    pm.setComponentEnabledSetting(
                        otherComponent,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }

            // 3. Cache the Drive icon image locally if available
            cacheIconLocally(context, icon)

            // 4. Update recent-apps icon / task description where Android permits
            updateTaskDescription(context, icon, activity)

            // 5. Save in persistent security preferences
            val securityPrefs = SecurityPrefs(context)
            securityPrefs.activeAppIconDriveId = icon.driveFileId
            securityPrefs.activeLauncherAlias = targetAlias.aliasName

            return ApplyLauncherResult(
                success = true,
                appliedAlias = targetAlias,
                previousAlias = previousAlias,
                message = "Applied \"${icon.name}\" to Android Launcher Home Screen & App Drawer."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply launcher alias ${targetAlias.aliasName}", e)
            return ApplyLauncherResult(
                success = false,
                appliedAlias = previousAlias,
                previousAlias = previousAlias,
                message = "Failed to switch launcher icon: ${e.localizedMessage ?: "Unknown error"}"
            )
        }
    }

    /**
     * Restores the default launcher icon (Classic Photo Views).
     */
    fun restoreDefaultLauncherIcon(
        context: Context,
        activity: Activity? = null
    ): ApplyLauncherResult {
        val classicIcon = DriveAppIcon(
            driveFileId = "drive_icon_default",
            name = "Photo Views Classic",
            folderPath = "Icon/",
            uploadDateTime = "Today",
            styleKey = "classic",
            isDefault = true
        )
        return applyLauncherIcon(context, classicIcon, activity)
    }

    /**
     * Restores the saved active launcher icon after device restart or app startup.
     */
    fun restoreActiveLauncherIcon(context: Context) {
        val securityPrefs = SecurityPrefs(context)
        val savedAliasName = securityPrefs.activeLauncherAlias
        val targetAlias = SUPPORTED_ALIASES.find { it.aliasName == savedAliasName } ?: defaultAlias
        val currentActive = getActiveLauncherAlias(context)

        if (currentActive.aliasName != targetAlias.aliasName) {
            Log.i(TAG, "Restoring active launcher alias to ${targetAlias.simpleName} (was ${currentActive.simpleName})")
            val pm = context.packageManager
            pm.setComponentEnabledSetting(
                ComponentName(context, targetAlias.aliasName),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            for (other in SUPPORTED_ALIASES) {
                if (other.aliasName != targetAlias.aliasName) {
                    pm.setComponentEnabledSetting(
                        ComponentName(context, other.aliasName),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        }
    }

    /**
     * Updates recent-apps task branding where Android permits.
     */
    private fun updateTaskDescription(
        context: Context,
        icon: DriveAppIcon,
        activity: Activity?
    ) {
        val act = activity ?: (context as? Activity) ?: return
        try {
            val alias = findAliasForIcon(icon)
            val resId = context.resources.getIdentifier(
                "ic_launcher_${alias.styleKey}",
                "mipmap",
                context.packageName
            ).let { if (it != 0) it else com.example.R.mipmap.ic_launcher }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val builder = ActivityManager.TaskDescription.Builder()
                    .setLabel("Photo Views • ${icon.name}")
                    .setIcon(resId)
                act.setTaskDescription(builder.build())
            } else {
                @Suppress("DEPRECATION")
                val cachedBitmap = getCachedIconBitmap(context)
                @Suppress("DEPRECATION")
                act.setTaskDescription(ActivityManager.TaskDescription("Photo Views • ${icon.name}", cachedBitmap))
            }
        } catch (e: Exception) {
            Log.w(TAG, "TaskDescription update skipped: ${e.message}")
        }
    }

    /**
     * Caches icon file to private app storage.
     */
    private fun cacheIconLocally(context: Context, icon: DriveAppIcon) {
        try {
            if (icon.localCacheUri.isNotBlank()) {
                val uri = Uri.parse(icon.localCacheUri)
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val dest = File(context.filesDir, "active_launcher_icon.png")
                    FileOutputStream(dest).use { output ->
                        inputStream.copyTo(output)
                    }
                    inputStream.close()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not cache local icon file: ${e.message}")
        }
    }

    fun getCachedIconBitmap(context: Context): Bitmap? {
        val file = File(context.filesDir, "active_launcher_icon.png")
        return if (file.exists()) {
            try {
                BitmapFactory.decodeFile(file.absolutePath)
            } catch (e: Exception) {
                null
            }
        } else null
    }
}
