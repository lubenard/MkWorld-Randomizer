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
import com.escatrag.mkworldrandomiser.backend.toTrackItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Phase { SELECTION_CUBE, SPINNING_TRACK, DUAL_SPINNER }

class TrackViewModel : ViewModel() {

    // Liste des circuits sélectionnés (TRACK uniquement)
    private val _selectedTracks = MutableStateFlow(TrackRepository.trackItems)
    val selectedTracks: StateFlow<List<TrackCombo>> = _selectedTracks

    // Liste des trajets sélectionnés (CONNECTION uniquement)
    private val _selectedConnections = MutableStateFlow<List<TrackCombo>>(emptyList())
    val selectedConnections: StateFlow<List<TrackCombo>> = _selectedConnections

    // Ensemble des circuits déjà tombés — ne peuvent plus réapparaître en tant que circuit seul
    private val _usedCircuits = MutableStateFlow<Set<TrackItems>>(emptySet())
    val usedCircuits: StateFlow<Set<TrackItems>> = _usedCircuits

    // All tracks availables: Used for Selection tracks (will include routes if selected in SelectionScreen),
    // but they will not be selected (tho available for selection)
    private val _allTracksAvailable = MutableStateFlow(TrackRepository.trackItems)
    val allTracksAvailable: StateFlow<List<TrackCombo>> = _allTracksAvailable

    // Randomly Selected Item
    // -1 is for infinite loop
    var selectedTrackIndex = MutableStateFlow(-1)

    // Option to delete One track from the selectedTracks after completion (after selecting a random track)
    private val _deleteTrackAfterCompletion = MutableStateFlow(true)
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

    // Phase actuelle de l'écran
    private val _phase = MutableStateFlow(Phase.SELECTION_CUBE)
    val phase: StateFlow<Phase> = _phase

    // Option to include routes between tracks (UI toggle state)
    private val _includeRoutes = MutableStateFlow(false)
    val includeRoutes: StateFlow<Boolean> = _includeRoutes

    // Stocke les destinations éligibles pour la map choisie (utilisé entre pickRandomMap et pickRandomDestination)
    private var pendingDestinations: List<TrackItems>? = null

    // Liste des destinations possibles formatées en TrackCombo pour le second spinner
    private val _destinationItems = MutableStateFlow<List<TrackCombo>>(emptyList())
    val destinationItems: StateFlow<List<TrackCombo>> = _destinationItems

    // Indique si le second spinner est prêt (phase 2 du double spinner)
    private val _isSecondSpinnerReady = MutableStateFlow(false)
    val isSecondSpinnerReady: StateFlow<Boolean> = _isSecondSpinnerReady

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

    fun completeRace(result: TrackCombo) {
        viewModelScope.launch {
            delay(3000L)
            if (result.type == TrackComboType.CONNECTION) {
                _selectedConnections.value = _selectedConnections.value.filter { it != result }
                Log.d("lubenard", "completeRace: trajet retire de _selectedConnections — ${result.start.text} -> ${result.end?.text}")
            } else {
                val item = result.start.toTrackItem()
                if (item != null) {
                    _usedCircuits.value = _usedCircuits.value + item
                }
                _selectedTracks.value = _selectedTracks.value.filter { it != result }
                _selectedConnections.value = _selectedConnections.value.filter { it.start != result.start }
                Log.d("lubenard", "completeRace: circuit retire de _selectedTracks — ${result.start.text}")
            }
        }
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

        val shouldAttemptRoute = when (_generationBias.value) {
            0f -> false
            100f -> true
            else -> Random.nextBoolean()
        }
        Log.d("lubenard", "pickRandomMap: shouldAttemptRoute=$shouldAttemptRoute (bias=${_generationBias.value})")

        pendingDestinations = null

        // Toujours tirer une map depuis la pool des circuits (filtrée par _usedCircuits)
        val available = currentSelectedTracks.filter {
            it.start.toTrackItem() !in _usedCircuits.value
        }
        if (available.isEmpty()) {
            Log.w("lubenard", "pickRandomMap: tous les circuits deja joues, abandon")
            return
        }
        val randomTrack = available.random().start
        Log.d("lubenard", "pickRandomMap: depart choisi — ${randomTrack.text}")

        // Si mode trajet, vérifier si cette map a des trajets activés
        if (shouldAttemptRoute) {
            val trackEnum = randomTrack.toTrackItem()
            val allPossible = trackEnum?.let { TrackRepository.connections[it] } ?: emptyList()
            val enabled = allPossible.filter { dest ->
                _selectedConnections.value.any { c ->
                    c.start == randomTrack && c.end == dest.map()
                }
            }
            if (enabled.isNotEmpty()) {
                pendingDestinations = enabled
                Log.d("lubenard", "pickRandomMap: ${enabled.size} destination(s) disponible(s) pour ${randomTrack.text}")

            } else {
                Log.d("lubenard", "pickRandomMap: aucun trajet active pour ${randomTrack.text}, course simple")
            }
        }

        val displayIndex = currentSelectedTracks.indexOfFirst {
            it.start == randomTrack && it.type == TrackComboType.TRACK
        }.let { if (it >= 0) it else currentSelectedTracks.indexOfFirst { c -> c.start == randomTrack } }
        Log.d("lubenard", "pickRandomMap: displayIndex=$displayIndex")

        selectedTrackIndex.value = displayIndex
        _selectedTrack.value = TrackCombo(start = randomTrack, type = TrackComboType.TRACK)
        _destinationTargetIndex.value = -1

        if (pendingDestinations != null) {
            _destinationItems.value = pendingDestinations!!.map { dest ->
                TrackCombo(start = dest.map(), type = TrackComboType.TRACK)
            }
            Log.d("lubenard", "pickRandomMap: destinationItems popule avec ${_destinationItems.value.size} entree(s)")
        } else {
            _destinationItems.value = emptyList()
            Log.d("lubenard", "pickRandomMap: destinationItems vide (pas de trajet)")
        }

        _phase.value = if (pendingDestinations != null) Phase.DUAL_SPINNER else Phase.SPINNING_TRACK
        Log.d("lubenard", "pickRandomMap: FIN — phase=${_phase.value}, selectedTrack=${_selectedTrack.value?.start?.text}")
    }

    fun pickRandomDestination(): Boolean {
        val dests = pendingDestinations ?: emptyList()
        pendingDestinations = null

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
        _isSecondSpinnerReady.value = true

        Log.d("lubenard", "Destination sélectionnée : ${randomDest.nameRes}")
        return true
    }

    fun resetCourse() {
        selectedTrackIndex.value = -1
        _destinationTargetIndex.value = -1
        pendingDestinations = null
        _destinationItems.value = emptyList()
        _isSecondSpinnerReady.value = false
        _phase.value = Phase.SELECTION_CUBE
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        _usedCircuits.value = emptySet()
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
        Log.d("lubenard", "Trying to delete track $circuit -> ${_deleteTrackAfterCompletion.value}")
        if (_deleteTrackAfterCompletion.value) {
            if (circuit.type == TrackComboType.CONNECTION) {
                _selectedConnections.value = _selectedConnections.value.filter { it != circuit }
            } else {
                _selectedTracks.value = _selectedTracks.value.filter { it != circuit }
                _selectedConnections.value = _selectedConnections.value.filter { it.start != circuit.start }
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
