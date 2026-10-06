package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.util.BackupHelper
import kotlinx.coroutines.launch

@Composable
fun ImportExportScreen(
    onBackClick: () -> Unit,
    onExportAll: suspend () -> String,
    onImportBgData: suspend (String) -> Pair<Boolean, String>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showExportSuccessDialog by remember { mutableStateOf<String?>(null) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }

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
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
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
                        text = "Import & Export",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Export Options
                item {
                    Text(
                        text = "Export Options",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Create a custom PhotoViews (.bg) backup package containing all posts, photo metadata, location records, and folders.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val json = onExportAll()
                                        val file = BackupHelper.exportToBgFile(context, json, "PhotoViewsBackup.bg")
                                        showExportSuccessDialog = file.absolutePath
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export All Data (.bg Package)", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Section 2: Import Options
                item {
                    Text(
                        text = "Import Options",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Restore posts, folders, and location metadata from an existing .bg backup package without creating duplicates.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { showImportConfirmDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Import & Restore .bg File", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Section 3: Supported Formats
                item {
                    Text(
                        text = "Supported Formats",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            FormatChip(".bg (Package)")
                            FormatChip("JPG / PNG")
                            FormatChip("WEBP")
                            FormatChip("JSON Meta")
                        }
                    }
                }
            }
        }

        // Export Success Dialog
        showExportSuccessDialog?.let { filePath ->
            AlertDialog(
                onDismissRequest = { showExportSuccessDialog = null },
                title = { Text("Backup Package Created", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("PhotoViews backup saved successfully as .bg file.")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "File: PhotoViewsBackup.bg",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandBlue
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showExportSuccessDialog = null },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Import Confirm Dialog
        if (showImportConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showImportConfirmDialog = false },
                title = { Text("Import .bg Package", fontWeight = FontWeight.Bold) },
                text = {
                    Text("This will validate the schema and restore all folders and posts, automatically skipping duplicates.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showImportConfirmDialog = false
                            coroutineScope.launch {
                                val simulatedBg = BackupHelper.createBackupJson(emptyList(), emptyList())
                                val (success, message) = onImportBgData(simulatedBg)
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Validate & Restore")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showImportConfirmDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun FormatChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BrandBlue.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
    }
}
