package com.escatrag.mkworldrandomiser.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.ui.composables.PlayersDetailsComposable
import com.escatrag.mkworldrandomiser.ui.composables.PodiumSection
import com.escatrag.mkworldrandomiser.ui.composables.TitleComposable
import com.escatrag.mkworldrandomiser.viewmodels.PlayerProfile
import com.escatrag.mkworldrandomiser.viewmodels.ScoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyScoreScreen(
    viewModel: ScoreViewModel,
    navController: NavHostController,
    padding: PaddingValues = PaddingValues(0.dp),
) {
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

    var selectedPlayerForDelete by remember { mutableStateOf<PlayerProfile?>(null) }
    var selectedPlayerForDetails by remember { mutableStateOf<PlayerProfile?>(null) }

    // Reinitialiser les scores du mois
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

    if (selectedPlayerForDetails != null) {
        PlayersDetailsComposable(selectedPlayerForDetails!!) {
            selectedPlayerForDetails = null
        }
    }

    // --- Supprimer tous les joueurs
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

    if (selectedPlayerForDelete != null) {
        AlertDialog(
            onDismissRequest = { selectedPlayerForDelete = null },
            title = { Text("Supprimer le joueur ?") },
            text = { Text("Voulez-vous vraiment supprimer ${selectedPlayerForDelete?.name} ? Cette action est irréversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPlayerForDelete?.let { viewModel.deletePlayer(it) }
                        selectedPlayerForDelete = null
                    }
                ) {
                    Text("Oui", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlayerForDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }


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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleComposable(text = "CLASSEMENT", fontSize = 30.sp)
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
                }
            }

            // --- GESTION DE L'ÉTAT VIDE ---
            if (players.isEmpty() && unsortedPlayers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Aucun pilote pour l'instant...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Text(
                            "Clique sur le + pour créer ton profil !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Vérifier si tous les scores sont à zéro
                val allScoresAreDefault = players.all { it.currentMonthScore == 3000 }

                if (allScoresAreDefault) {
                    // --- MODE LISTE UNIQUEMENT (Début de mois / Reset) ---
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // On affiche TOUS les joueurs (players) et non juste "theRest"
                        itemsIndexed(players) { index, player ->
                            ScoreRow(rank = index + 1, player = player,
                                onClick = {
                                    selectedPlayerForDetails = player
                                },
                                onLongClick = {
                                    selectedPlayerForDelete = player
                                }
                            )
                        }
                    }
                } else {
                    // --- MODE PODIUM (Quand il y a de la compétition) ---
                    PodiumSection(podium,
                        onClick = { player ->
                            selectedPlayerForDetails = player
                        },
                        onLongClick = { player ->
                            selectedPlayerForDelete = player
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(theRest) { index, player ->
                            // On reprend à partir du 4ème
                            ScoreRow(rank = index + 4, player = player,
                                onClick = {
                                    selectedPlayerForDetails = player
                                },
                                onLongClick = {
                                    selectedPlayerForDelete = player
                                }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.startCreatingProfile() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 120.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter un pilote")
        }
    }
}


// --- Mise à jour de ScoreRow pour utiliser PlayerProfile et sa couleur de fond ---
@Composable
fun ScoreRow(
    rank: Int,
    player: PlayerProfile,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )

            // Avatar
            Image(
                painter = painterResource(id = player.avatarRes ?: R.drawable.mont_tchou_tchou),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(45.dp).clip(CircleShape).border(2.dp, player.composeColor, CircleShape)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = player.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${player.currentMonthScore}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}