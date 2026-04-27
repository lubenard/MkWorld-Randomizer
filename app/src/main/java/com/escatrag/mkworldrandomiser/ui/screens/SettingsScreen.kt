package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.BuildConfig
import com.escatrag.mkworldrandomiser.ui.composables.TestSlider
import com.escatrag.mkworldrandomiser.viewmodels.SettingsViewModel
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: TrackViewModel, settingsViewModel: SettingsViewModel) {
    var darkModeEnabled by remember { mutableStateOf(false) }
    var showMirroredTracks by remember { mutableStateOf(false) }
    var showTrackPopup by remember { mutableStateOf(true) }

    val showPopup = settingsViewModel.isPopupEnabled.collectAsState()

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
            // Option 1 : Mode Sombre
            SettingSwitchRow(
                title = "Mode Sombre",
                subtitle = "Activer le thème de la Route Arc-en-Ciel (Sombre)",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )

            // Option 3 : Circuits Miroir
            SettingSwitchRow(
                title = "Mode Miroir - pas encore disponible",
                subtitle = "Inclure les circuits inversés dans les choix",
                checked = showMirroredTracks,
                onCheckedChange = { showMirroredTracks = it }
            )

            SettingSwitchRow(
                title = "Voir la popup de circuit",
                subtitle = "Afficher ou pas la popup de circuit",
                checked = showPopup.value,
                onCheckedChange = { settingsViewModel.setPopupEnabled(it) }
            )

            val bias = vm.generationBias.collectAsState()
            TestSlider(bias.value) {
                vm.updateGenerationBias(it)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Version de l'app en bas
            Text(
                text = "Version 1.0 - Mario Kart World App - ${BuildConfig.COMMIT_SHA}",
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