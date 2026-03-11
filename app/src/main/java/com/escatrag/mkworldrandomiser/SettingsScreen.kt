package com.escatrag.mkworldrandomiser

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: TrackViewModel) {
    // États locaux (State) pour stocker les préférences
    // Note : Dans une vraie app, on utiliserait un ViewModel et DataStore/SharedPreferences
    var darkModeEnabled by remember { mutableStateOf(false) }
    var musicEnabled by remember { mutableStateOf(true) }
    var showMirroredTracks by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Préférences de l'application",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            // Option 1 : Mode Sombre
            SettingSwitchRow(
                title = "Mode Sombre",
                subtitle = "Activer le thème de la Route Arc-en-Ciel (Sombre)",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )

            // Option 2 : Musique
            SettingSwitchRow(
                title = "Musique et Sons",
                subtitle = "Jouer la musique des circuits dans les menus",
                checked = musicEnabled,
                onCheckedChange = { musicEnabled = it }
            )

            // Option 3 : Circuits Miroir
            SettingSwitchRow(
                title = "Mode Miroir",
                subtitle = "Inclure les circuits inversés dans les choix",
                checked = showMirroredTracks,
                onCheckedChange = { showMirroredTracks = it }
            )

            val bias = vm.generationBias.collectAsState()
            TestSlider(bias.value) {
                vm.updateGenerationBias(it)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Version de l'app en bas
            Text(
                text = "Version 1.0 - Mario Kart World App",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// Composant réutilisable pour chaque ligne de paramètre
@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}