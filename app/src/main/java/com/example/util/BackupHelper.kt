package com.example.util

import android.content.Context
import com.example.data.model.Folder
import com.example.data.model.Post
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

data class BackupValidationResult(
    val isValid: Boolean,
    val schemaVersion: Int,
    val postCount: Int,
    val folderCount: Int,
    val backupDate: String,
    val posts: List<Post>,
    val folders: List<Folder>,
    val errorMessage: String? = null
)

object BackupHelper {
    private const val SCHEMA_VERSION = 2
    private const val SIGNATURE = "PHOTOVIEWS_BG_PACKAGE"

    fun createBackupJson(
        posts: List<Post>,
        folders: List<Folder>,
        exportType: String = "ALL_DATA"
    ): String {
        val root = JSONObject()
        root.put("signature", SIGNATURE)
        root.put("schemaVersion", SCHEMA_VERSION)
        root.put("appVersion", "1.0")
        root.put("createdAt", System.currentTimeMillis())
        root.put("exportType", exportType)

        // Folders
        val foldersArray = JSONArray()
        folders.forEach { f ->
            val fo = JSONObject().apply {
                put("id", f.id)
                put("name", f.name)
                put("iconName", f.iconName)
                put("colorHex", f.colorHex)
                put("postCount", f.postCount)
                put("storageBytes", f.storageBytes)
                put("createdAt", f.createdAt)
                put("updatedAt", f.updatedAt)
            }
            foldersArray.put(fo)
        }
        root.put("folders", foldersArray)

        // Posts
        val postsArray = JSONArray()
        posts.forEach { p ->
            val po = JSONObject().apply {
                put("id", p.id)
                put("title", p.title)
                put("description", p.description)
                put("mobile", p.mobile)
                put("mapsUrl", p.mapsUrl)
                if (p.latitude != null) put("latitude", p.latitude)
                if (p.longitude != null) put("longitude", p.longitude)
                put("locationName", p.locationName)
                put("plusCode", p.plusCode)
                put("date", p.date)
                put("time", p.time)
                put("conditions", p.conditions)
                put("folderId", p.folderId ?: "")
                put("folderName", p.folderName)
                put("isFavorite", p.isFavorite)
                put("createdAt", p.createdAt)
                put("updatedAt", p.updatedAt)
                put("storageBytes", p.storageBytes)
                put("cloudFileId", p.cloudFileId ?: "")
                put("syncStatus", p.syncStatus.name)
                put("isCameraPhoto", p.isCameraPhoto)
                put("isImported", true) // Mark as imported when restored
                put("isDownloaded", true)

                val urisArray = JSONArray()
                p.photoUris.forEach { urisArray.put(it) }
                put("photoUris", urisArray)

                val tagsArray = JSONArray()
                p.tags.forEach { tagsArray.put(it) }
                put("tags", tagsArray)
            }
            postsArray.put(po)
        }
        root.put("posts", postsArray)

        return root.toString(2)
    }

    fun exportToBgFile(context: Context, jsonContent: String, fileName: String = "PhotoViewsBackup.bg"): File {
        val exportDir = File(context.filesDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()
        val file = File(exportDir, fileName)
        FileOutputStream(file).use {
            it.write(jsonContent.toByteArray(StandardCharsets.UTF_8))
        }
        return file
    }

    fun validateAndParseBgContent(rawContent: String): BackupValidationResult {
        return try {
            val root = JSONObject(rawContent)
            val signature = root.optString("signature", "")
            if (signature != SIGNATURE && !signature.contains("PHOTOVIEWS")) {
                return BackupValidationResult(
                    isValid = false,
                    schemaVersion = 0,
                    postCount = 0,
                    folderCount = 0,
                    backupDate = "",
                    posts = emptyList(),
                    folders = emptyList(),
                    errorMessage = "Invalid .bg package header signature."
                )
            }

            val schemaVersion = root.optInt("schemaVersion", 1)
            val createdAt = root.optLong("createdAt", System.currentTimeMillis())
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.US).format(createdAt)

            val parsedFolders = mutableListOf<Folder>()
            val foldersArray = root.optJSONArray("folders")
            if (foldersArray != null) {
                for (i in 0 until foldersArray.length()) {
                    val fo = foldersArray.getJSONObject(i)
                    parsedFolders.add(
                        Folder(
                            id = fo.getString("id"),
                            name = fo.getString("name"),
                            iconName = fo.optString("iconName", "folder"),
                            colorHex = fo.optString("colorHex", "#3B82F6"),
                            postCount = fo.optInt("postCount", 0),
                            storageBytes = fo.optLong("storageBytes", 0L),
                            createdAt = fo.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = fo.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val parsedPosts = mutableListOf<Post>()
            val postsArray = root.optJSONArray("posts")
            if (postsArray != null) {
                for (i in 0 until postsArray.length()) {
                    val po = postsArray.getJSONObject(i)
                    val photoUris = mutableListOf<String>()
                    val pArray = po.optJSONArray("photoUris")
                    if (pArray != null) {
                        for (j in 0 until pArray.length()) {
                            photoUris.add(pArray.getString(j))
                        }
                    }

                    val tags = mutableListOf<String>()
                    val tArray = po.optJSONArray("tags")
                    if (tArray != null) {
                        for (k in 0 until tArray.length()) {
                            tags.add(tArray.getString(k))
                        }
                    }

                    parsedPosts.add(
                        Post(
                            id = po.getString("id"),
                            title = po.getString("title"),
                            description = po.optString("description", ""),
                            mobile = po.optString("mobile", ""),
                            mapsUrl = po.optString("mapsUrl", ""),
                            latitude = if (po.has("latitude")) po.getDouble("latitude") else null,
                            longitude = if (po.has("longitude")) po.getDouble("longitude") else null,
                            locationName = po.optString("locationName", ""),
                            plusCode = po.optString("plusCode", ""),
                            date = po.optString("date", ""),
                            time = po.optString("time", ""),
                            conditions = po.optString("conditions", "Sunny"),
                            folderId = if (po.optString("folderId").isNotEmpty()) po.getString("folderId") else null,
                            folderName = po.optString("folderName", "Unorganized"),
                            isFavorite = po.optBoolean("isFavorite", false),
                            createdAt = po.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = po.optLong("updatedAt", System.currentTimeMillis()),
                            storageBytes = po.optLong("storageBytes", 4_200_000L),
                            cloudFileId = po.optString("cloudFileId", null),
                            syncStatus = com.example.data.model.SyncState.SYNCED,
                            photoUris = photoUris,
                            tags = tags,
                            isCameraPhoto = po.optBoolean("isCameraPhoto", false),
                            isImported = true,
                            isDownloaded = true
                        )
                    )
                }
            }

            BackupValidationResult(
                isValid = true,
                schemaVersion = schemaVersion,
                postCount = parsedPosts.size,
                folderCount = parsedFolders.size,
                backupDate = dateStr,
                posts = parsedPosts,
                folders = parsedFolders,
                errorMessage = null
            )
        } catch (e: Exception) {
            BackupValidationResult(
                isValid = false,
                schemaVersion = 0,
                postCount = 0,
                folderCount = 0,
                backupDate = "",
                posts = emptyList(),
                folders = emptyList(),
                errorMessage = "Failed to parse backup package: ${e.localizedMessage}"
            )
        }
    }
}
