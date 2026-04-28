package com.escatrag.mkworldrandomiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.escatrag.mkworldrandomiser.ui.screens.MainScreen
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

            NavHost(navController, startDestination = "main") {

                composable("main") {
                    MainScreen(
                        viewModel = vm,
                        settingsViewModel = settingsVM,
                        onGenerate = { delay ->
                            vm.generateCourse()
                            vm.pickRandomTeams()
                        },
                        onNavigate = { navController.navigate("selection") },
                        onSettings = { navController.navigate("settings") },
                        onScore = { navController.navigate("score") },
                        onScoreSelection = { navController.navigate("scoreSelection") },
                    ) { navController.navigate("teams") }
                }

                composable("selection") {
                    TrackSelectionScreen(vm, navController)
                }

                composable("settings") {
                    SettingsScreen(vm, settingsVM)
                }

                composable("scoreSelection") {
                    RaceResultScreen(scoreVm, {
                        navController.popBackStack()
                    })
                }

                composable("score") {
                    MonthlyScoreScreen(scoreVm, navController)
                }
            }
        }
    }
}