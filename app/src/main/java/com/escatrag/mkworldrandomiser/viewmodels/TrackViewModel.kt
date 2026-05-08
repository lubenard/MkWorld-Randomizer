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

    // Selected tracks that will be used for random generation
    private val _selectedTracks = MutableStateFlow(TrackRepository.trackItems)
    val selectedTracks: StateFlow<List<TrackCombo>> = _selectedTracks

    // All tracks availables: Used for Selection tracks (will include routes if selected in SelectionScreen),
    // but they will not be selected (tho available for selection)
    private val _allTracksAvailable = MutableStateFlow(TrackRepository.trackItems)
    val allTracksAvailable: StateFlow<List<TrackCombo>> = _allTracksAvailable

    // Randomly Selected Item
    // -1 is for infinite loop
    var selectedTrackIndex = MutableStateFlow(-1)

    // Option to include routes between tracks
    private val _includeRoutes = MutableStateFlow(false)
    var includeRoutes: StateFlow<Boolean> = _includeRoutes

    // Option to delete One track from the selectedTracks after completion (after selecting a random track)
    private val _deleteTrackAfterCompletion = MutableStateFlow(false)
    val deleteTrackAfterCompletion: StateFlow<Boolean> = _deleteTrackAfterCompletion

    // Set the selected track or trajet
    private val _selectedTrack = MutableStateFlow<TrackCombo?>(null)
    val selectedTrack:MutableStateFlow<TrackCombo?> = _selectedTrack

    // generation bias: 0 for only tracks, 50 for random between tracks & connection, 100 for connections only
    private val _generationBias = MutableStateFlow(0F)
    val generationBias: StateFlow<Float> = _generationBias

    fun toggleTrack(id: TrackCombo) {
        // 1. On cherche le vrai objet Track qui correspond à cet ID
        val trackItemsToToggle = _allTracksAvailable.value.find { it == id }

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

    fun getConnectionsForTrack(trackItem: TrackItems): List<TrackItems> {
        return TrackRepository.connections[trackItem] ?: emptyList()
    }

    fun toggleConnection(parent: Track, childItem: TrackItems) {
        val combo = TrackCombo(
            start = parent,
            end = childItem.map(),
            type = TrackComboType.CONNECTION
        )

        // Si ce trajet existe déjà dans les sélectionnés, on l'enlève, sinon on l'ajoute
        if (_selectedTracks.value.contains(combo)) {
            _selectedTracks.value = _selectedTracks.value.minus(combo)
        } else {
            _selectedTracks.value = _selectedTracks.value.plus(combo)
        }
    }

    // Generate a route based on selectedTracks
    fun generateCourse() {
        val currentSelectedTracks = _selectedTracks.value

        if (currentSelectedTracks.isNotEmpty()) {
            // 1. Tirage au sort de l'index
            val randomIndex = Random.nextInt(currentSelectedTracks.size)
            val selectedCombo = currentSelectedTracks[randomIndex]

            // 2. Détermination du type de course (Bias)
            val shouldIncludeRoute = when (_generationBias.value) {
                0f -> false
                100f -> true
                else -> Random.nextBoolean()
            }

            // 3. Construction du résultat final
            var finalResult = selectedCombo // Par défaut, on garde le combo tel quel

            if (shouldIncludeRoute) {
                // Matching : Track -> TrackItems (Enum)
                val startEnum = TrackItems.entries.find { it.nameRes == selectedCombo.start.text }
                val possibleDestinations = startEnum?.let { TrackRepository.connections[it] }

                if (!possibleDestinations.isNullOrEmpty()) {
                    val randomDestEnum = possibleDestinations.random()

                    // Création du combo avec la destination
                    finalResult = TrackCombo(
                        start = selectedCombo.start,
                        end = randomDestEnum.map(),
                        type = TrackComboType.CONNECTION
                    )
                    _includeRoutes.value = true
                } else {
                    // Pas de connexions trouvées, on force le type TRACK
                    finalResult = selectedCombo.copy(type = TrackComboType.TRACK)
                    _includeRoutes.value = false
                }
            } else {
                // Simple circuit
                finalResult = selectedCombo.copy(type = TrackComboType.TRACK)
                _includeRoutes.value = false
            }

            // 4. Mise à jour des StateFlow pour l'UI
            selectedTrackIndex.value = randomIndex
            _selectedTrack.value = finalResult


            Log.d("lubenard", "Course générée à l'index $randomIndex : ${finalResult.start.text}")
        }
    }

    fun resetCourse() {
        selectedTrackIndex.value = -1
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        _selectedTracks.value = emptyList()
        Log.d("lubenard", "test $includeRoutes")
        if (includeRoutes) {
            val test = _allTracksAvailable.value.toMutableList()
            Log.d("lubenard", "test before ${test.size}")
            test.addAll(transformConnectionsToList(TrackRepository.connections))
            Log.d("lubenard", "test after ${test.size}")
            _selectedTracks.value = test
        } else {
            _selectedTracks.value = TrackRepository.trackItems
        }
    }

    fun clearAllTracks() {
        _selectedTracks.value = emptyList()
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
            if (_deleteTrackAfterCompletion.value && !_includeRoutes.value) {
                Log.d("lubenard", "Deleting track $circuit}")
                val tempValue = _selectedTracks.value.toMutableList()
                Log.d("lubenard", "Updating selectedTracks without $circuit")
                _selectedTracks.value = tempValue.filter { it != circuit }
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