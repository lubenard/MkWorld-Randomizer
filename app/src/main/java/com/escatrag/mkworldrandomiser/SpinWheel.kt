import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive

@Composable
fun SpinningWheel(
    items: List<String>,
    targetIndex: Int,
    onItemSelected: (String) -> Unit
) {
    val itemHeight = 60.dp
    val density = LocalDensity.current
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = Int.MAX_VALUE / 2)

    // Conversion propre une seule fois
    val itemHeightPx = with(density) { itemHeight.toPx() }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight(0.80f)
            .fillMaxWidth()
            .clipToBounds(),
        contentAlignment = Alignment.Center // Le curseur sera PILE au milieu
    ) {
        // Calcul du centre exact du composant en pixels
        val centerContainerPx = constraints.maxHeight / 2f
        val halfItemPx = itemHeightPx / 2f

        val centerOffsetPx = (centerContainerPx - halfItemPx).toInt()

        LaunchedEffect(targetIndex) {
            if (targetIndex == -1) {
                while (isActive) {
                    withFrameMillis { listState.dispatchRawDelta(15f) }
                }
            } else {
              //if (targetIndex != -1) {

                val currentVisibleIndex = targetIndex
                val itemsPerRound = items.size

                // Calcul de la destination
                val nextOccurrence = itemsPerRound - (currentVisibleIndex % itemsPerRound) + targetIndex
                val finalTargetIndex = currentVisibleIndex + nextOccurrence + (itemsPerRound * 5)

                val test = items[targetIndex]

                Log.d("Luca2", "$finalTargetIndex items ${items}/ $targetIndex / $nextOccurrence -> $test")
                //Log.d("Luca", "$items}/ $targetIndex : item = ${items[test]}")

                onItemSelected(test)

                // On utilise l'animation intégrée avec le scrollOffset négatif
                listState.animateScrollToItem(
                    index = targetIndex,
                    scrollOffset = -centerOffsetPx // On "pousse" l'item vers le bas
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
            // Suppression du contentPadding pour éviter les conflits
        ) {
            items(Int.MAX_VALUE) { index ->
                Box(
                    modifier = Modifier.height(itemHeight).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    //Log.d("Luca", "item selected is ${items[index % items.size]}")
                    Text(
                        text = items[index % items.size],
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        // Le curseur de sélection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            // Pas de padding ici, align(Alignment.Center) suffit
        )
    }
}