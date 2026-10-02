package com.escatrag.mkworldrandomiser.backend

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class ScoreViewModel(application: Application) : ViewModel() {
    private val storeManager = ScoreDataStoreManager(application)

    // On transforme le Flow du DataStore en StateFlow pour Compose
    val allScores = storeManager.getAllScoresFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun updateScore(name: String, newScore: Int) {
        viewModelScope.launch {
            storeManager.saveScore(name, newScore)
        }
    }
}