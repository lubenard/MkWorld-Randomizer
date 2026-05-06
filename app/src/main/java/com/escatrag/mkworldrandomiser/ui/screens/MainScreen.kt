package com.escatrag.mkworldrandomiser.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.ui.composables.SpinWheel
import com.escatrag.mkworldrandomiser.ui.composables.TestNewHomeUI
import com.escatrag.mkworldrandomiser.viewmodels.SettingsViewModel
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

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
            //delay(3000)
            //viewModel.resetCourse()
        }
    }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Sélection des circuits") },
//                actions = {
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
//                }
//            )
//        },
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

        val dialogString = viewModel.showResultPopup.collectAsState().value
        val selectedItem = viewModel.selectedTrack.collectAsState().value
        val selectedTeams = viewModel.selectedRandomTeams.collectAsState().value
        val selectedTrackIndex = viewModel.selectedTrackIndex.collectAsState().value

        val colors = listOf(0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFFFFFF00.toInt(), 0xFFFF00FF.toInt())

        val partyLeft = Party(
            speed = 40f, // Plus de patate pour monter haut
            maxSpeed = 100f,
            angle = 300, // Diagonale haut-droite
            spread = 10,
            colors = colors,
            emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(300),
            position = Position.Relative(0.0, 0.6) // Bas gauche
        )

        val partyRight = Party(
            speed = 40f,
            maxSpeed = 100f,
            angle = 240, // Diagonale haut-gauche
            spread = 10,
            colors = colors,
            emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(300),
            position = Position.Relative(1.0, 0.6) // Bas droit
        )

        var showConfetti by remember { mutableStateOf(false) }

        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(partyLeft, partyRight),
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        if (activeSystems == 0) {
                            showConfetti = false
                        }
                    }
                }
            )
        }

        // Background image
        Image(
            modifier = Modifier.fillMaxSize().alpha(0.7f),
            painter = painterResource(R.drawable.map),
            contentScale = ContentScale.Crop,
            contentDescription = "",
        )

        var lastClickTime by remember { mutableLongStateOf(0L) }
        var showSelectionCube by remember { mutableStateOf(true) }


        Column(
            modifier = Modifier
                .padding(padding)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxSize()
        ) {

            if (showSelectionCube) {
                TestNewHomeUI(selectedTracks.size, onClick = {
                    val currentTime = System.currentTimeMillis()
                    showSelectionCube = false
                    onGenerate(currentTime - lastClickTime)
                    lastClickTime = currentTime
                })

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
            } else {
                SpinWheel(
                    items = selectedTracks,
                    targetIndex = selectedTrackIndex,
                    selectedItem = selectedItem,
                    onFinished = {
                        viewModel.setResultPopupDisplay(true)
                        showConfetti = true
                        //TODO: fix crash here
                        if (viewModel.deleteTrackAfterCompletion.value)
                            Log.d("lubenard", "${selectedItem}")
                            viewModel.deleteCircuit(selectedItem)
                    },
                    onRetry = {
                        showSelectionCube = true
                    },
                    onScoreSelection = onScoreSelection
                )
            }
        }
    }
}