package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.BuildConfig
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.ui.composables.BiasSlider
import com.escatrag.mkworldrandomiser.viewmodels.SettingsViewModel
import com.escatrag.mkworldrandomiser.viewmodels.SnesMaxPerCoupe
import com.escatrag.mkworldrandomiser.viewmodels.ThemeMode
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: TrackViewModel,
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit,
    padding: PaddingValues
) {
    val themeMode by settingsViewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.parametres)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_retour)
                        )
                    }
                },
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
            Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    val label = when (mode) {
                        ThemeMode.SYSTEM -> stringResource(R.string.theme_systeme)
                        ThemeMode.LIGHT -> stringResource(R.string.theme_clair)
                        ThemeMode.DARK -> stringResource(R.string.theme_sombre)
                    }
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { settingsViewModel.setThemeMode(mode) },
                        label = { Text(label) }
                    )
                }
            }

            Text(stringResource(R.string.biais_generation), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.biais_generation_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val bias = vm.generationBias.collectAsState()
            BiasSlider(bias.value) {
                vm.updateGenerationBias(it)
            }

            val popupEnabled = settingsViewModel.isPopupEnabled.collectAsState()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.voir_popup_circuit),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.voir_popup_circuit_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = popupEnabled.value,
                    onCheckedChange = { settingsViewModel.setPopupEnabled(it) }
                )
            }

            val snesEnabled by vm.snesEnabled.collectAsState()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.snes_maps),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.snes_maps_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = snesEnabled,
                    onCheckedChange = { vm.setSnesEnabled(it) }
                )
            }

            val racesPerCoupe by vm.racesPerCoupe.collectAsState()
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.courses_par_coupe),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.courses_par_coupe_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "$racesPerCoupe",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                var localCoupe by remember {
                    mutableStateOf(TrackViewModel.RACES_PER_COUPE_OPTIONS.indexOf(racesPerCoupe).toFloat())
                }
                Slider(
                    value = localCoupe,
                    onValueChange = { localCoupe = it },
                    onValueChangeFinished = {
                        vm.updateRacesPerCoupe(
                            TrackViewModel.RACES_PER_COUPE_OPTIONS[localCoupe.roundToInt()]
                        )
                    },
                    valueRange = 0f..(TrackViewModel.RACES_PER_COUPE_OPTIONS.size - 1).toFloat(),
                    steps = TrackViewModel.RACES_PER_COUPE_OPTIONS.size - 2
                )
            }

            val maxSnes by vm.maxSnesPerCoupe.collectAsState()
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.max_snes_par_coupe),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.max_snes_par_coupe_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = when (maxSnes) {
                            SnesMaxPerCoupe.ZERO -> "0"
                            SnesMaxPerCoupe.ONE -> "1"
                            SnesMaxPerCoupe.TWO -> "2"
                            SnesMaxPerCoupe.THREE -> "3"
                            SnesMaxPerCoupe.FOUR -> "4"
                            SnesMaxPerCoupe.RANDOM -> stringResource(R.string.max_snes_aleatoire)
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                var localMax by remember { mutableStateOf(maxSnes.ordinal.toFloat()) }
                Slider(
                    value = localMax,
                    onValueChange = { localMax = it },
                    onValueChangeFinished = {
                        vm.updateMaxSnesPerCoupe(SnesMaxPerCoupe.entries[localMax.roundToInt()])
                    },
                    valueRange = 0f..5f,
                    steps = 4
                )
            }

            val deleteSnesInBlock by vm.deleteSnesInBlock.collectAsState()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.supprimer_snes_en_bloc),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.supprimer_snes_en_bloc_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = deleteSnesInBlock,
                    onCheckedChange = { vm.updateDeleteSnesInBlock(it) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.version_app, BuildConfig.COMMIT_SHA),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
