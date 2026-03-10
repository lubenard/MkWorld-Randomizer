package com.escatrag.mkworldrandomiser

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val vm: TrackViewModel = viewModel()

            NavHost(navController, startDestination = "main") {

                composable("main") {
                    MainScreen(
                        viewModel = vm,
                        onGenerate = { delay ->
                            vm.generateCourse(delay)
                        },
                        onNavigate = {
                            navController.navigate("selection")
                        },
                        onSettings = {
                            navController.navigate("settings")
                        }
                    )
                }

                composable("selection") {
                    TrackSelectionScreen(vm, navController)
                }

                composable("settings") {
                    Text("Hello World")
                    val bias = vm.generationBias.collectAsState()
                    TestSlider(bias.value) {
                        vm.updateGenerationBias(it)
                    }
                }
            }
        }
    }
}