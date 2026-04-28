package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.ui.composables.SpinningWheel
import com.escatrag.mkworldrandomiser.viewmodels.SettingsViewModel
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TrackViewModel,
    settingsViewModel: SettingsViewModel,
    onGenerate: (delay: Long) -> Unit,
    onNavigate: () -> Unit,
    onSettings: () -> Unit,
    onScore: () -> Unit,
    onScoreSelection: () -> Unit,
    onInfo: () -> Unit,
    onTeam: () -> Unit,
) {
    val selectedTracks by viewModel.selectedTracks.collectAsState()
    val deleteTrackAfterCompletion by viewModel.deleteTrackAfterCompletion.collectAsState()
    val selectedItem by viewModel.selectedTrackIndex.collectAsState()

    var mexpanded by remember { mutableStateOf(false) }

    // État pour l'onglet sélectionné dans la barre du bas
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.resetCourse()
    }

    LaunchedEffect(selectedItem) {
        if (selectedItem != -1) {
            delay(3000)
            viewModel.resetCourse()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sélection des circuits") },
                actions = {
//                    IconButton(onClick = { mexpanded = true }) {
//                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
//                    }
//                    DropdownMenu(
//                        expanded = mexpanded,
//                        onDismissRequest = { mexpanded = false }
//                    ) {
//                        DropdownMenuItem(
//                            text = { Text("Paramètres") },
//                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
//                            onClick = { mexpanded = false; onSettings() }
//                        )
//                        DropdownMenuItem(
//                            text = { Text("Infos") },
//                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
//                            onClick = { mexpanded = false; onInfo() }
//                        )
//                    }
                }
            )
        },
        // --- AJOUT DE LA BARRE DE NAVIGATION ---
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Aléatoire") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onNavigate()
                    },
                    label = { Text("Circuits") },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onScore()
                    },
                    label = { Text("Scores") },
                    icon = { Icon(Icons.Default.Groups, contentDescription = null) }
                )
//                NavigationBarItem(
//                    selected = selectedTab == 2,
//                    onClick = {
//                        selectedTab = 2
//                        onTeam()
//                    },
//                    label = { Text("Teams") },
//                    icon = { Icon(Icons.Default.Groups, contentDescription = null) }
//                )
//                NavigationBarItem(
//                    selected = selectedTab == 3,
//                    onClick = {
//                        selectedTab = 3
//                        //onSettings()
//                    },
//                    label = { Text("Infos") },
//                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) }
//                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = {
                        selectedTab = 4
                        onSettings()
                    },
                    label = { Text("Réglages") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) { padding ->

        val dialogString = viewModel.showResultPopup.collectAsState()
        val selectedTeams = viewModel.selectedRandomTeams.collectAsState()

        val context = LocalContext.current

        val shopPopup = settingsViewModel.isPopupEnabled.collectAsState()

        if (dialogString.value != null) {

            //val endTrackAvailable = endTrack.value != null

            AlertDialog(
                modifier = Modifier.then(if (!shopPopup.value) Modifier.alpha(0.4f) else Modifier),
                onDismissRequest = { viewModel.setPopupDisplay(null) },
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
                                    modifier = Modifier.size(if (dialogString.value?.end != null) 120.dp else 240.dp),
                                    painter = painterResource(dialogString.value?.start!!.icon),
                                    contentDescription = context.getString(dialogString.value?.start!!.text),
                                )
                                Text(
                                    text = context.getString(dialogString.value?.start!!.text),
                                    fontSize = if (dialogString.value?.end != null) 15.sp else 25.sp
                                )
                            }

                            if (dialogString.value?.end != null) {
                                Text(">", fontSize = 50.sp)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        modifier = Modifier.size(120.dp),
                                        painter = painterResource(dialogString.value!!.end?.icon!!),
                                        contentDescription = context.getString(dialogString.value!!.end?.text!!),
                                    )
                                    Text(
                                        text = context.getString(dialogString.value!!.end?.text!!),
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                        if (selectedTeams.value.isNotEmpty()) {
                            Text("Joueurs")
                            Row {
                                // Max is 4 players
                                repeat(selectedTeams.value.size) { index ->
                                    Text(selectedTeams.value[index], modifier = Modifier.padding(end = 5.dp))
                                }
                            }
                        }
                        // --- LE NOUVEAU BOUTON ---
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.setPopupDisplay(null) // Ferme la popup actuelle
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
                        Button(onClick = { viewModel.setPopupDisplay(null) }) {
                            Text("Fermer")
                        }
                    }
                }
            )
        }

        Image(
            modifier = Modifier.fillMaxSize().alpha(0.7f),
            painter = painterResource(R.drawable.map),
            contentScale = ContentScale.Crop,
            contentDescription = "",
        )

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxSize()
        ) {

            SpinningWheel(
                items = selectedTracks,
                targetIndex = selectedItem,
                placeholder = "Merci de choisir au moins une carte",
                onItemSelected = {
                    viewModel.setPopupDisplay(viewModel.selectedTracks.value[it])
                    if (viewModel.deleteTrackAfterCompletion.value)
                        viewModel.deleteCircuit(viewModel.selectedTracks.value[it])
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = deleteTrackAfterCompletion,
                    onCheckedChange = {
                        viewModel.updateDeleteTrackAfterCompletion(it)
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text("Supp. les trajets faits")
            }

            var lastClickTime by remember { mutableLongStateOf(0L) }

            val rainbowColors = listOf(
                Color(0xFFFC7777), // Rouge pastel
                Color(0xFFFDB468), // Orange pastel
                Color(0xFFFCFC6E), // Jaune pastel
                Color(0xFF75FF75), // Vert pastel
                Color(0xFF6FC4FF), // Bleu pastel
                Color(0xFF72BAFD), // Indigo pastel (bleu-violet très clair)
                Color(0xFFBF66FF)  // Violet pastel
            )

            val rainbowBrush = Brush.horizontalGradient(colors = rainbowColors)

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp).background(brush = rainbowBrush, shape = RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                onClick = {
                val currentTime = System.currentTimeMillis()
                onGenerate(currentTime - lastClickTime)
                lastClickTime = currentTime
            }) {
                Text("Choisir un trajet", color = Color.Black)
            }
        }
    }
}