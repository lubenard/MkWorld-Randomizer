package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.ui.theme.MinecraftFontFamily
import com.escatrag.mkworldrandomiser.viewmodels.PlayerProfile

@Composable
fun PodiumSection(
    podium: List<PlayerProfile>,
    onClick: (PlayerProfile) -> Unit,
    onLongClick: (PlayerProfile) -> Unit
) {
    // On récupère les profils s'ils existent
    val first = podium.getOrNull(0)
    val second = podium.getOrNull(1)
    val third = podium.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Un peu plus haut pour les avatars
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom // Important : tout le monde est posé au sol
    ) {
        // 2ème Place (à gauche)
        PodiumBar(
            player = second,
            rank = 2,
            heightFraction = 0.55f, // 55% de la hauteur max
            modifier = Modifier.weight(1f),
            onClick = { onClick(second!!) },
            onLongClick = { onLongClick(second!!) }

        )

        // 1ère Place (au milieu)
        PodiumBar(
            player = first,
            rank = 1,
            heightFraction = 0.85f, // 85% de la hauteur max
            modifier = Modifier.weight(1.2f), // Un peu plus large
            onClick = { onClick(first!!) },
            onLongClick = { onLongClick(first!!) }
        )

        // 3ème Place (à droite)
        PodiumBar(
            player = third,
            rank = 3,
            heightFraction = 0.35f, // 35% de la hauteur max
            modifier = Modifier.weight(1f),
            onClick = { onClick(third!!) },
            onLongClick = { onLongClick(third!!) }
        )
    }
}

@Composable
fun PodiumBar(
    player: PlayerProfile?,
    rank: Int,
    heightFraction: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    if (player == null) return

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // 1. Avatar du joueur au dessus de la barre
        Image(
            painter = painterResource(id = player.avatarRes ?: R.drawable.mont_tchou_tchou),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(if (rank == 1) 70.dp else 55.dp) // Le premier est plus gros
                .clip(CircleShape)
                .border(2.dp, player.composeColor, CircleShape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
        )

        Spacer(Modifier.height(4.dp))

        // 2. Nom et Score
        Text(
            text = player.name,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            fontFamily = MinecraftFontFamily,
            style = if (rank == 1) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${player.currentMonthScore} pts",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = MinecraftFontFamily,
            color = Color.Gray
        )

        Spacer(Modifier.height(8.dp))

        // Palette de couleurs pour l'effet métal
        val goldColors = listOf(Color(0xFFFFD700), Color(0xFFFFF1A6), Color(0xFFD4AF37), Color(0xFFF9E498))
        val silverColors = listOf(Color(0xFFC0C0C0), Color(0xFFE8E8E8), Color(0xFF8A8A8A), Color(0xFFD1D1D1))
        val bronzeColors = listOf(Color(0xFFCD7F32), Color(0xFFE3AF84), Color(0xFF8B4513), Color(0xFFA0522D))

        val chromeColors = when (rank) {
            1 -> goldColors
            2 -> silverColors
            3 -> bronzeColors
            else -> listOf(player.composeColor, player.composeColor.copy(alpha = 0.7f))
        }
        // 3. La barre du podium
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(heightFraction) // Hauteur proportionnelle au rang
                .background(
                    brush = Brush.verticalGradient(
                        colors = chromeColors
                    ),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .border(
                    width = 2.dp,
                    color = player.composeColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            // Chiffre du rang (1, 2 ou 3)
            Text(
                text = rank.toString(),
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}