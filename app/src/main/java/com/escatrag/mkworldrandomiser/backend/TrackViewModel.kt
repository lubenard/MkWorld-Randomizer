package com.escatrag.mkworldrandomiser.backend

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
    private val _selectedTracks = MutableStateFlow(TrackRepository.trackItems)
    val selectedTracks: StateFlow<List<TrackCombo>> = _selectedTracks

    // All tracks available for selection (tracks + connections if includeRoutes is on)
    private val _allTracksAvailable = MutableStateFlow(TrackRepository.trackItems)
    val allTracksAvailable: StateFlow<List<TrackCombo>> = _allTracksAvailable

    // Randomly Selected Item
    // -1 is for infinite loop
    var selectedTrackIndex = MutableStateFlow(-1)

    // Option to include routes between tracks
    private val _includeRoutes = MutableStateFlow(false)
    var includeRoutes: StateFlow<Boolean> = _includeRoutes

    // Option to delete One track from the selectedTracks after completion (after selecting a random track)
    private val _deleteTrackAfterCompletion = MutableStateFlow(true)
    val deleteTrackAfterCompletion: StateFlow<Boolean> = _deleteTrackAfterCompletion

    // Option to delete the finish circuit (end) after a connection draw
    private val _deleteFinishCircuit = MutableStateFlow(false)
    val deleteFinishCircuit: StateFlow<Boolean> = _deleteFinishCircuit

    // Show the result popup.... or not !
    private val _showResultPopup = MutableStateFlow<TrackCombo?>(null)
    val showResultPopup: StateFlow<TrackCombo?> = _showResultPopup

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
        _allTracksAvailable.value = TrackRepository.allCombos(value)
    }

    // Generate a route based on selectedTracks
    fun generateCourse() {
        val currentSelected = _selectedTracks.value

        if (currentSelected.isNotEmpty()) {
            // 1. Tirage au sort de l'index
            val randomIndex = Random.nextInt(currentSelected.size)
            val selectedCombo = currentSelected[randomIndex]

            // 2. Détermination du type de course (Bias)
            val shouldIncludeRoute = when (_generationBias.value) {
                0f -> false
                100f -> true
                else -> Random.nextBoolean()
            }

            // 3. Construction du résultat final
            var finalResult = selectedCombo // Par défaut, on garde le combo tel quel

            if (shouldIncludeRoute) {
                val possibleDestinations = TrackRepository.connections[selectedCombo.start]

                if (!possibleDestinations.isNullOrEmpty()) {
                    val randomDestEnum = possibleDestinations.random()

                    // Création du combo avec la destination
                    finalResult = TrackCombo(
                        start = selectedCombo.start,
                        end = randomDestEnum
                    )
                    _includeRoutes.value = true
                } else {
                    // Pas de connexions trouvées, on force le type TRACK
                    finalResult = selectedCombo.copy(end = null)
                    _includeRoutes.value = false
                }
            } else {
                // Simple circuit
                finalResult = selectedCombo.copy(end = null)
                _includeRoutes.value = false
            }

            // 4. Mise à jour des StateFlow pour l'UI
            selectedTrackIndex.value = randomIndex
            _showResultPopup.value = finalResult

            Log.d("lubenard", "Course générée à l'index $randomIndex : ${finalResult.type}")
        }
    }

    fun resetCourse() {
        selectedTrackIndex.value = -1
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        _selectedTracks.value = TrackRepository.allCombos(includeRoutes)
    }

    fun clearAllTracks() {
        _selectedTracks.value = emptyList()
    }

    fun deleteCircuit(result: TrackCombo, skipScrollDelay: Long = 3500) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_deleteTrackAfterCompletion.value || _deleteFinishCircuit.value) {
                Log.d("lubenard", "Deleting ${result.type} starting at ${result.start}")
                delay(skipScrollDelay)
                if (_deleteTrackAfterCompletion.value) {
                    _selectedTracks.value = removeUsedCombo(_selectedTracks.value, result)
                }
                if (_deleteFinishCircuit.value && result.end != null) {
                    _selectedTracks.value = removeFinishCircuit(_selectedTracks.value, result.end)
                }
            }
        }
    }

    // Retire le combo "joué" : la connexion exacte (start+end) si présente, sinon le circuit de départ
    private fun removeUsedCombo(pool: List<TrackCombo>, result: TrackCombo): List<TrackCombo> {
        val toRemove = if (result.end != null) {
            pool.firstOrNull { it.start == result.start && it.end == result.end }
                ?: pool.firstOrNull { it.start == result.start }
        } else {
            pool.firstOrNull { it.start == result.start }
        }
        return toRemove?.let { pool - it } ?: pool
    }

    // Retire le circuit d'arrivée d'une connexion
    private fun removeFinishCircuit(pool: List<TrackCombo>, endTrack: TrackItems): List<TrackCombo> {
        val toRemove = pool.firstOrNull { it.start == endTrack }
        return toRemove?.let { pool - it } ?: pool
    }

    fun updateDeleteTrackAfterCompletion(it: Boolean) {
        _deleteTrackAfterCompletion.value = it
    }

    fun updateDeleteFinishCircuit(it: Boolean) {
        _deleteFinishCircuit.value = it
    }

    fun setPopupDisplay(newValue: TrackCombo?) {
        _showResultPopup.value = newValue
        /*if (newValue == null) {
            selectedEndTrackItems.value = null
        }*/
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