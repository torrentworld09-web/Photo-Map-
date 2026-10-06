package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.cloud.SyncRestoreResult
import com.example.data.model.AppThemeMode
import com.example.data.model.Folder
import com.example.data.model.GridLayoutMode
import com.example.data.model.Post
import com.example.data.model.SyncProgressStep
import com.example.data.model.UserRole
import com.example.data.repository.PhotoViewsRepository
import com.example.ui.components.CreateFolderDialog
import com.example.ui.components.DeleteFolderConfirmDialog
import com.example.ui.components.DeletePostConfirmDialog
import com.example.ui.components.FullScreenPostViewer
import com.example.ui.components.GlassCard
import com.example.ui.components.OwnerPermanentDeleteDialog
import com.example.ui.components.PostOptionsMenu
import com.example.ui.components.SyncProgressDialog
import com.example.ui.screens.AddEditPostScreen
import com.example.ui.screens.AppIconScreen
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.FilterSortSheet
import com.example.ui.screens.FilterState
import com.example.ui.screens.FoldersScreen
import com.example.ui.screens.GoogleDriveScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImageCompressionPreviewScreen
import com.example.ui.screens.ImportExportScreen
import com.example.ui.screens.OwnerDashboardScreen
import com.example.ui.screens.OwnerLoginScreen
import com.example.ui.screens.OwnerSettingsScreen
import com.example.ui.screens.PostsGridScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StorageDetailsScreen
import com.example.ui.screens.ThemeCustomizerScreen
import com.example.ui.screens.UserManagementScreen
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandRed
import com.example.ui.theme.PhotoViewsTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class PostsGrid(val title: String, val sectionKey: String, val folderId: String? = null) : Screen()
    object Folders : Screen()
    object Favorites : Screen()
    object Camera : Screen()
    data class AddEdit(
        val post: Post? = null,
        val photoUri: String? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        val location: String? = null,
        val plusCode: String? = null,
        val date: String? = null,
        val time: String? = null,
        val conditions: String? = null
    ) : Screen()
    object Search : Screen()
    object Settings : Screen()
    object ThemeCustomizer : Screen()
    object GoogleDrive : Screen()
    object OwnerLogin : Screen()
    object OwnerDashboard : Screen()
    object OwnerSettings : Screen()
    object UserManagement : Screen()
    object ImportExport : Screen()
    object ImageCompression : Screen()
    object StorageDetails : Screen()
    object AppIconSettings : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewsApp(repository: PhotoViewsRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Glass Theming System Reactive State
    val activeGlassTheme by repository.activeGlassTheme.collectAsStateWithLifecycle()
    val customGlassThemes by repository.customGlassThemes.collectAsStateWithLifecycle()

    // Preferences & Settings State
    val themeMode by repository.themeMode.collectAsStateWithLifecycle()
    val gridLayoutMode by repository.gridLayoutMode.collectAsStateWithLifecycle()
    val imageQuality by repository.imageQuality.collectAsStateWithLifecycle()
    val activeRole by repository.activeRole.collectAsStateWithLifecycle()
    val driveAccount by repository.driveAccount.collectAsStateWithLifecycle()
    val isSyncing by repository.isSyncing.collectAsStateWithLifecycle()
    val recentSearches by repository.recentSearches.collectAsStateWithLifecycle()
    val driveAppIcons by repository.driveAppIcons.collectAsStateWithLifecycle()
    val activeAppIconDriveId by repository.activeAppIconDriveId.collectAsStateWithLifecycle()
    val activeAppIcon = remember(driveAppIcons, activeAppIconDriveId) {
        driveAppIcons.find { it.driveFileId == activeAppIconDriveId } ?: driveAppIcons.firstOrNull()
    }

    // Database & Cloud reactive streams
    val allPosts by repository.allPosts.collectAsStateWithLifecycle(emptyList())
    val favoritePosts by repository.favoritePosts.collectAsStateWithLifecycle(emptyList())
    val allFolders by repository.allFolders.collectAsStateWithLifecycle(emptyList())
    val allUsers by repository.allUsers.collectAsStateWithLifecycle(emptyList())
    val cloudStats by repository.cloudStats.collectAsStateWithLifecycle()
    val locallyDeletedPosts by repository.locallyDeletedCloudPosts.collectAsStateWithLifecycle()

    // Navigation Stack
    val backstack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = backstack.lastOrNull() ?: Screen.Home

    fun navigateTo(screen: Screen) {
        backstack.add(screen)
    }

    fun navigateBack() {
        if (backstack.size > 1) {
            backstack.removeAt(backstack.size - 1)
        }
    }

    // BackHandler for Android Hardware & Gesture Navigation
    BackHandler(enabled = backstack.size > 1) {
        navigateBack()
    }

    // Modal Sheet & Dialog States
    var showCreateOptionsSheet by remember { mutableStateOf(false) }
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPostForViewer by remember { mutableStateOf<Post?>(null) }
    var selectedPostForMenu by remember { mutableStateOf<Post?>(null) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Working Delete Confirm Dialog States
    var postToDelete by remember { mutableStateOf<Post?>(null) }
    var postToPermanentDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    var folderToDelete by remember { mutableStateOf<Folder?>(null) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }

    // Cloud Restore & Sync Progress States
    var syncProgressStep by remember { mutableStateOf<SyncProgressStep?>(null) }
    var syncRestoreResult by remember { mutableStateOf<SyncRestoreResult?>(null) }
    var showSyncProgressDialog by remember { mutableStateOf(false) }

    fun triggerRestoreAndSync() {
        syncRestoreResult = null
        syncProgressStep = SyncProgressStep.PREPARING
        showSyncProgressDialog = true
        coroutineScope.launch {
            val res = repository.restoreAndSyncFromCloud { step ->
                syncProgressStep = step
            }
            syncRestoreResult = res
            Toast.makeText(context, res.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Filter & Sort State
    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var filterState by remember { mutableStateOf(FilterState()) }

    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.GLASS_DARK -> true
        AppThemeMode.SYSTEM -> systemInDark
        else -> false
    }

    // Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val firstUri = uris.first().toString()
            navigateTo(Screen.AddEdit(photoUri = firstUri))
        }
    }

    PhotoViewsTheme(glassTheme = activeGlassTheme) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                // Bottom Bar visible on primary tabs
                val isRootTab = currentScreen is Screen.Home ||
                    currentScreen is Screen.Folders ||
                    currentScreen is Screen.Favorites ||
                    currentScreen is Screen.Settings ||
                    currentScreen is Screen.OwnerDashboard

                if (isRootTab && selectedPostForViewer == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .background(Color.Transparent)
                    ) {
                        NavigationBar(
                            containerColor = activeGlassTheme.bottomNavSurfaceColor,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            NavigationBarItem(
                                selected = currentScreen is Screen.Home,
                                onClick = {
                                    backstack.clear()
                                    backstack.add(Screen.Home)
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = activeGlassTheme.primaryAccent,
                                    selectedTextColor = activeGlassTheme.primaryAccent,
                                    indicatorColor = activeGlassTheme.primaryAccent.copy(alpha = 0.16f)
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Folders,
                                onClick = {
                                    if (currentScreen !is Screen.Folders) {
                                        backstack.clear()
                                        backstack.add(Screen.Home)
                                        backstack.add(Screen.Folders)
                                    }
                                },
                                icon = { Icon(Icons.Default.Folder, contentDescription = "Folders") },
                                label = { Text("Folders", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = activeGlassTheme.primaryAccent,
                                    selectedTextColor = activeGlassTheme.primaryAccent,
                                    indicatorColor = activeGlassTheme.primaryAccent.copy(alpha = 0.16f)
                                )
                            )

                            // Center Floating (+) Button in bottom navigation bar
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(activeGlassTheme.primaryAccent)
                                    .clickable { showCreateOptionsSheet = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Post",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            NavigationBarItem(
                                selected = currentScreen is Screen.Favorites,
                                onClick = {
                                    if (currentScreen !is Screen.Favorites) {
                                        backstack.clear()
                                        backstack.add(Screen.Home)
                                        backstack.add(Screen.Favorites)
                                    }
                                },
                                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                                label = { Text("Favorites", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandRed,
                                    selectedTextColor = BrandRed,
                                    indicatorColor = BrandRed.copy(alpha = 0.12f)
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Settings,
                                onClick = {
                                    if (currentScreen !is Screen.Settings) {
                                        backstack.clear()
                                        backstack.add(Screen.Home)
                                        backstack.add(Screen.Settings)
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Settings", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = activeGlassTheme.primaryAccent,
                                    selectedTextColor = activeGlassTheme.primaryAccent,
                                    indicatorColor = activeGlassTheme.primaryAccent.copy(alpha = 0.16f)
                                )
                            )

                            if (activeRole == UserRole.OWNER) {
                                NavigationBarItem(
                                    selected = currentScreen is Screen.OwnerDashboard,
                                    onClick = {
                                        if (currentScreen !is Screen.OwnerDashboard) {
                                            backstack.clear()
                                            backstack.add(Screen.Home)
                                            backstack.add(Screen.OwnerDashboard)
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Shield, contentDescription = "Admin") },
                                    label = { Text("Admin", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = activeGlassTheme.primaryAccent,
                                        selectedTextColor = activeGlassTheme.primaryAccent,
                                        indicatorColor = activeGlassTheme.primaryAccent.copy(alpha = 0.16f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(activeGlassTheme.backgroundBrush)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                // Navigation Screen Router
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            posts = allPosts,
                            folders = allFolders,
                            favoritePosts = favoritePosts,
                            driveAccount = driveAccount,
                            userRole = activeRole,
                            isSyncing = isSyncing,
                            onNavigateToSection = { sectionKey ->
                                when (sectionKey) {
                                    "folders" -> navigateTo(Screen.Folders)
                                    "favorites" -> navigateTo(Screen.Favorites)
                                    "cloud_storage" -> navigateTo(Screen.GoogleDrive)
                                    "storage_details" -> navigateTo(Screen.StorageDetails)
                                    "recent" -> navigateTo(Screen.PostsGrid("Recent Photos", "recent"))
                                    "camera_posts" -> navigateTo(Screen.PostsGrid("Camera Photos", "camera"))
                                    "imported_posts" -> navigateTo(Screen.PostsGrid("Imported Photos", "imported"))
                                    "offline_posts" -> navigateTo(Screen.PostsGrid("Offline Photos", "offline"))
                                    else -> navigateTo(Screen.PostsGrid("All Posts", "all"))
                                }
                            },
                            onSearchClick = { navigateTo(Screen.Search) },
                            onProfileClick = {
                                if (activeRole == UserRole.OWNER) {
                                    navigateTo(Screen.OwnerDashboard)
                                } else {
                                    navigateTo(Screen.OwnerLogin)
                                }
                            },
                            onDriveBannerClick = { navigateTo(Screen.GoogleDrive) },
                            onPostClick = { post -> selectedPostForViewer = post },
                            onPostLongClick = { post -> selectedPostForMenu = post },
                            onToggleFavorite = { post ->
                                coroutineScope.launch {
                                    repository.toggleFavorite(post.id, !post.isFavorite)
                                }
                            },
                            onFolderClick = { folder ->
                                navigateTo(Screen.PostsGrid(title = folder.name, sectionKey = "folder", folderId = folder.id))
                            },
                            onAddFolderClick = {
                                showCreateFolderDialog = true
                            },
                            onDeleteFolderClick = { folder ->
                                folderToDelete = folder
                            },
                            onResetClick = {
                                coroutineScope.launch {
                                    repository.resetAllData()
                                    Toast.makeText(context, "Sample posts and folders restored successfully", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCameraClick = {
                                navigateTo(Screen.Camera)
                            },
                            onUploadClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onRestoreAndSyncClick = {
                                triggerRestoreAndSync()
                            }
                        )
                    }

                    is Screen.PostsGrid -> {
                        val postsToDisplay = when (screen.sectionKey) {
                            "favorites" -> favoritePosts
                            "camera" -> allPosts.filter { it.isCameraPhoto }
                            "imported" -> allPosts.filter { it.isImported }
                            "offline" -> allPosts.filter { it.syncStatus == com.example.data.model.SyncState.OFFLINE }
                            "folder" -> allPosts.filter { it.folderId == screen.folderId }
                            "recent" -> allPosts.take(15)
                            else -> allPosts
                        }

                        PostsGridScreen(
                            title = screen.title,
                            posts = postsToDisplay,
                            currentLayoutMode = gridLayoutMode,
                            onBackClick = { navigateBack() }, // Clear Back button handler!
                            onLayoutChange = { newMode -> repository.setGridLayoutMode(newMode) },
                            onSearchClick = { navigateTo(Screen.Search) },
                            onFilterClick = { showFilterSheet = true },
                            onPostClick = { post -> selectedPostForViewer = post },
                            onPostLongClick = { post -> selectedPostForMenu = post },
                            onToggleFavorite = { post ->
                                coroutineScope.launch {
                                    repository.toggleFavorite(post.id, !post.isFavorite)
                                }
                            },
                            onAddClick = { showCreateOptionsSheet = true }
                        )
                    }

                    is Screen.Folders -> {
                        FoldersScreen(
                            folders = allFolders,
                            onBackClick = { navigateBack() }, // Clear Back button handler!
                            onFolderClick = { folder ->
                                navigateTo(Screen.PostsGrid(title = folder.name, sectionKey = "folder", folderId = folder.id))
                            },
                            onCreateFolder = { name, color ->
                                coroutineScope.launch {
                                    repository.createFolder(name, color)
                                    Toast.makeText(context, "Folder created", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRenameFolder = { folderId, newName ->
                                coroutineScope.launch {
                                    repository.renameFolder(folderId, newName)
                                    Toast.makeText(context, "Folder renamed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDeleteFolder = { folderId, deleteInnerPosts ->
                                coroutineScope.launch {
                                    val success = repository.deleteFolder(folderId, deleteInnerPosts)
                                    if (success) {
                                        Toast.makeText(context, "Folder deleted successfully", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }

                    is Screen.Favorites -> {
                        PostsGridScreen(
                            title = "Favorites",
                            posts = favoritePosts,
                            currentLayoutMode = gridLayoutMode,
                            onBackClick = { navigateBack() },
                            onLayoutChange = { newMode -> repository.setGridLayoutMode(newMode) },
                            onSearchClick = { navigateTo(Screen.Search) },
                            onFilterClick = { showFilterSheet = true },
                            onPostClick = { post -> selectedPostForViewer = post },
                            onPostLongClick = { post -> selectedPostForMenu = post },
                            onToggleFavorite = { post ->
                                coroutineScope.launch {
                                    repository.toggleFavorite(post.id, !post.isFavorite)
                                }
                            },
                            onAddClick = { showCreateOptionsSheet = true }
                        )
                    }

                    is Screen.Camera -> {
                        CameraScreen(
                            onBackClick = { navigateBack() },
                            onGalleryClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onPhotoCaptured = { uri, lat, lng, loc, plusCode, date, time, cond ->
                                navigateTo(
                                    Screen.AddEdit(
                                        photoUri = uri,
                                        lat = lat,
                                        lng = lng,
                                        location = loc,
                                        plusCode = plusCode,
                                        date = date,
                                        time = time,
                                        conditions = cond
                                    )
                                )
                            }
                        )
                    }

                    is Screen.AddEdit -> {
                        AddEditPostScreen(
                            initialPost = screen.post,
                            initialPhotoUri = screen.photoUri,
                            initialLat = screen.lat,
                            initialLng = screen.lng,
                            initialLocation = screen.location,
                            initialPlusCode = screen.plusCode,
                            initialDate = screen.date,
                            initialTime = screen.time,
                            initialCondition = screen.conditions,
                            folders = allFolders,
                            onBackClick = { navigateBack() },
                            onSavePost = { post ->
                                coroutineScope.launch {
                                    if (screen.post != null) {
                                        repository.updatePost(post)
                                        Toast.makeText(context, "Post updated successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        repository.insertPost(post)
                                        Toast.makeText(context, "Post created successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    navigateBack()
                                }
                            }
                        )
                    }

                    is Screen.Search -> {
                        SearchScreen(
                            posts = allPosts,
                            recentSearches = recentSearches,
                            onAddRecentSearch = { query -> repository.addRecentSearch(query) },
                            onRemoveRecentSearch = { query -> repository.removeRecentSearch(query) },
                            onClearRecentSearches = { repository.clearRecentSearches() },
                            onBackClick = { navigateBack() },
                            onPostClick = { post -> selectedPostForViewer = post },
                            onPostLongClick = { post -> selectedPostForMenu = post },
                            onToggleFavorite = { post ->
                                coroutineScope.launch {
                                    repository.toggleFavorite(post.id, !post.isFavorite)
                                }
                            }
                        )
                    }

                    is Screen.Settings -> {
                        SettingsScreen(
                            currentThemeMode = themeMode,
                            currentGridLayout = gridLayoutMode,
                            currentImageQuality = imageQuality,
                            driveAccount = driveAccount,
                            userRole = activeRole,
                            isSyncing = isSyncing,
                            activeGlassTheme = activeGlassTheme,
                            onBackClick = { navigateBack() },
                            onThemeChange = { mode -> repository.setThemeMode(mode) },
                            onGridLayoutChange = { mode -> repository.setGridLayoutMode(mode) },
                            onImageQualityChange = { q -> repository.setImageQuality(q) },
                            onSyncNow = {
                                coroutineScope.launch {
                                    repository.syncNow()
                                    Toast.makeText(context, "Sync completed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onNavigateToDrive = { navigateTo(Screen.GoogleDrive) },
                            onNavigateToCompression = { navigateTo(Screen.ImageCompression) },
                            onNavigateToImportExport = { navigateTo(Screen.ImportExport) },
                            onNavigateToOwnerLogin = { navigateTo(Screen.OwnerLogin) },
                            onNavigateToOwnerDashboard = { navigateTo(Screen.OwnerDashboard) },
                            onNavigateToThemeCustomizer = { navigateTo(Screen.ThemeCustomizer) },
                            onNavigateToAppIcon = { navigateTo(Screen.AppIconSettings) },
                            activeAppIconName = activeAppIcon?.name ?: "Photo Views Classic",
                            cloudIconsCount = driveAppIcons.size,
                            onSelectThemePreset = { presetId -> repository.selectThemePreset(presetId) },
                            onResetAppearance = {
                                repository.resetThemeToDefault()
                                Toast.makeText(context, "Appearance reset to default", Toast.LENGTH_SHORT).show()
                            },
                            onRestoreAndSync = { triggerRestoreAndSync() }
                        )
                    }

                    is Screen.AppIconSettings -> {
                        val activity = context as? android.app.Activity
                        AppIconScreen(
                            icons = driveAppIcons,
                            activeIconId = activeAppIconDriveId,
                            driveAccount = driveAccount,
                            activeGlassTheme = activeGlassTheme,
                            onBackClick = { navigateBack() },
                            onSetActiveIcon = { id -> repository.setActiveAppIcon(id, activity) },
                            onRestoreDefaultIcon = { repository.restoreDefaultAppIcon(activity) },
                            onSyncWithDrive = { repository.syncAppIconsWithDrive() },
                            onUploadNewIcon = { name, uri -> repository.uploadNewAppIcon(name, uri) },
                            onRenameIcon = { id, newName -> repository.renameAppIcon(id, newName) },
                            onDeleteIcon = { id -> repository.deleteAppIconFromDrive(id) }
                        )
                    }

                    is Screen.ThemeCustomizer -> {
                        ThemeCustomizerScreen(
                            currentTheme = activeGlassTheme,
                            customThemes = customGlassThemes,
                            onBackClick = { navigateBack() },
                            onSelectPreset = { presetId -> repository.selectThemePreset(presetId) },
                            onApplyTheme = { theme -> repository.applyGlassTheme(theme) },
                            onSaveCustomTheme = { theme -> repository.saveCustomTheme(theme) },
                            onDuplicateTheme = { theme -> repository.duplicateTheme(theme) },
                            onRenameTheme = { id, newName -> repository.renameCustomTheme(id, newName) },
                            onDeleteCustomTheme = { id -> repository.deleteCustomTheme(id) },
                            onImportTheme = { json -> repository.importThemeJson(json) },
                            onResetToDefault = { repository.resetThemeToDefault() }
                        )
                    }

                    is Screen.GoogleDrive -> {
                        GoogleDriveScreen(
                            driveAccount = driveAccount,
                            isSyncing = isSyncing,
                            onBackClick = { navigateBack() },
                            onConnectDrive = { email -> repository.connectDriveAccount(email) },
                            onDisconnectDrive = { repository.disconnectDriveAccount() },
                            onAddLinkedDrive = { email, label ->
                                repository.addLinkedDrive(email, label)
                            },
                            onSyncNow = {
                                coroutineScope.launch {
                                    repository.syncNow()
                                    Toast.makeText(context, "Sync completed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onViewStorageDetails = { navigateTo(Screen.StorageDetails) }
                        )
                    }

                    is Screen.OwnerLogin -> {
                        OwnerLoginScreen(
                            onBackClick = { navigateBack() },
                            onLoginSuccess = {
                                repository.setActiveRole(UserRole.OWNER)
                                Toast.makeText(context, "Owner authenticated successfully", Toast.LENGTH_SHORT).show()
                                navigateBack()
                                navigateTo(Screen.OwnerDashboard)
                            },
                            onVerifyCredentials = { id, pass ->
                                repository.verifyOwner(id, pass)
                            }
                        )
                    }

                    is Screen.OwnerDashboard -> {
                        OwnerDashboardScreen(
                            posts = allPosts,
                            folders = allFolders,
                            users = allUsers,
                            driveAccount = driveAccount,
                            cloudStats = cloudStats,
                            locallyDeletedPosts = locallyDeletedPosts,
                            onBackClick = { navigateBack() },
                            onOpenPost = { post -> selectedPostForViewer = post },
                            onEditPost = { post -> navigateTo(Screen.AddEdit(post = post)) },
                            onDeletePostLocally = { post -> postToDelete = post },
                            onPermanentDeletePost = { postId, title ->
                                coroutineScope.launch {
                                    val result = repository.permanentCloudDeletePost(postId)
                                    if (result.isSuccess) {
                                        Toast.makeText(context, "Permanently purged \"$title\" from cloud", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, result.exceptionOrNull()?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onRestoreSinglePost = { postId ->
                                coroutineScope.launch {
                                    val restored = repository.restoreSinglePostFromCloud(postId)
                                    if (restored) {
                                        Toast.makeText(context, "Post restored to device", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onRestoreAndSyncAll = {
                                triggerRestoreAndSync()
                            },
                            onNavigateToUserManagement = { navigateTo(Screen.UserManagement) },
                            onNavigateToOwnerSettings = { navigateTo(Screen.OwnerSettings) },
                            onCreateFolderClick = { showCreateFolderDialog = true },
                            onLogoutOwner = {
                                repository.setActiveRole(UserRole.USER)
                                Toast.makeText(context, "Owner logged out", Toast.LENGTH_SHORT).show()
                                backstack.clear()
                                backstack.add(Screen.Home)
                            }
                        )
                    }

                    is Screen.OwnerSettings -> {
                        OwnerSettingsScreen(
                            driveAccount = driveAccount,
                            isSyncing = isSyncing,
                            onBackClick = { navigateBack() },
                            onChangeDriveAccount = { newEmail ->
                                repository.changeDriveStorageAccount(newEmail)
                            },
                            onAddLinkedDrive = { email, label ->
                                repository.addLinkedDrive(email, label)
                            },
                            onRemoveLinkedDrive = { driveId ->
                                repository.removeLinkedDrive(driveId)
                            },
                            onSetPrimaryLinkedDrive = { driveId ->
                                repository.setPrimaryLinkedDrive(driveId)
                            },
                            onSyncNow = {
                                coroutineScope.launch {
                                    repository.syncNow()
                                    Toast.makeText(context, "Cloud sync complete", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDisconnectDrive = {
                                repository.disconnectDriveAccount()
                                Toast.makeText(context, "Drive account disconnected", Toast.LENGTH_SHORT).show()
                            },
                            onChangeCredentials = { oldPass, newId, newPass ->
                                val isValid = repository.verifyCurrentOwnerPassword(oldPass)
                                if (isValid) {
                                    repository.updateOwnerCredentials(newId, newPass)
                                    true
                                } else {
                                    false
                                }
                            }
                        )
                    }

                    is Screen.UserManagement -> {
                        UserManagementScreen(
                            users = allUsers,
                            onBackClick = { navigateBack() },
                            onToggleUserActive = { userId, active ->
                                coroutineScope.launch {
                                    repository.setUserActive(userId, active)
                                    Toast.makeText(context, "User status updated", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    is Screen.ImportExport -> {
                        ImportExportScreen(
                            onBackClick = { navigateBack() },
                            onExportAll = { repository.exportAllDataJson() },
                            onImportBgData = { bgContent -> repository.importData(bgContent) }
                        )
                    }

                    is Screen.ImageCompression -> {
                        ImageCompressionPreviewScreen(
                            currentQuality = imageQuality,
                            onBackClick = { navigateBack() },
                            onQualitySelected = { q -> repository.setImageQuality(q) }
                        )
                    }

                    is Screen.StorageDetails -> {
                        StorageDetailsScreen(
                            driveAccount = driveAccount,
                            onBackClick = { navigateBack() }
                        )
                    }
                }
            }
        }

        // CREATE POST OPTIONS BOTTOM SHEET (matching screenshot "Create Post")
        if (showCreateOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCreateOptionsSheet = false },
                sheetState = createSheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = if (activeGlassTheme.isDark) Color(0xFF0F172A) else Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Create Post",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (activeGlassTheme.isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                        )
                        IconButton(onClick = { showCreateOptionsSheet = false }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Close",
                                modifier = Modifier.size(24.dp),
                                tint = if (activeGlassTheme.isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Add Photo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (activeGlassTheme.isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 1: Gallery
                    CreateOptionRow(
                        icon = Icons.Default.PhotoLibrary,
                        iconTint = BrandBlue,
                        title = "Gallery",
                        subtitle = "Select from gallery",
                        onClick = {
                            showCreateOptionsSheet = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    // Option 2: Camera
                    CreateOptionRow(
                        icon = Icons.Default.CameraAlt,
                        iconTint = Color(0xFF00ACC1),
                        title = "Camera",
                        subtitle = "Take a photo with GPS and Plus Code",
                        onClick = {
                            showCreateOptionsSheet = false
                            navigateTo(Screen.Camera)
                        }
                    )

                    // Option 3: File Manager
                    CreateOptionRow(
                        icon = Icons.Default.FolderOpen,
                        iconTint = BrandAmber,
                        title = "File Manager",
                        subtitle = "Choose from files & downloads",
                        onClick = {
                            showCreateOptionsSheet = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // FULL SCREEN PHOTO VIEWER MODAL
        selectedPostForViewer?.let { post ->
            FullScreenPostViewer(
                post = post,
                userRole = activeRole,
                onClose = { selectedPostForViewer = null },
                onToggleFavorite = {
                    coroutineScope.launch {
                        repository.toggleFavorite(post.id, !post.isFavorite)
                        selectedPostForViewer = post.copy(isFavorite = !post.isFavorite)
                    }
                },
                onEdit = {
                    val p = selectedPostForViewer
                    selectedPostForViewer = null
                    if (p != null) {
                        navigateTo(Screen.AddEdit(post = p))
                    }
                },
                onDelete = {
                    val p = selectedPostForViewer
                    selectedPostForViewer = null
                    postToDelete = p
                },
                onMoveFolder = {
                    selectedPostForMenu = post
                    selectedPostForViewer = null
                },
                onPermanentDelete = {
                    val p = selectedPostForViewer
                    selectedPostForViewer = null
                    if (p != null) {
                        postToPermanentDelete = Pair(p.id, p.title)
                    }
                }
            )
        }

        // POST OPTIONS LONG PRESS BOTTOM SHEET
        selectedPostForMenu?.let { post ->
            PostOptionsMenu(
                post = post,
                userRole = activeRole,
                sheetState = menuSheetState,
                onDismiss = { selectedPostForMenu = null },
                onOpen = {
                    selectedPostForViewer = post
                },
                onEdit = {
                    navigateTo(Screen.AddEdit(post = post))
                },
                onToggleFavorite = {
                    coroutineScope.launch {
                        repository.toggleFavorite(post.id, !post.isFavorite)
                    }
                },
                onMoveToFolder = {
                    // Quick cycle folders or assign to Nature
                    val nextFolder = allFolders.firstOrNull { it.id != post.folderId } ?: allFolders.firstOrNull()
                    if (nextFolder != null) {
                        coroutineScope.launch {
                            repository.movePostToFolder(post.id, nextFolder.id, nextFolder.name)
                            Toast.makeText(context, "Moved to ${nextFolder.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onShare = {
                    val sendIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(android.content.Intent.EXTRA_TEXT, "${post.title} - ${post.locationName}")
                        type = "text/plain"
                    }
                    context.startActivity(android.content.Intent.createChooser(sendIntent, "Share post"))
                },
                onDownload = {
                    Toast.makeText(context, "Downloaded to device gallery", Toast.LENGTH_SHORT).show()
                },
                onViewDetails = {
                    selectedPostForViewer = post
                },
                onExport = {
                    coroutineScope.launch {
                        val singleJson = com.example.util.BackupHelper.createBackupJson(listOf(post), emptyList(), "SINGLE_POST")
                        com.example.util.BackupHelper.exportToBgFile(context, singleJson, "${post.title}.bg")
                        Toast.makeText(context, "Exported as ${post.title}.bg", Toast.LENGTH_SHORT).show()
                    }
                },
                onDelete = {
                    postToDelete = post
                },
                onPermanentDelete = {
                    postToPermanentDelete = Pair(post.id, post.title)
                }
            )
        }

        // POST LOCAL DELETE CONFIRMATION DIALOG (CRITICAL REQUIREMENT 1: Safe local delete, cloud intact)
        postToDelete?.let { post ->
            DeletePostConfirmDialog(
                postTitle = post.title,
                onDismiss = { postToDelete = null },
                onConfirmDelete = {
                    coroutineScope.launch {
                        val deleted = repository.deletePostLocally(post.id)
                        if (deleted) {
                            Toast.makeText(context, "Deleted from this device only. Your cloud backup remains available.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Error deleting post", Toast.LENGTH_SHORT).show()
                        }
                        postToDelete = null
                    }
                }
            )
        }

        // OWNER-ONLY PERMANENT CLOUD DELETE CONFIRMATION DIALOG (CRITICAL REQUIREMENT 3 & 4)
        postToPermanentDelete?.let { (id, title) ->
            OwnerPermanentDeleteDialog(
                postTitle = title,
                onDismiss = { postToPermanentDelete = null },
                onConfirmPermanentDelete = {
                    coroutineScope.launch {
                        val result = repository.permanentCloudDeletePost(id)
                        if (result.isSuccess) {
                            Toast.makeText(context, "Permanently purged \"$title\" from cloud", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, result.exceptionOrNull()?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                        }
                        postToPermanentDelete = null
                    }
                }
            )
        }

        // CLOUD RESTORE & SYNC PROGRESS DIALOG (CRITICAL REQUIREMENT 2: Preparing -> Downloading -> Restoring -> Sync Complete)
        if (showSyncProgressDialog) {
            SyncProgressDialog(
                currentStep = syncProgressStep,
                result = syncRestoreResult,
                onDismiss = {
                    showSyncProgressDialog = false
                    syncProgressStep = null
                    syncRestoreResult = null
                }
            )
        }

        // CREATE FOLDER DIALOG (ACCESSIBLE FROM HOME & FOLDERS)
        if (showCreateFolderDialog) {
            CreateFolderDialog(
                onDismiss = { showCreateFolderDialog = false },
                onConfirmCreate = { name, colorHex ->
                    coroutineScope.launch {
                        repository.createFolder(name, colorHex)
                        Toast.makeText(context, "Folder \"$name\" created successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // FOLDER DELETE CONFIRMATION DIALOG (WORKING RELIABLY)
        folderToDelete?.let { folder ->
            DeleteFolderConfirmDialog(
                folderName = folder.name,
                onDismiss = { folderToDelete = null },
                onConfirmDelete = { deleteInnerPosts ->
                    coroutineScope.launch {
                        val success = repository.deleteFolder(folder.id, deleteInnerPosts)
                        if (success) {
                            Toast.makeText(context, "Folder deleted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error deleting folder", Toast.LENGTH_SHORT).show()
                        }
                        folderToDelete = null
                    }
                }
            )
        }

        // FILTER & SORT SHEET
        if (showFilterSheet) {
            FilterSortSheet(
                initialFilter = filterState,
                sheetState = filterSheetState,
                onDismiss = { showFilterSheet = false },
                onApply = { newFilter ->
                    filterState = newFilter
                    showFilterSheet = false
                }
            )
        }
    }
}

@Composable
private fun CreateOptionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
