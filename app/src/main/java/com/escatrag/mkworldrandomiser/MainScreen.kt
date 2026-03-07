package com.escatrag.mkworldrandomiser

import SpinningWheel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TrackViewModel,
    onGenerate: (delay: Long) -> Unit,
    onNavigate: () -> Unit,
    onSettings: () -> Unit
) {

    val selectedTracks by viewModel.selectedTracks.collectAsState()
    val deleteTrackAfterCompletion by viewModel.deleteTrackAfterCompletion.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()

    var mexpanded by remember { mutableStateOf(false) }

    var lastClickTime by remember { mutableLongStateOf(0L) }

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
                    // 2. L'icône des trois points
                    IconButton(onClick = { mexpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }

                    // 3. Le menu qui s'ouvre
                    DropdownMenu(
                        expanded = mexpanded,
                        onDismissRequest = { mexpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Paramètres") },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            },
                            onClick = {
                                mexpanded = false
                                onSettings()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->

        val dialogString = viewModel.showResultPopup.collectAsState()

        if (dialogString.value != null) {
            AlertDialog(
                onDismissRequest = { viewModel.setPopupDisplay(null) },
                title = {
                    Image(painter = painterResource())
                    Text("Circuit Arrivé ! -> ${dialogString.value}")
                },
                text = { Text("Sélectionné.") },
                confirmButton = {
                    Button(onClick = { viewModel.setPopupDisplay(null) }) {
                        Text("OK")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            SpinningWheel(
                items = selectedTracks,
                targetIndex = selectedItem,
                placeholder = "Merci de choisir au moins une carte",
                onItemSelected = {
                    viewModel.setPopupDisplay(it)
                    if (viewModel.deleteTrackAfterCompletion.value)
                        viewModel.deleteCircuit(it)
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

            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Séléctioner des trajet")
            }

            var lastClickTime by remember { mutableLongStateOf(0L) }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                val currentTime = System.currentTimeMillis()
                onGenerate(currentTime - lastClickTime)
                lastClickTime = currentTime
            }) {
                Text("Choisir un trajet")
            }
        }
    }
}