# SPEC 05 — Gameplay Flow (Génération Aléatoire)

## 1. Les 3 Phases de l'Écran Principal

L'écran `MainScreen` alterne entre 3 phases définies par `TrackViewModel.phase` :

### Phase 1 : SELECTION_CUBE (Écran d'accueil)

```
┌──────────────────────────────────┐
│          ⚙️ (settings)           │
│                                  │
│       MARIO KART                 │
│     Circuit Aléatoire            │
│                                  │
│   [🏁 48 courses disponibles]    │
│                                  │
│       ┌──────────────┐           │
│       │      ?       │ ← cliquable
│       └──────────────┘           │
│                                  │
│   tape le bloc pour révéler      │
│                                  │
│   ☆  ☆  ☆  ☆  ☆ (vague)         │
│                                  │
│   [🔘] Supp. les trajets faits   │
└──────────────────────────────────┘
```

- Affiche `HomeUI` avec animation du bloc « ? » et compteur de courses
- Switch « Supp. les trajets faits » (correspond à `deleteTrackAfterCompletion`)
- Clic sur le bloc → appelle `viewModel.pickRandomMap()`

### Phase 2 : SPINNING_TRACK (Roue simple)

```
┌──────────────────────────────────┐
│                                  │
│     SÉLECTION EN COURS (blink)   │
│                                  │
│     ┌──────────────────────┐     │
│     │  [images défilent]    │     │
│     │  VerticalPager        │     │
│     └──────────────────────┘     │
│                                  │
│       (après animation)          │
│     ┌──────────────────────┐     │
│     │   LARGE ICON          │     │
│     └──────────────────────┘     │
│       Nom du circuit (Minecraft) │
│                                  │
│   [Saisir les scores] (si joueurs)│
│   [RECOMMENCER] (blink jaune)    │
└──────────────────────────────────┘
```

- `SpinWheel` avec `simpleAnimation = true` (15 pages, décélération)
- À la fin : confetti, `completeRace()` (retire la piste après 3s si auto-delete activé)
- Actions : saisir les scores ou recommencer

### Phase 3 : DUAL_SPINNER (Double roue)

```
┌──────────────────────────────────┐
│                                  │
│  SÉLECTION EN COURS (blink)     │
│  ┌──────────────────────┐       │
│  │  Roue 1 : Circuit A   │       │
│  └──────────────────────┘       │
│          "vers"                  │
│  ┌──────────────────────┐       │
│  │  Roue 2 : Circuit B   │       │
│  └──────────────────────┘       │
│                                  │
│   [Saisir les scores]           │
│   [RECOMMENCER]                  │
└──────────────────────────────────┘
```

- Roue 1 : sélectionne le circuit de départ (parmi `selectedTracks`)
- Texte « vers » (Minecraft Font)
- Roue 2 : sélectionne la destination (parmi les connexions activées du départ)
- `onFirstSpinFinished → pickRandomDestination()` prépare la 2e roue

## 2. Logique de pickRandomMap() — `TrackViewModel.kt:127`

```kotlin
fun pickRandomMap()
```

### Algorithme

1. **Vérification** : `selectedTracks` non vide
2. **Décision biaisée** : basée sur `_generationBias`
   - `0f` → toujours circuit simple
   - `100f` → toujours trajet (si connexions disponibles)
   - autre → `Random.nextBoolean()`
3. **Sélection aléatoire** : `available.random()` (parmi les circuits du pool)
4. **Vérification des trajets** : si mode trajet, cherche les connexions activées pour ce circuit
   - Si connexions existent → `pendingDestinations = enabled`, phase `DUAL_SPINNER`
   - Sinon → phase `SPINNING_TRACK`
5. **Mise à jour des StateFlow** : `selectedTrackIndex`, `_selectedTrack`, `_destinationItems`, `_phase`

## 3. Logique de pickRandomDestination() — `TrackViewModel.kt:193`

```kotlin
fun pickRandomDestination(): Boolean
```

1. Prend `pendingDestinations`, le vide
2. Si vide → return false
3. Choisit `randomDest = dests.random()`
4. Construit `TrackCombo(type=CONNECTION, start=startTrack, end=randomDest.map())`
5. Set `_isSecondSpinnerReady = true` → la 2e roue s'affiche
6. Met à jour `_destinationTargetIndex` pour l'animation

## 4. Auto-delete — `TrackViewModel.kt:268`

```kotlin
fun deleteCircuit(circuit: TrackCombo?)
```

Si `_deleteTrackAfterCompletion == true` :
- `CONNECTION` → supprime de `_selectedConnections`
- `TRACK` → supprime de `_selectedTracks` + toutes les connexions associées

**Note :** `completeRace()` (`TrackViewModel.kt:98`) fait un `delay(3000L)` puis supprime aussi. Il y a donc **deux chemins** de suppression qui peuvent interférer : `completeRace()` (automatique, avec délai) et `deleteCircuit()` (appelé au clic sur "Recommencer").

## 5. Reset — `TrackViewModel.kt:216`

```kotlin
fun resetCourse()
```

Remet tous les index à -1, vide `pendingDestinations`, `_destinationItems`, repasse en `SELECTION_CUBE`.

## 6. Résumé du Flow Complet

```
1. [SELECTION_CUBE] → clic bloc → pickRandomMap()
2. [DUAL_SPINNER ou SPINNING_TRACK] → animation SpinWheel
3. [SpinWheel] → onFinished → pickRandomDestination() (si duel)
4. [SpinWheel] → onFinished → completeRace() + showResultActions = true
5. [RaceResultActions] → "Saisir les scores" → RaceResultScreen
                         "RECOMMENCER" → deleteCircuit() + resetCourse() → [SELECTION_CUBE]
```
