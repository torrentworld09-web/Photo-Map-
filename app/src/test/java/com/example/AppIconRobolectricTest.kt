package com.example

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.cloud.CloudStorageManager
import com.example.data.local.AppDatabase
import com.example.data.local.SecurityPrefs
import com.example.data.model.DriveAccount
import com.example.data.model.DriveAppIcon
import com.example.data.repository.PhotoViewsRepository
import com.example.ui.screens.AppIconScreen
import com.example.util.AppIconLauncherManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-420dpi")
class AppIconRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var securityPrefs: SecurityPrefs
    private lateinit var cloudStorageManager: CloudStorageManager
    private lateinit var database: AppDatabase
    private lateinit var repository: PhotoViewsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val sp = context.getSharedPreferences("photoviews_secure_prefs", Context.MODE_PRIVATE)
        sp.edit().clear().commit()

        securityPrefs = SecurityPrefs(context)
        cloudStorageManager = CloudStorageManager(context)
        database = AppDatabase.getDatabase(context)
        repository = PhotoViewsRepository(database, securityPrefs, context)
    }

    @Test
    fun `drive icon folder seeds 12 aesthetic cloud icons by default`() {
        val icons = cloudStorageManager.getDriveAppIcons()
        assertEquals(12, icons.size)
        assertTrue(icons.all { it.folderPath == "Icon/" })

        val defaultIcon = icons.find { it.driveFileId == "drive_icon_default" }
        assertNotNull(defaultIcon)
        assertEquals("Photo Views Classic", defaultIcon?.name)
    }

    @Test
    fun `setActiveAppIcon switches real launcher alias and updates securityPrefs`() {
        assertEquals("drive_icon_default", repository.activeAppIconDriveId.value)

        val result = repository.setActiveAppIcon("drive_icon_neon")
        assertTrue(result.success)
        assertEquals("MainActivityNeon", result.appliedAlias.simpleName)
        assertEquals("drive_icon_neon", repository.activeAppIconDriveId.value)
        assertEquals("drive_icon_neon", securityPrefs.activeAppIconDriveId)
        assertEquals("com.example.MainActivityNeon", securityPrefs.activeLauncherAlias)

        // Verify PackageManager component states
        val pm = context.packageManager
        val neonState = pm.getComponentEnabledSetting(ComponentName(context, "com.example.MainActivityNeon"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, neonState)

        val defaultState = pm.getComponentEnabledSetting(ComponentName(context, "com.example.MainActivityDefault"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, defaultState)
    }

    @Test
    fun `restoreDefaultAppIcon reenables default launcher alias and resets prefs`() {
        repository.setActiveAppIcon("drive_icon_sunset")
        assertEquals("drive_icon_sunset", securityPrefs.activeAppIconDriveId)

        val result = repository.restoreDefaultAppIcon()
        assertTrue(result.success)
        assertEquals("MainActivityDefault", result.appliedAlias.simpleName)
        assertEquals("drive_icon_default", repository.activeAppIconDriveId.value)
        assertEquals("drive_icon_default", securityPrefs.activeAppIconDriveId)

        val pm = context.packageManager
        val defaultState = pm.getComponentEnabledSetting(ComponentName(context, "com.example.MainActivityDefault"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, defaultState)

        val sunsetState = pm.getComponentEnabledSetting(ComponentName(context, "com.example.MainActivitySunset"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, sunsetState)
    }

    @Test
    fun `restoreActiveLauncherIcon ensures persistent alias restored on boot`() {
        securityPrefs.activeLauncherAlias = "com.example.MainActivityCobalt"
        AppIconLauncherManager.restoreActiveLauncherIcon(context)

        val active = AppIconLauncherManager.getActiveLauncherAlias(context)
        assertEquals("MainActivityCobalt", active.simpleName)
    }

    @Test
    fun `uploadNewAppIcon adds icon to Drive Icon folder`() = runBlocking {
        val initialCount = cloudStorageManager.getDriveAppIcons().size
        val uploaded = repository.uploadNewAppIcon("My Custom Lens", "content://media/external/images/1")

        assertNotNull(uploaded)
        assertEquals("My Custom Lens", uploaded.name)
        assertEquals("Icon/", uploaded.folderPath)

        val updatedIcons = repository.driveAppIcons.value
        assertEquals(initialCount + 1, updatedIcons.size)
        assertTrue(updatedIcons.any { it.driveFileId == uploaded.driveFileId })
    }

    @Test
    fun `renameAppIcon updates name in Drive Icon folder`() = runBlocking {
        val success = repository.renameAppIcon("drive_icon_neon", "Aurora Borealis Pro")
        assertTrue(success)

        val updated = repository.driveAppIcons.value.find { it.driveFileId == "drive_icon_neon" }
        assertEquals("Aurora Borealis Pro", updated?.name)
    }

    @Test
    fun `deleteAppIconFromDrive removes icon and falls back if active`() = runBlocking {
        repository.setActiveAppIcon("drive_icon_obsidian")
        assertEquals("drive_icon_obsidian", repository.activeAppIconDriveId.value)

        val success = repository.deleteAppIconFromDrive("drive_icon_obsidian")
        assertTrue(success)

        val updated = repository.driveAppIcons.value
        assertFalse(updated.any { it.driveFileId == "drive_icon_obsidian" })
        assertFalse(repository.activeAppIconDriveId.value == "drive_icon_obsidian")
    }

    @Test
    fun `appIconScreen displays current active icon hero card and active indicator`() {
        val icons = cloudStorageManager.getDriveAppIcons()
        val firstIcon = icons.first()
        val driveAccount = DriveAccount(currentAccountEmail = "storageaccount@gmail.com")

        composeTestRule.setContent {
            AppIconScreen(
                icons = icons,
                activeIconId = firstIcon.driveFileId,
                driveAccount = driveAccount,
                onBackClick = {},
                onSetActiveIcon = {},
                onRestoreDefaultIcon = {},
                onSyncWithDrive = {},
                onUploadNewIcon = { _, _ -> },
                onRenameIcon = { _, _ -> true },
                onDeleteIcon = { _ -> true }
            )
        }

        composeTestRule.waitForIdle()

        // Hero card check
        composeTestRule.onNodeWithTag("active_icon_hero_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Current App Icon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active Launcher Icon").assertIsDisplayed()

        // Verify title & count
        composeTestRule.onNodeWithText("App Icon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cloud Icons • 12").assertIsDisplayed()

        // Verify Restore Default button
        composeTestRule.onNodeWithTag("restore_default_icon_button").assertIsDisplayed()

        // Verify Change App Icon section
        composeTestRule.onNodeWithText("Change App Icon").assertIsDisplayed()

        // Verify FAB
        composeTestRule.onNodeWithTag("upload_new_icon_fab").assertIsDisplayed()

        // Check active badge on active icon card
        composeTestRule.onNodeWithTag("app_icon_grid").performScrollToIndex(3)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("app_icon_card_${firstIcon.driveFileId}").assertExists()
        composeTestRule.onNodeWithTag("active_indicator_${firstIcon.driveFileId}", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `appIconScreen tap opens tap action sheet and shows applied success dialog`() {
        val icons = cloudStorageManager.getDriveAppIcons()
        val firstIcon = icons.first()
        val secondIcon = icons[1]
        val driveAccount = DriveAccount(currentAccountEmail = "storageaccount@gmail.com")
        var selectedId: String? = null

        composeTestRule.setContent {
            AppIconScreen(
                icons = icons,
                activeIconId = firstIcon.driveFileId,
                driveAccount = driveAccount,
                onBackClick = {},
                onSetActiveIcon = { selectedId = it },
                onRestoreDefaultIcon = {},
                onSyncWithDrive = {},
                onUploadNewIcon = { _, _ -> },
                onRenameIcon = { _, _ -> true },
                onDeleteIcon = { _ -> true }
            )
        }

        composeTestRule.waitForIdle()

        // Scroll to and tap second icon
        composeTestRule.onNodeWithTag("app_icon_grid").performScrollToIndex(3)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("app_icon_card_${secondIcon.driveFileId}").performClick()
        composeTestRule.waitForIdle()

        // Tap action sheet should be shown
        composeTestRule.onNodeWithTag("action_use_as_icon").assertExists()
        composeTestRule.onNodeWithTag("action_preview_icon").assertExists()
        composeTestRule.onNodeWithTag("action_delete_icon").assertExists()

        // Click "Use as App Icon"
        composeTestRule.onNodeWithTag("action_use_as_icon").performClick()
        composeTestRule.waitForIdle()

        assertEquals(secondIcon.driveFileId, selectedId)

        // Applied success dialog should now appear with "✓ App Icon Changed"
        composeTestRule.onNodeWithText("✓ App Icon Changed").assertIsDisplayed()
        composeTestRule.onNodeWithTag("applied_success_done_button").assertIsDisplayed()
    }

    @Test
    fun `appIconScreen long-press opens menu sheet with rename and details`() {
        val icons = cloudStorageManager.getDriveAppIcons()
        val firstIcon = icons.first()
        val thirdIcon = icons[2]
        val driveAccount = DriveAccount(currentAccountEmail = "storageaccount@gmail.com")

        composeTestRule.setContent {
            AppIconScreen(
                icons = icons,
                activeIconId = firstIcon.driveFileId,
                driveAccount = driveAccount,
                onBackClick = {},
                onSetActiveIcon = {},
                onRestoreDefaultIcon = {},
                onSyncWithDrive = {},
                onUploadNewIcon = { _, _ -> },
                onRenameIcon = { _, _ -> true },
                onDeleteIcon = { _ -> true }
            )
        }

        composeTestRule.waitForIdle()

        // Scroll to and long press on third icon
        composeTestRule.onNodeWithTag("app_icon_grid").performScrollToIndex(5)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("app_icon_card_${thirdIcon.driveFileId}").performTouchInput {
            longClick()
        }
        composeTestRule.waitForIdle()

        // Verify long-press sheet options
        composeTestRule.onNodeWithTag("longpress_use_as_icon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("longpress_rename_icon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("longpress_details_icon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("longpress_delete_icon").assertIsDisplayed()
    }
}
