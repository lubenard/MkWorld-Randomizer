# SPEC 07 — Écrans (Screens)

## 1. MainScreen — `ui/screens/MainScreen.kt`

### Paramètres

```kotlin
@Composable
fun MainScreen(
    viewModel: TrackViewModel,
    settingsViewModel: SettingsViewModel,
    onGenerate: (delay: Long) -> Unit,
    onSettings: () -> Unit,
    onScoreSelection: () -> Unit,
    scoreViewModel: ScoreViewModel,
    padding: PaddingValues,
)
```

### Comportement
- Icône ⚙️ en haut à droite → navigation vers `"settings"`
- Affiche l'une des 3 phases selon `viewModel.phase` :
  - `SELECTION_CUBE` → `SelectionCubePhase(trackPool, ..., callbacks)`
  - `SPINNING_TRACK` → `SpinningTrackPhase(selectedTracks, selectedTrackIndex, ...)`
  - `DUAL_SPINNER` → `DualSpinnerPhase(selectedTracks, ..., destinationItems, ...)`
- Confetti (bibliothèque Konfetti) : lancé après chaque tirage réussi
- `showResultActions` contrôlé localement (devient `true` après animation de la roue)
- `LaunchedEffect(Unit) { viewModel.resetCourse() }` réinitialise au montage
- `LaunchedEffect(phase) { ... }` remet `showResultActions = false` si phase change

## 2. TrackSelectionScreen — `ui/screens/TrackSelectionScreen.kt`

### Paramètres

```kotlin
@Composable
fun TrackSelectionScreen(viewModel: TrackViewModel, padding: PaddingValues)
```

### Comportement
- Titre "CIRCUITS" en Minecraft Font
- Barre de progression : `selectedCount / totalPoolCount`
- Champ de recherche : filtre par nom (français, insensible à la casse)
- Boutons « Tout activer » / « Tout désactiver »
- Liste (`LazyColumn`) des 29 pistes, chaque carte contient :
  - Image de la piste (60×40dp) avec overlay sombre + ❌ si non sélectionnée
  - Switch pour activer/désactiver
  - Flèche dropdown pour déplier les connexions
  - Fond pastel aléatoire (HSL) pour chaque carte
- Menu déroulant par piste : liste des trajets disponibles (`TrackSelectionConnectionTile`)
- Switch « Inclure les trajets » en bas → active `includeRoutes` + `selectAllTracks(true)`

### Tri et comptage
- `selectedCount` = `selectedTracks + selectedConnections` (si routes activées)
- `totalPoolCount` = `trackItems + totalConnectionsCount` (si routes activées)
- `totalConnectionsCount` = somme des tailles de toutes les listes de l'adjacency map

## 3. RaceResultScreen — `ui/screens/RaceResultScreen.kt`

### Paramètres

```kotlin
@Composable
fun RaceResultScreen(
    viewModel: ScoreViewModel,
    trackViewModel: TrackViewModel,
    onResultsSubmitted: () -> Unit,
    padding: PaddingValues
)
```

### Comportement
- Titre "Résultats de la course"
- **Étape 1 :** Sélection des participants (max 4) via `LazyRow` de `FilterChip`
- **Étape 2 :** Classement final — clic sur chaque participant pour attribuer une position (cyclique : position suivante → désassigner)
- Bouton « Enregistrer les scores » activé quand tous les participants ont un rang
- `onClick` du bouton :
  1. Appelle `viewModel.submitRaceResults(rankings, mapId)` avec `mapId = selectedTrack.value?.start?.text ?: 0`
  2. `onResultsSubmitted()` → `navController.popBackStack()`

### PlayerAvatar
```kotlin
@Composable
fun PlayerAvatar(player: PlayerProfile, size: Dp, modifier: Modifier)
```
- Cercle avec `player.composeColor` en fond
- Image de l'avatar ou initiales (si `avatarRes == null`)

## 4. MonthlyScoreScreen — `ui/screens/MonthlyScoreScreen.kt`

### Paramètres

```kotlin
@Composable
fun MonthlyScoreScreen(viewModel: ScoreViewModel, navController: NavHostController)
```

### Comportement
- Titre "CLASSEMENT"
- Boutons d'action (icônes) : réinitialiser les scores (🗑️) et réinitialiser les joueurs (🚫)
- **État vide :** message "Aucun pilote pour l'instant..." + FAB +
- **Mode liste (tous les scores à 3000) :** `LazyColumn` de `ScoreRow`
- **Mode podium (scores variés) :** `PodiumSection` (top 3) + liste pour les suivants
- `ScoreRow` : `#rang`, avatar, nom, score — fond coloré avec `player.composeColor(alpha=0.2f)`
- Clic sur un joueur : `PlayerDetailsComposable` (ModalBottomSheet)
- Long-clic sur un joueur : boîte de dialogue de suppression
- FAB (`+`) → `viewModel.startCreatingProfile()` → `ProfileCreationPopup`
- `allScoresAreDefault = players.all { it.currentMonthScore == 3000 }`

## 5. SettingsScreen — `ui/screens/SettingsScreen.kt`

### Paramètres

```kotlin
@Composable
fun SettingsScreen(
    vm: TrackViewModel,
    settingsViewModel: SettingsViewModel,
    padding: PaddingValues
)
```

### Comportement
- TopAppBar "Paramètres"
- **Thème :** 3 `FilterChip` (Système / Clair / Sombre)
- **BiasSlider :** connecté à `vm.generationBias`
- Pied de page : version + `BuildConfig.COMMIT_SHA`

## 6. ProfileCreationPopup — `ui/screens/ProfileCreationPopup.kt`

### Paramètres

```kotlin
@Composable
fun ProfileCreationPopup(
    profile: PlayerProfile,
    onDismiss: () -> Unit,
    onSave: (PlayerProfile) -> Unit
)
```

### Comportement
- Boîte de dialogue (`Dialog`) avec carte arrondie
- Champ texte "Prénom" (obligatoire)
- Grille d'avatars : `LazyVerticalGrid(columns = Fixed(4))`, 12 drawables disponibles
- Sélecteur de couleur : 6 cercles de couleur pastel
- Boutons "Annuler" / "Sauvegarder"
- `onSave(profile.copy(name = tempName, avatarRes = tempAvatar, profileColor = tempColor.toArgb()))`

### ⚠️ Bug connu
- `LazyVerticalGrid` a `height(150.dp)` fixe
- 12 items en 4 colonnes = 3 lignes × ~56dp = 168dp → dépasse la hauteur allouée

### Avatars disponibles
```
circuit_mario, circuit_mario_bros, alpes_dk, cinema_boo,
bloc_antique, bateau_volant, chemin_du_chene, mont_tchou_tchou,
tropheopolis, galion_warion, gouffre_pissenlit, jungle_dino_dino
```

### Couleurs disponibles
```
\#FFCDD2 (rose clair), #E1BEE7 (violet clair), #BBDEFB (bleu clair),
#C8E6C9 (vert clair), #FFF9C4 (jaune clair), #FFCC80 (orange clair)
```
