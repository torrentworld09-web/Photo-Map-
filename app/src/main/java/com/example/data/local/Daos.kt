package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: String): PostEntity?

    @Query("SELECT * FROM posts WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritePosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE folderId = :folderId ORDER BY createdAt DESC")
    fun getPostsByFolder(folderId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isCameraPhoto = 1 ORDER BY createdAt DESC")
    fun getCameraPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isImported = 1 ORDER BY createdAt DESC")
    fun getImportedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE syncStatus = 'OFFLINE' ORDER BY createdAt DESC")
    fun getOfflinePosts(): Flow<List<PostEntity>>

    @Query("""
        SELECT * FROM posts 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR mobile LIKE '%' || :query || '%' 
           OR locationName LIKE '%' || :query || '%' 
           OR plusCode LIKE '%' || :query || '%' 
           OR conditions LIKE '%' || :query || '%' 
           OR folderName LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchPosts(query: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("UPDATE posts SET isFavorite = :isFav, updatedAt = :time WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean, time: Long = System.currentTimeMillis())

    @Query("UPDATE posts SET folderId = :folderId, folderName = :folderName, updatedAt = :time WHERE id = :id")
    suspend fun updateFolder(id: String, folderId: String?, folderName: String, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePostById(id: String): Int

    @Query("DELETE FROM posts WHERE id IN (:ids)")
    suspend fun deletePostsByIds(ids: List<String>): Int

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    @Query("SELECT SUM(storageBytes) FROM posts")
    suspend fun getTotalStorageBytes(): Long?
}

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :id LIMIT 1")
    suspend fun getFolderById(id: String): FolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<FolderEntity>)

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Query("UPDATE folders SET name = :newName, updatedAt = :time WHERE id = :id")
    suspend fun renameFolder(id: String, newName: String, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: String): Int

    @Query("DELETE FROM folders")
    suspend fun deleteAllFolders()

    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("UPDATE users SET isActive = :isActive WHERE id = :id")
    suspend fun setUserActive(id: String, isActive: Boolean)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}
