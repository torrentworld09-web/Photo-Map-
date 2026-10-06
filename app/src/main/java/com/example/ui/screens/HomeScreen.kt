package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriveAccount
import com.example.data.model.Folder
import com.example.data.model.GridLayoutMode
import com.example.data.model.Post
import com.example.data.model.UserRole
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassCircleIconButton
import com.example.ui.components.PostCard
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.BrandRed
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.LocalGlassTheme

data class SectionItem(
    val title: String,
    val count: String,
    val storage: String,
    val icon: ImageVector,
    val iconColor: Color,
    val sectionKey: String
)

@Composable
fun HomeScreen(
    posts: List<Post>,
    folders: List<Folder>,
    favoritePosts: List<Post>,
    driveAccount: DriveAccount,
    userRole: UserRole,
    isSyncing: Boolean,
    onNavigateToSection: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDriveBannerClick: () -> Unit,
    onPostClick: (Post) -> Unit,
    onPostLongClick: (Post) -> Unit,
    onToggleFavorite: (Post) -> Unit,
    onFolderClick: (Folder) -> Unit = {},
    onAddFolderClick: () -> Unit = {},
    onDeleteFolderClick: (Folder) -> Unit = {},
    onResetClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onRestoreAndSyncClick: () -> Unit = {}
) {
    val totalPostsCount = posts.size
    val totalPhotosCount = posts.sumOf { it.photoUris.size.coerceAtLeast(1) }
    val cameraPostsCount = posts.count { it.isCameraPhoto }
    val importedPostsCount = posts.count { it.isImported }
    val offlinePostsCount = posts.count { it.syncStatus == com.example.data.model.SyncState.OFFLINE }
    val favoritesCount = favoritePosts.size
    val foldersCount = folders.size
    var folderGridMode by remember { mutableStateOf(false) }
    var activeFilterTab by remember { mutableStateOf("All posts") }

    fun formatByteSize(bytes: Long): String {
        if (bytes <= 0L) return "0 MB"
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1024) {
            String.format(java.util.Locale.US, "%.1f GB", mb / 1024.0)
        } else {
            String.format(java.util.Locale.US, "%.1f MB", mb)
        }
    }

    // Dynamic storage calculation for 100% LIVE accuracy
    val formattedTotalStorage = formatByteSize(posts.sumOf { it.storageBytes })
    val formattedFoldersStorage = formatByteSize(folders.sumOf { it.storageBytes })
    val formattedFavStorage = formatByteSize(favoritePosts.sumOf { it.storageBytes })
    val formattedCameraStorage = formatByteSize(posts.filter { it.isCameraPhoto }.sumOf { it.storageBytes })
    val formattedImportedStorage = formatByteSize(posts.filter { it.isImported }.sumOf { it.storageBytes })
    val formattedOfflineStorage = formatByteSize(posts.filter { it.syncStatus == com.example.data.model.SyncState.OFFLINE }.sumOf { it.storageBytes })

    val sections = listOf(
        SectionItem("All Posts", "$totalPostsCount", formattedTotalStorage, Icons.Default.Collections, BrandBlue, "all_posts"),
        SectionItem("Folders", "$foldersCount", formattedFoldersStorage, Icons.Default.Folder, BrandAmber, "folders"),
        SectionItem("Favorites", "$favoritesCount", formattedFavStorage, Icons.Default.Favorite, BrandRed, "favorites"),
        SectionItem("Recent", "${minOf(posts.size, 10)}", formattedTotalStorage, Icons.Default.History, BrandGreen, "recent"),
        SectionItem("Camera Photos", "$cameraPostsCount", formattedCameraStorage, Icons.Default.CameraAlt, BrandCyan, "camera_posts"),
        SectionItem("Imported Photos", "$importedPostsCount", formattedImportedStorage, Icons.Default.Download, BrandPurple, "imported_posts"),
        SectionItem("Offline Photos", "$offlinePostsCount", formattedOfflineStorage, Icons.Default.CloudOff, BrandTeal, "offline_posts"),
        if (userRole == UserRole.OWNER) {
            SectionItem("Cloud Storage", driveAccount.formattedUsed, driveAccount.formattedTotal, Icons.Default.Cloud, BrandOrange, "cloud_storage")
        } else {
            SectionItem("Storage Details", formattedTotalStorage, "Local", Icons.Default.PieChart, BrandOrange, "storage_details")
        }
    )

    val glassTheme = LocalGlassTheme.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Header (Fixed empty space & dark theme color)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                if (glassTheme.isDark) Color(0xFF060911).copy(alpha = 0.85f) else MaterialTheme.colorScheme.surface,
                                Color.Transparent
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // App Logo icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(BrandBlue, BrandCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Photo Views",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Capture • Organize • Store",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassCircleIconButton(
                            onClick = onSearchClick,
                            icon = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassCircleIconButton(
                            onClick = onProfileClick,
                            icon = if (userRole == UserRole.OWNER) Icons.Default.Shield else Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = if (userRole == UserRole.OWNER) BrandBlue else null,
                            backgroundColor = if (userRole == UserRole.OWNER) BrandBlue.copy(alpha = 0.25f) else null
                        )
                    }
                }
            }
        }

        // FILTER TABS STRIP (All posts, Folders, Favourite, Reset, Camera photos, imported photos, Offline photos)
        item {
            val filterTabs = listOf(
                "All posts",
                "Folders",
                "Favourite",
                "Reset",
                "Camera photos",
                "imported photos",
                "Offline photos"
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs) { tab ->
                    val isSelected = activeFilterTab == tab
                    val tabColor = when (tab) {
                        "Favourite" -> BrandRed
                        "Reset" -> BrandAmber
                        "Folders" -> BrandAmber
                        "Camera photos" -> BrandCyan
                        "imported photos" -> BrandPurple
                        "Offline photos" -> BrandTeal
                        else -> BrandBlue
                    }
                    val unselectedTabColor = if (glassTheme.isDark) {
                        Color(0xFF141D2E).copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) tabColor else unselectedTabColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                if (tab == "Folders") {
                                    onNavigateToSection("folders")
                                } else if (tab == "Reset") {
                                    onResetClick()
                                    activeFilterTab = "All posts"
                                } else {
                                    activeFilterTab = tab
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (tab == "Reset") {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else tabColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = tab,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else if (glassTheme.isDark) Color(0xFFE2E8F0) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Main Sections Grid (4x2 cards)
        item {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                val chunked = sections.chunked(2)
                chunked.forEach { rowSections ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowSections.forEach { item ->
                            HomeSectionCard(
                                item = item,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigateToSection(item.sectionKey) }
                            )
                        }
                    }
                }
            }
        }

        // DIRECT FOLDER MANAGEMENT SECTION ON HOME SCREEN
        item {
            Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Folders (${folders.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Toggle between Grid View and Horizontal Row for Folders
                        IconButton(
                            onClick = { folderGridMode = !folderGridMode },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (folderGridMode) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = if (folderGridMode) "Switch to Row" else "Switch to Grid",
                                tint = BrandBlue,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        // Direct + Add Folder button on Home
                        IconButton(
                            onClick = onAddFolderClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = "Add Folder",
                                tint = BrandBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigateToSection("folders") }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (folders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No folders created yet. Tap + to add a folder.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (folderGridMode) {
                    // 2-column Grid option for Folders on Home Screen
                    val chunkedFolders = folders.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chunkedFolders.forEach { rowFolders ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowFolders.forEach { folder ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        HomeFolderTile(
                                            folder = folder,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(90.dp),
                                            onClick = { onFolderClick(folder) },
                                            onDelete = { onDeleteFolderClick(folder) }
                                        )
                                    }
                                }
                                if (rowFolders.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    // Horizontal Folders Strip with Folder cards and Delete menu
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(folders, key = { it.id }) { folder ->
                            HomeFolderTile(
                                folder = folder,
                                onClick = { onFolderClick(folder) },
                                onDelete = { onDeleteFolderClick(folder) }
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Summary
        item {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
                Text(
                    text = "Quick Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickStatItem("Total Photos", "$totalPhotosCount")
                        QuickStatItem("Total Posts", "$totalPostsCount")
                        QuickStatItem("Folders", "$foldersCount")
                        QuickStatItem("Favorites", "$favoritesCount")
                    }
                }
            }
        }

        // Recent Posts Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Photos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToSection("all_posts") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Recent Posts List (Filter sensitive & Empty state aware)
        item {
            val displayPosts = when (activeFilterTab) {
                "Favourite" -> favoritePosts
                "Camera photos" -> posts.filter { it.isCameraPhoto }
                "imported photos" -> posts.filter { it.isImported }
                "Offline photos" -> posts.filter { it.syncStatus == com.example.data.model.SyncState.OFFLINE }
                else -> posts
            }

            if (displayPosts.isEmpty()) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(BrandBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Text(
                            text = if (posts.isEmpty()) "All Posts Deleted" else "No posts found in '$activeFilterTab'",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (posts.isEmpty())
                                "No photos in storage. You can capture a new photo with live GPS telemetry, upload from device, or tap Reset to restore sample posts."
                            else
                                "No photos match '$activeFilterTab'. Try switching filter tabs or uploading a new photo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Button(
                                onClick = onCameraClick,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onUploadClick,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    onResetClick()
                                    activeFilterTab = "All posts"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                val previewPosts = displayPosts.take(8)
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    previewPosts.forEach { post ->
                        PostCard(
                            post = post,
                            layoutMode = GridLayoutMode.LARGE_CARD_VIEW,
                            onClick = { onPostClick(post) },
                            onLongClick = { onPostLongClick(post) },
                            onToggleFavorite = { onToggleFavorite(post) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeFolderTile(
    folder: Folder,
    modifier: Modifier = Modifier
        .width(150.dp)
        .height(90.dp),
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val folderColor = try {
        Color(android.graphics.Color.parseColor(folder.colorHex))
    } catch (e: Exception) {
        BrandBlue
    }

    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        elevation = 2.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(folderColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = folderColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuOpen = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Folder", color = BrandRed) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = BrandRed) },
                            onClick = {
                                menuOpen = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Column {
                Text(
                    text = folder.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${folder.postCount} Posts • ${folder.formattedStorageSize}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HomeSectionCard(
    item: SectionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.count} • ${item.storage}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun QuickStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
    }
}
