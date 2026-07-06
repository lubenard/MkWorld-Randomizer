# MkWorld Randomiser

App Android (Jetpack Compose + M3) qui randomise les circuits de *Mario Kart World* (Switch 2). Sélection de pistes/trajets → roue aléatoire → saisie des scores avec classement mensuel.

## Build & Run

```bash
./gradlew assembleDevelop        # debug flavor (appId: .dev)
./gradlew assembleProduction     # production flavor
./gradlew test                   # unit tests (JUnit 4)
./gradlew connectedAndroidTest   # instrumented tests (device/emulator required)
```

Product flavors: `developp` / `production` (dimension: `environnement`).  
AGP 8.13.2, Kotlin 2.3.10, Gradle 8.13, compileSdk 36, minSdk 24.

## Architecture

- Single-module `:app`, Jetpack Compose + Material3, Navigation Compose.
- Entrypoint: `MainActivity.kt` — bottom nav (Aléatoire, Circuits, Scores).
- ViewModels (`TrackViewModel`, `ScoreViewModel`, `SettingsViewModel`) with `StateFlow`.
- Backend: `TrackItems` enum (29 pistes MKW), `TrackRepository` (graphe de connexions statique).
- Persistance : DataStore Preferences + Gson pour les profils joueurs.

## Key Code Areas

| Path | Purpose |
|---|---|
| `backend/` | Enum des 29 pistes, graphe de connexions, mappers, ELO |
| `viewmodels/` | UI state holders — `ScoreViewModel` (AndroidViewModel, DataStore), `TrackViewModel`, `SettingsViewModel` (vide) |
| `ui/screens/` | Main, TrackSelection, Settings, RaceResult, MonthlyScore, ProfileCreationPopup |
| `ui/composables/` | SpinWheel (roue à 2 phases), PodiumSection, BiasSlider, HomeUI, PlayerDetails |
| `ui/theme/` | Color, Type (MinecraftFontFamily via `R.font.minecraft`), Theme (dynamic colors, Android 12+) |

## Scoring & ELO

- **Deux systèmes de score coexistent, sans lien :**
  - `backend/ElloCalculator.kt` — vrai ELO pairwise (score par paires, K/scale) — **inutilisé par l'UI**, importé nulle part
  - `RaceResultScreen.kt:194-250` — doublon inline du même algo, modifie `currentMonthScore` localement mais les résultats **ne sont pas persistés**
  - `ScoreViewModel.submitRaceResults()` — persiste une table de points fixes (15/12/10/...), ignorant tout calcul ELO
  - **Conséquence : le calcul ELO dans `RaceResultScreen` est du code mort ; seul le barème fixe est sauvegardé.**
- `currentMonthScore` sert à la fois de score mensuel et de rating ELO — aucun champ distinct.
- `Top3Maps.mapId: Int` stocke des `R.string.xxx` comme identifiant de piste (fragile : ne pas renommer les `string` resources sans migration).

## Bugs & Fragilités Connues

- `MapCard` (PlayerDetailsComposable.kt:197) cherche les pistes par `start.text` (R.string) → `TODO` dans le code.
- `ProfileCreationPopup` : grille d'avatars (12 items, 4 colonnes) dépasse la hauteur fixe de 150dp.
- `deleteCircuit()` dans TrackViewModel lance `Dispatchers.IO` pour une opération mémoire.
- `Divider` déprécié (M3 récent → `HorizontalDivider`).
- `SettingsViewModel` est vide — le Dark Mode (SettingsScreen.kt:35) est un état local non persisté.
- `HomeUI` affiche "48 courses disponibles" par défaut alors que le pool réel dépend des sélections.

## Quirks & Conventions

- UI strings et commentaires en **français**.
- Release builds utilisent `signingConfigs.debug` — configurer un vrai keystore avant prod.
- `BuildConfig.COMMIT_SHA` auto-peuplé par `git rev-parse --short HEAD`.
- `SpinWheel` gère la roue à 2 phases (piste → destination). `generationBias` contrôle la probabilité piste/trajet.
- `Mappers.kt` définit `TrackItems.map()` et `List<TrackItems>.map()` — ces extensions **shadowent** le `map` standard de Kotlin.
- Logs tag : `"lubenard"` (parfois `"escatrag"`).
- Les 2 jeux de tests (`test/`, `androidTest/`) sont des stubs de démarrage — aucun test fonctionnel écrit.
- Avatar par défaut : `R.drawable.mont_tchou_tchou`.
