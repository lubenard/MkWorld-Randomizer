package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escatrag.mkworldrandomiser.backend.TrackCombo
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel

@Composable
fun ResultDialog(
    viewModel: TrackViewModel,
    track: TrackCombo,
    selectedTeams: List<String>,
    onScoreSelection: () -> Unit,
    shopPopup: Boolean,
) {
    val context = LocalContext.current

    AlertDialog(
        modifier = then(if (!shopPopup) Modifier.alpha(0.4f) else Modifier),
        onDismissRequest = { viewModel.setResultPopupDisplay(false) },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Sélectionné !",
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            modifier = Modifier.size(if (track.end != null) 120.dp else 240.dp),
                            painter = painterResource(track.start.icon),
                            contentDescription = context.getString(track.start.text),
                        )
                        Text(
                            text = context.getString(track.start.text),
                            fontSize = if (track.end != null) 15.sp else 25.sp
                        )
                    }

                    if (track.end != null) {
                        Text(">", fontSize = 50.sp)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                modifier = Modifier.size(120.dp),
                                painter = painterResource(track.end.icon),
                                contentDescription = context.getString(track.end.text),
                            )
                            Text(
                                text = context.getString(track.end.text),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                if (selectedTeams.isNotEmpty()) {
                    Text("Joueurs")
                    Row {
                        // Max is 4 players
                        repeat(selectedTeams.size) { index ->
                            Text(
                                selectedTeams[index],
                                modifier = Modifier.padding(end = 5.dp)
                            )
                        }
                    }
                }
                // --- LE NOUVEAU BOUTON ---
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.setResultPopupDisplay(false) // Ferme la popup actuelle
                        onScoreSelection() // Déclenche la navigation vers l'écran des scores
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Entrer les scores de la course")
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { viewModel.setResultPopupDisplay(false) }) {
                    Text("Fermer")
                }
            }
        }
    )
}