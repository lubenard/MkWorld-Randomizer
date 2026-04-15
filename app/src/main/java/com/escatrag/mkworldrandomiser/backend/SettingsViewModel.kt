package com.escatrag.mkworldrandomiser.backend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class SettingsViewModel : ViewModel() {
    private val _isPopupEnabled = MutableStateFlow(true)
    val isPopupEnabled = _isPopupEnabled.asStateFlow()

    fun setPopupEnabled(enabled: Boolean) {
        _isPopupEnabled.value = enabled
    }
}