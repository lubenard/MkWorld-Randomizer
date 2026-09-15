package com.escatrag.mkworldrandomiser.viewmodels

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class ThemeMode { SYSTEM, LIGHT, DARK }

private val Context.dataStore by preferencesDataStore(name = "settings_prefs")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isPopupEnabled = MutableStateFlow(true)
    val isPopupEnabled: StateFlow<Boolean> = _isPopupEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data.map { prefs ->
                prefs[IS_POPUP_ENABLED_KEY] ?: true
            }.collect { value ->
                _isPopupEnabled.value = value
            }
        }
        viewModelScope.launch {
            context.dataStore.data.map { prefs ->
                prefs[THEME_MODE_KEY]?.let { name ->
                    try { ThemeMode.valueOf(name) } catch (_: IllegalArgumentException) { ThemeMode.SYSTEM }
                } ?: ThemeMode.SYSTEM
            }.collect { value ->
                _themeMode.value = value
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        viewModelScope.launch {
            context.dataStore.edit { it[THEME_MODE_KEY] = mode.name }
        }
    }

    fun setPopupEnabled(enabled: Boolean) {
        _isPopupEnabled.value = enabled
        viewModelScope.launch {
            context.dataStore.edit { it[IS_POPUP_ENABLED_KEY] = enabled }
        }
    }

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val IS_POPUP_ENABLED_KEY = booleanPreferencesKey("is_popup_enabled")
    }
}
