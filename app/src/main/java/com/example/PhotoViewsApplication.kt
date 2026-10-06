package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.local.SecurityPrefs
import com.example.data.repository.PhotoViewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PhotoViewsApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this) }
    val securityPrefs by lazy { SecurityPrefs(this) }
    val repository by lazy { PhotoViewsRepository(database, securityPrefs, this) }

    override fun onCreate() {
        super.onCreate()
        com.example.util.AppIconLauncherManager.restoreActiveLauncherIcon(this)
        applicationScope.launch {
            repository.initializeSampleDataIfNeeded()
        }
    }
}
