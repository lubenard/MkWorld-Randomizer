package com.escatrag.mkworldrandomiser.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escatrag.mkworldrandomiser.backend.Track
import com.escatrag.mkworldrandomiser.backend.TrackCombo
import com.escatrag.mkworldrandomiser.backend.TrackComboType
import com.escatrag.mkworldrandomiser.backend.TrackItems
import com.escatrag.mkworldrandomiser.backend.TrackRepository
import com.escatrag.mkworldrandomiser.backend.isSnesOrDefault
import com.escatrag.mkworldrandomiser.backend.map
import com.escatrag.mkworldrandomiser.backend.removalTargets
import com.escatrag.mkworldrandomiser.backend.toTrackItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Phase { SELECTION_CUBE, SPINNING_TRACK, DUAL_SPINNER }

enum class SnesMaxPerCoupe { ZERO, ONE, TWO, THREE, FOUR, RANDOM }

class TrackViewModel : ViewModel() {

    companion object {
        // Valeurs possibles du slider "courses par coupe" (non-linéaires : le Slider travaille sur l'index).
        val RACES_PER_COUPE_OPTIONS = listOf(3, 4, 5, 6, 8, 12, 16, 32)
    }

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
    private val _deleteTrackAfterCompletion = MutableStateFlow(true)
    val deleteTrackAfterCompletion: StateFlow<Boolean> = _deleteTrackAfterCompletion

    // Set the selected track or trajet
    private val _selectedTrack = MutableStateFlow<TrackCombo?>(null)
    val selectedTrack:MutableStateFlow<TrackCombo?> = _selectedTrack

    // generation bias: 0 for only tracks, 50 for random between tracks & connection, 100 for connections only
    private val _generationBias = MutableStateFlow(0F)
    val generationBias: StateFlow<Float> = _generationBias

    // Maps SNES dans le tirage ? Valeur en mémoire uniquement, comme le switch de suppression :
    // remise à true à chaque lancement de l'app.
    private val _snesEnabled = MutableStateFlow(true)
    val snesEnabled: StateFlow<Boolean> = _snesEnabled

    // Coupe : nombre de courses avant rotation du quota SNES. Modifiable via le slider des réglages.
    // Valeur en mémoire uniquement, comme _snesEnabled.
    private val _racesPerCoupe = MutableStateFlow(4)
    val racesPerCoupe: StateFlow<Int> = _racesPerCoupe

    // Courses écoulées depuis le début de la session ; une coupe = racesPerCoupe courses.
    private val _racesInCoupe = MutableStateFlow(0)

    // SNES terminées dans la coupe courante ; comparées à _maxSnesPerCoupe dans pickRandomMap.
    private val _snesDrawnInCoupe = MutableStateFlow(0)

    // Quota de maps SNES par coupe (slider réglages) : 0..4 puis RANDOM (aucun filtrage).
    // Valeur en mémoire uniquement, comme _racesPerCoupe.
    private val _maxSnesPerCoupe = MutableStateFlow(SnesMaxPerCoupe.ONE)
    val maxSnesPerCoupe: StateFlow<SnesMaxPerCoupe> = _maxSnesPerCoupe

    // Suppression "en bloc" : si activé, une course SNES retire toute la série du circuit tiré
    // (ex. « Circuit Mario 1 » retire « Circuit Mario 2 » et « Circuit Mario 3 »), sans toucher
    // aux autres séries SNES. Désactivé : seul le circuit tiré est retiré du pool.
    private val _deleteSnesInBlock = MutableStateFlow(true)
    val deleteSnesInBlock: StateFlow<Boolean> = _deleteSnesInBlock

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

    // Popup de résultat affichant le circuit/trajet sélectionné (null = masquée)
    private val _showResultPopup = MutableStateFlow<TrackCombo?>(null)
    val showResultPopup: StateFlow<TrackCombo?> = _showResultPopup

    fun setPopupDisplay(newValue: TrackCombo?) {
        _showResultPopup.value = newValue
    }

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
        // La course est jouée : la coupe avance dans tous les cas (même si le switch
        // de suppression est off). Une map SNES terminée consomme le quota de la coupe.
        _racesInCoupe.value = _racesInCoupe.value + 1
        if (result.start.isSnesOrDefault()) {
            _snesDrawnInCoupe.value = _snesDrawnInCoupe.value + 1
        }
        // Fin de coupe : rotation, le quota SNES repart de zéro.
        if (_racesInCoupe.value % _racesPerCoupe.value == 0) {
            _snesDrawnInCoupe.value = 0
        }
        viewModelScope.launch {
            delay(3000L)
            if (!_deleteTrackAfterCompletion.value) {
                Log.d("lubenard", "completeRace: suppression annulee (switch off) — ${result.start.text}")
                return@launch
            }
            if (result.type == TrackComboType.CONNECTION) {
                _selectedConnections.value = _selectedConnections.value.filter { it != result }
                Log.d("lubenard", "completeRace: trajet retire de _selectedConnections — ${result.start.text} -> ${result.end?.text}")
            } else {
                removeSeriesFromPool(result)
            }
        }
    }

    /**
     * Retire du pool le circuit tiré, ou sa série entière si la suppression en bloc est activée.
     *
     * Suppression en bloc : « Circuit Mario 2 (SNES) » retire Circuit Mario 1, 2 et 3,
     * sans toucher aux autres circuits SNES ni au « Circuit Mario » de Mario Kart World.
     * Désactivée : seul le circuit exact est retiré.
     */
    private fun removeSeriesFromPool(combo: TrackCombo) {
        val item = combo.start.toTrackItem()
        if (item == null) {
            // Track inconnu du pool : repli sur la suppression de l'entrée exacte
            _selectedTracks.value = _selectedTracks.value.filter { it != combo }
            _selectedConnections.value = _selectedConnections.value.filter { it.start != combo.start }
            return
        }
        val members = item.removalTargets(_deleteSnesInBlock.value)
        _selectedTracks.value = _selectedTracks.value.filterNot { it.start.toTrackItem() in members }
        _selectedConnections.value = _selectedConnections.value.filterNot { it.start.toTrackItem() in members }
        Log.d("lubenard", "completeRace: ${members.size} circuit(s) retire(s) — ${combo.start.text}")
    }

    /**
     * Active ou désactive d'un coup les 10 circuits SNES dans le tirage.
     *
     * Action de lot : le switch ne fait que donner la valeur initiale, les cartes SNES
     * de l'écran Circuits restent ensuite indépendantes de ce réglage. Les 10 circuits
     * restent toujours listés dans `_allTracksAvailable`, ils ne sont donc jamais retirés
     * de la liste visible — juste décochés, comme un circuit déjà tiré.
     *
     * Valeur en mémoire uniquement, comme le switch de suppression : elle repart de true
     * à chaque lancement de l'app.
     */
    fun setSnesEnabled(enabled: Boolean) {
        _snesEnabled.value = enabled
        if (enabled) {
            val alreadyInPool = _selectedTracks.value.filter { it.start.isSnesOrDefault() }
            _selectedTracks.value = _selectedTracks.value +
                    (TrackRepository.trackItems.filter { it.start.isSnesOrDefault() } - alreadyInPool)
        } else {
            _selectedTracks.value = _selectedTracks.value.filterNot { it.start.isSnesOrDefault() }
        }
        Log.d("lubenard", "setSnesEnabled: snes=$enabled, pool=${_selectedTracks.value.size}")
    }

    fun toggleConnection(parent: Track, childItem: TrackItems) {
        if (_selectedTracks.value.none { it.start == parent }) {
            Log.d("lubenard", "toggleConnection: circuit parent desactive, ignore — ${parent.text}")
            return
        }

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

        val snesQuotaReached = when (val maxSnes = _maxSnesPerCoupe.value) {
            SnesMaxPerCoupe.RANDOM -> false
            SnesMaxPerCoupe.ZERO -> true
            else -> _snesDrawnInCoupe.value >= maxSnes.ordinal
        }
        val available = if (snesQuotaReached) {
            // Quota SNES de la coupe consommé : les SNES restantes sont exclues du tirage.
            currentSelectedTracks.filterNot { it.start.isSnesOrDefault() }
        } else {
            currentSelectedTracks
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
        _showResultPopup.value = null
        _phase.value = Phase.SELECTION_CUBE
    }

    fun selectAllTracks(includeRoutes: Boolean) {
        _selectedTracks.value = TrackRepository.trackItems
        _racesInCoupe.value = 0
        _snesDrawnInCoupe.value = 0
        if (includeRoutes) {
            val tracksInPool = _selectedTracks.value.map { it.start }
            _selectedConnections.value = transformConnectionsToList(TrackRepository.connections)
                .filter { it.start in tracksInPool }
        } else {
            _selectedConnections.value = mutableListOf()
        }
    }

    fun clearAllTracks() {
        _selectedTracks.value = mutableListOf()
        _selectedConnections.value = mutableListOf()
        _racesInCoupe.value = 0
        _snesDrawnInCoupe.value = 0
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

    fun updateRacesPerCoupe(value: Int) {
        if (value in RACES_PER_COUPE_OPTIONS) {
            _racesPerCoupe.value = value
        }
    }

    fun updateMaxSnesPerCoupe(value: SnesMaxPerCoupe) {
        _maxSnesPerCoupe.value = value
    }

    fun updateDeleteSnesInBlock(value: Boolean) {
        _deleteSnesInBlock.value = value
    }
}
