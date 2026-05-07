package com.escatrag.mkworldrandomiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.escatrag.mkworldrandomiser.ui.screens.MainScreen
import com.escatrag.mkworldrandomiser.ui.screens.ManagePlayersScreen
import com.escatrag.mkworldrandomiser.ui.screens.MonthlyScoreScreen
import com.escatrag.mkworldrandomiser.ui.screens.RaceResultScreen
import com.escatrag.mkworldrandomiser.ui.screens.SettingsScreen
import com.escatrag.mkworldrandomiser.ui.screens.TrackSelectionScreen
import com.escatrag.mkworldrandomiser.viewmodels.ScoreViewModel
import com.escatrag.mkworldrandomiser.viewmodels.SettingsViewModel
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val vm: TrackViewModel = viewModel()
            val scoreVm: ScoreViewModel = viewModel()
            val settingsVM: SettingsViewModel = viewModel()

            // État pour l'onglet sélectionné dans la barre du bas
            var selectedTab by remember { mutableIntStateOf(0) }

            Scaffold(
                // --- AJOUT DE LA BARRE DE NAVIGATION ---
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                navController.navigate("main")
                            },
                            label = { Text("Aléatoire") },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                navController.navigate("selection")
                            },
                            label = { Text("Circuits") },
                            icon = { Icon(Icons.Default.Map, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = {
                                selectedTab = 2
                                navController.navigate("score")
                            },
                            label = { Text("Scores") },
                            icon = { Icon(Icons.Default.Groups, contentDescription = null) }
                        )
                    }
                }
            ) { padding ->

                NavHost(navController, startDestination = "main") {

                    composable("main") {
                        MainScreen(
                            viewModel = vm,
                            settingsViewModel = settingsVM,
                            scoreViewModel = scoreVm,
                            padding = padding,
                            onGenerate = { _ ->
                                vm.generateCourse()
                                vm.pickRandomTeams()
                            },
                            onSettings = { navController.navigate("settings") },
                            onScoreSelection = { navController.navigate("scoreSelection") },
                        )
                    }

                    composable("selection") {
                        TrackSelectionScreen(vm, padding)
                    }

                    composable("settings") {
                        SettingsScreen(vm, settingsVM, padding)
                    }

                    composable("scoreSelection") {
                        RaceResultScreen(scoreVm, { navController.popBackStack() }, padding)
                    }

                    composable("score") {
                        MonthlyScoreScreen(scoreVm, navController, padding)
                    }

                    composable("players") {
                        ManagePlayersScreen(scoreVm, onBack = { navController.popBackStack() }, padding)
                    }
                }
            }
        }
    }
}