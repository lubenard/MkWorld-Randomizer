package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.escatrag.mkworldrandomiser.backend.TrackCombo
import com.escatrag.mkworldrandomiser.ui.theme.MinecraftFontFamily

@Composable
fun DualSpinnerPhase(
    selectedTracks: List<TrackCombo>,
    selectedTrackIndex: Int,
    selectedItem: TrackCombo?,
    destinationItems: List<TrackCombo>,
    destinationTargetIndex: Int,
    isSecondSpinnerReady: Boolean,
    showResultActions: Boolean,
    hasPlayers: Boolean,
    onFirstSpinFinished: () -> Unit,
    onSecondSpinFinished: () -> Unit,
    onScoreSelection: () -> Unit,
    onRecommencer: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedItem != null) {
                    SpinWheel(
                        items = selectedTracks,
                        targetIndex = selectedTrackIndex,
                        selectedItem = selectedItem.start,
                        onFinished = { onFirstSpinFinished() },
                        modifier = Modifier
                    )

                Text(
                    text = "vers",
                    fontFamily = MinecraftFontFamily,
                    fontSize = 22.sp,
                    color = Color.Gray
                )

                if (isSecondSpinnerReady) {
                    SpinWheel(
                        items = destinationItems,
                        targetIndex = destinationTargetIndex,
                        selectedItem = selectedItem.end,
                        onFinished = { onSecondSpinFinished() },
                        modifier = Modifier
                    )
                } else {
                    Spacer(modifier = Modifier)
                }
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
