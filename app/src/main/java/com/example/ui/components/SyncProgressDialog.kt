package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.cloud.SyncRestoreResult
import com.example.data.model.SyncProgressStep
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandGreen

/**
 * CRITICAL USER REQUIREMENT 2:
 * Restore & Return Sync Progress UI
 * Shows: Preparing → Downloading → Restoring → Sync Complete
 */
@Composable
fun SyncProgressDialog(
    currentStep: SyncProgressStep?,
    result: SyncRestoreResult?,
    onDismiss: () -> Unit
) {
    if (currentStep == null && result == null) return

    val isFinished = currentStep == SyncProgressStep.COMPLETE || result != null

    AlertDialog(
        onDismissRequest = {
            if (isFinished) onDismiss()
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isFinished) BrandGreen.copy(alpha = 0.15f) else BrandBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFinished) Icons.Default.DoneAll else Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = if (isFinished) BrandGreen else BrandBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isFinished) "Sync Complete!" else "Restore & Sync",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (isFinished) "Cloud data restored" else "Syncing with cloud storage...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Progress Bar
                val progressFraction = when (currentStep) {
                    SyncProgressStep.PREPARING -> 0.25f
                    SyncProgressStep.DOWNLOADING -> 0.55f
                    SyncProgressStep.RESTORING -> 0.85f
                    SyncProgressStep.COMPLETE -> 1.0f
                    null -> if (result != null) 1.0f else 0.1f
                }

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isFinished) BrandGreen else BrandBlue,
                    trackColor = Color(0xFFE2E8F0)
                )

                // 4 Steps visualization
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StepItem(
                        stepIndex = 1,
                        name = "Preparing",
                        subtitle = "Scanning cloud storage",
                        icon = Icons.Default.Storage,
                        active = currentStep == SyncProgressStep.PREPARING,
                        completed = currentStep != null && currentStep.stepNumber > 1 || isFinished
                    )

                    StepItem(
                        stepIndex = 2,
                        name = "Downloading",
                        subtitle = "Retrieving cloud backups & photos",
                        icon = Icons.Default.CloudDownload,
                        active = currentStep == SyncProgressStep.DOWNLOADING,
                        completed = currentStep != null && currentStep.stepNumber > 2 || isFinished
                    )

                    StepItem(
                        stepIndex = 3,
                        name = "Restoring",
                        subtitle = "Rebuilding local database (avoiding duplicates)",
                        icon = Icons.Default.CloudSync,
                        active = currentStep == SyncProgressStep.RESTORING,
                        completed = currentStep != null && currentStep.stepNumber > 3 || isFinished
                    )

                    StepItem(
                        stepIndex = 4,
                        name = "Sync Complete",
                        subtitle = "All posts & folders restored",
                        icon = Icons.Default.DoneAll,
                        active = currentStep == SyncProgressStep.COMPLETE,
                        completed = isFinished
                    )
                }

                // Summary details when complete
                if (result != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandGreen.copy(alpha = 0.12f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Restore Results:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BrandGreen
                            )
                            Text(
                                text = "• Posts Restored: ${result.restoredPostsCount}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• Folders Restored: ${result.restoredFoldersCount}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• Total Cloud Posts: ${result.totalCloudPostsCount}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• Local database safely updated without duplicates.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandGreen
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isFinished) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun StepItem(
    stepIndex: Int,
    name: String,
    subtitle: String,
    icon: ImageVector,
    active: Boolean,
    completed: Boolean
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            completed -> BrandGreen.copy(alpha = 0.15f)
            active -> BrandBlue.copy(alpha = 0.15f)
            else -> Color(0xFFF1F5F9)
        },
        animationSpec = tween(300),
        label = "step_bg"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            completed -> BrandGreen
            active -> BrandBlue
            else -> Color(0xFF94A3B8)
        },
        animationSpec = tween(300),
        label = "step_icon"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (completed) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(18.dp)
                )
            } else if (active) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = BrandBlue
                )
            } else {
                Text(
                    text = "$stepIndex",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = if (active || completed) FontWeight.Bold else FontWeight.Normal,
                color = if (completed) BrandGreen else if (active) BrandBlue else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
