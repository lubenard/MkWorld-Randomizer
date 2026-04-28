package com.escatrag.mkworldrandomiser.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID


// Extension pour le DataStore
private val Context.dataStore by preferencesDataStore(name = "scores_prefs")

data class PlayerProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val avatarRes: Int? = null, // Mis en optionnel pour gérer les initiales
    val profileColor: Int = Color.Gray.toArgb(),
    val currentMonthScore: Int = 0
) {
    val composeColor: Color get() = Color(profileColor)
    val initials: String get() = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
}

class ScoreViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()
    private val PLAYERS_KEY = stringPreferencesKey("players_list")
    private val context = application.applicationContext

    // 1. Source de vérité : Chargée depuis le DataStore
    private val _players = MutableStateFlow<List<PlayerProfile>>(emptyList())
    val players = _players.asStateFlow()

    // 2. Liste triée automatique : Elle réagit dès que _players change
    val sortedPlayers: StateFlow<List<PlayerProfile>> = _players
        .map { list -> list.sortedByDescending { it.currentMonthScore } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _editingProfile = MutableStateFlow<PlayerProfile?>(null)
    val editingProfile = _editingProfile.asStateFlow()

    init {
        // Charger les données sauvegardées au démarrage
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                val json = prefs[PLAYERS_KEY] ?: ""
                if (json.isNotEmpty()) {
                    val type = object : TypeToken<List<PlayerProfile>>() {}.type
                    val savedList: List<PlayerProfile> = gson.fromJson(json, type)
                    _players.value = savedList
                }
            }
        }
    }

    private fun persistData(newList: List<PlayerProfile>) {
        viewModelScope.launch {
            _players.value = newList
            context.dataStore.edit { prefs ->
                prefs[PLAYERS_KEY] = gson.toJson(newList)
            }
        }
    }

    // Ajoute cette fonction dans ScoreViewModel
    fun resetMonthlyScores() {
        val currentList = _players.value
        // On recrée la liste en mettant le score de tout le monde à 0
        val resetList = currentList.map { it.copy(currentMonthScore = 0) }
        persistData(resetList)
    }

    fun startCreatingProfile() {
        _editingProfile.value = PlayerProfile()
    }

    fun closeEditPopup() {
        _editingProfile.value = null
    }

    fun saveProfile(profile: PlayerProfile) {
        if (profile.name.isBlank()) return
        val currentList = _players.value
        val newList = if (currentList.any { it.id == profile.id }) {
            currentList.map { if (it.id == profile.id) profile else it }
        } else {
            currentList + profile
        }
        persistData(newList)
        closeEditPopup()
    }

    fun submitRaceResults(results: Map<String, Int>) {
        val newList = _players.value.map { player ->
            val position = results[player.id]
            if (position != null) {
                val pointsGained = when (position) {
                    1 -> 15
                    2 -> 12
                    3 -> 10
                    4 -> 9
                    5 -> 8
                    6, 7 -> 7
                    else -> 6
                }
                player.copy(currentMonthScore = player.currentMonthScore + pointsGained)
            } else {
                player
            }
        }
        persistData(newList)
    }

    fun resetUsers() {
        _players.value = listOf()
        persistData(listOf())
    }

    fun deletePlayer(player: PlayerProfile) {
        // Si tu utilises une liste mutable ou une DB (Room), adapte ici
        val currentList = _players.value.toMutableList()
        currentList.remove(player)
        _players.value = currentList
    }
}