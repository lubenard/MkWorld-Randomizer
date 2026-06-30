package com.escatrag.mkworldrandomiser.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escatrag.mkworldrandomiser.backend.Track
import com.escatrag.mkworldrandomiser.backend.TrackCombo
import com.escatrag.mkworldrandomiser.backend.TrackComboType
import com.escatrag.mkworldrandomiser.backend.TrackItems
import com.escatrag.mkworldrandomiser.backend.TrackRepository
import com.escatrag.mkworldrandomiser.backend.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class TrackViewModel : ViewModel() {

    // Liste des circuits sélectionnés (TRACK uniquement)
    private val _selectedTracks = MutableStateFlow(TrackRepository.trackItems)
    val selectedTracks: StateFlow<List<TrackCombo>> = _selectedTracks

    // Liste des trajets sélectionnés (CONNECTION uniquement)
    private val _selectedConnections = MutableStateFlow<List<TrackCombo>>(emptyList())
    val selectedConnections: StateFlow<List<TrackCombo>> = _selectedConnections

    // All tracks availables: Used for Selection tracks (will include routes if selected in SelectionScreen),
    // but they will not be selected (tho available for selection)
    private val _allTracksAvailable = MutableStateFlow(TrackRepository.trackItems)
    val allTracksAvailable: StateFlow<List<TrackCombo>> = _allTracksAvailable

    // Randomly Selected Item
    // -1 is for infinite loop
    var selectedTrackIndex = MutableStateFlow(-1)

    // Option to delete One track from the selectedTracks after completion (after selecting a random track)
    private val _deleteTrackAfterCompletion = MutableStateFlow(false)
    val deleteTrackAfterCompletion: StateFlow<Boolean> = _deleteTrackAfterCompletion

    // Set the selected track or trajet
    private val _selectedTrack = MutableStateFlow<TrackCombo?>(null)
    val selectedTrack:MutableStateFlow<TrackCombo?> = _selectedTrack

    // generation bias: 0 for only tracks, 50 for random between tracks & connection, 100 for connections only
    private val _generationBias = MutableStateFlow(0F)
    val generationBias: StateFlow<Float> = _generationBias

    // Index de la destination dans TrackRepository.trackItems pour le second spinner
    private val _destinationTargetIndex = MutableStateFlow(-1)
    val destinationTargetIndex: StateFlow<Int> = _destinationTargetIndex

    // Indique si on doit afficher le double spinner (phase 2 pour la destination)
    private val _showTwoPhaseSpinner = MutableStateFlow(false)
    val showTwoPhaseSpinner: StateFlow<Boolean> = _showTwoPhaseSpinner

    // Option to include routes between tracks (UI toggle state)
    private val _includeRoutes = MutableStateFlow(false)
    val includeRoutes: StateFlow<Boolean> = _includeRoutes

    // Stocke les destinations éligibles pour la map choisie (utilisé entre pickRandomMap et pickRandomDestination)
    private var pendingDestinations: List<TrackItems>? = null

    // Indique si un trajet est en attente après pickRandomMap (pour afficher 2 spinners)
    private val _hasPendingDestination = MutableStateFlow(false)
    val hasPendingDestination: StateFlow<Boolean> = _hasPendingDestination

    // Liste des destinations possibles formatées en TrackCombo pour le second spinner
    private val _destinationItems = MutableStateFlow<List<TrackCombo>>(emptyList())
    val destinationItems: StateFlow<List<TrackCombo>> = _destinationItems

    fun toggleTrack(id: TrackCombo) {
        val trackItemsToToggle = _allTracksAvailable.value.find { it == id }
        if (trackItemsToToggle == null) return

        _selectedTracks.update { current ->
            if (current.contains(trackItemsToToggle)) {
                current - trackItemsToToggle
            } else {
                current + trackItemsToToggle
            }
        }
    }

    fun getConnectionsForTrack(trackItem: TrackItems): List<TrackItems> {
        return TrackRepository.connections[trackItem] ?: emptyList()
    }

    fun setIncludeRoutes(value: Boolean) {
        _includeRoutes.value = value
    }

    fun toggleConnection(parent: Track, childItem: TrackItems) {
        val combo = TrackCombo(
            start = parent,
            end = childItem.map(),
            type = TrackComboType.CONNECTION
        )

        if (_selectedConnections.value.contains(combo)) {
            _selectedConnections.value = _selectedConnections.value.minus(combo)
        } else {
            _selectedConnections.value = _selectedConnections.value.plus(combo)
        }
    }

    // Étape 1 : choisir une map aléatoire et vérifier si un trajet est possible
    fun pickRandomMap() {
        val currentSelectedTracks = _selectedTracks.value
        if (currentSelectedTracks.isEmpty()) return

        val uniqueStarts = currentSelectedTracks.map { it.start }.distinct()
        if (uniqueStarts.isEmpty()) return
        val randomTrack = uniqueStarts.random()

        val shouldIncludeRoute = when (_generationBias.value) {
            0f -> false
            100f -> true
            else -> Random.nextBoolean()
        }

        pendingDestinations = null
        _hasPendingDestination.value = false
        if (shouldIncludeRoute) {
            val startEnum = TrackItems.entries.find { it.nameRes == randomTrack.text }
            val allPossible = startEnum?.let { TrackRepository.connections[it] } ?: emptyList()
            val enabled = allPossible.filter { dest ->
                _selectedConnections.value.any { c ->
                    c.start == randomTrack && c.end == dest.map()
                }
            }
            if (enabled.isNotEmpty()) {
                pendingDestinations = enabled
                _hasPendingDestination.value = true
            }
        }

        val displayIndex = currentSelectedTracks.indexOfFirst {
            it.start == randomTrack && it.type == TrackComboType.TRACK
        }.let { if (it >= 0) it else currentSelectedTracks.indexOfFirst { c -> c.start == randomTrack } }

        selectedTrackIndex.value = displayIndex
        _selectedTrack.value = TrackCombo(start = randomTrack, type = TrackComboType.TRACK)
        _destinationTargetIndex.value = -1
        _showTwoPhaseSpinner.value = false

        Log.d("lubenard", "Map sélectionnée : ${randomTrack.text}")
    }

    // Étape 2 : choisir une destination aléatoire pour le trajet (appelé après la Phase 1 du spinner)
    fun pickRandomDestination(): Boolean {
        val dests = pendingDestinations ?: emptyList()
        pendingDestinations = null
        _hasPendingDestination.value = false

        if (dests.isEmpty()) return false

        val startTrack = _selectedTrack.value?.start ?: return false
        val randomDest = dests.random()

        _selectedTrack.value = TrackCombo(
            start = startTrack,
            end = randomDest.map(),
            type = TrackComboType.CONNECTION
        )
        _destinationTargetIndex.value = _destinationItems.value.indexOfFirst {
            it.start == randomDest.map()
        }
        _showTwoPhaseSpinner.value = true

        Log.d("lubenard", "Destination sélectionnée : ${randomDest.nameRes}")
        return true
    }

    fun generateCourse() {
        pickRandomMap()
        if (_hasPendingDestination.value) {
            _destinationItems.value = pendingDestinations?.map { dest ->
                TrackCombo(start = dest.map(), type = TrackComboType.TRACK)
            } ?: emptyList()
        } else {
            _destinationItems.value = emptyList()
        }
    }

    fun resetCourse() {
        selectedTrackIndex.value = -1
        _destinationTargetIndex.value = -1
        _showTwoPhaseSpinner.value = false
        pendingDestinations = null
        _hasPendingDestination.value = false
        _destinationItems.value = emptyList()
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        _selectedTracks.value = TrackRepository.trackItems
        if (includeRoutes) {
            _selectedConnections.value = transformConnectionsToList(TrackRepository.connections)
        } else {
            _selectedConnections.value = emptyList()
        }
    }

    fun clearAllTracks() {
        _selectedTracks.value = emptyList()
        _selectedConnections.value = emptyList()
    }

    fun transformConnectionsToList(connections: Map<TrackItems, List<TrackItems>>): List<TrackCombo> {
        return connections.flatMap { (parent, children) ->
            children.map { child ->
                TrackCombo(
                    start = parent.map(),
                    end = child.map(),
                    type = TrackComboType.CONNECTION,
                )
            }
        }
    }

    fun deleteCircuit(circuit: TrackCombo?) {
        if (circuit == null) {
            Log.e("lubenard", "circuit is null !!")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("lubenard", "Trying to delete track $circuit -> ${_deleteTrackAfterCompletion.value}")
            if (_deleteTrackAfterCompletion.value && !_showTwoPhaseSpinner.value) {
                if (circuit.type == TrackComboType.CONNECTION) {
                    _selectedConnections.value = _selectedConnections.value.filter { it != circuit }
                } else {
                    _selectedTracks.value = _selectedTracks.value.filter { it != circuit }
                }
            }
        }
    }

    fun updateDeleteTrackAfterCompletion(it: Boolean) {
        _deleteTrackAfterCompletion.value = it
    }

    fun updateGenerationBias(newValue: Float) {
        _generationBias.value = newValue
    }
}
