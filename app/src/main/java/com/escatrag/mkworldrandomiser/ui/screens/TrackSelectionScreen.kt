package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.escatrag.mkworldrandomiser.backend.TrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSelectionScreen(viewModel: TrackViewModel, navController: NavController) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuration") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // 1. TITRE
            item {
                Text(
                    text = "Circuits",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { viewModel.toggleTrack(track) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icône de la map (Start)
                        Image(
                            painter = painterResource(id = track.start.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Nom de la map
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(track.start.text),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            if (track.end != null) {
                                Text(
                                    text = "vers " + stringResource(track.end.text),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Switch activé/désactivé
                        Switch(
                            checked = isSelected,
                            onCheckedChange = { viewModel.toggleTrack(track) }
                        )
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
                        onCheckedChange = { viewModel.setIncludeRoutes(it) }
                    )
                }
            }
        }
    }
}