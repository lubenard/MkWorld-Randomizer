package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.backend.TrackRepository
import com.escatrag.mkworldrandomiser.viewmodels.PlayerProfile
import com.escatrag.mkworldrandomiser.viewmodels.Top3Maps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersDetailsComposable(selectedPlayerForDetails: PlayerProfile, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {

        // Contenu du Bottom Sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Image(
                painter = painterResource(
                    id = selectedPlayerForDetails.avatarRes ?: R.drawable.mont_tchou_tchou
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )

            Text(
                text = selectedPlayerForDetails.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Affichage des infos du joueur
            Text("${selectedPlayerForDetails.currentMonthScore} pts")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espace entre les tuiles
            ) {
                // Tuile 1 : Courses
                StatTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Flag,
                    value = selectedPlayerForDetails.runNumbers.toString(),
                    label = "Courses",
                    color = Color.Gray
                )

                // Tuile 2 : Victoires
                StatTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.EmojiEvents, // Icône Coupe
                    value = selectedPlayerForDetails.victoryNumbers.toString(),
                    label = "Victoires",
                    color = Color(0xFFFFD700) // Or
                )

                // Tuile 3 : Podiums
                StatTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.MilitaryTech, // Icône Médaille
                    value = selectedPlayerForDetails.timesInPodium.toString(),
                    label = "Podiums",
                    color = Color(0xFFC0C0C0) // Argent
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Circuits favoris")
            Spacer(modifier = Modifier.height(10.dp))
            val sortedMaps = selectedPlayerForDetails.top3Maps.sortedByDescending { it.timeInTop3 }

            MapSwimlane(maps = sortedMaps)
        }
    }
}

@Composable
fun StatTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MapSwimlane(maps: List<Top3Maps>) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {

        if (maps.isEmpty()) {
            Text("Aucun podium pour le moment", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(maps) { mapId ->
                    MapCard(mapId)
                }
            }
        }
    }
}

@Composable
fun MapCard(mapId: Top3Maps) {
    // TODO: Use a id for map instead of hardcoded mapping
    val tmpTracks = TrackRepository.trackItems

    val correctTrack = tmpTracks.find { it.start.text == mapId.mapId }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 112.5.dp)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            Image(painter = painterResource(correctTrack!!.start.largeIcon), contentDescription = "", modifier = Modifier.fillMaxSize())
            Text(
                text = "${mapId.timeInTop3} x in Top3",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(2.dp)
            )
        }
    }
}