package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun PodiumSection(topThree: List<PlayerProfile>) {
    // On récupère les profils s'ils existent
    val first = topThree.getOrNull(0)
    val second = topThree.getOrNull(1)
    val third = topThree.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Un peu plus haut pour les avatars
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom // Important : tout le monde est posé au sol
    ) {
        // 2ème Place (à gauche)
        PodiumBar(
            player = second,
            rank = 2,
            heightFraction = 0.55f, // 55% de la hauteur max
            modifier = Modifier.weight(1f)
        )

        // 1ère Place (au milieu)
        PodiumBar(
            player = first,
            rank = 1,
            heightFraction = 0.85f, // 85% de la hauteur max
            modifier = Modifier.weight(1.2f) // Un peu plus large
        )

        // 3ème Place (à droite)
        PodiumBar(
            player = third,
            rank = 3,
            heightFraction = 0.35f, // 35% de la hauteur max
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PodiumBar(
    player: PlayerProfile?,
    rank: Int,
    heightFraction: Float,
    modifier: Modifier = Modifier
) {
    if (player == null) return

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // 1. Avatar du joueur au dessus de la barre
        Image(
            painter = painterResource(id = player.avatarRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(if (rank == 1) 70.dp else 55.dp) // Le premier est plus gros
                .clip(CircleShape)
                .border(2.dp, player.composeColor, CircleShape)
        )

        Spacer(Modifier.height(4.dp))

        // 2. Nom et Score
        Text(
            text = player.name,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            style = if (rank == 1) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${player.currentMonthScore} pts",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(8.dp))

        // 3. La barre du podium
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(heightFraction) // Hauteur proportionnelle au rang
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            player.composeColor, // Sa couleur choisie
                            player.composeColor.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            // Chiffre du rang (1, 2 ou 3)
            Text(
                text = rank.toString(),
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyScoreScreen(viewModel: ScoreViewModel = viewModel()) {
    // Liste triée des joueurs (commence vide)
    val players by viewModel.sortedPlayers.collectAsState()

    // Profil en cours d'édition (si non null, on montre la popup)
    val editingProfile by viewModel.editingProfile.collectAsState()

    // On sépare les données pour le podium
    val topThree = players.take(3)
    val theRest = players.drop(3)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Classement du Mois") })
        },
        // --- AJOUT DU BOUTON ROND EN BAS A DROITE ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startCreatingProfile() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un pilote")
            }
        }
    ) { padding ->

        // --- GESTION DE LA POPUP D'EDITION ---
        editingProfile?.let { profile ->
            EditProfilePopup(
                profile = profile,
                onDismiss = { viewModel.closeEditPopup() },
                onSave = { updatedProfile ->
                    viewModel.saveProfile(updatedProfile)
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // --- GESTION DE L'ÉTAT VIDE ---
            if (players.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Aucun pilote pour l'instant...", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Text("Clique sur le + pour créer ton profil !", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            } else {
                // Si la liste n'est pas vide, on affiche le podium et la liste
                PodiumSection(topThree)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(theRest) { index, player ->
                        ScoreRow(rank = index + 4, player = player)
                    }
                }
            }
        }
    }
}

// --- Mise à jour de ScoreRow pour utiliser PlayerProfile et sa couleur de fond ---
@Composable
fun ScoreRow(rank: Int, player: PlayerProfile) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = player.composeColor.copy(alpha = 0.2f)), // Utilise la couleur choisie
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                modifier = Modifier.width(40.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                style = MaterialTheme.typography.titleMedium
            )

            // Avatar
            Image(
                painter = painterResource(id = player.avatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(45.dp).clip(CircleShape).border(1.dp, Color.LightGray, CircleShape)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = player.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${player.currentMonthScore} pts",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}