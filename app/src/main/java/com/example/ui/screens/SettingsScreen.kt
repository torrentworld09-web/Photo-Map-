package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppThemeMode
import com.example.data.model.DriveAccount
import com.example.data.model.GridLayoutMode
import com.example.data.model.ImageQuality
import com.example.data.model.UserRole
import com.example.ui.components.GlassCard
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.GlassThemePresets
import com.example.ui.theme.GlassThemeState

@Composable
fun SettingsScreen(
    currentThemeMode: AppThemeMode,
    currentGridLayout: GridLayoutMode,
    currentImageQuality: ImageQuality,
    driveAccount: DriveAccount,
    userRole: UserRole,
    isSyncing: Boolean,
    activeGlassTheme: GlassThemeState = GlassThemePresets.PremiumDarkGlass,
    onBackClick: () -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onGridLayoutChange: (GridLayoutMode) -> Unit,
    onImageQualityChange: (ImageQuality) -> Unit,
    onSyncNow: () -> Unit,
    onNavigateToDrive: () -> Unit,
    onNavigateToCompression: () -> Unit,
    onNavigateToImportExport: () -> Unit,
    onNavigateToOwnerLogin: () -> Unit,
    onNavigateToOwnerDashboard: () -> Unit,
    onNavigateToThemeCustomizer: () -> Unit = {},
    onNavigateToAppIcon: () -> Unit = {},
    activeAppIconName: String = "Photo Views Classic",
    cloudIconsCount: Int = 12,
    onSelectThemePreset: (String) -> Unit = {},
    onResetAppearance: () -> Unit = {},
    onRestoreAndSync: () -> Unit = {}
) {
    var showResetThemeDialog by remember { mutableStateOf(false) }
    var layoutMenuExpanded by remember { mutableStateOf(false) }
    var qualityMenuExpanded by remember { mutableStateOf(false) }

    var autoOptimize by remember { mutableStateOf(true) }
    var preserveOriginal by remember { mutableStateOf(true) }
    var saveLocation by remember { mutableStateOf(true) }
    var savePlusCode by remember { mutableStateOf(true) }
    var saveDateTime by remember { mutableStateOf(true) }
    var saveConditions by remember { mutableStateOf(true) }
    var wifiOnly by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar (Theme-aware, no extra empty space)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Appearance & Themes
                item {
                    SettingsSectionHeader("Appearance & Themes", Icons.Default.Palette)
                    Spacer(modifier = Modifier.height(6.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Current Active Theme Card & Customizer trigger
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(activeGlassTheme.primaryAccent.copy(alpha = 0.10f))
                                    .clickable { onNavigateToThemeCustomizer() }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(activeGlassTheme.backgroundBrush)
                                            .border(BorderStroke(1.5.dp, activeGlassTheme.primaryAccent), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(activeGlassTheme.primaryAccent)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = activeGlassTheme.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(activeGlassTheme.primaryAccent.copy(alpha = 0.2f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = activeGlassTheme.primaryAccent
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Custom glass, blur, borders & colors",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = activeGlassTheme.primaryAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // App Icon Row (SETTINGS -> APP APPEARANCE -> APP ICON)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(activeGlassTheme.primaryAccent.copy(alpha = 0.08f))
                                    .clickable { onNavigateToAppIcon() }
                                    .padding(12.dp)
                                    .testTag("settings_app_icon_entry"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(BrandBlue, Color(0xFF1D4ED8))
                                                )
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "App Icon",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(BrandBlue.copy(alpha = 0.2f))
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "Cloud Icons • $cloudIconsCount",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BrandBlue
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Active: $activeAppIconName • Google Drive: Icon/",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // 1-Tap Preset Selector
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "10 Theme Presets",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Tap to switch",
                                        fontSize = 11.sp,
                                        color = activeGlassTheme.primaryAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(GlassThemePresets.allPresets) { preset ->
                                        val isSelected = activeGlassTheme.id == preset.id
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { onSelectThemePreset(preset.id) },
                                            label = { Text(preset.name, fontSize = 11.sp) },
                                            leadingIcon = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(preset.primaryAccent)
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = preset.primaryAccent.copy(alpha = 0.20f),
                                                selectedLabelColor = activeGlassTheme.primaryText
                                            )
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            // Full Theme Studio button
                            OutlinedButton(
                                onClick = onNavigateToThemeCustomizer,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, activeGlassTheme.primaryAccent.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp), tint = activeGlassTheme.primaryAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open Theme Studio & Custom Editor", color = activeGlassTheme.primaryAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            // Grid layout selector
                            Box {
                                SettingsRow(
                                    title = "Grid Layout",
                                    subtitle = currentGridLayout.displayName,
                                    onClick = { layoutMenuExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = layoutMenuExpanded,
                                    onDismissRequest = { layoutMenuExpanded = false }
                                ) {
                                    GridLayoutMode.entries.forEach { mode ->
                                        DropdownMenuItem(
                                            text = { Text(mode.displayName) },
                                            onClick = {
                                                layoutMenuExpanded = false
                                                onGridLayoutChange(mode)
                                            }
                                        )
                                    }
                                }
                            }

                            // Reset Appearance button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showResetThemeDialog = true }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reset Appearance to Default", fontSize = 13.sp, color = BrandAmber, fontWeight = FontWeight.Medium)
                                }
                                Text("Reset", fontSize = 12.sp, color = BrandAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Section: Photos & Compression
                item {
                    SettingsSectionHeader("Photos & Compression", Icons.Default.Compress)
                    Spacer(modifier = Modifier.height(6.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box {
                                SettingsRow(
                                    title = "Upload Quality",
                                    subtitle = currentImageQuality.displayName,
                                    onClick = { qualityMenuExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = qualityMenuExpanded,
                                    onDismissRequest = { qualityMenuExpanded = false }
                                ) {
                                    ImageQuality.entries.forEach { q ->
                                        DropdownMenuItem(
                                            text = { Text(q.displayName) },
                                            onClick = {
                                                qualityMenuExpanded = false
                                                onImageQualityChange(q)
                                            }
                                        )
                                    }
                                }
                            }

                            SettingsRow(
                                title = "Compression Details & Preview",
                                subtitle = "View savings (Original vs Optimized)",
                                onClick = onNavigateToCompression
                            )

                            SettingsSwitchRow("Auto Optimize Before Upload", autoOptimize) { autoOptimize = it }
                            SettingsSwitchRow("Preserve Originals Locally", preserveOriginal) { preserveOriginal = it }
                        }
                    }
                }

                // Section: Camera & Location
                item {
                    SettingsSectionHeader("Camera & Location", Icons.Default.CameraAlt)
                    Spacer(modifier = Modifier.height(6.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            SettingsSwitchRow("Save GPS Location", saveLocation) { saveLocation = it }
                            SettingsSwitchRow("Save Plus Code", savePlusCode) { savePlusCode = it }
                            SettingsSwitchRow("Save Date & Time", saveDateTime) { saveDateTime = it }
                            SettingsSwitchRow("Save Weather Conditions", saveConditions) { saveConditions = it }
                        }
                    }
                }

                // Section: Import & Export (.bg package)
                item {
                    SettingsSectionHeader("Import & Export", Icons.Default.FileUpload)
                    Spacer(modifier = Modifier.height(6.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            SettingsRow(
                                title = "Import / Export Data",
                                subtitle = "Custom .bg backup package & JSON metadata",
                                onClick = onNavigateToImportExport
                            )
                        }
                    }
                }

                // Section: Owner / Admin Access
                item {
                    SettingsSectionHeader(
                        if (userRole == UserRole.OWNER) "Owner Panel" else "Owner Access",
                        Icons.Default.Shield
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (userRole == UserRole.OWNER) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "🟢 Owner Mode is Active",
                                        fontWeight = FontWeight.Bold,
                                        color = BrandGreen,
                                        fontSize = 14.sp
                                    )
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

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = onNavigateToOwnerDashboard,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Open Owner Dashboard", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = "Protected administrative controls for post editing and cloud storage management.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = onNavigateToOwnerLogin,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Owner Login")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showResetThemeDialog) {
            AlertDialog(
                onDismissRequest = { showResetThemeDialog = false },
                title = { Text("Reset Appearance?") },
                text = {
                    Text(
                        "Reset all appearance settings to the default theme?\n\n" +
                            "This only resets visual theme styling. Your photos, posts, folders, favorites, and cloud storage are completely unaffected.",
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetAppearance()
                            showResetThemeDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                    ) {
                        Text("Reset Appearance")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetThemeDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BrandBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
