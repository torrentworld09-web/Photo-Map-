package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Folder
import com.example.data.model.Post
import com.example.data.model.SyncState
import com.example.ui.components.GlassCard
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandRed
import com.example.util.PlusCodeHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AddEditPostScreen(
    initialPost: Post? = null,
    initialPhotoUri: String? = null,
    initialLat: Double? = null,
    initialLng: Double? = null,
    initialLocation: String? = null,
    initialPlusCode: String? = null,
    initialDate: String? = null,
    initialTime: String? = null,
    initialCondition: String? = null,
    folders: List<Folder>,
    onBackClick: () -> Unit,
    onSavePost: (Post) -> Unit
) {
    val context = LocalContext.current

    val isEditing = initialPost != null

    var photos by remember {
        val list = mutableListOf<String>()
        if (initialPost != null) {
            list.addAll(initialPost.photoUris)
        } else if (initialPhotoUri != null) {
            list.add(initialPhotoUri)
        }
        mutableStateOf(list)
    }

    var title by remember { mutableStateOf(initialPost?.title ?: "") }
    var description by remember { mutableStateOf(initialPost?.description ?: "") }
    var mobile by remember { mutableStateOf(initialPost?.mobile ?: "") }
    var mapsUrl by remember { mutableStateOf(initialPost?.mapsUrl ?: "") }
    var latitude by remember { mutableStateOf(initialPost?.latitude ?: initialLat) }
    var longitude by remember { mutableStateOf(initialPost?.longitude ?: initialLng) }
    var locationName by remember { mutableStateOf(initialPost?.locationName ?: initialLocation ?: "Coimbatore, Tamil Nadu") }
    var plusCode by remember {
        val code = initialPost?.plusCode ?: initialPlusCode ?: (
            if (latitude != null && longitude != null) PlusCodeHelper.encode(latitude!!, longitude!!, locality = "Coimbatore")
            else "8F6Q+4X Coimbatore"
        )
        mutableStateOf(code)
    }

    val defaultDate = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())
    val defaultTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
    var date by remember { mutableStateOf(initialPost?.date ?: initialDate ?: defaultDate) }
    var time by remember { mutableStateOf(initialPost?.time ?: initialTime ?: defaultTime) }
    var conditions by remember { mutableStateOf(initialPost?.conditions ?: initialCondition ?: "Sunny") }

    var selectedFolder by remember {
        val folder = folders.firstOrNull { it.id == initialPost?.folderId } ?: folders.firstOrNull()
        mutableStateOf(folder)
    }
    var folderMenuExpanded by remember { mutableStateOf(false) }

    // Multi-photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val updated = photos.toMutableList()
            uris.forEach { updated.add(it.toString()) }
            photos = updated
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar with Functional Back Button (Theme-aware, no extra empty space)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
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
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEditing) "Edit Post" else "Add Post Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                title = "Photo Post ${SimpleDateFormat("dd/MM", Locale.US).format(Date())}"
                            }

                            val postId = initialPost?.id ?: "BG-${SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())}-${(100000..999999).random()}"

                            val post = Post(
                                id = postId,
                                title = title.trim(),
                                description = description.trim(),
                                mobile = mobile.trim(),
                                mapsUrl = if (mapsUrl.isNotBlank()) mapsUrl.trim() else "https://maps.google.com/?q=${latitude ?: 11.0168},${longitude ?: 76.9558}",
                                latitude = latitude,
                                longitude = longitude,
                                locationName = locationName.trim(),
                                plusCode = plusCode.trim(),
                                date = date,
                                time = time,
                                conditions = conditions,
                                folderId = selectedFolder?.id,
                                folderName = selectedFolder?.name ?: "Unorganized",
                                isFavorite = initialPost?.isFavorite ?: false,
                                createdAt = initialPost?.createdAt ?: System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis(),
                                storageBytes = initialPost?.storageBytes ?: (photos.size * 4_200_000L).coerceAtLeast(4_200_000L),
                                cloudFileId = initialPost?.cloudFileId ?: "drive_${UUID.randomUUID().toString().take(8)}",
                                syncStatus = initialPost?.syncStatus ?: SyncState.SYNCED,
                                photoUris = if (photos.isNotEmpty()) photos else listOf("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop"),
                                tags = listOf("Photo", selectedFolder?.name ?: "General"),
                                isCameraPhoto = initialPost?.isCameraPhoto ?: (initialPhotoUri != null),
                                isImported = initialPost?.isImported ?: false,
                                isDownloaded = true
                            )
                            onSavePost(post)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save")
                    }
                }
            }

            // Scrollable Form Fields
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Photo Thumbnails Strip
                Text(
                    text = "Photos (${photos.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(photos) { index, uri ->
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(uri).crossfade(true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Remove button
                            IconButton(
                                onClick = {
                                    val list = photos.toMutableList()
                                    list.removeAt(index)
                                    photos = list
                                },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Color(0x99000000), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    item {
                        // Add more photos button
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Add",
                                    tint = BrandBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text("Add", fontSize = 11.sp, color = BrandBlue, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. Mountain View") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Beautiful mountain landscape with lake") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                // Mobile Number (With dialer support)
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("Mobile Number") },
                    placeholder = { Text("e.g. 9876543210") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Google Maps Link
                OutlinedTextField(
                    value = mapsUrl,
                    onValueChange = { mapsUrl = it },
                    label = { Text("Google Maps Link") },
                    placeholder = { Text("https://maps.google.com/?q=...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Location & Plus Code
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Location") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = plusCode,
                        onValueChange = { plusCode = it },
                        label = { Text("Plus Code") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Date, Time, Conditions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = conditions,
                        onValueChange = { conditions = it },
                        label = { Text("Conditions") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Folder Dropdown
                Text(
                    text = "Folder",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { folderMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = BrandBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedFolder?.name ?: "Select Folder",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text("▼", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    DropdownMenu(
                        expanded = folderMenuExpanded,
                        onDismissRequest = { folderMenuExpanded = false }
                    ) {
                        folders.forEach { folder ->
                            DropdownMenuItem(
                                text = { Text(folder.name) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = BrandBlue
                                    )
                                },
                                onClick = {
                                    selectedFolder = folder
                                    folderMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
