package com.escatrag.mkworldrandomiser.ui.screens

import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.viewmodels.PlayerProfile
import com.escatrag.mkworldrandomiser.viewmodels.ScoreViewModel

@Composable
fun PodiumSection(podium: List<PlayerProfile>) {
    // On récupère les profils s'ils existent
    val first = podium.getOrNull(0)
    val second = podium.getOrNull(1)
    val third = podium.getOrNull(2)

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
            painter = painterResource(id = player.avatarRes ?: R.drawable.mont_tchou_tchou),
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
fun MonthlyScoreScreen(viewModel: ScoreViewModel, navController: NavHostController) {
    // Liste triée des joueurs
    val players by viewModel.sortedPlayers.collectAsState()
    val unsortedPlayers by viewModel.players.collectAsState()

    // Profil en cours d'édition
    val editingProfile by viewModel.editingProfile.collectAsState()

    // --- NOUVEAU : État pour la popup de confirmation de réinitialisation ---
    var showResetDialog by remember { mutableStateOf(false) }
    var showResetPlayersDialog by remember { mutableStateOf(false) }

    // On sépare les données pour le podium
    val podium = if (players.isNotEmpty()) players.take(3) else unsortedPlayers.take(3)
    val theRest = if (players.isNotEmpty()) players.drop(3) else unsortedPlayers.drop(3)

    // --- NOUVEAU : Popup de confirmation pour éviter les missclicks ---
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Réinitialiser le mois ?") },
            text = { Text("Tous les scores seront remis à zéro. Cette action est irréversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetMonthlyScores()
                        showResetDialog = false
                    }
                ) {
                    Text("Confirmer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // --- NOUVEAU : Popup de confirmation pour éviter les missclicks ---
    if (showResetPlayersDialog) {
        AlertDialog(
            onDismissRequest = { showResetPlayersDialog = false },
            title = { Text("Réinitialiser les joueurs ?") },
            text = { Text("Tous les joueurs seront remis à zéro. Cette action est irréversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetUsers()
                        showResetPlayersDialog = false
                    }
                ) {
                    Text("Confirmer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPlayersDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Classement du Mois") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                // --- NOUVEAU : Bouton pour supprimer/réinitialiser ---
                actions = {
                    Log.d("escatrag", "${players.isNotEmpty()} || ${unsortedPlayers.isNotEmpty()}")
                    if (players.isNotEmpty() || unsortedPlayers.isNotEmpty()) {
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Réinitialiser les scores",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(onClick = { showResetPlayersDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.GroupOff,
                                contentDescription = "Supprimer tous les joueurs",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(onClick = { navController.navigate("players") }) {
                            Icon(imageVector = Icons.Default.Group, contentDescription = "Gérer les joueurs")
                        }
                    }
                }
            )
        },
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
            ProfileCreationPopup(
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
            if (players.isEmpty() && unsortedPlayers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Aucun pilote pour l'instant...", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Text("Clique sur le + pour créer ton profil !", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            } else {
                // Vérifier si tous les scores sont à zéro
                val allScoresAreZero = players.all { it.currentMonthScore == 0 }

                if (allScoresAreZero) {
                    // --- MODE LISTE UNIQUEMENT (Début de mois / Reset) ---
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // On affiche TOUS les joueurs (players) et non juste "theRest"
                        itemsIndexed(players) { index, player ->
                            ScoreRow(rank = index + 1, player = player)
                        }
                    }
                } else {
                    // --- MODE PODIUM (Quand il y a de la compétition) ---
                    PodiumSection(podium)

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(theRest) { index, player ->
                            // On reprend à partir du 4ème
                            ScoreRow(rank = index + 4, player = player)
                        }
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
                painter = painterResource(id = player.avatarRes ?: R.drawable.mont_tchou_tchou),
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