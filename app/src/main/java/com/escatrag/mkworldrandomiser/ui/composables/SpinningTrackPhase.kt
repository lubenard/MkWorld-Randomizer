package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.runtime.Composable
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
    if (selectedItem != null) {
        SpinWheel(
            items = selectedTracks,
            targetIndex = selectedTrackIndex,
            selectedItem = selectedItem.start,
            onFinished = { onSpinFinished() },
        )

        RaceResultActions(
            showResultActions = showResultActions,
            hasPlayers = hasPlayers,
            onScoreSelection = onScoreSelection,
            onRecommencer = onRecommencer,
        )
    }
}
