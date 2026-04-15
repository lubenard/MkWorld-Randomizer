package com.escatrag.mkworldrandomiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.escatrag.mkworldrandomiser.backend.SettingsViewModel
import com.escatrag.mkworldrandomiser.backend.TrackViewModel
import com.escatrag.mkworldrandomiser.ui.screens.MainScreen
import com.escatrag.mkworldrandomiser.ui.screens.PlayerGroupingScreen
import com.escatrag.mkworldrandomiser.ui.screens.SettingsScreen
import com.escatrag.mkworldrandomiser.ui.screens.TrackSelectionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val vm: TrackViewModel = viewModel()
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
                        onTeam = { navController.navigate("teams") }
                    )
                }

                composable("selection") {
                    TrackSelectionScreen(vm, navController)
                }

                composable("settings") {
                    SettingsScreen(vm, settingsVM)
                }

                composable("teams") {
                    //GroupingScreen(vm)
                    PlayerGroupingScreen(vm)
                }
            }
        }
    }
}