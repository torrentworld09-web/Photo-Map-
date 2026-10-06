package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassThemedButton
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandOrange
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.BrandRed
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.GlassThemePresets
import com.example.ui.theme.GlassThemeState
import com.example.ui.theme.LocalGlassTheme

/**
 * Powerful, interactive Theme Customizer & Appearance Studio.
 * Live real-time preview, 10 presets, custom theme editor, import/export, save/duplicate/delete.
 */
@Composable
fun ThemeCustomizerScreen(
    currentTheme: GlassThemeState,
    customThemes: List<GlassThemeState>,
    onBackClick: () -> Unit,
    onSelectPreset: (String) -> Unit,
    onApplyTheme: (GlassThemeState) -> Unit,
    onSaveCustomTheme: (GlassThemeState) -> Unit,
    onDuplicateTheme: (GlassThemeState) -> Unit,
    onRenameTheme: (themeId: String, newName: String) -> Unit,
    onDeleteCustomTheme: (themeId: String) -> Unit,
    onImportTheme: (jsonString: String) -> Boolean,
    onResetToDefault: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Working draft theme for live preview & editing
    var draftTheme by remember(currentTheme) { mutableStateOf(currentTheme) }
    var editorTabIndex by remember { mutableIntStateOf(0) } // 0: Glass & Borders, 1: Colors & Accents, 2: Buttons & Style

    // Dialog states
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveThemeNameInput by remember { mutableStateOf(if (draftTheme.isCustom) draftTheme.name else "${draftTheme.name} (Custom)") }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var themeToRename by remember { mutableStateOf<GlassThemeState?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var themeToDelete by remember { mutableStateOf<GlassThemeState?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(draftTheme.backgroundBrush)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
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
                                .background(Color.Black.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = draftTheme.primaryText
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Appearance & Themes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = draftTheme.primaryText
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(draftTheme.primaryAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = draftTheme.primaryAccent
                                    )
                                }
                            }
                            Text(
                                text = "Real-time Glass Theming Studio",
                                style = MaterialTheme.typography.labelSmall,
                                color = draftTheme.secondaryText
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Appearance",
                                tint = BrandAmber
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = "Export Theme",
                                tint = draftTheme.primaryText
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                importJsonInput = ""
                                showImportDialog = true
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Import Theme",
                                tint = draftTheme.primaryText
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Interactive Live Preview Card
                item {
                    Text(
                        text = "LIVE PREVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = draftTheme.primaryAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LiveThemePreviewCard(theme = draftTheme)
                }

                // Section: Presets (10 Options)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "THEME PRESETS (10)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = draftTheme.primaryAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Tap to apply",
                            fontSize = 11.sp,
                            color = draftTheme.secondaryText
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(GlassThemePresets.allPresets) { preset ->
                            val isSelected = draftTheme.id == preset.id
                            PresetPreviewTile(
                                preset = preset,
                                isSelected = isSelected,
                                onClick = {
                                    draftTheme = preset
                                    onSelectPreset(preset.id)
                                    Toast.makeText(context, "Applied ${preset.name}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                // Section: Saved Custom Themes (if any)
                if (customThemes.isNotEmpty()) {
                    item {
                        Text(
                            text = "MY CUSTOM THEMES (${customThemes.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = draftTheme.primaryAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            customThemes.forEach { customTheme ->
                                CustomThemeItemRow(
                                    theme = customTheme,
                                    isActive = draftTheme.id == customTheme.id,
                                    onSelect = {
                                        draftTheme = customTheme
                                        onApplyTheme(customTheme)
                                        Toast.makeText(context, "Applied ${customTheme.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    onEdit = {
                                        draftTheme = customTheme
                                    },
                                    onDuplicate = {
                                        onDuplicateTheme(customTheme)
                                        Toast.makeText(context, "Duplicated ${customTheme.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    onRename = {
                                        themeToRename = customTheme
                                        renameInput = customTheme.name
                                    },
                                    onDelete = {
                                        themeToDelete = customTheme
                                    }
                                )
                            }
                        }
                    }
                }

                // Section: Custom Theme Editor
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CUSTOM THEME EDITOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = draftTheme.primaryAccent,
                            letterSpacing = 1.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    saveThemeNameInput = if (draftTheme.isCustom) draftTheme.name else "${draftTheme.name} (Custom)"
                                    showSaveDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = draftTheme.primaryAccent),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Theme", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Editor Tabs
                    TabRow(
                        selectedTabIndex = editorTabIndex,
                        containerColor = draftTheme.cardSurfaceColor,
                        contentColor = draftTheme.primaryAccent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .border(BorderStroke(1.dp, draftTheme.cardBorderColor), RoundedCornerShape(14.dp))
                    ) {
                        Tab(
                            selected = editorTabIndex == 0,
                            onClick = { editorTabIndex = 0 },
                            text = { Text("Glass & Blur", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = editorTabIndex == 1,
                            onClick = { editorTabIndex = 1 },
                            text = { Text("Colors & Glow", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = editorTabIndex == 2,
                            onClick = { editorTabIndex = 2 },
                            text = { Text("Style & Buttons", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab 0: Glass & Borders
                    if (editorTabIndex == 0) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = draftTheme.cardSurfaceColor,
                            borderColor = draftTheme.cardBorderColor,
                            shape = RoundedCornerShape(draftTheme.cornerRadius.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                ThemeSliderItem(
                                    label = "Glass Transparency",
                                    value = draftTheme.glassTransparency,
                                    range = 0.1f..0.98f,
                                    format = { "${(it * 100).toInt()}%" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(glassTransparency = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Card Opacity",
                                    value = draftTheme.cardTransparency,
                                    range = 0.2f..0.98f,
                                    format = { "${(it * 100).toInt()}%" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(cardTransparency = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Bottom Nav Transparency",
                                    value = draftTheme.bottomNavTransparency,
                                    range = 0.2f..0.98f,
                                    format = { "${(it * 100).toInt()}%" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(bottomNavTransparency = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Glass Blur Intensity",
                                    value = draftTheme.glassBlurIntensity,
                                    range = 0f..24f,
                                    format = { "${it.toInt()} dp" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(glassBlurIntensity = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Glass Border Opacity",
                                    value = draftTheme.glassBorderOpacity,
                                    range = 0.05f..0.90f,
                                    format = { "${(it * 100).toInt()}%" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(glassBorderOpacity = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Border Thickness",
                                    value = draftTheme.borderThickness,
                                    range = 0.5f..4.0f,
                                    format = { String.format("%.1f dp", it) },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(borderThickness = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Corner Radius",
                                    value = draftTheme.cornerRadius,
                                    range = 4f..32f,
                                    format = { "${it.toInt()} dp" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(cornerRadius = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Shadow Intensity",
                                    value = draftTheme.shadowIntensity,
                                    range = 0f..16f,
                                    format = { "${it.toInt()} dp" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(shadowIntensity = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )
                            }
                        }
                    }

                    // Tab 1: Colors & Accents
                    if (editorTabIndex == 1) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = draftTheme.cardSurfaceColor,
                            borderColor = draftTheme.cardBorderColor,
                            shape = RoundedCornerShape(draftTheme.cornerRadius.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                // Dark/Light Mode switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Dark Glass Mode", fontWeight = FontWeight.Bold, color = draftTheme.primaryText)
                                        Text("Inverts text & system controls for dark contrast", fontSize = 11.sp, color = draftTheme.secondaryText)
                                    }
                                    Switch(
                                        checked = draftTheme.isDark,
                                        onCheckedChange = {
                                            draftTheme = draftTheme.copy(
                                                isDark = it,
                                                primaryTextVal = if (it) 0xFFF8FAFCL else 0xFF0F172AL,
                                                secondaryTextVal = if (it) 0xFF94A3B8L else 0xFF64748BL
                                            )
                                            onApplyTheme(draftTheme)
                                        }
                                    )
                                }

                                HorizontalDivider(color = draftTheme.cardBorderColor)

                                // Primary Accent Color
                                ColorPickerField(
                                    label = "Primary Accent Color",
                                    selectedColor = draftTheme.primaryAccent,
                                    onColorSelected = {
                                        draftTheme = draftTheme.copy(primaryAccentVal = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                // Secondary Accent Color
                                ColorPickerField(
                                    label = "Secondary Accent Color",
                                    selectedColor = draftTheme.secondaryAccent,
                                    onColorSelected = {
                                        draftTheme = draftTheme.copy(secondaryAccentVal = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                // Background Color
                                ColorPickerField(
                                    label = "Background Base Color",
                                    selectedColor = draftTheme.backgroundColor,
                                    onColorSelected = {
                                        draftTheme = draftTheme.copy(backgroundColorVal = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                // Background Gradient End Color
                                ColorPickerField(
                                    label = "Gradient End Color",
                                    selectedColor = draftTheme.backgroundGradientEndColor,
                                    onColorSelected = {
                                        draftTheme = draftTheme.copy(backgroundGradientEndColorVal = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                // Glow Intensity
                                ThemeSliderItem(
                                    label = "Glow Intensity",
                                    value = draftTheme.glowIntensity,
                                    range = 0f..1.0f,
                                    format = { "${(it * 100).toInt()}%" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(glowIntensity = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )
                            }
                        }
                    }

                    // Tab 2: Style, Buttons & Wallpaper
                    if (editorTabIndex == 2) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = draftTheme.cardSurfaceColor,
                            borderColor = draftTheme.cardBorderColor,
                            shape = RoundedCornerShape(draftTheme.cornerRadius.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Button Style", fontWeight = FontWeight.Bold, color = draftTheme.primaryText)
                                val buttonStyles = listOf("Gradient", "Glass", "Filled", "Outlined")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    buttonStyles.forEach { style ->
                                        val isSelected = draftTheme.buttonStyle == style
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                draftTheme = draftTheme.copy(buttonStyle = style)
                                                onApplyTheme(draftTheme)
                                            },
                                            label = { Text(style, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = draftTheme.primaryAccent,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Button Glow Effect", fontWeight = FontWeight.Bold, color = draftTheme.primaryText)
                                        Text("Adds colored ambient halo under buttons", fontSize = 11.sp, color = draftTheme.secondaryText)
                                    }
                                    Switch(
                                        checked = draftTheme.buttonGlow,
                                        onCheckedChange = {
                                            draftTheme = draftTheme.copy(buttonGlow = it)
                                            onApplyTheme(draftTheme)
                                        }
                                    )
                                }

                                HorizontalDivider(color = draftTheme.cardBorderColor)

                                Text("Wallpaper Backdrop", fontWeight = FontWeight.Bold, color = draftTheme.primaryText)
                                val wallpapers = listOf(
                                    "mesh_gradient" to "Mesh Gradient",
                                    "aurora" to "Aurora Waves",
                                    "cosmic" to "Cosmic Deep",
                                    "abstract_fluid" to "Fluid Crystal",
                                    "none" to "Solid Clean"
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(wallpapers) { (key, label) ->
                                        val isSelected = draftTheme.appBackgroundImage == key
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                draftTheme = draftTheme.copy(appBackgroundImage = key)
                                                onApplyTheme(draftTheme)
                                            },
                                            label = { Text(label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = draftTheme.primaryAccent,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                ThemeSliderItem(
                                    label = "Wallpaper Blur",
                                    value = draftTheme.wallpaperBlur,
                                    range = 0f..20f,
                                    format = { "${it.toInt()} dp" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(wallpaperBlur = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )

                                ThemeSliderItem(
                                    label = "Wallpaper Brightness",
                                    value = draftTheme.wallpaperBrightness,
                                    range = 0.2f..1.5f,
                                    format = { "${(it * 100).toInt()}%" },
                                    accentColor = draftTheme.primaryAccent,
                                    textColor = draftTheme.primaryText,
                                    onValueChange = {
                                        draftTheme = draftTheme.copy(wallpaperBrightness = it)
                                        onApplyTheme(draftTheme)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Save Theme Dialog
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Custom Theme") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Enter a unique name for your custom glass theme:", fontSize = 13.sp)
                        OutlinedTextField(
                            value = saveThemeNameInput,
                            onValueChange = { saveThemeNameInput = it },
                            label = { Text("Theme Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val nameToSave = saveThemeNameInput.trim().ifEmpty { "My Custom Theme" }
                            val newTheme = draftTheme.copy(name = nameToSave, isCustom = true)
                            onSaveCustomTheme(newTheme)
                            draftTheme = newTheme
                            showSaveDialog = false
                            Toast.makeText(context, "Saved theme '$nameToSave'", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Rename Theme Dialog
        themeToRename?.let { target ->
            AlertDialog(
                onDismissRequest = { themeToRename = null },
                title = { Text("Rename Theme") },
                text = {
                    OutlinedTextField(
                        value = renameInput,
                        onValueChange = { renameInput = it },
                        label = { Text("New Theme Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newName = renameInput.trim().ifEmpty { target.name }
                            onRenameTheme(target.id, newName)
                            if (draftTheme.id == target.id) {
                                draftTheme = draftTheme.copy(name = newName)
                            }
                            themeToRename = null
                            Toast.makeText(context, "Renamed to '$newName'", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Rename")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { themeToRename = null }) { Text("Cancel") }
                }
            )
        }

        // Delete Theme Dialog
        themeToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { themeToDelete = null },
                title = { Text("Delete Theme?") },
                text = { Text("Are you sure you want to delete '${target.name}'? This cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteCustomTheme(target.id)
                            themeToDelete = null
                            Toast.makeText(context, "Deleted '${target.name}'", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandRed)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { themeToDelete = null }) { Text("Cancel") }
                }
            )
        }

        // Export Theme Dialog
        if (showExportDialog) {
            val jsonExport = draftTheme.toJsonString()
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Export Theme Configuration") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Copy the JSON configuration below to share or back up this theme:", fontSize = 12.sp)
                        OutlinedTextField(
                            value = jsonExport,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(jsonExport))
                            Toast.makeText(context, "Theme JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                            showExportDialog = false
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy JSON")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) { Text("Close") }
                }
            )
        }

        // Import Theme Dialog
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import Theme Configuration") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Paste a valid Glass Theme JSON configuration below:", fontSize = 12.sp)
                        OutlinedTextField(
                            value = importJsonInput,
                            onValueChange = { importJsonInput = it },
                            placeholder = { Text("{\"name\": \"My Theme\", ...}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val success = onImportTheme(importJsonInput)
                            if (success) {
                                showImportDialog = false
                                Toast.makeText(context, "Theme imported successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Invalid theme format. Please check JSON.", Toast.LENGTH_LONG).show()
                            }
                        }
                    ) {
                        Text("Import & Apply")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Reset Appearance Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
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
                            onResetToDefault()
                            draftTheme = GlassThemePresets.PremiumDarkGlass
                            showResetDialog = false
                            Toast.makeText(context, "Reset to default theme", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                    ) {
                        Text("Reset Appearance")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

/**
 * Real-time dynamic visual preview card showing mini mockup of photo post & controls
 */
@Composable
private fun LiveThemePreviewCard(theme: GlassThemeState) {
    val cardBg = theme.cardSurfaceColor
    val cardBorder = theme.cardBorderColor
    val shape = RoundedCornerShape(theme.cornerRadius.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(theme.shadowIntensity.dp, shape = shape, spotColor = Color(0x33000000)),
        shape = shape,
        color = cardBg,
        border = BorderStroke(theme.borderThickness.dp, cardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Mock Photo Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(theme.cornerRadius.dp * 0.75f))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(theme.primaryAccent, theme.secondaryAccent)
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Summit Peak, Alps", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = BrandRed, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Details Mockup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = theme.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = theme.primaryText
                    )
                    Text(
                        text = "Real-time preview • ${theme.buttonStyle} Buttons • ${(theme.glassTransparency * 100).toInt()}% Glass",
                        style = MaterialTheme.typography.bodySmall,
                        color = theme.secondaryText,
                        fontSize = 11.sp
                    )
                }

                // Sample Badges
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.primaryAccent.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primaryAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons & Controls Mockup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Interactive themed button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(
                            if (theme.buttonStyle == "Gradient") {
                                Brush.horizontalGradient(listOf(theme.primaryAccent, theme.secondaryAccent))
                            } else {
                                Brush.linearGradient(listOf(theme.primaryAccent, theme.primaryAccent))
                            }
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Primary Button",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Glass secondary button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(BorderStroke(theme.borderThickness.dp, theme.cardBorderColor), shape)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Glass Button",
                        color = theme.primaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Thumbnail Tile for a Preset Theme
 */
@Composable
private fun PresetPreviewTile(
    preset: GlassThemeState,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val ringColor = if (isSelected) preset.primaryAccent else Color.Transparent

    Column(
        modifier = Modifier
            .width(108.dp)
            .clip(shape)
            .clickable { onClick() }
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(104.dp, 80.dp)
                .clip(shape)
                .border(
                    BorderStroke(if (isSelected) 2.5.dp else 1.dp, if (isSelected) ringColor else Color.White.copy(alpha = 0.2f)),
                    shape
                )
                .background(preset.backgroundBrush)
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            // Mini inner card mockup
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(preset.cardSurfaceColor)
                    .border(BorderStroke(preset.borderThickness.dp, preset.cardBorderColor), RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(preset.primaryAccent)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(preset.secondaryAccent)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(preset.primaryText.copy(alpha = 0.7f))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(preset.primaryAccent.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = preset.name,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) preset.primaryAccent else preset.primaryText,
            maxLines = 1
        )
    }
}

/**
 * Row displaying user's custom saved theme with actions
 */
@Composable
private fun CustomThemeItemRow(
    theme: GlassThemeState,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Color swatch
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.backgroundBrush)
                        .border(BorderStroke(1.dp, theme.primaryAccent), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(theme.primaryAccent)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = theme.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = theme.primaryText
                        )
                        if (isActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.primaryAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("ACTIVE", fontSize = 8.sp, color = theme.primaryAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        text = "Custom Theme • ${(theme.glassTransparency * 100).toInt()}% Glass",
                        fontSize = 11.sp,
                        color = theme.secondaryText
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDuplicate, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = theme.secondaryText, modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = onRename, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Rename", tint = theme.secondaryText, modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandRed, modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

/**
 * Slider item with value label and consistent formatting
 */
@Composable
private fun ThemeSliderItem(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    format: (Float) -> String,
    accentColor: Color,
    textColor: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textColor)
            Text(format(value), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = accentColor.copy(alpha = 0.2f)
            )
        )
    }
}

/**
 * Color picker row with quick palette swatches
 */
@Composable
private fun ColorPickerField(
    label: String,
    selectedColor: Color,
    onColorSelected: (Long) -> Unit
) {
    val quickPalette = listOf(
        0xFF1E88E5L to "Blue",
        0xFF00ACC1L to "Cyan",
        0xFF10B981L to "Emerald",
        0xFF00F0FFL to "Neon Cyan",
        0xFFFF007FL to "Hot Pink",
        0xFF7E57C2L to "Purple",
        0xFFFF9800L to "Amber",
        0xFFE53935L to "Red",
        0xFF0A0F1DL to "Obsidian",
        0xFF041026L to "Midnight",
        0xFFF1F5F9L to "Ice White",
        0xFF121214L to "Minimal Dark"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .border(BorderStroke(1.5.dp, Color.White), CircleShape)
            )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(quickPalette) { (colorVal, _) ->
                val isSelected = selectedColor == Color(colorVal)
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(colorVal))
                        .border(
                            BorderStroke(if (isSelected) 2.5.dp else 1.dp, if (isSelected) Color.White else Color(0x33000000)),
                            CircleShape
                        )
                        .clickable { onColorSelected(colorVal) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = if (colorVal > 0xFF888888L && colorVal != 0xFFF1F5F9L) Color.White else Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
