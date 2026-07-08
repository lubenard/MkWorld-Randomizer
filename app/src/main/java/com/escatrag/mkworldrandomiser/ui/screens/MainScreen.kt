package com.escatrag.mkworldrandomiser.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.ui.composables.DualSpinnerPhase
import com.escatrag.mkworldrandomiser.ui.composables.SelectionCubePhase
import com.escatrag.mkworldrandomiser.ui.composables.SpinningTrackPhase
import com.escatrag.mkworldrandomiser.viewmodels.Phase
import com.escatrag.mkworldrandomiser.viewmodels.ScoreViewModel
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
    onSettings: () -> Unit,
    onScoreSelection: () -> Unit,
    scoreViewModel: ScoreViewModel,
    padding: PaddingValues,
) {
    val selectedTracks by viewModel.selectedTracks.collectAsState()
    val selectedConnections by viewModel.selectedConnections.collectAsState()
    val includeRoutes by viewModel.includeRoutes.collectAsState()
    val deleteTrackAfterCompletion by viewModel.deleteTrackAfterCompletion.collectAsState()

    val selectedItem = viewModel.selectedTrack.collectAsState().value
    val selectedTrackIndex = viewModel.selectedTrackIndex.collectAsState().value
    val players = scoreViewModel.players.collectAsState().value
    val phase by viewModel.phase.collectAsState()
    val isSecondSpinnerReady by viewModel.isSecondSpinnerReady.collectAsState()
    val destinationItems by viewModel.destinationItems.collectAsState()
    val destinationTargetIndex by viewModel.destinationTargetIndex.collectAsState()

    val confettiColors = listOf(0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFFFFFF00.toInt(), 0xFFFF00FF.toInt())

    val partyLeft = Party(
        speed = 40f,
        maxSpeed = 100f,
        angle = 300,
        spread = 10,
        colors = confettiColors,
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(300),
        position = Position.Relative(0.0, 0.6)
    )

    val partyRight = Party(
        speed = 40f,
        maxSpeed = 100f,
        angle = 240,
        spread = 10,
        colors = confettiColors,
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(300),
        position = Position.Relative(1.0, 0.6)
    )

    var showConfetti by remember { mutableStateOf(false) }
    var showResultActions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.resetCourse()
    }

    LaunchedEffect(phase) {
        if (phase != Phase.SPINNING_TRACK && phase != Phase.DUAL_SPINNER) {
            showResultActions = false
        }
    }

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

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {

        Icon(
            Icons.Default.Settings,
            contentDescription = "Paramètres",
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 10.dp)
                .clickable {
                    onSettings()
                }
        )

        Log.d("lubenard" , "phase is $phase")
        when (phase) {

            Phase.SELECTION_CUBE -> {
                val totalPool = selectedTracks.size + (if (includeRoutes) selectedConnections.size else 0)
                SelectionCubePhase(
                    totalPool = totalPool,
                    deleteTrackAfterCompletion = deleteTrackAfterCompletion,
                    onPickRandomMap = { viewModel.pickRandomMap() },
                    onToggleDeleteTrack = { viewModel.updateDeleteTrackAfterCompletion(it) },
                )
            }

            Phase.SPINNING_TRACK -> {
                SpinningTrackPhase(
                    selectedTracks = selectedTracks,
                    selectedTrackIndex = selectedTrackIndex,
                    selectedItem = selectedItem,
                    showResultActions = showResultActions,
                    hasPlayers = players.isNotEmpty(),
                    onSpinFinished = {
                        showResultActions = true
                        selectedItem?.let { viewModel.completeRace(it) }
                        showConfetti = true
                    },
                    onScoreSelection = onScoreSelection,
                    onRecommencer = {
                        if (deleteTrackAfterCompletion) {
                            Log.d("lubenard", "$selectedItem")
                            viewModel.deleteCircuit(selectedItem)
                        }
                        viewModel.resetCourse()
                    },
                )
            }

            Phase.DUAL_SPINNER -> {
                DualSpinnerPhase(
                    selectedTracks = selectedTracks,
                    selectedTrackIndex = selectedTrackIndex,
                    selectedItem = selectedItem,
                    destinationItems = destinationItems,
                    destinationTargetIndex = destinationTargetIndex,
                    isSecondSpinnerReady = isSecondSpinnerReady,
                    showResultActions = showResultActions,
                    hasPlayers = players.isNotEmpty(),
                    onFirstSpinFinished = { viewModel.pickRandomDestination() },
                    onSecondSpinFinished = {
                        showResultActions = true
                        selectedItem?.let { viewModel.completeRace(it) }
                        showConfetti = true
                    },
                    onScoreSelection = onScoreSelection,
                    onRecommencer = {
                        if (deleteTrackAfterCompletion) {
                            Log.d("lubenard", "$selectedItem")
                            viewModel.deleteCircuit(selectedItem)
                        }
                        viewModel.resetCourse()
                    },
                )
            }

        }
    }
}
