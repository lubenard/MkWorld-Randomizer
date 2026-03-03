package com.escatrag.mkworldrandomiser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class TrackViewModel : ViewModel() {

    // Selected tracks that will used for random generation
    private val _selectedTracks = MutableStateFlow(TrackRepository.tracks)
    val selectedTracks: StateFlow<List<String>> = _selectedTracks

    // Randomly Selected Item
    // -1 is for infinite loop
    var selectedItem = MutableStateFlow(-1)

    // All tracks availables: Used for Selection tracks (will include routes if selected in SelectionScreen),
    // but they will not be selected (tho available for selection)
    private val _allTracksAvailable = MutableStateFlow(TrackRepository.tracks)
    val allTracksAvailable: StateFlow<List<String>> = _allTracksAvailable

    // Option to include routes between tracks
    private val _includeRoutes = MutableStateFlow(false)
    var includeRoutes: StateFlow<Boolean> = _includeRoutes

    // Option to delete One track from the selectedTracks after completion (after selecting a random track)
    private val _deleteTrackAfterCompletion = MutableStateFlow(false)
    val deleteTrackAfterCompletion: StateFlow<Boolean> = _deleteTrackAfterCompletion

    fun toggleTrack(id: String) {
        _selectedTracks.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun setIncludeRoutes(value: Boolean) {
        _includeRoutes.value = value
    }

    // Generate a route based on selectedTracks
    fun generateCourse(delay: Long) {
        if (selectedTracks.value.isNotEmpty()) {
            selectedItem.value = Random.nextInt(selectedTracks.value.size)
            Log.d("Luca", "onClick ${selectedItem.value} ${selectedTracks.value} -> ${_selectedTracks.value.get(selectedItem.value)} / $delay")
            //if (_deleteTrackAfterCompletion.value)
                //deleteCircuit(_selectedTracks.value[selectedItem.value], skipScrollDelay = 500)
        }
    }

    fun resetCourse() {
        selectedItem.value = -1
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        _selectedTracks.value = emptyList()
        _selectedTracks.value = TrackRepository.tracks
        if (includeRoutes) {
            val addConnectionToList = _selectedTracks.value.toMutableList()
            addConnectionToList.addAll(transformConnectionsToList(TrackRepository.connections))
            _selectedTracks.value = addConnectionToList
        }
    }

    fun clearAllTracks() {
        _selectedTracks.value = emptyList()
    }

    fun transformConnectionsToList(connections: Map<String, List<String>>): List<String> {
        return connections.flatMap { (depart, destinations) ->
            destinations.map { destination ->
                "$depart > $destination"
            }
        }
    }

    fun addTrajetsToList() {
        val newTracks = _allTracksAvailable.value.toMutableList()
        newTracks.addAll(transformConnectionsToList(TrackRepository.connections))
        _allTracksAvailable.value = newTracks
    }

    fun deleteRoutesToAvailableTracks() {
        _allTracksAvailable.value = emptyList()
        _allTracksAvailable.value = TrackRepository.tracks
    }

    fun deleteCircuit(circuit: String, skipScrollDelay: Long = 3500) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("Luca", "Trying to delete track $circuit -> ${_deleteTrackAfterCompletion.value}")
            if (_deleteTrackAfterCompletion.value) {
                Log.d("Luca", "Deleting track $circuit}")
                val tempValue = _selectedTracks.value.toMutableList()
                delay(skipScrollDelay)
                Log.d("Luca", "Updating selectedTracks without $circuit")
                _selectedTracks.value = tempValue.filter { it != circuit }
            }
        }
    }

    fun updateDeleteTrackAfterCompletion(it: Boolean) {
        _deleteTrackAfterCompletion.value = it
    }
}