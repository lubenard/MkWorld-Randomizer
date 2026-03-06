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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.Track
import kotlinx.coroutines.isActive

@Composable
fun SpinningWheel(
    items: List<Track>,
    targetIndex: Int,
    onItemSelected: (Track) -> Unit,
    placeholder: String = ""
) {
    val itemHeight = 60.dp
    val density = LocalDensity.current
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = Int.MAX_VALUE / 2)

    val itemHeightPx = with(density) { itemHeight.toPx() }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight(0.80f)
            .fillMaxWidth()
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        val centerContainerPx = constraints.maxHeight / 2f
        val halfItemPx = itemHeightPx / 2f
        val centerOffsetPx = (centerContainerPx - halfItemPx).toInt()

        LaunchedEffect(targetIndex) {
            if (targetIndex == -1) {
                while (isActive) {
                    withFrameMillis { listState.dispatchRawDelta(15f) }
                }
            } else if (items.isNotEmpty()) {
                val currentVisibleIndex = listState.firstVisibleItemIndex
                val itemsPerRound = items.size
                val nextOccurrence = itemsPerRound - (currentVisibleIndex % itemsPerRound) + targetIndex
                val finalTargetIndex = currentVisibleIndex + nextOccurrence + (itemsPerRound * 5)

                val selectedTrack = items[targetIndex]
                onItemSelected(selectedTrack)

                listState.animateScrollToItem(
                    index = finalTargetIndex,
                    scrollOffset = -centerOffsetPx
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) {
            items(Int.MAX_VALUE) { index ->
                Box(
                    modifier = Modifier.height(itemHeight).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val text = if (items.isEmpty()) placeholder else stringResource(items[index % items.size].nameRes)
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        )
    }
}