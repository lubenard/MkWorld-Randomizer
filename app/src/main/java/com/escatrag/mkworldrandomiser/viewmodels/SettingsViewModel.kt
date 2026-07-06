package com.escatrag.mkworldrandomiser.viewmodels

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings_prefs")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            application.dataStore.data.map { it[DARK_MODE_KEY] ?: false }.collect { value ->
                _darkModeEnabled.value = value
            }
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        viewModelScope.launch {
            application.dataStore.edit { it[DARK_MODE_KEY] = enabled }
        }
    }

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    }
}
