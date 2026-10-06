package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Folder
import com.example.data.model.Post
import com.example.data.model.SyncState
import com.example.data.model.UserAccount
import com.example.data.model.UserRole

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val mobile: String,
    val mapsUrl: String,
    val latitude: Double?,
    val longitude: Double?,
    val locationName: String,
    val plusCode: String,
    val date: String,
    val time: String,
    val conditions: String,
    val folderId: String?,
    val folderName: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val storageBytes: Long,
    val cloudFileId: String?,
    val syncStatus: SyncState,
    val photoUris: List<String>,
    val tags: List<String>,
    val isCameraPhoto: Boolean,
    val isImported: Boolean,
    val isDownloaded: Boolean
) {
    fun toPost(): Post = Post(
        id = id,
        title = title,
        description = description,
        mobile = mobile,
        mapsUrl = mapsUrl,
        latitude = latitude,
        longitude = longitude,
        locationName = locationName,
        plusCode = plusCode,
        date = date,
        time = time,
        conditions = conditions,
        folderId = folderId,
        folderName = folderName,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt,
        storageBytes = storageBytes,
        cloudFileId = cloudFileId,
        syncStatus = syncStatus,
        photoUris = photoUris,
        tags = tags,
        isCameraPhoto = isCameraPhoto,
        isImported = isImported,
        isDownloaded = isDownloaded
    )

    companion object {
        fun fromPost(post: Post): PostEntity = PostEntity(
            id = post.id,
            title = post.title,
            description = post.description,
            mobile = post.mobile,
            mapsUrl = post.mapsUrl,
            latitude = post.latitude,
            longitude = post.longitude,
            locationName = post.locationName,
            plusCode = post.plusCode,
            date = post.date,
            time = post.time,
            conditions = post.conditions,
            folderId = post.folderId,
            folderName = post.folderName,
            isFavorite = post.isFavorite,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
            storageBytes = post.storageBytes,
            cloudFileId = post.cloudFileId,
            syncStatus = post.syncStatus,
            photoUris = post.photoUris,
            tags = post.tags,
            isCameraPhoto = post.isCameraPhoto,
            isImported = post.isImported,
            isDownloaded = post.isDownloaded
        )
    }
}

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val postCount: Int,
    val storageBytes: Long,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toFolder(): Folder = Folder(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        postCount = postCount,
        storageBytes = storageBytes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromFolder(folder: Folder): FolderEntity = FolderEntity(
            id = folder.id,
            name = folder.name,
            iconName = folder.iconName,
            colorHex = folder.colorHex,
            postCount = folder.postCount,
            storageBytes = folder.storageBytes,
            createdAt = folder.createdAt,
            updatedAt = folder.updatedAt
        )
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean,
    val lastLogin: String,
    val lastSync: String,
    val postCount: Int,
    val photoCount: Int,
    val storageBytes: Long
) {
    fun toUser(): UserAccount = UserAccount(
        id = id,
        username = username,
        email = email,
        role = role,
        isActive = isActive,
        lastLogin = lastLogin,
        lastSync = lastSync,
        postCount = postCount,
        photoCount = photoCount,
        storageBytes = storageBytes
    )

    companion object {
        fun fromUser(u: UserAccount): UserEntity = UserEntity(
            id = u.id,
            username = u.username,
            email = u.email,
            role = u.role,
            isActive = u.isActive,
            lastLogin = u.lastLogin,
            lastSync = u.lastSync,
            postCount = u.postCount,
            photoCount = u.photoCount,
            storageBytes = u.storageBytes
        )
    }
}
