package com.escatrag.mkworldrandomiser.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _isPopupEnabled = MutableStateFlow(true)
    val isPopupEnabled = _isPopupEnabled.asStateFlow()

    fun setPopupEnabled(enabled: Boolean) {
        _isPopupEnabled.value = enabled
    }
}