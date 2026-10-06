package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.UserRole
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandRed
import com.example.ui.theme.LocalGlassTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOptionsMenu(
    post: Post,
    userRole: UserRole,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMoveToFolder: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    onViewDetails: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit,
    onPermanentDelete: () -> Unit = {}
) {
    val glassTheme = LocalGlassTheme.current
    val isDark = glassTheme.isDark
    val sheetBg = if (isDark) Color(0xFF0F172A) else Color.White.copy(alpha = 0.96f)
    val textPrimary = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val dividerColor = if (isDark) Color(0x22FFFFFF) else Color(0x1F000000)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = sheetBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Post preview mini header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = textPrimary
                    )
                    Text(
                        text = "${post.date} • ${post.formattedStorageSize} • ${post.folderName}",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = dividerColor
            )

            // Menu Items
            OptionMenuItem(
                icon = Icons.Default.OpenInFull,
                label = "Open",
                onClick = { onDismiss(); onOpen() }
            )

            // Owner only edit check
            if (userRole == UserRole.OWNER) {
                OptionMenuItem(
                    icon = Icons.Default.Edit,
                    label = "Edit",
                    trailingBadge = "Owner only",
                    iconTint = BrandBlue,
                    onClick = { onDismiss(); onEdit() }
                )
            }

            OptionMenuItem(
                icon = if (post.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                label = if (post.isFavorite) "Remove from Favorites" else "Add to Favorites",
                iconTint = if (post.isFavorite) BrandAmber else Color(0xFF475569),
                onClick = { onDismiss(); onToggleFavorite() }
            )

            OptionMenuItem(
                icon = Icons.Default.DriveFileMove,
                label = "Move to Folder",
                onClick = { onDismiss(); onMoveToFolder() }
            )

            OptionMenuItem(
                icon = Icons.Default.Share,
                label = "Share",
                onClick = { onDismiss(); onShare() }
            )

            OptionMenuItem(
                icon = Icons.Default.FileDownload,
                label = "Download",
                onClick = { onDismiss(); onDownload() }
            )

            OptionMenuItem(
                icon = Icons.Default.Info,
                label = "View Details",
                onClick = { onDismiss(); onViewDetails() }
            )

            OptionMenuItem(
                icon = Icons.Default.UploadFile,
                label = "Export (.bg / JSON)",
                onClick = { onDismiss(); onExport() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color(0x1F000000)
            )

            // Local delete: available for users and owner (deletes only from device, keeps cloud backup safe)
            OptionMenuItem(
                icon = Icons.Default.Delete,
                label = "Delete from Device",
                textColor = BrandAmber,
                iconTint = BrandAmber,
                onClick = { onDismiss(); onDelete() }
            )

            // Owner-Only Permanent Cloud Deletion
            if (userRole == UserRole.OWNER) {
                OptionMenuItem(
                    icon = Icons.Default.Delete,
                    label = "Permanently Delete (Cloud)",
                    textColor = BrandRed,
                    iconTint = BrandRed,
                    trailingBadge = "Owner Only",
                    onClick = { onDismiss(); onPermanentDelete() }
                )
            }
        }
    }
}

@Composable
private fun OptionMenuItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    textColor: Color? = null,
    iconTint: Color? = null,
    trailingBadge: String? = null,
    onClick: () -> Unit
) {
    val glassTheme = LocalGlassTheme.current
    val isDark = glassTheme.isDark
    val effTextColor = textColor ?: if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val effIconTint = iconTint ?: if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = effIconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = effTextColor,
            modifier = Modifier.weight(1f)
        )
        if (trailingBadge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = trailingBadge,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
