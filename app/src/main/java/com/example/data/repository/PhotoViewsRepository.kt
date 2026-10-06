package com.example.data.repository

import android.app.Activity
import android.content.Context
import com.example.data.cloud.CloudPostRecord
import com.example.data.cloud.CloudStorageManager
import com.example.data.cloud.CloudStorageStats
import com.example.data.cloud.SyncRestoreResult
import com.example.data.local.AppDatabase
import com.example.data.local.FolderEntity
import com.example.data.local.PostEntity
import com.example.data.local.SecurityPrefs
import com.example.data.local.ThemeManager
import com.example.data.local.UserEntity
import com.example.data.model.AppThemeMode
import com.example.data.model.DriveAccount
import com.example.data.model.DriveAppIcon
import com.example.data.model.Folder
import com.example.data.model.GridLayoutMode
import com.example.data.model.ImageQuality
import com.example.data.model.Post
import com.example.data.model.SyncProgressStep
import com.example.data.model.SyncState
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.theme.GlassThemePresets
import com.example.ui.theme.GlassThemeState
import com.example.util.AppIconLauncherManager
import com.example.util.ApplyLauncherResult
import com.example.util.BackupHelper
import com.example.util.NotificationHelper
import com.example.util.SampleDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class PhotoViewsRepository(
    private val database: AppDatabase,
    private val securityPrefs: SecurityPrefs,
    private val context: Context
) {
    private val postDao = database.postDao()
    private val folderDao = database.folderDao()
    private val userDao = database.userDao()
    private val cloudStorageManager = CloudStorageManager(context)
    private val themeManager = ThemeManager(context)

    // Glass Theming System Reactive StateFlows
    val activeGlassTheme = themeManager.activeTheme
    val customGlassThemes = themeManager.customThemes

    // Cloud Storage reactive state flows
    private val _cloudStats = MutableStateFlow(cloudStorageManager.getCloudStorageStats())
    val cloudStats = _cloudStats.asStateFlow()

    private val _locallyDeletedCloudPosts = MutableStateFlow(cloudStorageManager.getLocallyDeletedPosts())
    val locallyDeletedCloudPosts = _locallyDeletedCloudPosts.asStateFlow()

    private val _allCloudPosts = MutableStateFlow(cloudStorageManager.getAllCloudPosts())
    val allCloudPosts = _allCloudPosts.asStateFlow()

    fun refreshCloudData() {
        _cloudStats.value = cloudStorageManager.getCloudStorageStats()
        _locallyDeletedCloudPosts.value = cloudStorageManager.getLocallyDeletedPosts()
        _allCloudPosts.value = cloudStorageManager.getAllCloudPosts()
    }

    // Preferences & Settings StateFlows
    private val _activeRole = MutableStateFlow(securityPrefs.activeRole)
    val activeRole = _activeRole.asStateFlow()

    private val _gridLayoutMode = MutableStateFlow(securityPrefs.gridLayoutMode)
    val gridLayoutMode = _gridLayoutMode.asStateFlow()

    private val _themeMode = MutableStateFlow(securityPrefs.themeMode)
    val themeMode = _themeMode.asStateFlow()

    private val _imageQuality = MutableStateFlow(securityPrefs.imageQuality)
    val imageQuality = _imageQuality.asStateFlow()

    private val _driveAccount = MutableStateFlow(securityPrefs.getDriveAccount())
    val driveAccount = _driveAccount.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    // Recent Searches reactive state flow
    private val _recentSearches = MutableStateFlow(securityPrefs.getRecentSearches())
    val recentSearches = _recentSearches.asStateFlow()

    fun addRecentSearch(query: String) {
        securityPrefs.addRecentSearch(query)
        _recentSearches.value = securityPrefs.getRecentSearches()
    }

    fun removeRecentSearch(query: String) {
        securityPrefs.removeRecentSearch(query)
        _recentSearches.value = securityPrefs.getRecentSearches()
    }

    fun clearRecentSearches() {
        securityPrefs.clearRecentSearches()
        _recentSearches.value = emptyList()
    }

    // Google Drive "Icon" Folder App Icons
    private val _driveAppIcons = MutableStateFlow(cloudStorageManager.getDriveAppIcons())
    val driveAppIcons = _driveAppIcons.asStateFlow()

    private val _activeAppIconDriveId = MutableStateFlow(securityPrefs.activeAppIconDriveId)
    val activeAppIconDriveId = _activeAppIconDriveId.asStateFlow()

    fun setActiveAppIcon(driveFileId: String, activity: Activity? = null): ApplyLauncherResult {
        securityPrefs.activeAppIconDriveId = driveFileId
        _activeAppIconDriveId.value = driveFileId
        cloudStorageManager.updateActiveIconInCloud(driveFileId)

        val icon = _driveAppIcons.value.find { it.driveFileId == driveFileId }
            ?: AppIconLauncherManager.SUPPORTED_ALIASES.find { it.driveFileId == driveFileId }?.let {
                DriveAppIcon(
                    driveFileId = it.driveFileId,
                    name = it.displayName,
                    folderPath = "Icon/",
                    uploadDateTime = "Today",
                    styleKey = it.styleKey,
                    isDefault = it.isDefault
                )
            }
            ?: _driveAppIcons.value.firstOrNull()
            ?: DriveAppIcon("drive_icon_default", "Photo Views Classic", "Icon/", "", "Today", isDefault = true)

        return AppIconLauncherManager.applyLauncherIcon(context, icon, activity)
    }

    fun restoreDefaultAppIcon(activity: Activity? = null): ApplyLauncherResult {
        val result = AppIconLauncherManager.restoreDefaultLauncherIcon(context, activity)
        _activeAppIconDriveId.value = "drive_icon_default"
        securityPrefs.activeAppIconDriveId = "drive_icon_default"
        cloudStorageManager.updateActiveIconInCloud("drive_icon_default")
        return result
    }

    suspend fun syncAppIconsWithDrive(): List<DriveAppIcon> {
        val icons = cloudStorageManager.syncAppIconsWithDrive()
        _driveAppIcons.value = icons

        // Restore active icon from Google Drive cloud settings if synchronized across devices
        val cloudActiveId = cloudStorageManager.getActiveIconFromCloud()
        if (cloudActiveId.isNotBlank() && icons.any { it.driveFileId == cloudActiveId }) {
            if (_activeAppIconDriveId.value != cloudActiveId) {
                securityPrefs.activeAppIconDriveId = cloudActiveId
                _activeAppIconDriveId.value = cloudActiveId
                val targetIcon = icons.find { it.driveFileId == cloudActiveId }
                if (targetIcon != null) {
                    AppIconLauncherManager.applyLauncherIcon(context, targetIcon)
                }
            }
        } else if (icons.none { it.driveFileId == _activeAppIconDriveId.value }) {
            val fallback = icons.firstOrNull()?.driveFileId ?: "drive_icon_default"
            securityPrefs.activeAppIconDriveId = fallback
            _activeAppIconDriveId.value = fallback
            val targetIcon = icons.firstOrNull() ?: DriveAppIcon("drive_icon_default", "Photo Views Classic", "Icon/", "", "Today", isDefault = true)
            AppIconLauncherManager.applyLauncherIcon(context, targetIcon)
        }
        return icons
    }

    suspend fun uploadNewAppIcon(name: String, sourceUri: String): DriveAppIcon {
        val newIcon = cloudStorageManager.uploadNewAppIcon(name, sourceUri)
        _driveAppIcons.value = cloudStorageManager.getDriveAppIcons()
        return newIcon
    }

    suspend fun renameAppIcon(driveFileId: String, newName: String): Boolean {
        val ok = cloudStorageManager.renameAppIconInDrive(driveFileId, newName)
        if (ok) {
            _driveAppIcons.value = cloudStorageManager.getDriveAppIcons()
        }
        return ok
    }

    suspend fun deleteAppIconFromDrive(driveFileId: String): Boolean {
        val ok = cloudStorageManager.deleteAppIconFromDrive(driveFileId)
        if (ok) {
            val updated = cloudStorageManager.getDriveAppIcons()
            _driveAppIcons.value = updated
            if (_activeAppIconDriveId.value == driveFileId) {
                val fallback = updated.firstOrNull()?.driveFileId ?: "drive_icon_default"
                securityPrefs.activeAppIconDriveId = fallback
                _activeAppIconDriveId.value = fallback
                restoreDefaultAppIcon()
            }
        }
        return ok
    }

    // Posts Flows
    val allPosts: Flow<List<Post>> = postDao.getAllPosts().map { list -> list.map { it.toPost() } }
    val favoritePosts: Flow<List<Post>> = postDao.getFavoritePosts().map { list -> list.map { it.toPost() } }
    val cameraPosts: Flow<List<Post>> = postDao.getCameraPosts().map { list -> list.map { it.toPost() } }
    val importedPosts: Flow<List<Post>> = postDao.getImportedPosts().map { list -> list.map { it.toPost() } }
    val offlinePosts: Flow<List<Post>> = postDao.getOfflinePosts().map { list -> list.map { it.toPost() } }

    val allFolders: Flow<List<Folder>> = folderDao.getAllFolders().map { list -> list.map { it.toFolder() } }
    val allUsers: Flow<List<UserAccount>> = userDao.getAllUsers().map { list -> list.map { it.toUser() } }

    fun getPostsByFolder(folderId: String): Flow<List<Post>> =
        postDao.getPostsByFolder(folderId).map { list -> list.map { it.toPost() } }

    fun searchPosts(query: String): Flow<List<Post>> =
        postDao.searchPosts(query).map { list -> list.map { it.toPost() } }

    suspend fun getPostById(id: String): Post? = withContext(Dispatchers.IO) {
        postDao.getPostById(id)?.toPost()
    }

    suspend fun getFolderById(id: String): Folder? = withContext(Dispatchers.IO) {
        folderDao.getFolderById(id)?.toFolder()
    }

    suspend fun insertPost(post: Post) = withContext(Dispatchers.IO) {
        val syncedPost = post.copy(syncStatus = SyncState.CLOUD_BACKED_UP)
        postDao.insertPost(PostEntity.fromPost(syncedPost))
        cloudStorageManager.backupPost(syncedPost)
        updateFolderStatistics(post.folderId)
        NotificationHelper.notifyNewPostUploaded(context, post)
        refreshCloudData()
    }

    suspend fun updatePost(post: Post) = withContext(Dispatchers.IO) {
        postDao.updatePost(PostEntity.fromPost(post))
        cloudStorageManager.backupPost(post)
        updateFolderStatistics(post.folderId)
        refreshCloudData()
    }

    /**
     * CRITICAL USER REQUIREMENT 1 & 7:
     * Local Delete Without Cloud Delete
     * Deletes post ONLY from local database. Cloud backup remains intact and safe.
     */
    suspend fun deletePost(id: String): Boolean = withContext(Dispatchers.IO) {
        deletePostLocally(id)
    }

    suspend fun deletePostLocally(id: String): Boolean = withContext(Dispatchers.IO) {
        val existing = postDao.getPostById(id)
        val folderId = existing?.folderId
        val deletedRows = postDao.deletePostById(id)
        cloudStorageManager.markPostDeletedLocally(id)
        if (folderId != null) {
            updateFolderStatistics(folderId)
        }
        refreshCloudData()
        deletedRows > 0
    }

    /**
     * CRITICAL USER REQUIREMENT 3, 4, 5 & 8:
     * Owner-Only Permanent Cloud Deletion
     * Server-side authorization check: only executed if caller has OWNER role.
     */
    suspend fun permanentCloudDeletePost(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (securityPrefs.activeRole != UserRole.OWNER) {
            return@withContext Result.failure(
                SecurityException("Unauthorized: Only authenticated Owner can permanently delete cloud data")
            )
        }
        val existing = postDao.getPostById(id)
        val folderId = existing?.folderId
        postDao.deletePostById(id)
        val removedFromCloud = cloudStorageManager.permanentlyDeleteCloudPost(id, isOwner = true)
        if (folderId != null) {
            updateFolderStatistics(folderId)
        }
        refreshCloudData()
        Result.success(removedFromCloud)
    }

    /**
     * CRITICAL USER REQUIREMENT 2:
     * Restore & Sync from Cloud
     * Rebuilds local database from cloud backup without duplicate posts (matching unique postId).
     */
    suspend fun restoreAndSyncFromCloud(
        onProgress: suspend (SyncProgressStep) -> Unit
    ): SyncRestoreResult = withContext(Dispatchers.IO) {
        _isSyncing.value = true
        try {
            val result = cloudStorageManager.restoreAndSyncFromCloud(postDao, folderDao, onProgress)
            val folders = folderDao.getAllFolders().first()
            folders.forEach { updateFolderStatistics(it.id) }
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
            val now = System.currentTimeMillis()
            val currentDrive = _driveAccount.value
            val updated = currentDrive.copy(lastSyncTime = "Today, ${timeFormat.format(now)}")
            securityPrefs.updateDriveAccount(updated)
            _driveAccount.value = updated
            refreshCloudData()
            result
        } finally {
            _isSyncing.value = false
        }
    }

    suspend fun restoreSinglePostFromCloud(postId: String): Boolean = withContext(Dispatchers.IO) {
        val success = cloudStorageManager.restoreSinglePostToDevice(postId, postDao)
        if (success) {
            val restored = postDao.getPostById(postId)
            if (restored?.folderId != null) {
                updateFolderStatistics(restored.folderId)
            }
            refreshCloudData()
        }
        success
    }

    suspend fun deleteAllPosts(): Boolean = withContext(Dispatchers.IO) {
        val allPostsList = postDao.getAllPosts().first()
        allPostsList.forEach { post ->
            cloudStorageManager.markPostDeletedLocally(post.id)
        }
        postDao.deleteAllPosts()
        val folders = folderDao.getAllFolders().first()
        folders.forEach { updateFolderStatistics(it.id) }
        refreshCloudData()
        true
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        postDao.deleteAllPosts()
        folderDao.deleteAllFolders()
        folderDao.insertFolders(SampleDataGenerator.sampleFolders)
        postDao.insertPosts(SampleDataGenerator.samplePosts)
        userDao.insertUsers(SampleDataGenerator.sampleUsers)
        val folders = folderDao.getAllFolders().first()
        folders.forEach { updateFolderStatistics(it.id) }
    }

    suspend fun deletePosts(ids: List<String>): Boolean = withContext(Dispatchers.IO) {
        val count = postDao.deletePostsByIds(ids)
        // Refresh folders stats
        val folders = folderDao.getAllFolders().first()
        folders.forEach { updateFolderStatistics(it.id) }
        count > 0
    }

    suspend fun toggleFavorite(id: String, isFav: Boolean) = withContext(Dispatchers.IO) {
        postDao.updateFavorite(id, isFav)
    }

    suspend fun movePostToFolder(id: String, targetFolderId: String?, targetFolderName: String) =
        withContext(Dispatchers.IO) {
            val existing = postDao.getPostById(id)
            val oldFolderId = existing?.folderId
            postDao.updateFolder(id, targetFolderId, targetFolderName)
            if (oldFolderId != null) updateFolderStatistics(oldFolderId)
            if (targetFolderId != null) updateFolderStatistics(targetFolderId)
        }

    suspend fun createFolder(name: String, colorHex: String = "#3B82F6"): Folder = withContext(Dispatchers.IO) {
        val folder = Folder(
            id = "folder_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            iconName = "folder",
            colorHex = colorHex,
            postCount = 0,
            storageBytes = 0L,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        folderDao.insertFolder(FolderEntity.fromFolder(folder))
        folder
    }

    suspend fun renameFolder(id: String, newName: String) = withContext(Dispatchers.IO) {
        folderDao.renameFolder(id, newName.trim())
    }

    suspend fun deleteFolder(id: String, deleteInnerPosts: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        if (deleteInnerPosts) {
            val posts = postDao.getPostsByFolder(id).first()
            if (posts.isNotEmpty()) {
                postDao.deletePostsByIds(posts.map { it.id })
            }
        } else {
            // Unassign posts from this folder
            val posts = postDao.getPostsByFolder(id).first()
            posts.forEach { post ->
                postDao.updateFolder(post.id, null, "Unorganized")
            }
        }
        val rows = folderDao.deleteFolderById(id)
        rows > 0
    }

    private suspend fun updateFolderStatistics(folderId: String?) {
        if (folderId.isNullOrEmpty()) return
        val folder = folderDao.getFolderById(folderId) ?: return
        val posts = postDao.getPostsByFolder(folderId).first()
        val count = posts.size
        val totalBytes = posts.sumOf { it.storageBytes }
        folderDao.updateFolder(
            folder.copy(
                postCount = count,
                storageBytes = totalBytes,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // Role & Preferences
    fun setActiveRole(role: UserRole) {
        securityPrefs.activeRole = role
        _activeRole.value = role
    }

    fun setGridLayoutMode(mode: GridLayoutMode) {
        securityPrefs.gridLayoutMode = mode
        _gridLayoutMode.value = mode
    }

    fun setThemeMode(mode: AppThemeMode) {
        securityPrefs.themeMode = mode
        _themeMode.value = mode
        when (mode) {
            AppThemeMode.GLASS_LIGHT -> themeManager.selectPreset(GlassThemePresets.LightGlass.id)
            AppThemeMode.GLASS_DARK -> themeManager.selectPreset(GlassThemePresets.PremiumDarkGlass.id)
            AppThemeMode.SYSTEM -> themeManager.selectPreset(GlassThemePresets.PremiumDarkGlass.id)
        }
    }

    fun selectThemePreset(presetId: String) {
        themeManager.selectPreset(presetId)
    }

    fun applyGlassTheme(theme: GlassThemeState) {
        themeManager.applyTheme(theme)
    }

    fun saveCustomTheme(theme: GlassThemeState) {
        themeManager.saveCustomTheme(theme)
    }

    fun duplicateTheme(theme: GlassThemeState) {
        themeManager.duplicateTheme(theme)
    }

    fun renameCustomTheme(themeId: String, newName: String) {
        themeManager.renameCustomTheme(themeId, newName)
    }

    fun deleteCustomTheme(themeId: String) {
        themeManager.deleteCustomTheme(themeId)
    }

    fun importThemeJson(jsonString: String): Boolean {
        val result = themeManager.importThemeJson(jsonString)
        return result.isSuccess
    }

    fun exportThemeJson(theme: GlassThemeState): String {
        return themeManager.exportThemeJson(theme)
    }

    fun resetThemeToDefault() {
        themeManager.resetToDefault()
        securityPrefs.themeMode = AppThemeMode.GLASS_DARK
        _themeMode.value = AppThemeMode.GLASS_DARK
    }

    fun setImageQuality(quality: ImageQuality) {
        securityPrefs.imageQuality = quality
        _imageQuality.value = quality
    }

    // Owner Auth
    fun verifyOwner(ownerId: String, pass: String): Boolean {
        return securityPrefs.verifyOwner(ownerId, pass)
    }

    fun verifyCurrentOwnerPassword(pass: String): Boolean {
        return securityPrefs.verifyCurrentPassword(pass)
    }

    fun updateOwnerCredentials(newOwnerId: String, newPass: String) {
        securityPrefs.updateOwnerCredentials(newOwnerId, newPass)
    }

    // User management
    suspend fun setUserActive(userId: String, active: Boolean) = withContext(Dispatchers.IO) {
        userDao.setUserActive(userId, active)
    }

    // Google Drive Storage Account Management (Owner-level)
    fun changeDriveStorageAccount(newEmail: String) {
        val current = _driveAccount.value
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val now = System.currentTimeMillis()

        val updated = current.copy(
            previousAccountEmail = current.currentAccountEmail,
            currentAccountEmail = newEmail.trim(),
            isConnected = true,
            lastAccountChangeDate = dateFormat.format(now),
            lastSyncTime = "Today, ${timeFormat.format(now)}"
        )
        securityPrefs.updateDriveAccount(updated)
        _driveAccount.value = updated
    }

    fun disconnectDriveAccount() {
        val current = _driveAccount.value
        val updated = current.copy(isConnected = false)
        securityPrefs.updateDriveAccount(updated)
        _driveAccount.value = updated
    }

    fun connectDriveAccount(email: String = "storageaccount@gmail.com") {
        val current = _driveAccount.value
        val updated = current.copy(currentAccountEmail = email, isConnected = true)
        securityPrefs.updateDriveAccount(updated)
        _driveAccount.value = updated
    }

    fun addLinkedDrive(email: String, label: String = "Secondary Drive") {
        val current = _driveAccount.value
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val newDrive = com.example.data.model.LinkedDrive(
            id = "drive_${UUID.randomUUID().toString().take(8)}",
            email = email.trim(),
            isPrimary = false,
            totalBytes = 2_199_023_255_552L,
            usedBytes = 0L,
            label = label.ifBlank { "Secondary Drive" },
            linkedDate = dateFormat.format(System.currentTimeMillis()),
            status = "Connected"
        )
        val updatedList = current.linkedDrives + newDrive
        val updated = current.copy(linkedDrives = updatedList)
        securityPrefs.updateDriveAccount(updated)
        _driveAccount.value = updated
    }

    fun removeLinkedDrive(driveId: String) {
        val current = _driveAccount.value
        val updatedList = current.linkedDrives.filter { it.id != driveId }
        val updated = current.copy(linkedDrives = updatedList)
        securityPrefs.updateDriveAccount(updated)
        _driveAccount.value = updated
    }

    fun setPrimaryLinkedDrive(driveId: String) {
        val current = _driveAccount.value
        val target = current.linkedDrives.firstOrNull { it.id == driveId } ?: return
        val updatedList = current.linkedDrives.map {
            it.copy(isPrimary = (it.id == driveId))
        }
        val updated = current.copy(
            currentAccountEmail = target.email,
            linkedDrives = updatedList
        )
        securityPrefs.updateDriveAccount(updated)
        _driveAccount.value = updated
    }

    suspend fun syncNow() = withContext(Dispatchers.IO) {
        _isSyncing.value = true
        delay(1200) // Simulated network sync with Drive
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val now = System.currentTimeMillis()
        val current = _driveAccount.value
        val updated = current.copy(
            lastSyncTime = "Today, ${timeFormat.format(now)}"
        )
        securityPrefs.updateDriveAccount(updated)
        _driveAccount.value = updated
        _isSyncing.value = false
    }

    // Backup & Restore
    suspend fun exportAllDataJson(): String = withContext(Dispatchers.IO) {
        val posts = postDao.getAllPosts().first().map { it.toPost() }
        val folders = folderDao.getAllFolders().first().map { it.toFolder() }
        BackupHelper.createBackupJson(posts, folders, "ALL_DATA")
    }

    suspend fun importData(bgContent: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val result = BackupHelper.validateAndParseBgContent(bgContent)
        if (!result.isValid) {
            return@withContext Pair(false, result.errorMessage ?: "Invalid backup file")
        }

        // Insert folders first
        result.folders.forEach { folder ->
            val existing = folderDao.getFolderById(folder.id)
            if (existing == null) {
                folderDao.insertFolder(FolderEntity.fromFolder(folder))
            }
        }

        // Insert posts
        var importedCount = 0
        result.posts.forEach { post ->
            val existing = postDao.getPostById(post.id)
            if (existing == null) {
                postDao.insertPost(PostEntity.fromPost(post.copy(isImported = true)))
                importedCount++
            }
        }

        Pair(true, "Successfully restored ${result.folders.size} folders and $importedCount posts.")
    }

    suspend fun initializeSampleDataIfNeeded() = withContext(Dispatchers.IO) {
        val count = postDao.getPostCount()
        if (count == 0) {
            folderDao.insertFolders(SampleDataGenerator.sampleFolders)
            postDao.insertPosts(SampleDataGenerator.samplePosts)
            userDao.insertUsers(SampleDataGenerator.sampleUsers)
        }
    }
}
