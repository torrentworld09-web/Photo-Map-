package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Post
import com.example.data.model.UserRole
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPostViewer(
    post: Post,
    userRole: UserRole,
    onClose: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveFolder: () -> Unit,
    onPermanentDelete: () -> Unit = {}
) {
    val context = LocalContext.current
    var showOverlays by remember { mutableStateOf(true) }
    var menuExpanded by remember { mutableStateOf(false) }

    val photos = if (post.photoUris.isNotEmpty()) post.photoUris else listOf("")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { photos.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Horizontal Pager with Zoom
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            var scale by remember { mutableFloatStateOf(1f) }
            var offsetX by remember { mutableFloatStateOf(0f) }
            var offsetY by remember { mutableFloatStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            if (scale > 1f) {
                                offsetX += pan.x
                                offsetY += pan.y
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
                    .clickable { showOverlays = !showOverlays },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photos[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = post.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            }
        }

        // Top App Bar Overlay
        AnimatedVisibility(
            visible = showOverlays,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xCC000000), Color.Transparent)
                        )
                    )
                    .padding(top = 40.dp, start = 12.dp, end = 12.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.background(Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "${pagerState.currentPage + 1} of ${photos.size}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.background(Color(0x33FFFFFF), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (post.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (post.isFavorite) BrandAmber else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box {
                            IconButton(
                                onClick = { menuExpanded = true },
                                modifier = Modifier.background(Color(0x33FFFFFF), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More",
                                    tint = Color.White
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                if (userRole == UserRole.OWNER) {
                                    DropdownMenuItem(
                                        text = { Text("Edit Post") },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = BrandBlue) },
                                        onClick = { menuExpanded = false; onEdit() }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Move to Folder") },
                                    leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                                    onClick = { menuExpanded = false; onMoveFolder() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete from Device", color = BrandAmber) },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = BrandAmber) },
                                    onClick = { menuExpanded = false; onDelete() }
                                )
                                if (userRole == UserRole.OWNER) {
                                    DropdownMenuItem(
                                        text = { Text("Permanently Delete (Cloud)", color = BrandRed) },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = BrandRed) },
                                        onClick = { menuExpanded = false; onPermanentDelete() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Info & Quick Actions Overlay
        AnimatedVisibility(
            visible = showOverlays,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xDE000000), Color(0xF5000000))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Post Information Card
                Text(
                    text = post.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (post.description.isNotBlank()) {
                    Text(
                        text = post.description,
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metadata Rows
                if (post.locationName.isNotBlank()) {
                    MetadataLine(icon = Icons.Default.LocationOn, text = post.locationName)
                }
                if (post.plusCode.isNotBlank()) {
                    MetadataLine(icon = Icons.Default.QrCode, text = post.plusCode)
                }
                MetadataLine(
                    icon = Icons.Default.WbSunny,
                    text = "${post.date} • ${post.time} • ${post.conditions} • ☁ ${post.formattedStorageSize}"
                )

                if (post.mobile.isNotBlank()) {
                    MetadataLine(icon = Icons.Default.Call, text = post.mobile)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (post.mobile.isNotBlank()) {
                        ViewerActionButton(
                            icon = Icons.Default.Call,
                            label = "Call",
                            onClick = {
                                try {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${post.mobile}"))
                                    context.startActivity(dialIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open dialer", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    ViewerActionButton(
                        icon = Icons.Default.Map,
                        label = "Maps",
                        onClick = {
                            val uri = if (post.mapsUrl.isNotBlank()) {
                                Uri.parse(post.mapsUrl)
                            } else if (post.latitude != null && post.longitude != null) {
                                Uri.parse("geo:${post.latitude},${post.longitude}?q=${post.latitude},${post.longitude}(${post.title})")
                            } else {
                                Uri.parse("https://maps.google.com/?q=${post.locationName}")
                            }
                            try {
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open maps", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    ViewerActionButton(
                        icon = if (post.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        label = "Favorite",
                        tint = if (post.isFavorite) BrandAmber else Color.White,
                        onClick = onToggleFavorite
                    )

                    ViewerActionButton(
                        icon = Icons.Default.Share,
                        label = "Share",
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, post.title)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${post.title}\n${post.description}\nLocation: ${post.locationName}\nPlus Code: ${post.plusCode}\n${post.mapsUrl}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share post"))
                        }
                    )

                    ViewerActionButton(
                        icon = Icons.Default.Delete,
                        label = "Delete",
                        tint = BrandRed,
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataLine(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color(0xFFE2E8F0),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ViewerActionButton(
    icon: ImageVector,
    label: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
