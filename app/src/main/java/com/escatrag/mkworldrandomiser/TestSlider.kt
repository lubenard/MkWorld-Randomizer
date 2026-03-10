package com.escatrag.mkworldrandomiser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TestSlider(
    value: Float, // Provient de ton viewModel (ex: state.zoomValue)
    onValueChange: (Float) -> Unit // Fonction du viewModel (ex: viewModel::onZoomChange)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var localValue by remember { mutableStateOf(value) }

        // 1. Le Slider
        Slider(
            value = localValue,
            onValueChange = {
                localValue = it // Mise à jour instantanée de l'UI locale
            },
            onValueChangeFinished = {
                onValueChange(localValue) // Envoi au ViewModel seulement à la fin
            },
            valueRange = 0f..100f,
            steps = 1, // Cela crée un "cran" à 50% (car 1 point entre 0 et 100)
            modifier = Modifier.fillMaxWidth()
        )

        // 2. Les points de repère (Labels)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // Ajustement pour aligner avec le pouce du slider
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MarkerText(label = "Circuits")
            MarkerText(label = "50 / 50")
            MarkerText(label = "Connections")
        }
    }
}

@Composable
fun MarkerText(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}