package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.escatrag.mkworldrandomiser.backend.TrackCombo

@Composable
fun SpinningTrackPhase(
    selectedTracks: List<TrackCombo>,
    selectedTrackIndex: Int,
    selectedItem: TrackCombo?,
    showResultActions: Boolean,
    hasPlayers: Boolean,
    onSpinFinished: () -> Unit,
    onScoreSelection: () -> Unit,
    onRecommencer: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedItem != null) {
                SpinWheel(
                    items = selectedTracks,
                    targetIndex = selectedTrackIndex,
                    selectedItem = selectedItem.start,
                    onFinished = { onSpinFinished() },
                    simpleAnimation = true,
                )
            }
        }

        RaceResultActions(
            showResultActions = showResultActions,
            hasPlayers = hasPlayers,
            onScoreSelection = onScoreSelection,
            onRecommencer = onRecommencer,
        )
    }
}
