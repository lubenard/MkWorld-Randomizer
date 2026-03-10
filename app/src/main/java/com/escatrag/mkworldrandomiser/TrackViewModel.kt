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

    // Selected tracks that will be used for random generation
    private val _selectedTracks = MutableStateFlow(TrackRepository.tracks)
    val selectedTracks: StateFlow<List<Track>> = _selectedTracks

    // Randomly Selected Item
    // -1 is for infinite loop
    var selectedTrack = MutableStateFlow(-1)
    var selectedEndTrack = MutableStateFlow<Track?>(null)

    // All tracks availables: Used for Selection tracks (will include routes if selected in SelectionScreen),
    // but they will not be selected (tho available for selection)
    private val _allTracksAvailable = MutableStateFlow(TrackRepository.tracks)
    val allTracksAvailable: StateFlow<List<Track>> = _allTracksAvailable

    // Option to include routes between tracks
    private val _includeRoutes = MutableStateFlow(false)
    var includeRoutes: StateFlow<Boolean> = _includeRoutes

    // Option to delete One track from the selectedTracks after completion (after selecting a random track)
    private val _deleteTrackAfterCompletion = MutableStateFlow(false)
    val deleteTrackAfterCompletion: StateFlow<Boolean> = _deleteTrackAfterCompletion

    private val _showResultPopup = MutableStateFlow<Track?>(null)
    val showResultPopup: StateFlow<Track?> = _showResultPopup

    private val _generationBias = MutableStateFlow(0F)
    val generationBias: StateFlow<Float> = _generationBias

    fun toggleTrack(id: String) {
        // 1. On cherche le vrai objet Track qui correspond à cet ID
        val trackToToggle = Track.entries.find { it.name == id }

        // Si on ne trouve pas le circuit (ID invalide), on arrête tout pour éviter un crash
        if (trackToToggle == null) return

        // 2. On met à jour la liste avec le VRAI objet Track
        _selectedTracks.update { current ->
            if (current.contains(trackToToggle)) {
                current - trackToToggle // Le circuit est déjà là : on le retire
            } else {
                current + trackToToggle // Le circuit n'y est pas : on l'ajoute
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
                selectedEndTrack.value = TrackRepository.connections[mSelectedItem]!![Random.nextInt(circuit!!.size)]
                Log.d("lubenard", "onClick 2.5 ${selectedEndTrack.value}")
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

    fun transformConnectionsToList(connections: Map<Track, List<Track>>): List<Track> {
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
            if (_deleteTrackAfterCompletion.value) {
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

    fun setPopupDisplay(newValue: Track?) {
        _showResultPopup.value = newValue
        if (newValue == null) {
            selectedEndTrack.value = null
        }
    }

    fun updateGenerationBias(newValue: Float) {
        _generationBias.value = newValue
    }
}