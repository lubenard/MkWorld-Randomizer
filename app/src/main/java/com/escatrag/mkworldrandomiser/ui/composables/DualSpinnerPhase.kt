package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedItem != null) {
                SpinWheel(
                    items = selectedTracks,
                    targetIndex = selectedTrackIndex,
                    selectedItem = selectedItem.start,
                    onFinished = { onFirstSpinFinished() },
                    modifier = Modifier.weight(1f)
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
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        if (showResultActions) {
            Spacer(modifier = Modifier.height(20.dp))
            if (hasPlayers) {
                Button(
                    onClick = onScoreSelection,
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE401),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Saisir les scores",
                            fontWeight = FontWeight.Bold,
                            fontFamily = MinecraftFontFamily,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            RecommencerButton(onClick = onRecommencer)
        }
    }
}
