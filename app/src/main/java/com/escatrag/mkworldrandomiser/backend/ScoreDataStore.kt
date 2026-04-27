package com.escatrag.mkworldrandomiser.backend

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.escatrag.mkworldrandomiser.viewmodels.PlayerProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "scores_prefs")

class ScoreDataStore(private val context: Context) {
    private val gson = Gson()
    private val PLAYERS_KEY = stringPreferencesKey("players_list")

    // Lire les joueurs
    val getPlayers: Flow<List<PlayerProfile>> = context.dataStore.data.map { prefs ->
        val json = prefs[PLAYERS_KEY] ?: ""
        if (json.isEmpty()) emptyList()
        else {
            val type = object : TypeToken<List<PlayerProfile>>() {}.type
            gson.fromJson(json, type)
        }
    }

    // Sauvegarder les joueurs
    suspend fun savePlayers(players: List<PlayerProfile>) {
        context.dataStore.edit { prefs ->
            prefs[PLAYERS_KEY] = gson.toJson(players)
        }
    }
}