package com.escatrag.mkworldrandomiser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.escatrag.mkworldrandomiser.ui.screens.MainScreen
import com.escatrag.mkworldrandomiser.ui.screens.MonthlyScoreScreen
import com.escatrag.mkworldrandomiser.ui.screens.RaceResultScreen
import com.escatrag.mkworldrandomiser.ui.screens.SettingsScreen
import com.escatrag.mkworldrandomiser.ui.screens.TrackSelectionScreen
import com.escatrag.mkworldrandomiser.ui.theme.MkWorldRandomiserTheme
import com.escatrag.mkworldrandomiser.viewmodels.ScoreViewModel
import com.escatrag.mkworldrandomiser.viewmodels.SettingsViewModel
import com.escatrag.mkworldrandomiser.viewmodels.ThemeMode
import com.escatrag.mkworldrandomiser.viewmodels.TrackViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settingsVM: SettingsViewModel = viewModel()
            val themeMode by settingsVM.themeMode.collectAsState()
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            MkWorldRandomiserTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val trackViewModel: TrackViewModel = viewModel()
                val scoreViewModel: ScoreViewModel = viewModel()

                // Onglet de la barre du bas synchronisé avec la route courante
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val selectedTab = when (navBackStackEntry?.destination?.route) {
                    "selection" -> 1
                    "score" -> 2
                    else -> 0 // "main", "settings", "scoreSelection"
                }

                Scaffold(
                // --- AJOUT DE LA BARRE DE NAVIGATION ---
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = {
                                navController.navigate("main")
                            },
                            label = { Text(stringResource(R.string.aleatoire)) },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = {
                                navController.navigate("selection")
                            },
                            label = { Text(stringResource(R.string.circuits)) },
                            icon = { Icon(Icons.Default.Map, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = {
                                navController.navigate("score")
                            },
                            label = { Text(stringResource(R.string.scores)) },
                            icon = { Icon(Icons.Default.Groups, contentDescription = null) }
                        )
                    }
                }
            ) { padding ->

                // Background image
                Image(
                    modifier = Modifier.fillMaxSize().alpha(0.7f),
                    painter = painterResource(R.drawable.map),
                    contentScale = ContentScale.Crop,
                    contentDescription = "",
                )

                NavHost(navController, startDestination = "main") {

                    composable("main") {
                        MainScreen(
                            viewModel = trackViewModel,
                            settingsViewModel = settingsVM,
                            scoreViewModel = scoreViewModel,
                            padding = padding,
                            onGenerate = {},
                            onSettings = { navController.navigate("settings") },
                            onScoreSelection = { navController.navigate("scoreSelection") },
                        )
                    }

                    composable("selection") {
                        TrackSelectionScreen(trackViewModel, padding)
                    }

                    composable("settings") {
                        SettingsScreen(trackViewModel, settingsVM, padding)
                    }

                    composable("scoreSelection") {
                        RaceResultScreen(scoreViewModel, trackViewModel,{ navController.popBackStack() }, padding)
                    }

                    composable("score") {
                        MonthlyScoreScreen(scoreViewModel, navController, padding)
                    }
                }
            }
            }
        }
    }
}
