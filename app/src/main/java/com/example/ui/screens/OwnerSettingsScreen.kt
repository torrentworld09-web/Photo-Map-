package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriveAccount
import com.example.data.model.LinkedDrive
import com.example.ui.components.GlassCard
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerSettingsScreen(
    driveAccount: DriveAccount,
    isSyncing: Boolean,
    onBackClick: () -> Unit,
    onChangeDriveAccount: (newEmail: String) -> Unit,
    onAddLinkedDrive: (email: String, label: String) -> Unit = { _, _ -> },
    onRemoveLinkedDrive: (driveId: String) -> Unit = {},
    onSetPrimaryLinkedDrive: (driveId: String) -> Unit = {},
    onSyncNow: () -> Unit,
    onDisconnectDrive: () -> Unit,
    onChangeCredentials: (oldPass: String, newOwnerId: String, newPass: String) -> Boolean
) {
    val context = LocalContext.current

    // Dialog States for Change Google Drive Account Flow
    var showChangeConfirmDialog by remember { mutableStateOf(false) }
    var showNewAccountInputDialog by remember { mutableStateOf(false) }
    var newAccountEmailInput by remember { mutableStateOf("") }

    // Dialog States for Multi-Drive Link
    var showLinkMultiDriveDialog by remember { mutableStateOf(false) }
    var multiDriveEmailInput by remember { mutableStateOf("") }
    var multiDriveLabelInput by remember { mutableStateOf("Secondary Storage") }

    // Dialog States for Changing Owner Credentials
    var showChangeCredentialsDialog by remember { mutableStateOf(false) }
    var currentPasswordInput by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newOwnerIdInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var credentialsError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar (Theme aware, no excessive white space)
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
                    Column {
                        Text(
                            text = "Owner Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Cloud Storage & Security Control",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Warning Banner if Storage Full or Near Full
                if (driveAccount.isStorageFull) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BrandAmber.copy(alpha = 0.15f))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Primary Storage Almost Full",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BrandAmber
                                    )
                                    Text(
                                        text = "Link an additional Google Drive account below so new photos auto-route to secondary storage.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 1: Active Google Drive Storage Account
                item {
                    Text(
                        text = "Google Drive Storage Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        elevation = 3.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = if (driveAccount.isConnected) BrandGreen else BrandRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (driveAccount.isConnected) "Connected" else "Disconnected",
                                        fontWeight = FontWeight.Bold,
                                        color = if (driveAccount.isConnected) BrandGreen else BrandRed,
                                        fontSize = 14.sp
                                    )
                                }

                                Text(
                                    text = "🟢 Active",
                                    fontSize = 12.sp,
                                    color = BrandGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Current Storage Account:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = driveAccount.currentAccountEmail,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Cloud Storage: ${driveAccount.formattedUsed} / ${driveAccount.formattedTotal}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Last Sync: ${driveAccount.lastSyncTime}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 14.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )

                            // Action Buttons
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { showChangeConfirmDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Change Google Drive Account", fontWeight = FontWeight.SemiBold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = onSyncNow,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = !isSyncing
                                    ) {
                                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isSyncing) "Syncing..." else "Sync Now")
                                    }

                                    OutlinedButton(
                                        onClick = onDisconnectDrive,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Logout", color = BrandRed)
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Multi-Google Drive Storage Linking (NEW USER FEATURE)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Linked Google Drive Storage (${driveAccount.linkedDrives.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(
                            onClick = { showLinkMultiDriveDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Drive", tint = BrandBlue)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When primary drive is full, photos will automatically overflow into linked backup drives.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        driveAccount.linkedDrives.forEach { drive ->
                            LinkedDriveCard(
                                drive = drive,
                                onSetPrimary = { onSetPrimaryLinkedDrive(drive.id) },
                                onRemove = { onRemoveLinkedDrive(drive.id) }
                            )
                        }

                        OutlinedButton(
                            onClick = { showLinkMultiDriveDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("+ Link Another Google Drive Account")
                        }
                    }
                }

                // Section 3: Storage Account History
                item {
                    Text(
                        text = "Storage Account History",
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HistoryRow(label = "Current Google Drive Account", value = driveAccount.currentAccountEmail)
                            HistoryRow(label = "Previous Google Drive Account", value = driveAccount.previousAccountEmail)
                            HistoryRow(label = "Last Account Change Date", value = driveAccount.lastAccountChangeDate)
                            HistoryRow(label = "Last Sync", value = driveAccount.lastSyncTime)
                            HistoryRow(label = "Cloud Storage Used", value = "${driveAccount.formattedUsed} of ${driveAccount.formattedTotal}")
                        }
                    }
                }

                // Section 4: Owner Credentials
                item {
                    Text(
                        text = "Owner Credentials & Security",
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Update administrative credentials. All past sessions will be invalidated.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    showChangeCredentialsDialog = true
                                    currentPasswordInput = ""
                                    newOwnerIdInput = ""
                                    newPasswordInput = ""
                                    credentialsError = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Owner ID / Password")
                            }
                        }
                    }
                }
            }
        }

        // STEP 1: CHANGE GOOGLE DRIVE ACCOUNT CONFIRMATION DIALOG
        if (showChangeConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showChangeConfirmDialog = false },
                title = {
                    Text(
                        text = "Change Google Drive storage account?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "New Posts and synchronized app data will use the newly connected Google Drive account.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Existing cloud data will not be deleted automatically. All local posts, photos, folders, and metadata will remain safe.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showChangeConfirmDialog = false
                            newAccountEmailInput = ""
                            showNewAccountInputDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continue")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showChangeConfirmDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // STEP 2: CONNECT NEW GOOGLE ACCOUNT DIALOG (Simulating Google OAuth Account Picker)
        if (showNewAccountInputDialog) {
            AlertDialog(
                onDismissRequest = { showNewAccountInputDialog = false },
                title = {
                    Text(
                        text = "Connect Google Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Select or enter the new Gmail account for Photo Views cloud storage:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        listOf("photoviews.admin@gmail.com", "storage.survey@gmail.com", "enterprise.cloud@gmail.com").forEach { quickEmail ->
                            OutlinedButton(
                                onClick = { newAccountEmailInput = quickEmail },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(quickEmail, fontSize = 12.sp)
                            }
                        }

                        OutlinedTextField(
                            value = newAccountEmailInput,
                            onValueChange = { newAccountEmailInput = it },
                            label = { Text("Or Enter New Gmail Address") },
                            placeholder = { Text("newaccount@gmail.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val email = if (newAccountEmailInput.isNotBlank()) newAccountEmailInput.trim() else "newaccount@gmail.com"
                            onChangeDriveAccount(email)
                            showNewAccountInputDialog = false
                            Toast.makeText(context, "Connected to $email", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Authenticate & Connect")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showNewAccountInputDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // STEP 3: LINK MULTI-DRIVE STORAGE ACCOUNT DIALOG
        if (showLinkMultiDriveDialog) {
            AlertDialog(
                onDismissRequest = { showLinkMultiDriveDialog = false },
                title = {
                    Text("Link Multi-Google Drive Storage", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Link a secondary or expansion Google Drive account for extra storage capacity:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = multiDriveLabelInput,
                            onValueChange = { multiDriveLabelInput = it },
                            label = { Text("Drive Storage Label") },
                            placeholder = { Text("e.g. Survey Backup Drive") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = multiDriveEmailInput,
                            onValueChange = { multiDriveEmailInput = it },
                            label = { Text("Google Drive Gmail") },
                            placeholder = { Text("secondarydrive@gmail.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val email = if (multiDriveEmailInput.isNotBlank()) multiDriveEmailInput.trim() else "secondary.storage@gmail.com"
                            onAddLinkedDrive(email, multiDriveLabelInput)
                            showLinkMultiDriveDialog = false
                            Toast.makeText(context, "Linked $email to Drive pool", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Link Drive")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLinkMultiDriveDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // CHANGE OWNER CREDENTIALS DIALOG
        if (showChangeCredentialsDialog) {
            AlertDialog(
                onDismissRequest = { showChangeCredentialsDialog = false },
                title = {
                    Text("Change Owner Credentials", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = currentPasswordInput,
                            onValueChange = { currentPasswordInput = it; credentialsError = null },
                            label = { Text("Current Password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = BrandBlue)
                            },
                            trailingIcon = {
                                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                    Icon(
                                        imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (currentPasswordVisible) "Hide password" else "Show password",
                                        tint = if (currentPasswordVisible) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = newOwnerIdInput,
                            onValueChange = { newOwnerIdInput = it; credentialsError = null },
                            label = { Text("New Owner ID") },
                            placeholder = { Text("e.g. Boogeymancartoons") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = { newPasswordInput = it; credentialsError = null },
                            label = { Text("New Password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = BrandBlue)
                            },
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (newPasswordVisible) "Hide password" else "Show password",
                                        tint = if (newPasswordVisible) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (credentialsError != null) {
                            Text(
                                text = credentialsError!!,
                                color = BrandRed,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (currentPasswordInput.isBlank() || newPasswordInput.isBlank()) {
                                credentialsError = "Please fill in all fields"
                                return@Button
                            }
                            val success = onChangeCredentials(currentPasswordInput, newOwnerIdInput, newPasswordInput)
                            if (success) {
                                showChangeCredentialsDialog = false
                                Toast.makeText(context, "Owner credentials updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                credentialsError = "Incorrect current password"
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showChangeCredentialsDialog = false },
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
fun LinkedDriveCard(
    drive: LinkedDrive,
    onSetPrimary: () -> Unit = {},
    onRemove: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (drive.isPrimary) BrandBlue.copy(alpha = 0.15f) else BrandGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (drive.isPrimary) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = if (drive.isPrimary) BrandBlue else BrandGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = drive.label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (drive.isPrimary) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BrandBlue.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("Primary", fontSize = 9.sp, color = BrandBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(text = drive.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (!drive.isPrimary) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = onSetPrimary,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Set Primary", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Unlink Drive", tint = BrandRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { drive.usagePercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (drive.isFull) BrandRed else BrandBlue,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${drive.formattedUsed} / ${drive.formattedTotal} used",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Linked: ${drive.linkedDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HistoryRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
