# SPEC 01 — Architecture Globale

## 1. Présentation

**MkWorld Randomiser** est une application Android monopage (single-module) qui permet de randomiser les circuits de *Mario Kart World* (Switch 2). L'utilisateur sélectionne un pool de circuits/trajets, une roue aléatoire en choisit un, puis les scores des courses sont saisis et classés mensuellement.

**Package racine :** `com.escatrag.mkworldrandomiser`  
**Langage :** Kotlin 2.3.10  
**Target SDK :** 36 (Android 16) | **Min SDK :** 24 (Android 7.0)

## 2. Stack Technique

| Couche | Technologie |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose (`androidx.navigation:navigation-compose:2.9.7`) |
| ViewModels | `ViewModel` / `AndroidViewModel` avec `StateFlow` |
| Persistance | DataStore Preferences + Gson (sérialisation JSON) |
| Animation | Konfetti (`nl.dionsegijn:konfetti-compose:2.0.5`) |
| Typographie | Police Minecraft (`R.font.minecraft`) |
| Build | Gradle 8.13 + AGP 8.13.2 |
| Tests | JUnit 4 (unitaires) + Espresso (instrumentés) |

## 3. Architecture en Couches

```
┌──────────────────────────────────────────────────────────────┐
│                        UI LAYER                               │
│  MainActivity.kt (entry point, nav host, theme, bottom nav)   │
│  screens/      → MainScreen, TrackSelectionScreen,            │
│                  RaceResultScreen, MonthlyScoreScreen,         │
│                  SettingsScreen, ProfileCreationPopup          │
│  composables/  → HomeUI, SpinWheel, PodiumSection,            │
│                  BiasSlider, TitleComposable,                  │
│                  PlayerDetailsComposable, ...                  │
│  theme/        → Color.kt, Type.kt, Theme.kt                  │
├──────────────────────────────────────────────────────────────┤
│                       VIEWMODEL LAYER                          │
│  TrackViewModel     → Randomisation + sélection circuits      │
│  ScoreViewModel     → Profils joueurs + scores + persistence  │
│  SettingsViewModel  → Mode thème (clair/sombre/système)       │
├──────────────────────────────────────────────────────────────┤
│                       BACKEND LAYER                            │
│  Model.kt           → Track, TrackCombo, TrackComboType       │
│  TrackItems.kt      → Enum (29 pistes Mario Kart World)       │
│  Mappers.kt         → Extensions TrackItems→Track/shadow map │
│  TrackRepository.kt → Graphe de connexions statique           │
└──────────────────────────────────────────────────────────────┘
```

## 4. Flux de Données Général

```
[TrackRepository] (graphe statique 29×29)
       ↓
[TrackViewModel] (StateFlow: selectedTracks, selectedConnections, phase, ...)
       ↓
[MainScreen] (3 phases: SELECTION_CUBE → SPINNING_TRACK|DUAL_SPINNER → RaceResultActions)
       ↓
[RaceResultScreen] → [ScoreViewModel.submitRaceResults()] → DataStore (JSON)
       ↓
[MonthlyScoreScreen] ← [ScoreViewModel.sortedPlayers]
```

## 5. Structure du Projet

```
app/src/main/java/com/escatrag/mkworldrandomiser/
├── MainActivity.kt          # Entry point, NavHost, bottom nav, theme
├── backend/
│   ├── Model.kt             # Track, TrackCombo, TrackComboType
│   ├── TrackItems.kt        # Enum des 29 pistes
│   ├── Mappers.kt           # Extensions shadow map()
│   └── TrackRepository.kt   # Graphe de connexions statique
├── viewmodels/
│   ├── TrackViewModel.kt    # Randomisation, pool, phases
│   ├── ScoreViewModel.kt    # Profils, scores, DataStore
│   └── SettingsViewModel.kt # Thème
└── ui/
    ├── screens/
    │   ├── MainScreen.kt
    │   ├── TrackSelectionScreen.kt
    │   ├── RaceResultScreen.kt
    │   ├── MonthlyScoreScreen.kt
    │   ├── SettingsScreen.kt
    │   └── ProfileCreationPopup.kt
    ├── composables/
    │   ├── HomeUI.kt
    │   ├── SpinWheel.kt
    │   ├── SelectionCubePhase.kt
    │   ├── SpinningTrackPhase.kt
    │   ├── DualSpinnerPhase.kt
    │   ├── RaceResultActions.kt
    │   ├── BiasSlider.kt
    │   ├── TitleComposable.kt
    │   ├── PodiumSection.kt
    │   ├── PlayerDetailsComposable.kt
    │   └── TrackSelectionConnectionTile.kt
    └── theme/
        ├── Color.kt
        ├── Type.kt
        └── Theme.kt
```

## 6. Concepts Métier

| Terme | Définition |
|---|---|
| **Circuit (Track)** | Une piste de Mario Kart World, identifiée par son `@StringRes nameRes`. |
| **Trajet (Connection/Route)** | Une arête dirigée d'un circuit A vers un circuit B. Le joueur fait 1 tour du circuit B. |
| **Pool** | Ensemble des circuits + trajets activés pour le tirage aléatoire. |
| **Biais (Bias)** | Contrôle « Circuits » (0) ↔ « Connections » (100) — probabilité qu'un trajet soit tiré. |
| **Phase** | État de l'écran principal : `SELECTION_CUBE`, `SPINNING_TRACK`, `DUAL_SPINNER`. |
| **Score ELO** | Calcul pairwise (K=32/(n-1), scale=400) — **non persisté** dans l'état actuel. |
| **Barème fixe** | Points attribués par position (15/12/10/...) — seul système persisté. |

## 7. Fragilités Connues

1. **Top3Maps.mapId: Int** stocke des `R.string.xxx` comme identifiant — fragile si les strings sont renommées.
2. **ELO code mort** : `RaceResultScreen` calcule l'ELO localement mais `submitRaceResults()` persiste un barème fixe.
3. **deletePlayer() non persisté** : supprime de la mémoire mais n'appelle pas `persistData()`.
4. **Mappers.kt shadow `map()`** : `TrackItems.map()` et `List<TrackItems>.map()` écrasent le `map` standard de Kotlin.
5. **MapCard** force-unwrap `correctTrack!!` → NPE si `mapId` introuvable dans `TrackRepository.trackItems`.
6. **ProfileCreationPopup** : 12 avatars en grille 4 colonnes → 3 lignes > hauteur fixe de 150dp.
7. **Release signing** : utilise `signingConfigs.debug` — besoin d'un keystore de production.
