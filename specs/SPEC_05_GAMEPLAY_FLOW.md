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
- À la fin : confetti, `completeRace()` (retire la piste après 3s si auto-delete activé — et, pour une course `TRACK`, toute sa série de variantes : cf. §5)
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

## 2. Logique de pickRandomMap() — `TrackViewModel.kt:189`

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

## 3. Logique de pickRandomDestination() — `TrackViewModel.kt:255`

```kotlin
fun pickRandomDestination(): Boolean
```

1. Prend `pendingDestinations`, le vide
2. Si vide → return false
3. Choisit `randomDest = dests.random()`
4. Construit `TrackCombo(type=CONNECTION, start=startTrack, end=randomDest.map())`
5. Set `_isSecondSpinnerReady = true` → la 2e roue s'affiche
6. Met à jour `_destinationTargetIndex` pour l'animation

## 4. Session & Coupe

Deux notions d'**organisation de la rotation**, à distinguer du compteur de courses
mensuelles de `ScoreViewModel` (qui est persistant) : celles-ci ne sont **persistées nulle part**.

### Coupe

**4 courses consécutives par défaut**, réglable via le slider « Courses par coupe » des
réglages (2 à 10, valeur en mémoire uniquement, repart de 4 à chaque lancement). Une session
en compte **4** à défaut.

La coupe porte la **régulation SNES** : une map SNES est tirée **au plus 1 fois par coupe**
(quota). Les compteurs `_racesInCoupe`, `_snesDrawnInCoupe` et `_racesPerCoupe` sont en mémoire :

- `completeRace()` (`TrackViewModel.kt:124`) incrémente `_racesInCoupe` à chaque course
  terminée. Un **trajet compte pour une course**, `completeRace()` est appelé une fois par
  course, pas une fois par piste.
- si la course terminée est une SNES, `_snesDrawnInCoupe` passe à 1 pour la coupe en cours
- à la rotation (`_racesInCoupe % _racesPerCoupe == 0`), le quota repart de zéro
- `pickRandomMap()` (`TrackViewModel.kt:210`) exclut les SNES du pool de tirage tant que le
  quota de la coupe n'est pas consommé
- `selectAllTracks()` / `clearAllTracks()` remettent les deux compteurs à zéro (nouvelle session)

### Session

Une session de jeu dure environ **1 heure**, soit **4 coupes d'environ 16 courses**.

- **Début** : le joueur reconstruit le pool avec `selectAllTracks()`, qui réinjecte les 40
  circuits dans `_selectedTracks`
- **Fin** : le joueur relance (`selectAllTracks()`), ou le pool finit par s'épuiser
  (`pickRandomMap()` abandonne alors sur pool vide)
- `resetCourse()` **ne fait que changer de phase** : il ne touche pas au pool, donc il n'a pas
  d'effet sur la session en cours

## 5. Auto-delete — `TrackViewModel.kt:346`

```kotlin
fun deleteCircuit(circuit: TrackCombo?)
```

Si `_deleteTrackAfterCompletion == true` :
- `CONNECTION` → supprime de `_selectedConnections`
- `TRACK` → supprime de `_selectedTracks` + toutes les connexions associées

### Suppression de série — `TrackViewModel.kt:154`

```kotlin
private fun removeSeriesFromPool(combo: TrackCombo)
```

Appelée par `completeRace()` pour les courses `TRACK`. Contrairement à `deleteCircuit()` qui
ne retire que l'entrée exacte, elle retire **toute la série** du circuit tiré : « Circuit
Mario 2 (SNES) » désactive Circuit Mario 1, 2 **et** 3, sans toucher au « Circuit Mario » de
Mario Kart World. Un circuit `STANDALONE` n'appartient qu'à lui-même, donc son comportement
est inchangé.

Repli si `combo.start.toTrackItem()` renvoie `null` (Track absent du `TrackItems` enum) :
suppression de l'entrée exacte et des connexions de même `start`, comme `deleteCircuit()`.

### Désactivation globale des SNES — `TrackViewModel.kt:179`

```kotlin
fun setSnesEnabled(enabled: Boolean)
```

Action de lot pilotée par le switch « Activer les maps SNES » des réglages. Elle ne modifie que
`_selectedTracks` (le pool du tirage) et **jamais** `_allTracksAvailable` : les 10 circuits SNES
restent donc toujours listés dans l'écran Circuits, simplement décochés — et absents du compteur
de courses disponibles.

C'est une **action de lot qui ne fournit qu'une valeur initiale** : après coup, chaque carte SNES
de l'écran Circuits se pilote indépendamment via `toggleTrack()`. Le switch n'est pas persisté,
il repart de `true` à chaque lancement — exactement comme `_deleteTrackAfterCompletion`.

**Note :** `completeRace()` (`TrackViewModel.kt:124`) fait un `delay(3000L)` puis supprime aussi. Il y a donc **deux chemins de suppression** qui peuvent intervenir : `completeRace()` (automatique, avec délai) et `deleteCircuit()` (appelé au clic sur "Recommencer"). Le second ne retire qu'un circuit, le premier toute sa série.

## 6. Reset — `TrackViewModel.kt:304`

```kotlin
fun resetCourse()
```

Remet tous les index à -1, vide `pendingDestinations`, `_destinationItems`, repasse en `SELECTION_CUBE`. **Ne touche pas au pool** : la session continue.

## 7. Résumé du Flow Complet

```
1. [SELECTION_CUBE] → clic bloc → pickRandomMap()
2. [DUAL_SPINNER ou SPINNING_TRACK] → animation SpinWheel
3. [SpinWheel] → onFinished → pickRandomDestination() (si duel)
4. [SpinWheel] → onFinished → completeRace() + showResultActions = true
5. [RaceResultActions] → "Saisir les scores" → RaceResultScreen
                         "RECOMMENCER" → deleteCircuit() + resetCourse() → [SELECTION_CUBE]
```
