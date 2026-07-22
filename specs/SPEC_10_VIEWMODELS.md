# SPEC 10 — ViewModels

## 1. TrackViewModel — `viewmodels/TrackViewModel.kt`

**Type :** `ViewModel()` (pas AndroidViewModel)  
**Package :** `com.escatrag.mkworldrandomiser.viewmodels`

### StateFlows

| Champ privé | Type Flow | Public | Initial | Description |
|---|---|---|---|---|
| `_selectedTracks` | `StateFlow<List<TrackCombo>>` | `selectedTracks` | `TrackRepository.trackItems` | Circuits activés pour le tirage |
| `_selectedConnections` | `StateFlow<List<TrackCombo>>` | `selectedConnections` | `emptyList()` | Trajets activés |
| `_allTracksAvailable` | `StateFlow<List<TrackCombo>>` | `allTracksAvailable` | `TrackRepository.trackItems` | Tous les circuits disponibles (référence pour l'UI) |
| `_deleteTrackAfterCompletion` | `StateFlow<Boolean>` | `deleteTrackAfterCompletion` | `true` | Auto-suppression après course |
| `_selectedTrack` | `MutableStateFlow<TrackCombo?>` | `selectedTrack` (mutable) | `null` | Circuit/trajet sélectionné par la roue |
| `_generationBias` | `StateFlow<Float>` | `generationBias` | `0F` | Biais 0 (circuits) → 100 (trajets) |
| `_destinationTargetIndex` | `StateFlow<Int>` | `destinationTargetIndex` | `-1` | Index destination pour 2e spinner |
| `_phase` | `StateFlow<Phase>` | `phase` | `SELECTION_CUBE` | Phase UI actuelle |
| `_includeRoutes` | `StateFlow<Boolean>` | `includeRoutes` | `false` | Inclure les trajets dans le pool |
| `_destinationItems` | `StateFlow<List<TrackCombo>>` | `destinationItems` | `emptyList()` | Destinations éligibles pour dual spinner |
| `_isSecondSpinnerReady` | `StateFlow<Boolean>` | `isSecondSpinnerReady` | `false` | 2e spinner prêt |
| `selectedTrackIndex` | `MutableStateFlow<Int>` | (direct) | `-1` | Index dans selectedTracks pour l'animation |

### Internal state
```kotlin
private var pendingDestinations: List<TrackItems>? = null
```

### Fonctions Publiques

| Fonction | Description |
|---|---|
| `toggleTrack(id: TrackCombo)` | Active/désactive un circuit + supprime ses connexions associées |
| `getConnectionsForTrack(trackItem: TrackItems)` | Retourne les destinations possibles depuis le graphe |
| `setIncludeRoutes(value: Boolean)` | Met à jour `_includeRoutes` |
| `completeRace(result: TrackCombo)` | Après 3000ms, retire le résultat du pool |
| `toggleConnection(parent: Track, childItem: TrackItems)` | Active/désactive un trajet spécifique |
| `pickRandomMap()` | **Cœur** : sélection aléatoire avec biais |
| `pickRandomDestination()` | Pour dual spinner : choisit une destination |
| `resetCourse()` | Réinitialise tout l'état de phase |
| `selectAllTracks(includeRoutes: Boolean)` | Active tous les circuits + leurs connexions |
| `clearAllTracks()` | Vide les deux pools |
| `deleteCircuit(circuit: TrackCombo?)` | Supprime un élément du pool |
| `updateDeleteTrackAfterCompletion(Boolean)` | Change le mode auto-delete |
| `updateGenerationBias(Float)` | Change le biais |
| `transformConnectionsToList(Map)` | Utilitaire : convertit la map en `List<TrackCombo>` |

## 2. ScoreViewModel — `viewmodels/ScoreViewModel.kt`

**Type :** `AndroidViewModel(application)`  
**DataStore :** `scores_prefs` (clé : `players_list`)

### StateFlows

| Champ | Type | Initial | Description |
|---|---|---|---|
| `_players` | `StateFlow<List<PlayerProfile>>` | `emptyList()` | Tous les joueurs (source de vérité) |
| `players` | `StateFlow` (asStateFlow) | — | Exposition publique |
| `sortedPlayers` | `StateFlow<List<PlayerProfile>>` | `emptyList()` | Trié par `currentMonthScore` descendant |
| `_editingProfile` | `StateFlow<PlayerProfile?>` | `null` | Profil en cours d'édition dans la popup |

### Fonctions Publiques

| Fonction | Description |
|---|---|
| `resetMonthlyScores()` | Remet tous les `currentMonthScore` à 3000 + persist |
| `startCreatingProfile()` | Set `_editingProfile` à un nouveau `PlayerProfile()` |
| `closeEditPopup()` | Set `_editingProfile` à null |
| `saveProfile(profile)` | Upsert + persist (vérifie `name.isBlank()`) |
| `submitRaceResults(rankings, mapId)` | Calcule ELO pairwise + stats + persist |
| `resetUsers()` | Vide la liste + persist |
| `deletePlayer(player)` | Supprime en mémoire **sans persist** ⚠️ |
| `handleTop3Maps(position, player, mapId)` | Met à jour `top3Maps` (privée) |

### Dépendances
- `Gson` pour la sérialisation JSON
- `DataStore` pour la persistance
- `UUID` pour les IDs de joueurs

## 3. SettingsViewModel — `viewmodels/SettingsViewModel.kt`

**Type :** `AndroidViewModel(application)`  
**DataStore :** `settings_prefs` (clé : `theme_mode`)

### StateFlows

| Champ | Type | Initial | Description |
|---|---|---|---|
| `_themeMode` | `StateFlow<ThemeMode>` | `SYSTEM` | Mode de thème |

### Fonctions Publiques

| Fonction | Description |
|---|---|
| `setThemeMode(mode: ThemeMode)` | Met à jour l'état + persist dans DataStore |

### Chargement
`init` : collecte DataStore, parse `ThemeMode.valueOf(name)` ou fallback `SYSTEM`.

## 4. Relations entre ViewModels

```
MainActivity
├── TrackViewModel        (créé par viewModel() dans le NavHost)
├── ScoreViewModel        (créé par viewModel() dans le NavHost)
└── SettingsViewModel     (créé par viewModel() avant MkWorldRandomiserTheme)
```

Les ViewModels sont créés au niveau de `MainActivity` et passés aux screens.  
Pas de `hilt`/`koin` — injection manuelle via paramètres de composables.

**Partage de données :** `TrackViewModel.selectedTrack` est lu par `RaceResultScreen` pour obtenir le `mapId` à passer à `submitRaceResults()`.

## 5. Cycle de Vie

| ViewModel | Scope | Persistance |
|---|---|---|
| TrackViewModel | Session (détruit si Activity recréée) | Aucune |
| ScoreViewModel | Session | DataStore (restauré dans `init`) |
| SettingsViewModel | Session (mais créé avant le NavHost) | DataStore (restauré dans `init`) |
