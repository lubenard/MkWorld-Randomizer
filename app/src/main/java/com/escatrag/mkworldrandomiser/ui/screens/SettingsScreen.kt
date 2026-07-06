package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.BuildConfig
import com.escatrag.mkworldrandomiser.ui.composables.BiasSlider
import com.escatrag.mkworldrandomiser.viewmodels.SettingsViewModel
import com.escatrag.mkworldrandomiser.viewmodels.ThemeMode
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: TrackViewModel, settingsViewModel: SettingsViewModel, padding: PaddingValues) {
    val themeMode by settingsViewModel.themeMode.collectAsState()

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
            Text("Thème", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    val label = when (mode) {
                        ThemeMode.SYSTEM -> "Système"
                        ThemeMode.LIGHT -> "Clair"
                        ThemeMode.DARK -> "Sombre"
                    }
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { settingsViewModel.setThemeMode(mode) },
                        label = { Text(label) }
                    )
                }
            }

            val bias = vm.generationBias.collectAsState()
            BiasSlider(bias.value) {
                vm.updateGenerationBias(it)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Version 1.0 - Mario Kart World App - ${BuildConfig.COMMIT_SHA}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
