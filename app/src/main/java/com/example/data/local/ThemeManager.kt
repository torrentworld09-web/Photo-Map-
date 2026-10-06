package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.GlassThemePresets
import com.example.ui.theme.GlassThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages Persistence, Import/Export, and Real-Time State for Glass Theming System.
 * Changes propagate immediately without app restart.
 * Isolated from user posts, photos, and folders.
 */
class ThemeManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _activeTheme = MutableStateFlow(loadInitialTheme())
    val activeTheme: StateFlow<GlassThemeState> = _activeTheme.asStateFlow()

    private val _customThemes = MutableStateFlow(loadCustomThemes())
    val customThemes: StateFlow<List<GlassThemeState>> = _customThemes.asStateFlow()

    init {
        // Ensure initial sync
        _activeTheme.value = loadInitialTheme()
    }

    private fun loadInitialTheme(): GlassThemeState {
        val activeId = prefs.getString(KEY_ACTIVE_THEME_ID, GlassThemePresets.PremiumDarkGlass.id)
            ?: GlassThemePresets.PremiumDarkGlass.id

        // Check if it's one of the 10 built-in presets
        val preset = GlassThemePresets.allPresets.firstOrNull { it.id == activeId }
        if (preset != null) {
            // Check if there is an active edited version of this preset or if default
            val customJson = prefs.getString(KEY_ACTIVE_THEME_JSON, null)
            if (customJson != null) {
                val parsed = GlassThemeState.fromJsonString(customJson)
                if (parsed != null && parsed.id == preset.id) {
                    return parsed
                }
            }
            return preset
        }

        // Check if it's in custom themes
        val customList = loadCustomThemes()
        val custom = customList.firstOrNull { it.id == activeId }
        if (custom != null) {
            return custom
        }

        // Fallback to active theme JSON
        val customJson = prefs.getString(KEY_ACTIVE_THEME_JSON, null)
        if (customJson != null) {
            val parsed = GlassThemeState.fromJsonString(customJson)
            if (parsed != null) return parsed
        }

        return GlassThemePresets.PremiumDarkGlass
    }

    private fun loadCustomThemes(): List<GlassThemeState> {
        val jsonArrayStr = prefs.getString(KEY_CUSTOM_THEMES_ARRAY, null) ?: return emptyList()
        val list = mutableListOf<GlassThemeState>()
        try {
            val array = JSONArray(jsonArrayStr)
            for (i in 0 until array.length()) {
                val itemStr = array.getString(i)
                val theme = GlassThemeState.fromJsonString(itemStr)
                if (theme != null) {
                    list.add(theme)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveCustomThemes(list: List<GlassThemeState>) {
        val array = JSONArray()
        for (theme in list) {
            array.put(theme.toJsonString())
        }
        prefs.edit().putString(KEY_CUSTOM_THEMES_ARRAY, array.toString()).apply()
        _customThemes.value = list
    }

    fun selectPreset(presetId: String) {
        val preset = GlassThemePresets.allPresets.firstOrNull { it.id == presetId }
            ?: GlassThemePresets.PremiumDarkGlass
        prefs.edit()
            .putString(KEY_ACTIVE_THEME_ID, preset.id)
            .remove(KEY_ACTIVE_THEME_JSON)
            .apply()
        _activeTheme.value = preset
    }

    fun applyTheme(theme: GlassThemeState) {
        prefs.edit()
            .putString(KEY_ACTIVE_THEME_ID, theme.id)
            .putString(KEY_ACTIVE_THEME_JSON, theme.toJsonString())
            .apply()
        _activeTheme.value = theme
    }

    fun saveCustomTheme(theme: GlassThemeState, setActive: Boolean = true): GlassThemeState {
        val idToUse = if (theme.id.startsWith("preset_")) "custom_${System.currentTimeMillis()}" else theme.id
        val finalized = theme.copy(id = idToUse, isCustom = true)
        val currentList = _customThemes.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == finalized.id }
        if (existingIndex >= 0) {
            currentList[existingIndex] = finalized
        } else {
            currentList.add(0, finalized)
        }
        saveCustomThemes(currentList)
        if (setActive) {
            applyTheme(finalized)
        }
        return finalized
    }

    fun duplicateTheme(theme: GlassThemeState): GlassThemeState {
        val duplicated = theme.copy(
            id = "custom_${System.currentTimeMillis()}",
            name = "${theme.name} (Copy)",
            isCustom = true
        )
        return saveCustomTheme(duplicated, setActive = true)
    }

    fun renameCustomTheme(themeId: String, newName: String) {
        val currentList = _customThemes.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == themeId }
        if (index >= 0) {
            val updated = currentList[index].copy(name = newName)
            currentList[index] = updated
            saveCustomThemes(currentList)
            if (_activeTheme.value.id == themeId) {
                applyTheme(updated)
            }
        }
    }

    fun deleteCustomTheme(themeId: String) {
        val currentList = _customThemes.value.filter { it.id != themeId }
        saveCustomThemes(currentList)
        if (_activeTheme.value.id == themeId) {
            selectPreset(GlassThemePresets.PremiumDarkGlass.id)
        }
    }

    fun importThemeJson(jsonString: String): Result<GlassThemeState> {
        return try {
            val parsed = GlassThemeState.fromJsonString(jsonString)
                ?: return Result.failure(IllegalArgumentException("Invalid theme configuration format"))
            val imported = parsed.copy(
                id = "custom_${System.currentTimeMillis()}",
                name = if (parsed.name.endsWith("(Imported)")) parsed.name else "${parsed.name} (Imported)",
                isCustom = true
            )
            val saved = saveCustomTheme(imported, setActive = true)
            Result.success(saved)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportThemeJson(theme: GlassThemeState): String {
        return theme.toJsonString()
    }

    /**
     * Resets Appearance to default "Premium Dark Glass" theme.
     * Guaranteed never to affect photos, posts, folders or user data.
     */
    fun resetToDefault() {
        prefs.edit()
            .putString(KEY_ACTIVE_THEME_ID, GlassThemePresets.PremiumDarkGlass.id)
            .remove(KEY_ACTIVE_THEME_JSON)
            .apply()
        _activeTheme.value = GlassThemePresets.PremiumDarkGlass
    }

    companion object {
        private const val PREFS_NAME = "photo_views_glass_theme_prefs"
        private const val KEY_ACTIVE_THEME_ID = "key_active_theme_id"
        private const val KEY_ACTIVE_THEME_JSON = "key_active_theme_json"
        private const val KEY_CUSTOM_THEMES_ARRAY = "key_custom_themes_array"
    }
}
