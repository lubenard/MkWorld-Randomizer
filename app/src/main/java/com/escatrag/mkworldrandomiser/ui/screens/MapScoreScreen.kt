package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escatrag.mkworldrandomiser.viewmodels.PlayerProfile
import com.escatrag.mkworldrandomiser.viewmodels.ScoreViewModel

@Composable
fun PlayerAvatar(
    player: PlayerProfile,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(player.composeColor) // Utilise la couleur du profil
            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (player.avatarRes != null) {
            Image(painter = painterResource(id = player.avatarRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Text(
                text = player.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.4).sp // Texte proportionnel à la taille du cercle
            )
        }
    }
}

@Composable
fun RaceResultScreen(
    viewModel: ScoreViewModel,
    onResultsSubmitted: () -> Unit
) {
    val allPlayers by viewModel.players.collectAsState()
    val participants = remember { mutableStateListOf<PlayerProfile>() }
    // Map pour stocker [ID du joueur -> Position]
    val rankings = remember { mutableStateMapOf<String, Int>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Résultats de la course", style = MaterialTheme.typography.headlineMedium)

        Text("1. Sélectionnez les participants", modifier = Modifier.padding(vertical = 8.dp))

        // Liste horizontale des joueurs pour choisir qui a couru
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(allPlayers) { player ->
                val isSelected = participants.contains(player)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) participants.remove(player) else participants.add(player)
                    },
                    label = { Text(player.name) },
                    leadingIcon = { PlayerAvatar(player, 24.dp) }
                )
            }
        }

        Divider(Modifier.padding(vertical = 16.dp))

        Text("2. Classement final", modifier = Modifier.padding(bottom = 8.dp))

        // Liste des participants sélectionnés pour définir l'ordre
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(participants) { player ->
                val position = rankings[player.id]

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = {
                        // Logique simple : On incrémente la position à chaque clic
                        val nextPos = (rankings.values.maxOrNull() ?: 0) + 1
                        if (rankings[player.id] == null) {
                            rankings[player.id] = nextPos
                        } else {
                            rankings.remove(player.id) // Reset si on reclique
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlayerAvatar(player, 40.dp)
                        Text(player.name, modifier = Modifier.weight(1f).padding(start = 12.dp))

                        // Affichage de la position (Médailles ou chiffres)
                        if (position != null) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(
                                    text = when(position) {
                                        1 -> "1er 🏆"
                                        2 -> "2nd"
                                        3 -> "3ème"
                                        else -> "${position}ème"
                                    },
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        } else {
                            Text("Cliquer pour classer", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = participants.isNotEmpty() && rankings.size == participants.size,
            onClick = {
                viewModel.submitRaceResults(rankings)
                onResultsSubmitted()
            }
        ) {
            Text("Enregistrer les scores")
        }
    }
}