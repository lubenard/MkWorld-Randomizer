# MkWorld Randomiser — Agent Guide

App Android (Jetpack Compose + M3) qui randomise les circuits de *Mario Kart World* (Switch 2).  
Sélection de pistes/trajets → roue aléatoire → saisie des scores avec classement mensuel.

---

## Build & Run

```bash
./gradlew assembleDevelop        # debug flavor (appId: .dev)
./gradlew assembleProduction     # production flavor
./gradlew test                   # unit tests (JUnit 4)
./gradlew connectedAndroidTest   # instrumented tests (device/emulator required)
```

**Stack** : AGP 8.13.2 · Kotlin 2.3.10 · Gradle 8.13 · compileSdk 36 · minSdk 24  
**Flavors** : `developp` / `production` (dimension `environnement`)

---

## Architecture (Vue d'Ensemble)

```
[UI] MainActivity → NavHost → 6 screens
       │
[ViewModels] TrackVM · ScoreVM · SettingsVM  (StateFlow → UI)
       │
[Backend]  TrackItems (enum 29) · TrackRepository (graphe statique) · Model.kt
       │
[Data]    DataStore Preferences + Gson (players_list / settings_prefs)
```

- Module unique `:app`, sans DI (viewModel() manuel)
- UI strings et commentaires en **français**
- Log tag : `"lubenard"` (parfois `"escatrag"`)

---

## Règles de Développement

### Data & Modèles
- `Track` ne porte **pas** d'ID métier — utilise `R.string.xxx` comme ref (fragile)
- `Top3Maps.mapId: Int` stocke des `R.string.xxx` → **ne JAMAIS renommer une string resource sans migration de données**
- `Mappers.kt` définit `TrackItems.map()` et `List<TrackItems>.map()` → ces extensions **shadowent** le `map{}` standard de Kotlin → attention aux appels `map{}` dans le package backend
- `toTrackItem()` peut retourner `null` → toujours gérer le cas null

### ViewModels
- `TrackViewModel` = `ViewModel()` (pas AndroidViewModel)
- `ScoreViewModel` = `AndroidViewModel` (besoin du Context pour DataStore)
- `SettingsViewModel` = `AndroidViewModel`
- Tous les états UI = `StateFlow` exposés depuis le ViewModel
- Ne **jamais** modifier `_players.value` sans appeler `persistData()` ensuite (sauf si intentionnel)

### Scoring
- **Seul `ScoreViewModel.submitRaceResults()` est persisté** — tout calcul ELO fait ailleurs est du code mort
- `currentMonthScore` sert à la fois de score mensuel ET de rating ELO — pas de champ distinct
- Si tu ajoutes un calcul de score, il doit transiter par `submitRaceResults()` ou un nouveau chemin de persistance explicite

### UI / Compose
- Police Minecraft = `MinecraftFontFamily` (via `R.font.minecraft`)
- Titres = `TitleComposable` (ombre rouge + texte jaune)
- Arrière-plan = `R.drawable.map` avec `alpha(0.7f)` dans MainActivity
- Thème = Material3 avec Dynamic Colors (Android 12+)
- Ne pas utiliser `Divider` → utiliser `HorizontalDivider`
- Ne pas forcer `!!` sur un `Track` issu d'un lookup par `R.string` (risque de NPE)

### Test
- Les tests existants (`test/`, `androidTest/`) sont des stubs vides
- Tout nouveau code métier DOIT avoir un test unitaire correspondant
- Les tests instrumentés sont pour l'UI (Compose)

---

## Spécifications Détaillées

Toute la spec est dans `specs/`. Les fichiers sont la source de vérité :

| Fichier | Ce qu'il couvre |
|---|---|
| `SPEC_01_ARCHITECTURE.md` | Stack, couches, flux de données, concepts métier |
| `SPEC_02_DATA_MODEL.md` | Track, TrackCombo, TrackItems, PlayerProfile, Top3Maps |
| `SPEC_03_NAVIGATION.md` | Routes, bottom nav, transitions |
| `SPEC_04_BACKEND_TRACKS.md` | 29 pistes, Mappers (shadow), graphe de connexions |
| `SPEC_05_GAMEPLAY_FLOW.md` | 3 phases, pickRandomMap, biais, auto-delete |
| `SPEC_06_SCORING_PERSISTENCE.md` | ELO, barème fixe, DataStore, bugs |
| `SPEC_07_UI_SCREENS.md` | Les 6 screens : layout, comportement, paramètres |
| `SPEC_08_UI_COMPOSABLES.md` | 14 composables : HomeUI, SpinWheel, PodiumSection... |
| `SPEC_09_THEME_STYLING.md` | Couleurs, palette dynamique, Minecraft Font, ThemeMode |
| `SPEC_10_VIEWMODELS.md` | Les 3 ViewModels : StateFlows, fonctions, cycle de vie |
| `SPEC_11_BUILD_CONFIG.md` | Gradle, flavors, dépendances complètes |

**Règle :** Avant de modifier un fichier, lire la SPEC correspondante.  
**Règle :** Si une modification invalide une SPEC, mettre à jour la SPEC dans le même commit.

---

## Bugs & Fragilités Connues (À Corriger par Ordre de Priorité)

### Priorité Haute (Blocage fonctionnel ou perte de données)
1. **`deletePlayer()` ne persiste pas** — `ScoreViewModel.kt:198-203` supprime en mémoire mais n'appelle pas `persistData()`. Correction : ajouter `persistData(currentList)`.
2. **`MapCard` NPE potentiel** — `PlayerDetailsComposable.kt:197` force-unwrap `correctTrack!!` → crash si `R.string` ne correspond à rien. Migration vers un ID stable.
3. **`Top3Maps.mapId` fragile** — toute la persistence historique peut être cassée si une string resource est renommée. Ajouter un champ `trackItemName: String` ou migrer vers `TrackItems.name`.

### Priorité Moyenne (Fonctionnalité incomplète/incohérente)
4. **ELO code mort** — `RaceResultScreen` calcule l'ELO inline mais `submitRaceResults()` persiste un barème différent. Décider : soit supprimer l'ELO, soit l'intégrer dans `submitRaceResults()`.
5. **`ProfileCreationPopup` overflow** — 12 avatars en `LazyVerticalGrid(columns=4)` > hauteur fixe 150dp. Passer en hauteur dynamique ou réduire à 8 items.
6. **`deleteCircuit()` commentaire trompeur** — mentionne `Dispatchers.IO` mais opération synchrone en mémoire. Nettoyer le commentaire.

### Priorité Basse (UI/Polish)
7. **`SettingsViewModel` partiellement vide** — le Dark Mode est un état local dans `SettingsScreen.kt:35` non persisté, mais `SettingsViewModel` persiste déjà `themeMode`. La logique doit être unifiée dans le ViewModel.
8. **`selectedTab` désynchronisé** — `MainActivity.kt:61` n'est pas synchronisé avec `navController.currentBackStackEntryAsState()` après navigation programmatique.
9. **Release signing** — utilise `signingConfigs.debug`. Configurer un vrai keystore avant publication.

---

## Plan de Développement Recommandé

### Phase 1 — Stabilité (Correction des bugs bloquants)
1. Corriger `deletePlayer()` → ajouter `persistData()`
2. Migrer `Top3Maps.mapId` vers un identifiant stable (`TrackItems.name`)
3. Corriger `MapCard` → remplacer `!!` par gestion null + fallback
4. Fixer la hauteur de `ProfileCreationPopup`

### Phase 2 — Cohérence (Nettoyage fonctionnel)
5. Unifier le scoring : choisir ELO ou barème fixe, supprimer le code mort
6. Déplacer la logique Dark Mode de `SettingsScreen` vers `SettingsViewModel`
7. Ajouter les tests unitaires pour `ScoreViewModel.submitRaceResults()`
8. Synchroniser `selectedTab` avec le `NavController`

### Phase 3 — Features
9. Ajouter un écran de détail des statistiques mensuelles
10. Système d'archives mensuelles (DataStore multi-clés)
11. Interface multijoueur locale (hot-seat)
12. Export/import des données (JSON file)

### Phase 4 — Polish
13. Animations supplémentaires (transitions entre écrans)
14. Haptic feedback sur la roue
15. Mode sombre complet testé
16. Keystore production + Play Store build

---

## Conventions de Code

- **Fichiers :** Kotlin, par convention `UpperCamelCase.kt`
- **Commentaires :** en français (déjà existant) ou anglais (pour le nouveau code, au choix mais cohérent)
- **StateFlow :** `private val _xxx = MutableStateFlow(...)` + `val xxx: StateFlow<...> = _xxx.asStateFlow()`
- **Composables :** paramètres requis en premier, `Modifier` en dernier avec défaut
- **Pas de Hilt/Koin** — injection manuelle via paramètres de composables
- **Commit SHA :** `BuildConfig.COMMIT_SHA` auto-injecté, utilisable dans l'UI
