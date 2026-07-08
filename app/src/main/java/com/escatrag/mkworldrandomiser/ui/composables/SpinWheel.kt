package com.escatrag.mkworldrandomiser.ui.composables

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escatrag.mkworldrandomiser.backend.TrackCombo
import com.escatrag.mkworldrandomiser.ui.theme.MinecraftFontFamily
import kotlinx.coroutines.delay

@Composable
fun SpinWheel(
    items: List<TrackCombo>,
    targetIndex: Int,
    onFinished: (Int) -> Unit,
    onRetry: () -> Unit,
    selectedItem: TrackCombo?,
    onScoreSelection: () -> Unit,
    playersSize: Int,
    showResultUI: Boolean = true,
    modifier: Modifier = Modifier
) {
    // 1. État du Pager (on met un grand nombre pour simuler un défilement infini)
    val pageCount = 500
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    var showRestartButton by remember { mutableStateOf(false) }

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
        val itemsSize = if (items.isEmpty()) 1 else items.size
        val currentPage = pagerState.currentPage

        // 1. On calcule combien de pages il reste pour finir le tour actuel
        val offsetInRound = currentPage % itemsSize

        // 2. On définit combien de tours complets on veut faire avant de s'arrêter (ex: 3 tours)
        val fullSpins = 2

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
        delay(1000L)
        showRestartButton = true
        onFinished(finalPage)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Log.d("lubenard", "$targetIndex -> $showRestartButton")

        val context = LocalContext.current

        if (!showRestartButton) {
            // --- TEXTE CLIGNOTANT ---
            Text(
                text = "SÉLECTION EN COURS",
                modifier = Modifier.alpha(textAlpha),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Black
            )
        } else if (showRestartButton && selectedItem != null) {
            Text(
                text = context.getString(selectedItem.start.text),
                fontWeight = FontWeight.Bold,
                fontFamily = MinecraftFontFamily,
                fontSize = 35.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val fallbackEmptyText = if (items.isEmpty()) "Merci de choisir au moins une carte" else null

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
                val itemIndex = if (items.isEmpty()) 1 else page % items.size
                if (fallbackEmptyText == null) {
                    Image(
                        modifier = Modifier.size(width = 1200.dp, height = 600.dp),
                        painter = painterResource(items[itemIndex].start.largeIcon),
                        contentDescription = context.getString(items[itemIndex].start.text)
                    )
                } else {
                    Text(
                        text = fallbackEmptyText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showResultUI) {
            Spacer(modifier = Modifier.height(50.dp))

            if (selectedItem?.end != null) {
                Text(
                    text = context.getString(selectedItem.end.text),
                    fontWeight = FontWeight.Bold,
                    fontFamily = MinecraftFontFamily,
                    fontSize = 35.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = if (selectedItem?.start?.text == null || selectedItem.start.text <= 0) "Unknown" else context.getString(selectedItem.start.text),
                    fontWeight = FontWeight.Bold,
                    fontFamily = MinecraftFontFamily,
                    fontSize = 35.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (playersSize != 0) {
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onScoreSelection,
                    modifier = Modifier
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE401),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
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
            RecommencerButton(onClick = {
                showRestartButton = false
                onRetry()
            })
        }
    }
}


@Composable
fun RecommencerButton(onClick: () -> Unit) {
    // 1. Création de la transition infinie
    val infiniteTransition = rememberInfiniteTransition(label = "BlinkTransition")

    // 2. Animation de l'opacité (Alpha)
    // On passe de 1.0 (plein) à 0.4 (estompé) en 1 seconde, puis inversement (RepeatMode.Reverse)
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OpacityAnimation"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .graphicsLayer(alpha = alpha)
            .height(70.dp), // On donne une hauteur fixe pour stabiliser le rendu
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFE401),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        // ON FORCE LE PADDING À 0 pour que le bouton ne décale pas le texte
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // CENTRAGE TOTAL DANS LA BOX
        ) {
            Text(
                text = "RECOMMENCER",
                fontWeight = FontWeight.Bold,
                fontFamily = MinecraftFontFamily,
                fontSize = 35.sp,
                textAlign = TextAlign.Center // Centrage interne du texte
            )
        }
    }
}