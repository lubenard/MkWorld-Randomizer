package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.escatrag.mkworldrandomiser.ui.theme.MinecraftFontFamily

@Composable
fun RaceResultActions(
    showResultActions: Boolean,
    hasPlayers: Boolean,
    onScoreSelection: () -> Unit,
    onRecommencer: () -> Unit,
) {
    if (showResultActions) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (hasPlayers) {
                Button(
                    onClick = onScoreSelection,
                    modifier = Modifier.weight(1f).height(48.dp),
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
            RecommencerButton(
                onClick = onRecommencer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
