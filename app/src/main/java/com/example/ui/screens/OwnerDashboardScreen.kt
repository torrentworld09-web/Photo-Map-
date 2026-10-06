package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.cloud.CloudPostRecord
import com.example.data.cloud.CloudStorageStats
import com.example.data.model.DriveAccount
import com.example.data.model.Folder
import com.example.data.model.Post
import com.example.data.model.UserAccount
import com.example.ui.components.GlassCard
import com.example.ui.components.OwnerPermanentDeleteDialog
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandRed

/**
 * CRITICAL USER REQUIREMENT 3 & 9:
 * Owner Dashboard with Cloud Storage Management:
 * → Cloud Storage
 * → All Posts (Search, filter, open, edit, delete, permanently delete)
 * → Folders
 * → Sync & Restore
 * → Storage Usage
 * → Deleted/Archived Data
 * → Settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(
    posts: List<Post>,
    folders: List<Folder>,
    users: List<UserAccount>,
    driveAccount: DriveAccount,
    cloudStats: CloudStorageStats,
    locallyDeletedPosts: List<CloudPostRecord>,
    onBackClick: () -> Unit,
    onOpenPost: (Post) -> Unit,
    onEditPost: (Post) -> Unit,
    onDeletePostLocally: (Post) -> Unit,
    onPermanentDeletePost: (postId: String, postTitle: String) -> Unit,
    onRestoreSinglePost: (postId: String) -> Unit,
    onRestoreAndSyncAll: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    onNavigateToOwnerSettings: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onLogoutOwner: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var postToPermanentDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    var postSearchQuery by remember { mutableStateOf("") }
    var selectedFolderFilter by remember { mutableStateOf<String?>(null) }

    val tabs = listOf(
        "Cloud Storage",
        "All Posts",
        "Folders",
        "Sync & Restore",
        "Storage Usage",
        "Deleted/Archived Data",
        "Settings"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Owner Dashboard",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BrandGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "OWNER ONLY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandGreen
                                    )
                                }
                            }
                            Text(
                                text = "Secure Cloud Storage & Post Management",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onRestoreAndSyncAll) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Restore & Sync",
                                tint = BrandBlue
                            )
                        }
                        IconButton(onClick = onNavigateToOwnerSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Owner Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Scrollable Tabs: 7 sections
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrandBlue,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = BrandBlue,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (title == "Deleted/Archived Data" && locallyDeletedPosts.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(BrandAmber),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${locallyDeletedPosts.size}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> CloudStorageSection(
                    cloudStats = cloudStats,
                    driveAccount = driveAccount,
                    onRestoreAndSyncClick = onRestoreAndSyncAll,
                    onManageUsersClick = onNavigateToUserManagement
                )
                1 -> AllPostsManagementSection(
                    posts = posts,
                    folders = folders,
                    searchQuery = postSearchQuery,
                    onSearchQueryChange = { postSearchQuery = it },
                    selectedFolder = selectedFolderFilter,
                    onSelectFolder = { selectedFolderFilter = it },
                    onOpenPost = onOpenPost,
                    onEditPost = onEditPost,
                    onDeletePostLocally = onDeletePostLocally,
                    onPermanentDeletePost = { id, title ->
                        postToPermanentDelete = Pair(id, title)
                    }
                )
                2 -> FoldersManagementSection(
                    folders = folders,
                    onCreateFolderClick = onCreateFolderClick
                )
                3 -> SyncAndRestoreSection(
                    cloudStats = cloudStats,
                    driveAccount = driveAccount,
                    locallyDeletedCount = locallyDeletedPosts.size,
                    onRestoreAndSyncAll = onRestoreAndSyncAll
                )
                4 -> StorageUsageSection(
                    cloudStats = cloudStats,
                    driveAccount = driveAccount
                )
                5 -> DeletedArchivedDataSection(
                    deletedPosts = locallyDeletedPosts,
                    onRestorePost = onRestoreSinglePost,
                    onPermanentDelete = { id, title ->
                        postToPermanentDelete = Pair(id, title)
                    }
                )
                6 -> SettingsSection(
                    driveAccount = driveAccount,
                    onNavigateToOwnerSettings = onNavigateToOwnerSettings,
                    onNavigateToUserManagement = onNavigateToUserManagement,
                    onLogoutOwner = onLogoutOwner
                )
            }
        }

        // Owner-Only Permanent Cloud Deletion Dialog with strong warning + 2-step confirmation
        postToPermanentDelete?.let { (id, title) ->
            OwnerPermanentDeleteDialog(
                postTitle = title,
                onDismiss = { postToPermanentDelete = null },
                onConfirmPermanentDelete = {
                    onPermanentDeletePost(id, title)
                    postToPermanentDelete = null
                }
            )
        }
    }
}

/**
 * SECTION 1: Cloud Storage Overview
 */
@Composable
private fun CloudStorageSection(
    cloudStats: CloudStorageStats,
    driveAccount: DriveAccount,
    onRestoreAndSyncClick: () -> Unit,
    onManageUsersClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BrandGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Cloud Backup Status",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Active & Encrypted",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BrandGreen
                                )
                            }
                        }

                        Button(
                            onClick = onRestoreAndSyncClick,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore & Sync", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x1F000000))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Drive Account:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(driveAccount.currentAccountEmail, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Last Sync Time:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(cloudStats.lastSyncTime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrandBlue)
                    }
                }
            }
        }

        // Stats 4-Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Storage Used",
                    value = cloudStats.formattedStorageUsed,
                    subtext = "Google Cloud",
                    icon = Icons.Default.Cloud,
                    color = BrandBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Number of Photos",
                    value = "${cloudStats.totalPhotosCount}",
                    subtext = "Backed up in cloud",
                    icon = Icons.Default.ViewList,
                    color = BrandGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Number of Posts",
                    value = "${cloudStats.totalPostsCount}",
                    subtext = "Across all folders",
                    icon = Icons.Default.Shield,
                    color = BrandAmber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Number of Folders",
                    value = "${cloudStats.totalFoldersCount}",
                    subtext = "Cloud synchronized",
                    icon = Icons.Default.Folder,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Safety Architecture Notice
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Safety Architecture Guaranteed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "• Local deletes NEVER trigger cloud deletions.\n" +
                               "• When regular users delete from device, cloud backup remains intact.\n" +
                               "• Only the authenticated Owner can permanently delete cloud records.\n" +
                               "• Unified postId guarantees no duplicate posts during Restore & Sync.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onManageUsersClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manage User Roles & Access")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * SECTION 2: All Posts (Post Management)
 */
@Composable
private fun AllPostsManagementSection(
    posts: List<Post>,
    folders: List<Folder>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFolder: String?,
    onSelectFolder: (String?) -> Unit,
    onOpenPost: (Post) -> Unit,
    onEditPost: (Post) -> Unit,
    onDeletePostLocally: (Post) -> Unit,
    onPermanentDeletePost: (postId: String, postTitle: String) -> Unit
) {
    val filteredPosts = posts.filter { post ->
        val matchesSearch = searchQuery.isBlank() ||
                post.title.contains(searchQuery, ignoreCase = true) ||
                post.description.contains(searchQuery, ignoreCase = true) ||
                post.locationName.contains(searchQuery, ignoreCase = true) ||
                post.mobile.contains(searchQuery)
        val matchesFolder = selectedFolder == null || post.folderId == selectedFolder
        matchesSearch && matchesFolder
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search posts by title, location, phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandBlue) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Folder Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAll = selectedFolder == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAll) BrandBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { onSelectFolder(null) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "All Folders (${posts.size})",
                            fontSize = 12.sp,
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal,
                            color = if (isAll) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                items(folders) { folder ->
                    val isSelected = selectedFolder == folder.id
                    val count = posts.count { it.folderId == folder.id }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BrandBlue else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { onSelectFolder(folder.id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${folder.name} ($count)",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Count Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Posts (${filteredPosts.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Owner Controls Active",
                    fontSize = 11.sp,
                    color = BrandGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // List of Posts
        if (filteredPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "No posts matching \"$searchQuery\"" else "No posts found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredPosts, key = { it.id }) { post ->
                OwnerPostManagementCard(
                    post = post,
                    onOpen = { onOpenPost(post) },
                    onEdit = { onEditPost(post) },
                    onDeleteLocally = { onDeletePostLocally(post) },
                    onPermanentDelete = { onPermanentDeletePost(post.id, post.title) }
                )
            }
        }
    }
}

@Composable
private fun OwnerPostManagementCard(
    post: Post,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDeleteLocally: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo thumbnail
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (post.primaryPhotoUri.isNotBlank()) {
                        AsyncImage(
                            model = post.primaryPhotoUri,
                            contentDescription = post.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Cloud, contentDescription = null, tint = BrandBlue)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${post.date} • ${post.folderName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (post.locationName.isNotBlank()) {
                        Text(
                            text = post.locationName,
                            fontSize = 11.sp,
                            color = BrandBlue,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "ID: ${post.id}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Cloud Backed Up",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0x14000000))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.OpenInFull, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 11.sp, color = BrandBlue)
                }

                OutlinedButton(
                    onClick = onDeleteLocally,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandAmber)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete Local", fontSize = 11.sp, color = BrandAmber)
                }

                Button(
                    onClick = onPermanentDelete,
                    modifier = Modifier.weight(1.4f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRed),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Permanent Delete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * SECTION 3: Folders Management
 */
@Composable
private fun FoldersManagementSection(
    folders: List<Folder>,
    onCreateFolderClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cloud Folders (${folders.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Button(
                    onClick = onCreateFolderClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Folder")
                }
            }
        }

        items(folders) { folder ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    try {
                                        Color(android.graphics.Color.parseColor(folder.colorHex)).copy(alpha = 0.2f)
                                    } catch (e: Exception) {
                                        BrandBlue.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = try {
                                    Color(android.graphics.Color.parseColor(folder.colorHex))
                                } catch (e: Exception) {
                                    BrandBlue
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = folder.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${folder.postCount} posts • ${folder.formattedStorageSize}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("Synced", fontSize = 10.sp, color = BrandGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * SECTION 4: Sync & Restore
 */
@Composable
private fun SyncAndRestoreSection(
    cloudStats: CloudStorageStats,
    driveAccount: DriveAccount,
    locallyDeletedCount: Int,
    onRestoreAndSyncAll: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BrandBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "Restore & Sync from Cloud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Scan cloud storage, download all cloud-backed posts and photos, restore metadata, Google Maps links, phone numbers, folders, and favourites. Rebuilds local database without duplicates using unique postId.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onRestoreAndSyncAll,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Cloud Restore & Sync", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Cloud Backup Health",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Cloud Posts Available:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${cloudStats.totalPostsCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Locally Deleted (Cloud Safe):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$locallyDeletedCount posts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandAmber)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Cloud Photos:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${cloudStats.totalPhotosCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cloud Vault Size:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(cloudStats.formattedStorageUsed, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Drive Account:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(driveAccount.currentAccountEmail, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/**
 * SECTION 5: Storage Usage
 */
@Composable
private fun StorageUsageSection(
    cloudStats: CloudStorageStats,
    driveAccount: DriveAccount
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Storage Allocation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${driveAccount.formattedUsed} / ${driveAccount.formattedTotal}",
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { driveAccount.usagePercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = BrandBlue,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Current Active Account: ${driveAccount.currentAccountEmail}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Linked Storage Drives",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    driveAccount.linkedDrives.forEach { drive ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = drive.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = drive.email,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (drive.isPrimary) BrandBlue.copy(alpha = 0.15f) else BrandGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (drive.isPrimary) "Primary Active" else "Linked Backup",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (drive.isPrimary) BrandBlue else BrandGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * SECTION 6: Deleted/Archived Data (Posts deleted from device but kept in Cloud)
 */
@Composable
private fun DeletedArchivedDataSection(
    deletedPosts: List<CloudPostRecord>,
    onRestorePost: (postId: String) -> Unit,
    onPermanentDelete: (postId: String, postTitle: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cloud Safety Vault",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "Posts deleted from user devices are safely retained here in cloud storage. The Owner can restore them back to the device or permanently delete them from the cloud.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        if (deletedPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(40.dp))
                        Text(
                            text = "No locally deleted posts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "All cloud posts are currently present on this device.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(deletedPosts, key = { it.postId }) { record ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (record.photoUris.isNotEmpty()) {
                                    AsyncImage(
                                        model = record.photoUris.first(),
                                        contentDescription = record.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.Archive, contentDescription = null, tint = BrandAmber)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Folder: ${record.folderName} • ${record.date}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Status: Deleted on Device (Safe in Cloud)",
                                    fontSize = 11.sp,
                                    color = BrandAmber,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onRestorePost(record.postId) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restore to Device", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onPermanentDelete(record.postId, record.title) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandRed),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Permanent Delete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * SECTION 7: Settings & Owner Credentials
 */
@Composable
private fun SettingsSection(
    driveAccount: DriveAccount,
    onNavigateToOwnerSettings: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    onLogoutOwner: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Owner Controls & Credentials",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Manage master Owner ID, secure PIN, Google Drive storage link, and user access privileges.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onNavigateToOwnerSettings,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configure Owner Password & Drives")
                    }

                    OutlinedButton(
                        onClick = onNavigateToUserManagement,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ManageAccounts, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Manage App Users & Roles")
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onLogoutOwner,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Exit Owner Mode", color = BrandRed, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
