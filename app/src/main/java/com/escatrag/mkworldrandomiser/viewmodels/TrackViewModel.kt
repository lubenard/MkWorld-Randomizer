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
                _selectedConnections.value = _selectedConnections.value.filter { it.start != trackItemsToToggle.start }
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

    fun pickRandomMap() {
        Log.d("lubenard", "pickRandomMap: debut — bias=${_generationBias.value}, selectedTracks=${_selectedTracks.value.size}, selectedConnections=${_selectedConnections.value.size}")
        val currentSelectedTracks = _selectedTracks.value
        if (currentSelectedTracks.isEmpty()) {
            Log.w("lubenard", "pickRandomMap: selectedTracks vide, abandon")
            return
        }
        val randomTrack = currentSelectedTracks.random().start
        Log.d("lubenard", "pickRandomMap: depart choisi — ${randomTrack.text}")

        val shouldIncludeRoute = when (_generationBias.value) {
            0f -> false
            100f -> true
            else -> Random.nextBoolean()
        }
        Log.d("lubenard", "pickRandomMap: shouldIncludeRoute=$shouldIncludeRoute (bias=${_generationBias.value})")

        pendingDestinations = null
        _hasPendingDestination.value = false
        if (shouldIncludeRoute) {
            val startEnum = TrackItems.entries.find { it.nameRes == randomTrack.text }
            val allPossible = startEnum?.let { TrackRepository.connections[it] } ?: emptyList()
            Log.d("lubenard", "pickRandomMap: startEnum=$startEnum, allPossible=${allPossible.size} connexions dans le graphe")
            val enabled = allPossible.filter { dest ->
                _selectedConnections.value.any { c ->
                    c.start == randomTrack && c.end == dest.map()
                }
            }
            Log.d("lubenard", "pickRandomMap: trajets actives depuis ce depart=${enabled.size}")
            if (enabled.isNotEmpty()) {
                pendingDestinations = enabled
                _hasPendingDestination.value = true
                Log.d("lubenard", "pickRandomMap: trajet possible — destinations en attente: ${enabled.size}")
            } else {
                Log.d("lubenard", "pickRandomMap: aucun trajet actif depuis ce depart, affichage circuit seul")
                _showTwoPhaseSpinner.value = false
            }
        } else {
            Log.d("lubenard", "pickRandomMap: pas de trajet demande, affichage circuit seul")
            _showTwoPhaseSpinner.value = false
        }

        val displayIndex = currentSelectedTracks.indexOfFirst {
            it.start == randomTrack && it.type == TrackComboType.TRACK
        }.let { if (it >= 0) it else currentSelectedTracks.indexOfFirst { c -> c.start == randomTrack } }
        Log.d("lubenard", "pickRandomMap: displayIndex=$displayIndex")

        selectedTrackIndex.value = displayIndex
        _selectedTrack.value = TrackCombo(start = randomTrack, type = TrackComboType.TRACK)
        _destinationTargetIndex.value = -1

        Log.d("lubenard", "pickRandomMap: selectedTrack mis a jour, showTwoPhaseSpinner=false")

        if (_hasPendingDestination.value) {
            _destinationItems.value = pendingDestinations?.map { dest ->
                TrackCombo(start = dest.map(), type = TrackComboType.TRACK)
            } ?: emptyList()
            Log.d("lubenard", "pickRandomMap: destinationItems popule avec ${_destinationItems.value.size} entree(s)")
        } else {
            _destinationItems.value = emptyList()
            Log.d("lubenard", "pickRandomMap: destinationItems vide (pas de trajet)")
        }

        Log.d("lubenard", "pickRandomMap: FIN — hasPendingDestination=${_hasPendingDestination.value}, selectedTrack=${_selectedTrack.value?.start?.text}")
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

    fun resetCourse() {
        selectedTrackIndex.value = -1
        _destinationTargetIndex.value = -1
        _showTwoPhaseSpinner.value = false
        pendingDestinations = null
        _hasPendingDestination.value = false
        _destinationItems.value = emptyList()
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        if (includeRoutes) {
            _selectedConnections.value = transformConnectionsToList(TrackRepository.connections)
        } else {
            _selectedConnections.value = mutableListOf()
        }
        _selectedTracks.value = TrackRepository.trackItems
    }

    fun clearAllTracks() {
        _selectedTracks.value = mutableListOf()
        _selectedConnections.value = mutableListOf()
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
