package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SecurityPrefs
import com.example.data.model.Post
import com.example.ui.screens.SearchScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SearchScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var securityPrefs: SecurityPrefs

    private val samplePosts = listOf(
        Post(
            id = "post_1",
            title = "Sunset Mountain Overlook",
            description = "Breathtaking view from the peak",
            locationName = "Coimbatore Hills",
            plusCode = "7M3X+9V Coimbatore",
            mobile = "9876543210",
            folderName = "Land Survey",
            conditions = "Clear skies, mild wind"
        ),
        Post(
            id = "post_2",
            title = "Ocean Wave Coast",
            description = "Crashing azure waves",
            locationName = "Marina Beach",
            plusCode = "8N4Y+2C Chennai",
            mobile = "9123456780",
            folderName = "Beaches",
            conditions = "Humid, sunny"
        )
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val sp = context.getSharedPreferences("photoviews_secure_prefs", Context.MODE_PRIVATE)
        sp.edit().clear().commit()
        securityPrefs = SecurityPrefs(context)
    }

    @Test
    fun `securityPrefs recent searches initial defaults`() {
        val recent = securityPrefs.getRecentSearches()
        assertTrue("Recent searches should not be empty by default", recent.isNotEmpty())
        assertTrue("Default list should contain Mountain", recent.contains("Mountain"))
        assertTrue("Default list should contain Coimbatore", recent.contains("Coimbatore"))
    }

    @Test
    fun `securityPrefs add and remove recent searches`() {
        securityPrefs.addRecentSearch("Forest Trail")
        val afterAdd = securityPrefs.getRecentSearches()
        assertEquals("Forest Trail", afterAdd.first())

        // Adding duplicate case-insensitively should bring it to the top
        securityPrefs.addRecentSearch("forest trail")
        val afterDuplicate = securityPrefs.getRecentSearches()
        assertEquals("forest trail", afterDuplicate.first())
        assertEquals(
            1,
            afterDuplicate.count { it.equals("Forest Trail", ignoreCase = true) }
        )

        // Remove single search
        securityPrefs.removeRecentSearch("forest trail")
        val afterRemove = securityPrefs.getRecentSearches()
        assertFalse(afterRemove.any { it.equals("forest trail", ignoreCase = true) })
    }

    @Test
    fun `securityPrefs clear recent searches clears completely`() {
        securityPrefs.addRecentSearch("Test Query 1")
        securityPrefs.addRecentSearch("Test Query 2")
        assertFalse(securityPrefs.getRecentSearches().isEmpty())

        securityPrefs.clearRecentSearches()
        val cleared = securityPrefs.getRecentSearches()
        assertTrue("Recent searches should be empty after clear", cleared.isEmpty())
    }

    @Test
    fun `searchScreen renders recent searches and displays clear option`() {
        val searches = listOf("Mountain", "Coimbatore")
        var clearClicked = false
        var removedTerm: String? = null

        composeTestRule.setContent {
            SearchScreen(
                posts = samplePosts,
                recentSearches = searches,
                onAddRecentSearch = {},
                onRemoveRecentSearch = { removedTerm = it },
                onClearRecentSearches = { clearClicked = true },
                onBackClick = {},
                onPostClick = {},
                onPostLongClick = {},
                onToggleFavorite = {}
            )
        }

        composeTestRule.waitForIdle()

        // Verify Recent Searches title and Clear All button are displayed
        composeTestRule.onNodeWithText("Recent Searches").assertIsDisplayed()
        composeTestRule.onNodeWithTag("clear_recent_searches_button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mountain").assertIsDisplayed()
        composeTestRule.onNodeWithText("Coimbatore").assertIsDisplayed()

        // Clicking Clear All opens dialog
        composeTestRule.onNodeWithTag("clear_recent_searches_button").performClick()
        composeTestRule.waitForIdle()

        // Verify Dialog is shown
        composeTestRule.onNodeWithText("Clear Search History").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirm_clear_recent_searches").assertIsDisplayed()

        // Click confirm
        composeTestRule.onNodeWithTag("confirm_clear_recent_searches").performClick()
        composeTestRule.waitForIdle()
        assertTrue("onClearRecentSearches callback must be called", clearClicked)
    }

    @Test
    fun `searchScreen empty state displays helpful message when recent searches are cleared`() {
        composeTestRule.setContent {
            SearchScreen(
                posts = samplePosts,
                recentSearches = emptyList(),
                onAddRecentSearch = {},
                onRemoveRecentSearch = {},
                onClearRecentSearches = {},
                onBackClick = {},
                onPostClick = {},
                onPostLongClick = {},
                onToggleFavorite = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("recent_searches_empty_state").assertIsDisplayed()
        composeTestRule.onNodeWithText("No Recent Searches").assertIsDisplayed()
    }

    @Test
    fun `searchScreen query filters posts correctly`() {
        composeTestRule.setContent {
            SearchScreen(
                posts = samplePosts,
                recentSearches = emptyList(),
                onAddRecentSearch = {},
                onRemoveRecentSearch = {},
                onClearRecentSearches = {},
                onBackClick = {},
                onPostClick = {},
                onPostLongClick = {},
                onToggleFavorite = {}
            )
        }

        // Enter search query
        composeTestRule.onNodeWithTag("search_input_field").performTextInput("Sunset")
        composeTestRule.waitForIdle()

        // Should find Sunset Mountain Overlook
        composeTestRule.onNodeWithText("1 results found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sunset Mountain Overlook").assertIsDisplayed()
    }

    @Test
    fun `searchScreen individual remove button triggers callback`() {
        var removedItem: String? = null
        composeTestRule.setContent {
            SearchScreen(
                posts = samplePosts,
                recentSearches = listOf("Land Survey"),
                onAddRecentSearch = {},
                onRemoveRecentSearch = { removedItem = it },
                onClearRecentSearches = {},
                onBackClick = {},
                onPostClick = {},
                onPostLongClick = {},
                onToggleFavorite = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("delete_recent_search_Land Survey").performClick()
        composeTestRule.waitForIdle()

        assertEquals("Land Survey", removedItem)
    }
}
