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
        // Logique simplifiée : si le nom est vide, on n'enregistre pas
        if (profile.name.isBlank()) {
            closeEditPopup()
            return
        }

        _players.update { currentList ->
            // On vérifie si le joueur existe déjà (édition) ou si c'est un nouveau
            if (currentList.any { it.id == profile.id }) {
                currentList.map { if (it.id == profile.id) profile else it }
            } else {
                currentList + profile
            }
        }

        // Mise à jour de la liste triée
        updateSortedList()
        closeEditPopup()
    }

    private fun updateSortedList() {
        // Tri par score descendant (simulation car score=0 pour les nouveaux)
        // Pour tester le podium, j'ajoute un score aléatoire lors de la sauvegarde
        // Dans la vraie app, tu enlèveras le '.copy(score = ...)'
        _players.update { list ->
            list.map { if (it.currentMonthScore == 0) it.copy(currentMonthScore = (100..1000).random()) else it }
        }

        (sortedPlayers as MutableStateFlow).value =
            _players.value.sortedByDescending { it.currentMonthScore }
    }
}