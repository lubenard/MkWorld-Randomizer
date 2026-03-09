package com.escatrag.mkworldrandomiser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSelectionScreen(viewModel: TrackViewModel, navController: NavController) {
    val selectedTracks by viewModel.selectedTracks.collectAsState()
    val includeRoutes by viewModel.includeRoutes.collectAsState()

    val allTracksList by viewModel.allTracksAvailable.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sélection des circuits") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    val allSelected = selectedTracks.size == allTracksList.size

                    TextButton(onClick = {
                        if (allSelected) viewModel.clearAllTracks() else viewModel.selectAllTracks(includeRoutes)
                    }) {
                        Text(if (allSelected) "Désélectionner tout" else "Tout sélectionner")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Liste des circuits
            val context = LocalContext.current

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(allTracksList) { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleTrack(context.getString(track.text)) }
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedTracks.contains(track),
                            onCheckedChange = null
                        )
                        Text(stringResource(track.text), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            // Option Inclure les trajets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Inclure les trajets", modifier = Modifier.weight(1f))
                Switch(
                    checked = includeRoutes,
                    onCheckedChange = { state ->
                        viewModel.setIncludeRoutes(state)
                    }
                )
            }
        }
    }
}