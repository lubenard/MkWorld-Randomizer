package com.escatrag.mkworldrandomiser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escatrag.mkworldrandomiser.backend.Track
import com.escatrag.mkworldrandomiser.backend.TrackItems
import com.escatrag.mkworldrandomiser.backend.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class TrackViewModel : ViewModel() {

    // Selected tracks that will be used for random generation
    private val _selectedTracks = MutableStateFlow(TrackRepository.trackItems)
    val selectedTracks: StateFlow<List<TrackItems>> = _selectedTracks


    // Selected tracks that will be used for random generation
    private val _testSelectedTracks = MutableStateFlow(TrackRepository.trackItems.map())
    val testSelectedTracks: StateFlow<List<Track>> = _testSelectedTracks

    // Randomly Selected Item
    // -1 is for infinite loop
    var selectedTrack = MutableStateFlow(-1)
    var selectedEndTrackItems = MutableStateFlow<TrackItems?>(null)

    // All tracks availables: Used for Selection tracks (will include routes if selected in SelectionScreen),
    // but they will not be selected (tho available for selection)
    private val _allTracksAvailable = MutableStateFlow(TrackRepository.trackItems)
    val allTracksAvailable: StateFlow<List<TrackItems>> = _allTracksAvailable

    // Option to include routes between tracks
    private val _includeRoutes = MutableStateFlow(false)
    var includeRoutes: StateFlow<Boolean> = _includeRoutes

    // Option to delete One track from the selectedTracks after completion (after selecting a random track)
    private val _deleteTrackAfterCompletion = MutableStateFlow(false)
    val deleteTrackAfterCompletion: StateFlow<Boolean> = _deleteTrackAfterCompletion

    // Show the result popup.... or not !
    private val _showResultPopup = MutableStateFlow<TrackItems?>(null)
    val showResultPopup: StateFlow<TrackItems?> = _showResultPopup

    // generation bias: 0 for only tracks, 50 for random between tracks & connection, 100 for connections only
    private val _generationBias = MutableStateFlow(0F)
    val generationBias: StateFlow<Float> = _generationBias

    // Variables to manages teams
    private val _groups = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val groups: StateFlow<List<Pair<String, String>>> = _groups

    // Randomly selected teams
    private val _selectedRandomPlayers = MutableStateFlow<List<String>>(emptyList())
    val selectedRandomTeams: StateFlow<List<String>> = _selectedRandomPlayers

    private val _teamIndex = MutableStateFlow(false)
    val teamIndex: StateFlow<Boolean> = _teamIndex

    fun toggleTrack(id: String) {
        // 1. On cherche le vrai objet Track qui correspond à cet ID
        val trackItemsToToggle = TrackItems.entries.find { it.name == id }

        // Si on ne trouve pas le circuit (ID invalide), on arrête tout pour éviter un crash
        if (trackItemsToToggle == null) return

        // 2. On met à jour la liste avec le VRAI objet Track
        _selectedTracks.update { current ->
            if (current.contains(trackItemsToToggle)) {
                current - trackItemsToToggle
            } else {
                current + trackItemsToToggle
            }
        }
    }

    fun setIncludeRoutes(value: Boolean) {
        _includeRoutes.value = value
    }

    // Generate a route based on selectedTracks
    fun generateCourse(delay: Long) {
        if (_selectedTracks.value.isNotEmpty()) {
            val selectedTrackIndex = Random.nextInt(_selectedTracks.value.size)
            if (_generationBias.value == 0f) { _includeRoutes.value = false }
            else if (_generationBias.value == 100f) { _includeRoutes.value = true }
            else { _includeRoutes.value = Random.nextBoolean() }
            if (_includeRoutes.value) {
                Log.d("lubenard", "onClick1 _includeRoutes == true / selectedTrackIndex $selectedTrackIndex")
                val mSelectedItem = _selectedTracks.value[selectedTrackIndex]
                val circuit = TrackRepository.connections[mSelectedItem]
                selectedEndTrackItems.value = TrackRepository.connections[mSelectedItem]!![Random.nextInt(circuit!!.size)]
                Log.d("lubenard", "onClick 2.5 ${selectedEndTrackItems.value}")
            }
            selectedTrack.value = selectedTrackIndex
            Log.d("lubenard", "onClick2 ${selectedTrack.value} ${selectedTracks.value} -> ${_selectedTracks.value.get(selectedTrack.value)} / $delay")
        }
    }

    fun resetCourse() {
        selectedTrack.value = -1
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        _selectedTracks.value = emptyList()
        _selectedTracks.value = TrackRepository.trackItems
        if (includeRoutes) {
            val addConnectionToList = _selectedTracks.value.toMutableList()
            addConnectionToList.addAll(transformConnectionsToList(TrackRepository.connections))
            _selectedTracks.value = addConnectionToList
        }
    }

    fun clearAllTracks() {
        _selectedTracks.value = emptyList()
    }

    fun transformConnectionsToList(connections: Map<TrackItems, List<TrackItems>>): List<TrackItems> {
        return emptyList()
        /*return connections.flatMap { (depart, destinations) ->
            destinations.map { destination ->
                TrackItem("$depart > $destination")
            }
        }*/
    }

    fun deleteCircuit(circuit: String, skipScrollDelay: Long = 3500) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("lubenard", "Trying to delete track $circuit -> ${_deleteTrackAfterCompletion.value}")
            if (_deleteTrackAfterCompletion.value && !_includeRoutes.value) {
                Log.d("lubenard", "Deleting track $circuit}")
                val tempValue = _selectedTracks.value.toMutableList()
                delay(skipScrollDelay)
                Log.d("lubenard", "Updating selectedTracks without $circuit")
                _selectedTracks.value = tempValue.filter { it.name != circuit }
            }
        }
    }

    fun updateDeleteTrackAfterCompletion(it: Boolean) {
        _deleteTrackAfterCompletion.value = it
    }

    fun setPopupDisplay(newValue: TrackItems?) {
        _showResultPopup.value = newValue
        if (newValue == null) {
            selectedEndTrackItems.value = null
        }
    }

    fun updateGenerationBias(newValue: Float) {
        _generationBias.value = newValue
    }

    fun addGroup(pair: Pair<String, String>) {
        _groups.update { it + pair }
    }

    fun removeGroup(pair: Pair<String, String>) {
        _groups.update { it - pair }
    }

    fun pickRandomTeams() {
        val allTeams = _groups.value

        if (allTeams.size > 2) {
            val randomTeams = allTeams.shuffled().take(4)
            _selectedRandomPlayers.value = randomTeams.map { item ->
                if (!_teamIndex.value) item.first else item.second
            }
        } else {
            _selectedRandomPlayers.value = allTeams.flatMap { listOf(it.first, it.second) }
        }
        _teamIndex.value = !_teamIndex.value
    }
}