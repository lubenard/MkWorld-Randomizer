package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.backend.map
import com.escatrag.mkworldrandomiser.backend.toTrackItem
import com.escatrag.mkworldrandomiser.ui.composables.TrackSelectionConnectionTile
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSelectionScreen(
    viewModel: TrackViewModel,
    padding: PaddingValues
) {
    val selectedTracks by viewModel.selectedTracks.collectAsState()
    val includeRoutes by viewModel.includeRoutes.collectAsState()
    val allTracksList by viewModel.allTracksAvailable.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Filtrage de la liste selon la recherche
    val filteredTracks = allTracksList.filter {
        val startName = context.getString(it.start.text)
        val endName = it.end?.let { end -> context.getString(end.text) } ?: ""
        startName.contains(searchQuery, ignoreCase = true) || endName.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 2. BARRE DE RECHERCHE
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Rechercher un circuit") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // 3. BOUTONS TOUT ACTIVER / DÉSACTIVER
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.selectAllTracks(includeRoutes) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD), contentColor = Color.Blue)
                ) {
                    Text("Tout activer")
                }
                Button(
                    onClick = { viewModel.clearAllTracks() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red)
                ) {
                    Text("Tout désactiver")
                }
            }
        }

        // 4. LISTE DES CIRCUITS
        items(filteredTracks) { track ->
            val isSelected = selectedTracks.contains(track)

            // 1. GESTION DE L'ÉTAT D'OUVERTURE
            // rememberSaveable permet de garder l'état ouvert/fermé même si l'utilisateur scrolle la liste
            var isExpanded by rememberSaveable { mutableStateOf(false) }

            val randomPastel = remember(track) {
                val hue = (0..360).random().toFloat()
                // Saturation basse (0.4-0.6) et Luminosité haute (0.8-0.9) pour du pastel
                Color.hsl(hue = hue, saturation = 0.5f, lightness = 0.85f)
            }

            val borderColor = if (isSelected) randomPastel else Color.LightGray
            val backgroundColor = if (isSelected) randomPastel.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.4f)

            // 2. ANIMATION DE LA FLÈCHE (optionnel mais très stylé)
            val arrowRotationDegree by animateFloatAsState(
                targetValue = if (isExpanded) 180f else 0f,
                label = "arrowRotation"
            )

            val availableConnections = viewModel.getConnectionsForTrack(track.start.toTrackItem()!!)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(12.dp),
                onClick = { viewModel.toggleTrack(track) }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // --- LA ROW PRINCIPALE (Toujours visible) ---
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 60.dp, height = 40.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = track.start.largeIcon),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Si pas sélectionné : on ajoute l'ombre et la croix
                            if (!isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f)), // Ombre
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Nom de la map
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(track.start.text),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.Gray
                            )
                        }

                        // Switch
                        Switch(
                            checked = isSelected,
                            onCheckedChange = { viewModel.toggleTrack(track) },
                            colors = SwitchDefaults.colors(/* tes couleurs */)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        if (availableConnections.isNotEmpty()) {
                            // 4. LE BOUTON DROPDOWN (À droite du switch)
                            IconButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Plus d'options",
                                    modifier = Modifier.rotate(arrowRotationDegree), // La flèche tourne !
                                    tint = if (isSelected) Color.DarkGray else Color.Gray
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(32.dp))
                        }
                    }

                    // --- LE MENU DÉROULANT (Caché par défaut) ---
                    AnimatedVisibility(visible = isExpanded) {
                        // Ce bloc ne s'affiche que si isExpanded == true
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp) // Padding uniforme autour de la liste d'options
                        ) {
                            Divider(color = Color.Black.copy(alpha = 0.1f))

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Trajets:",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // --- LA LISTE VERTICALE ---
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                availableConnections.forEach { connectedTrackItem ->
                                    // On vérifie si CETTE connexion spécifique est sélectionnée
                                    val isThisConnectionSelected = selectedTracks.any {
                                        it.start == track.start && it.end == connectedTrackItem.map()
                                    }

                                    TrackSelectionConnectionTile(
                                        title = "-> ${stringResource(connectedTrackItem.nameRes)}",
                                        isActive = isThisConnectionSelected,
                                        themeColor = randomPastel
                                    ) {
                                        viewModel.toggleConnection(track.start, connectedTrackItem)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // OPTION INCLURE LES TRAJETS (en bas de liste)
        item {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Inclure les trajets", modifier = Modifier.weight(1f))
                Switch(
                    checked = includeRoutes,
                    onCheckedChange = {
                        viewModel.setIncludeRoutes(it)
                        viewModel.selectAllTracks(it)
                    }
                )
            }
        }
    }
}