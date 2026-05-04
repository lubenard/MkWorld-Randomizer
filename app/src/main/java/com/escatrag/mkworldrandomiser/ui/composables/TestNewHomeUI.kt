package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escatrag.mkworldrandomiser.R
import kotlin.math.sin

@Composable
fun TestNewHomeUI(availableCoursesCount: Int = 48, onClick: () -> Unit) {
    // 1. Création du gestionnaire d'animations infinies
    val infiniteTransition = rememberInfiniteTransition(label = "homepage_animations")

    // --- ANIMATION 1 : Le Shaker Lent (Translation X) ---
    val shakerOffsetX by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = -2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing), // Lent et fluide
            repeatMode = RepeatMode.Reverse
        ),
        label = "shakerX"
    )

    // --- ANIMATION 1 : Le Shaker Lent (Translation Y) ---
    val shakerOffsetY by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = -2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing), // Lent et fluide
            repeatMode = RepeatMode.Reverse
        ),
        label = "shakerY"
    )

    // --- ANIMATION 2 : Alpha du Texte (Opacité) ---
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // --- ANIMATION 3 : Phase pour la vague ---
    // On anime une valeur de 0 à 2 PI pour s'en servir dans une fonction sinus
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val MinecraftFontFamily = FontFamily(
        Font(R.font.minecraft, FontWeight.Normal),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("MARIO KART",
            fontFamily = MinecraftFontFamily,
            fontSize = 50.sp,
            color = Color.Yellow,
            modifier = Modifier.padding(top = 30.dp)
        )
        Text("Circuit aléatoire".uppercase(),
            fontFamily = MinecraftFontFamily,
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 15.dp)
        )
        Spacer(Modifier.height(30.dp))
        // --- ÉLÉMENT 1 : Texte et Icône (Pool de courses) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ).padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = "Courses",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$availableCoursesCount courses disponibles",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.height(24.dp))
        // --- ÉLÉMENT 2 : Le carré "Shaker" subtil ---
        Box(
            modifier = Modifier
                .offset(x = shakerOffsetX.dp, y = shakerOffsetY.dp) // Application de l'animation sur l'axe X
                .size(200.dp)
                .background(Color(0xFFFFC107), shape = RoundedCornerShape(16.dp))
                .clickable { onClick()}, // Jaune style bloc Mario
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.QuestionMark,
                contentDescription = "Star",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        // --- ÉLÉMENT 3 : Le texte qui clignote (Alpha) ---
        Text(
            text = "tape le bloc pour révéler".uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(textAlpha) // Application de l'animation d'opacité
        )
        Spacer(Modifier.height(24.dp))
        // --- ÉLÉMENT 4 : La ligne d'icônes avec effet de vague ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 5) {
                // On calcule le décalage Y avec un sinus.
                // i * 1f permet de décaler chaque icône sur la courbe de la vague.
                val yOffset = (sin(wavePhase - (i * 1f)) * 5f).dp

                Icon(
                    imageVector = Icons.Default.Star, // Tu peux remplacer par tes propres icônes
                    contentDescription = "Vague $i",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .offset(y = yOffset) // Application de l'animation sur l'axe Y
                )
            }
        }
    }
}