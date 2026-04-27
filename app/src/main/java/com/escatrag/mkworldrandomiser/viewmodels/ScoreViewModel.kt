package com.escatrag.mkworldrandomiser.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.escatrag.mkworldrandomiser.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID


data class PlayerProfile(
    val id: String = UUID.randomUUID().toString(), // Identifiant unique
    val name: String = "",
    val avatarRes: Int = R.drawable.ic_launcher_background, // Une icône par défaut
    val profileColor: Int = Color.Gray.toArgb(), // Couleur stockée en Int pour simplification
    val currentMonthScore: Int = 0 // Score initial à 0
) {
    // Helper pour récupérer la couleur Compose facilement
    val composeColor: Color get() = Color(profileColor)
}

// Modèle de données
data class PlayerScore(
    val name: String,
    val score: Int,
    val avatarRes: Int? = null
)

class ScoreViewModel : ViewModel() {

    // La liste commence VIDE au début
    private val _players = MutableStateFlow<List<PlayerProfile>>(emptyList())

    // Liste triée pour l'affichage (Podium + Reste)
    val sortedPlayers: StateFlow<List<PlayerProfile>> = MutableStateFlow(emptyList())

    // État pour gérer la popup d'édition
    private val _editingProfile = MutableStateFlow<PlayerProfile?>(null)
    val editingProfile = _editingProfile.asStateFlow()

    fun startCreatingProfile() {
        // Ouvre la popup avec un profil vierge
        _editingProfile.value = PlayerProfile()
    }

    fun closeEditPopup() {
        _editingProfile.value = null
    }

    fun saveProfile(profile: PlayerProfile) {
        if (profile.name.isBlank()) return
        _players.update { current ->
            if (current.any { it.id == profile.id }) {
                current.map { if (it.id == profile.id) profile else it }
            } else {
                // NOUVEAU : On ne met plus de score aléatoire ici
                current + profile
            }
        }
        closeEditPopup()
    }

    // Nouvelle fonction pour enregistrer les résultats d'une course
    fun submitRaceResults(results: Map<String, Int>) {
        // results est un Map<PlayerId, Position>
        _players.update { currentList ->
            currentList.map { player ->
                val position = results[player.id]
                if (position != null) {
                    // Barème de points (Exemple : 1er = 10pts, 2nd = 7pts, 3ème = 5pts, etc.)
                    val pointsGained = when(position) {
                        1 -> 10
                        2 -> 7
                        3 -> 5
                        else -> 2
                    }
                    player.copy(currentMonthScore = player.currentMonthScore + pointsGained)
                } else {
                    player
                }
            }
        }
    }


    private fun updateSortedList() {
        // Tri par score descendant (simulation car score=0 pour les nouveaux)
        // Pour tester le podium, j'ajoute un score aléatoire lors de la sauvegarde
        // Dans la vraie app, tu enlèveras le '.copy(score = ...)'
        /*_players.update { list ->
            list.map { if (it.currentMonthScore == 0) it.copy(currentMonthScore = (100..1000).random()) else it }
        }*/

        (sortedPlayers as MutableStateFlow).value =
            _players.value.sortedByDescending { it.currentMonthScore }
    }
}