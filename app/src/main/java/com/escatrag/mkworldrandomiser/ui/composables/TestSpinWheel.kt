package com.escatrag.mkworldrandomiser.ui.composables

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escatrag.mkworldrandomiser.backend.TrackCombo

@Composable
fun TestSpinWheel(
    items: List<TrackCombo>,
    targetIndex: Int,
    onFinished: (Int) -> Unit
) {
    // 1. État du Pager (on met un grand nombre pour simuler un défilement infini)
    val pageCount = 500
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    // 2. Animation du texte qui clignote (très rapide)
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )


    val ctc = LocalContext.current

    // 3. Logique du défilement avec ralentissement
    LaunchedEffect(targetIndex) {
        val itemsSize = items.size
        val currentPage = pagerState.currentPage

        // 1. On calcule combien de pages il reste pour finir le tour actuel
        val offsetInRound = currentPage % itemsSize

        // 2. On définit combien de tours complets on veut faire avant de s'arrêter (ex: 3 tours)
        val fullSpins = 3

        // 3. LA FORMULE MAGIQUE :
        // On calcule le nombre de pas nécessaires pour atteindre le targetIndex
        // (Distance pour finir le tour) + (Tours complets) + (Position de l'index dans le dernier tour)
        val stepsToTarget = (itemsSize - offsetInRound) + (itemsSize * fullSpins) + targetIndex

        val finalPage = currentPage + stepsToTarget

        // 4. Animation avec ralentissement
        var currentDelay = 50L
        for (page in (currentPage + 1)..finalPage) {

            // On ralentit sur les 15 derniers pas
            val stepsLeft = finalPage - page
            if (stepsLeft < 15) {
                currentDelay += 30L // Décélération progressive
            } else if (stepsLeft < 5) {
                currentDelay += 80L // Gros coup de frein final
            }

            pagerState.animateScrollToPage(
                page = page, // On ne fait plus de modulo ici pour l'animation
                animationSpec = tween(
                    durationMillis = currentDelay.toInt(),
                    easing = LinearOutSlowInEasing
                )
            )
        }
        Log.d("lubenard", "$targetIndex ->  ${if (targetIndex <= 0|| targetIndex > items.size -1 ) "<= 0" else ctc.getString(items[targetIndex].start.text)}/ $finalPage -> ${if (finalPage <= 0 || finalPage > items.size -1 ) "null" else items[finalPage]}")
        onFinished(finalPage)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- TEXTE CLIGNOTANT ---
        Text(
            text = "SÉLECTION EN COURS",
            modifier = Modifier.alpha(textAlpha),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- LE PAGER (ROULETTE) ---
        Box(
            modifier = Modifier
                .height(150.dp) // Hauteur pour voir un seul item à la fois ou un peu des voisins
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false, // On désactive le swipe manuel pendant le random
                horizontalAlignment = Alignment.CenterHorizontally
            ) { page ->
                val itemIndex = page % items.size
                Text(
                    text = context.getString(items[itemIndex].start.text),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}