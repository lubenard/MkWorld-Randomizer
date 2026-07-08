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
import kotlin.math.pow
import kotlin.math.roundToInt


// Extension pour le DataStore
private val Context.dataStore by preferencesDataStore(name = "scores_prefs")

data class PlayerProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val avatarRes: Int? = null, // Mis en optionnel pour gérer les initiales
    val profileColor: Int = Color.Gray.toArgb(),
    val currentMonthScore: Int = 3000,
    val runNumbers: Int = 0,
    val victoryNumbers: Int = 0,
    val timesInPodium: Int = 0,
    val top3Maps: List<Top3Maps> = emptyList()
) {
    val composeColor: Color get() = Color(profileColor)
    val initials: String get() = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
}

data class Top3Maps(
    val mapId: Int,
    val timeInTop3: Int
)

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
        val resetList = currentList.map { it.copy(currentMonthScore = 3000) }
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

    fun submitRaceResults(rankings: Map<String, Int>, mapId: Int) {
        val players = _players.value
        val n = rankings.size
        if (n < 2) return

        val scaledK = 32.0 / (n - 1)
        val eloDeltas = mutableMapOf<String, Double>()

        val rankedPlayers = players.filter { it.id in rankings }

        for (i in rankedPlayers.indices) {
            for (j in i + 1 until rankedPlayers.size) {
                val a = rankedPlayers[i]
                val b = rankedPlayers[j]

                val ratingA = a.currentMonthScore.toDouble()
                val ratingB = b.currentMonthScore.toDouble()
                val posA = rankings[a.id]!!
                val posB = rankings[b.id]!!

                val expectedA = 1.0 / (1.0 + 10.0.pow((ratingB - ratingA) / 400.0))
                val expectedB = 1.0 - expectedA

                val (actualA, actualB) = when {
                    posA < posB -> Pair(1.0, 0.0)
                    posA > posB -> Pair(0.0, 1.0)
                    else -> Pair(0.5, 0.5)
                }

                eloDeltas[a.id] = (eloDeltas[a.id] ?: 0.0) + scaledK * (actualA - expectedA)
                eloDeltas[b.id] = (eloDeltas[b.id] ?: 0.0) + scaledK * (actualB - expectedB)
            }
        }

        val newList = players.map { player ->
            val position = rankings[player.id]
            if (position != null) {
                val delta = (eloDeltas[player.id] ?: 0.0).roundToInt()
                player.copy(
                    currentMonthScore = maxOf(0, player.currentMonthScore + delta),
                    runNumbers = player.runNumbers + 1,
                    timesInPodium = if (position <= 3) player.timesInPodium + 1 else player.timesInPodium,
                    victoryNumbers = if (position == 1) player.victoryNumbers + 1 else player.victoryNumbers,
                    top3Maps = handleTop3Maps(position, player, mapId)
                )
            } else {
                player
            }
        }
        persistData(newList)
    }

    private fun handleTop3Maps(position: Int, player: PlayerProfile, currentMapId: Int): List<Top3Maps> {
        // Si on n'est pas sur le podium, on renvoie la liste actuelle sans changement
        if (position > 3) return player.top3Maps

        val currentList = player.top3Maps.toMutableList()

        // On cherche si la map est déjà présente dans les stats du joueur
        val existingMapIndex = currentList.indexOfFirst { it.mapId == currentMapId }

        if (existingMapIndex != -1) {
            // La map existe : on remplace l'ancien objet par un nouveau avec le compteur +1
            val existingMap = currentList[existingMapIndex]
            currentList[existingMapIndex] = existingMap.copy(
                timeInTop3 = existingMap.timeInTop3 + 1
            )
        } else {
            // La map n'existe pas : on l'ajoute avec un compteur initial de 1
            currentList.add(Top3Maps(mapId = currentMapId, timeInTop3 = 1))
        }

        return currentList
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