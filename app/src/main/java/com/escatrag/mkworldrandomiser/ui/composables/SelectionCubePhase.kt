package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectionCubePhase(
    totalPool: Int,
    deleteTrackAfterCompletion: Boolean,
    onPickRandomMap: () -> Unit,
    onToggleDeleteTrack: (Boolean) -> Unit,
) {
    HomeUI(totalPool, onClick = onPickRandomMap)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = deleteTrackAfterCompletion,
            onCheckedChange = onToggleDeleteTrack,
        )
        Spacer(Modifier.width(8.dp))
        Text("Supp. les trajets faits")
    }
}
