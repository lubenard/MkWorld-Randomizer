package com.escatrag.mkworldrandomiser.backend

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// On crée l'instance unique ici (Extension property)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scores_storage")

class ScoreDataStoreManager(private val context: Context) {

    /**
     * Sauvegarde le score d'un joueur.
     * C'est une fonction suspend (doit être appelée dans une coroutine).
     */
    suspend fun saveScore(userName: String, score: Int) {
        val key = intPreferencesKey(userName)
        context.dataStore.edit { preferences ->
            preferences[key] = score
        }
    }

    /**
     * Récupère le score d'un joueur sous forme de Flow.
     * Ton UI observera ce Flow et se mettra à jour automatiquement.
     */
    fun getScoreFlow(userName: String): Flow<Int> {
        val key = intPreferencesKey(userName)
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: 0 // Retourne 0 par défaut
        }
    }

    /**
     * Récupère TOUS les scores (Prénom -> Score)
     */
    fun getAllScoresFlow(): Flow<Map<String, Int>> {
        return context.dataStore.data.map { preferences ->
            val scoresMap = mutableMapOf<String, Int>()
            preferences.asMap().forEach { (key, value) ->
                if (value is Int) {
                    scoresMap[key.name] = value
                }
            }
            // On trie par score décroissant pour le fun
            scoresMap.toList().sortedByDescending { it.second }.toMap()
        }
    }
}