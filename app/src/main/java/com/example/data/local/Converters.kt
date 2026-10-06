package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.SyncState
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(";;;") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(";;;").filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromSyncState(value: SyncState): String {
        return value.name
    }

    @TypeConverter
    fun toSyncState(value: String?): SyncState {
        return try {
            if (value != null) SyncState.valueOf(value) else SyncState.SYNCED
        } catch (e: Exception) {
            SyncState.SYNCED
        }
    }

    @TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }

    @TypeConverter
    fun toUserRole(value: String?): UserRole {
        return try {
            if (value != null) UserRole.valueOf(value) else UserRole.USER
        } catch (e: Exception) {
            UserRole.USER
        }
    }
}
