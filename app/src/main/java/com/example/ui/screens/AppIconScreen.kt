package com.example.ui.screens

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DriveAccount
import com.example.data.model.DriveAppIcon
import com.example.ui.components.GlassCard
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandRed
import com.example.ui.theme.GlassThemePresets
import com.example.ui.theme.GlassThemeState
import com.example.util.AppIconLauncherManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppIconScreen(
    icons: List<DriveAppIcon>,
    activeIconId: String,
    driveAccount: DriveAccount,
    activeGlassTheme: GlassThemeState = GlassThemePresets.PremiumDarkGlass,
    onBackClick: () -> Unit,
    onSetActiveIcon: (String) -> Unit,
    onRestoreDefaultIcon: () -> Unit = {},
    onSyncWithDrive: suspend () -> Unit,
    onUploadNewIcon: suspend (name: String, uri: String) -> Unit,
    onRenameIcon: suspend (id: String, newName: String) -> Boolean,
    onDeleteIcon: suspend (id: String) -> Boolean
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }

    // Synchronize every time the App Icon page opens
    LaunchedEffect(Unit) {
        isSyncing = true
        onSyncWithDrive()
        isSyncing = false
    }

    // Active icon lookup
    val activeIcon = remember(icons, activeIconId) {
        icons.find { it.driveFileId == activeIconId }
            ?: AppIconLauncherManager.SUPPORTED_ALIASES.find { it.driveFileId == activeIconId }?.let {
                DriveAppIcon(
                    driveFileId = it.driveFileId,
                    name = it.displayName,
                    folderPath = "Icon/",
                    uploadDateTime = "Today",
                    styleKey = it.styleKey,
                    isDefault = it.isDefault
                )
            }
            ?: icons.firstOrNull()
    }

    // Dialog & Sheet States
    var selectedIconForTap by remember { mutableStateOf<DriveAppIcon?>(null) }
    var selectedIconForLongPress by remember { mutableStateOf<DriveAppIcon?>(null) }
    var iconForPreview by remember { mutableStateOf<DriveAppIcon?>(null) }
    var iconForRename by remember { mutableStateOf<DriveAppIcon?>(null) }
    var iconForDelete by remember { mutableStateOf<DriveAppIcon?>(null) }
    var iconForDetails by remember { mutableStateOf<DriveAppIcon?>(null) }
    var appliedSuccessIcon by remember { mutableStateOf<DriveAppIcon?>(null) }

    var renameInput by remember { mutableStateOf("") }
    var pendingUploadUri by remember { mutableStateOf<String?>(null) }
    var uploadNameInput by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val tapSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val longPressSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Image Picker for New Icon
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingUploadUri = uri.toString()
            uploadNameInput = "Custom Icon ${icons.size + 1}"
        }
    }

    // Infinite rotation for sync indicator
    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotate")
    val syncRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "sync_spin"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
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
                                .testTag("app_icon_back_button")
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
                                    text = "App Icon",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = BrandBlue.copy(alpha = 0.15f),
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = "Cloud Icons • ${icons.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "SETTINGS → APP APPEARANCE → APP ICON",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Pull-to-refresh / Sync Button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isSyncing = true
                                onSyncWithDrive()
                                isSyncing = false
                                Toast.makeText(context, "Google Drive Icon folder synced", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .testTag("app_icon_sync_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Icons with Google Drive",
                            tint = activeGlassTheme.primaryAccent,
                            modifier = if (isSyncing) Modifier.rotate(syncRotation) else Modifier
                        )
                    }
                }
            }

            // Scrollable Content Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("app_icon_grid"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google Drive Connection Status Card
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BrandBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Google Drive: Icon/",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Single Cloud Source",
                                        fontSize = 9.sp,
                                        color = BrandGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "Connected: ${driveAccount.currentAccountEmail}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = activeGlassTheme.primaryAccent
                                )
                            }
                        }
                    }
                }

                // Current App Icon Section & Hero Preview Card
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current App Icon",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Quick Action: Restore Default App Icon
                            TextButton(
                                onClick = {
                                    onRestoreDefaultIcon()
                                    Toast.makeText(context, "Restored default launcher icon", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("restore_default_icon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = activeGlassTheme.primaryAccent
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Restore Default",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = activeGlassTheme.primaryAccent
                                )
                            }
                        }

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("active_icon_hero_card"),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Large Actual Current Icon Preview
                                AppIconGraphic(
                                    icon = activeIcon,
                                    size = 76,
                                    isActive = true,
                                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(20.dp))
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activeIcon?.name ?: "Photo Views Classic",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Active Status Tag
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = BrandGreen.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = BrandGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Active Launcher Icon",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandGreen
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Drive Path: ${activeIcon?.fullDrivePath ?: "Icon/"}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Uploaded: ${activeIcon?.uploadDateTime ?: "Today"}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }

                                // Quick Preview Button
                                IconButton(
                                    onClick = { iconForPreview = activeIcon },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(activeGlassTheme.primaryAccent.copy(alpha = 0.12f))
                                        .testTag("preview_active_icon_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Preview on Launcher",
                                        tint = activeGlassTheme.primaryAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Change App Icon Header
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp, start = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Change App Icon",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "All icons from Google Drive \"Icon\" folder",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Upload New Icon trigger button in header
                        TextButton(
                            onClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.testTag("header_upload_icon_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // App Icons Grid Gallery
                items(icons, key = { it.driveFileId }) { icon ->
                    val isActive = icon.driveFileId == activeIconId

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .combinedClickable(
                                onClick = { selectedIconForTap = icon },
                                onLongClick = { selectedIconForLongPress = icon }
                            )
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) BrandGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("app_icon_card_${icon.driveFileId}"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top Row: Clear Active Indicator badge or Cloud indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isActive) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = BrandGreen,
                                        modifier = Modifier.testTag("active_indicator_${icon.driveFileId}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "✓ Active",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Stored in Drive",
                                    tint = BrandBlue.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Icon Graphics Thumbnail
                            AppIconGraphic(
                                icon = icon,
                                size = 64,
                                isActive = isActive
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Icon Name
                            Text(
                                text = icon.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Upload Date/Time
                            Text(
                                text = icon.uploadDateTime,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // File size
                            Text(
                                text = "${icon.formattedFileSize} • Icon/",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button: + Upload New Icon
        ExtendedFloatingActionButton(
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Upload New Icon", fontWeight = FontWeight.Bold) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("upload_new_icon_fab"),
            containerColor = activeGlassTheme.primaryAccent,
            contentColor = Color.White
        )

        // TAP ACTIONS MODAL BOTTOM SHEET
        selectedIconForTap?.let { icon ->
            ModalBottomSheet(
                onDismissRequest = { selectedIconForTap = null },
                sheetState = tapSheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconGraphic(icon = icon, size = 48, isActive = icon.driveFileId == activeIconId)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = icon.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Google Drive: ${icon.fullDrivePath}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action 1: Use as App Icon (Changes real Android Launcher icon)
                    Button(
                        onClick = {
                            val target = icon
                            selectedIconForTap = null
                            onSetActiveIcon(target.driveFileId)
                            appliedSuccessIcon = target
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_use_as_icon"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activeGlassTheme.primaryAccent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (icon.driveFileId == activeIconId) "Current Active Icon" else "Use as App Icon",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action 2: Preview
                    OutlinedButton(
                        onClick = {
                            val target = icon
                            selectedIconForTap = null
                            iconForPreview = target
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_preview_icon"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Preview on Launcher")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action 3: Delete from Cloud
                    TextButton(
                        onClick = {
                            val target = icon
                            selectedIconForTap = null
                            iconForDelete = target
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_delete_icon"),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete from Cloud (Drive Icon/)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // LONG PRESS ACTIONS MODAL BOTTOM SHEET
        selectedIconForLongPress?.let { icon ->
            ModalBottomSheet(
                onDismissRequest = { selectedIconForLongPress = null },
                sheetState = longPressSheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconGraphic(icon = icon, size = 48, isActive = icon.driveFileId == activeIconId)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = icon.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Drive: ${icon.fullDrivePath} • ${icon.formattedFileSize}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Item 1: Use as App Icon
                    FilledTonalButton(
                        onClick = {
                            val target = icon
                            selectedIconForLongPress = null
                            onSetActiveIcon(target.driveFileId)
                            appliedSuccessIcon = target
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("longpress_use_as_icon"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use as App Icon", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Item 2: Rename
                    OutlinedButton(
                        onClick = {
                            val target = icon
                            selectedIconForLongPress = null
                            renameInput = target.name
                            iconForRename = target
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("longpress_rename_icon"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rename Icon")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Item 3: View Details
                    OutlinedButton(
                        onClick = {
                            val target = icon
                            selectedIconForLongPress = null
                            iconForDetails = target
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("longpress_details_icon"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Cloud Details")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Item 4: Delete
                    TextButton(
                        onClick = {
                            val target = icon
                            selectedIconForLongPress = null
                            iconForDelete = target
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("longpress_delete_icon"),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete from Google Drive")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // APPLIED SUCCESS CONFIRMATION DIALOG: "✓ App Icon Changed"
        appliedSuccessIcon?.let { icon ->
            val alias = AppIconLauncherManager.findAliasForIcon(icon)
            AlertDialog(
                onDismissRequest = { appliedSuccessIcon = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BrandGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ App Icon Changed",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AppIconGraphic(icon = icon, size = 68, isActive = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = icon.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Launcher Alias: ${alias.simpleName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandBlue
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "The Android home screen and app drawer launcher icon has been updated to this design. The change is saved and persists across app restarts.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Note: Most launchers update immediately. If your launcher does not refresh right away, return to the home screen or swipe away recent apps.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { appliedSuccessIcon = null },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        modifier = Modifier.testTag("applied_success_done_button")
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        // PREVIEW ON LAUNCHER DIALOG
        iconForPreview?.let { icon ->
            AlertDialog(
                onDismissRequest = { iconForPreview = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = BrandBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Home Screen Preview")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "This is how \"${icon.name}\" appears on the Android launcher home screen and app dock:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Simulated Android Launcher Dock / Wallpaper
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF1E293B),
                                            Color(0xFF0F172A),
                                            Color(0xFF020617)
                                        )
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AppIconGraphic(
                                    icon = icon,
                                    size = 72,
                                    isActive = true,
                                    modifier = Modifier.shadow(12.dp, RoundedCornerShape(20.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Photo Views",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Saved in Google Drive: ${icon.fullDrivePath}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val target = icon
                            iconForPreview = null
                            onSetActiveIcon(target.driveFileId)
                            appliedSuccessIcon = target
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = activeGlassTheme.primaryAccent)
                    ) {
                        Text("Apply as Active Icon")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { iconForPreview = null }) {
                        Text("Close")
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        // RENAME DIALOG
        iconForRename?.let { icon ->
            AlertDialog(
                onDismissRequest = { iconForRename = null },
                title = { Text("Rename App Icon") },
                text = {
                    Column {
                        Text(
                            text = "Enter a new name for this icon in Google Drive:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = renameInput,
                            onValueChange = { renameInput = it },
                            label = { Text("Icon Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rename_icon_text_field")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val success = onRenameIcon(icon.driveFileId, renameInput)
                                if (success) {
                                    Toast.makeText(context, "Renamed to $renameInput", Toast.LENGTH_SHORT).show()
                                }
                                iconForRename = null
                            }
                        },
                        modifier = Modifier.testTag("confirm_rename_button")
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { iconForRename = null }) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // DELETE CONFIRMATION DIALOG
        iconForDelete?.let { icon ->
            AlertDialog(
                onDismissRequest = { iconForDelete = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete from Google Drive?")
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete \"${icon.name}\" from the Google Drive \"Icon/\" folder? This file will be permanently removed.",
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val success = onDeleteIcon(icon.driveFileId)
                                if (success) {
                                    Toast.makeText(context, "${icon.name} deleted from Drive", Toast.LENGTH_SHORT).show()
                                }
                                iconForDelete = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("confirm_delete_icon_button")
                    ) {
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { iconForDelete = null }) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // DETAILS DIALOG
        iconForDetails?.let { icon ->
            val alias = AppIconLauncherManager.findAliasForIcon(icon)
            AlertDialog(
                onDismissRequest = { iconForDetails = null },
                title = { Text("Cloud Icon Details") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailItem("Drive File ID", icon.driveFileId)
                        DetailItem("Folder Path", icon.folderPath)
                        DetailItem("File Name", icon.name)
                        DetailItem("Storage Size", icon.formattedFileSize)
                        DetailItem("Upload Date", icon.uploadDateTime)
                        DetailItem("Launcher Alias", alias.simpleName)
                        DetailItem("Primary Tone", icon.primaryColorHex)
                        DetailItem("Status", if (icon.driveFileId == activeIconId) "Active Launcher Icon" else "Cloud Stored")
                        DetailItem("Google Account", driveAccount.currentAccountEmail)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { iconForDetails = null }) {
                        Text("Close")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // UPLOAD NEW ICON CONFIRMATION DIALOG
        pendingUploadUri?.let { uri ->
            AlertDialog(
                onDismissRequest = { if (!isUploading) pendingUploadUri = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = BrandBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload to Google Drive")
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Upload selected image to existing Google Drive \"Icon/\" folder:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Image Preview
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(2.dp, activeGlassTheme.primaryAccent, RoundedCornerShape(22.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "New Icon Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = uploadNameInput,
                            onValueChange = { uploadNameInput = it },
                            label = { Text("App Icon Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_icon_name_input")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Destination: Google Drive / Icon/",
                            fontSize = 11.sp,
                            color = BrandBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isUploading = true
                                onUploadNewIcon(uploadNameInput, uri)
                                isUploading = false
                                pendingUploadUri = null
                                Toast.makeText(context, "Uploaded to Google Drive Icon/ folder", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isUploading,
                        colors = ButtonDefaults.buttonColors(containerColor = activeGlassTheme.primaryAccent),
                        modifier = Modifier.testTag("confirm_upload_button")
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Upload to Drive")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { pendingUploadUri = null },
                        enabled = !isUploading
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

/**
 * Reusable high-polish squircle App Icon thumbnail renderer
 */
@Composable
fun AppIconGraphic(
    icon: DriveAppIcon?,
    size: Int,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryHex = icon?.primaryColorHex ?: "#3B82F6"
    val secondaryHex = icon?.secondaryColorHex ?: "#1D4ED8"
    val primaryColor = try { Color(android.graphics.Color.parseColor(primaryHex)) } catch (e: Exception) { BrandBlue }
    val secondaryColor = try { Color(android.graphics.Color.parseColor(secondaryHex)) } catch (e: Exception) { Color(0xFF1D4ED8) }

    val cornerRadius = (size * 0.28f).dp
    val localUri = icon?.localCacheUri.orEmpty()

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    listOf(
                        primaryColor,
                        secondaryColor
                    )
                )
            )
            .border(
                BorderStroke(
                    width = if (isActive) 2.5.dp else 1.dp,
                    brush = if (isActive) {
                        Brush.linearGradient(listOf(Color.White, BrandGreen))
                    } else {
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f)))
                    }
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (localUri.isNotEmpty()) {
            AsyncImage(
                model = localUri,
                contentDescription = icon?.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // High-polish vector composition for Photo Views App Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size((size * 0.42f).dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Glass lens aperture ring
                Box(
                    modifier = Modifier
                        .size((size * 0.18f).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.35f))
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
