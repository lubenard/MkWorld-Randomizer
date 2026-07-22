# SPEC 08 — Composables Réutilisables

## 1. HomeUI — `ui/composables/HomeUI.kt`

### Paramètres
```kotlin
@Composable
fun HomeUI(availableCoursesCount: Int, onClick: () -> Unit)
```

### Description
Écran d'accueil animé de l'application.

**Animations :**
| Animation | Type | Détail |
|---|---|---|
| Shaker (X/Y) | `animateFloat`, 1200ms, FastOutSlowIn, RepeatMode.Reverse | Décalage ±2dp du bloc ? |
| Texte alpha | `animateFloat`, 1500ms, FastOutSlowIn, RepeatMode.Reverse | Opacité 1.0 → 0.3 (blinking) |
| Vague d'étoiles | `animateFloat`, 3000ms, LinearEasing, RepeatMode.Restart | 5 étoiles avec décalage sinusoïdal |

**Structure :**
- `TitleComposable("MARIO KART", 50.sp)`
- Texte "CIRCUIT ALÉATOIRE" (Minecraft Font)
- Bannière `[🏁 N courses disponibles]` (primaryContainer)
- Bloc `?` jaune (`Color(0xFFFFC107)`, 200×200dp, cliquable)
- Texte "tape le bloc pour révéler" (alpha animé)
- 5 étoiles avec effet de vague

## 2. SpinWheel — `ui/composables/SpinWheel.kt`

### Paramètres
```kotlin
@Composable
fun SpinWheel(
    items: List<TrackCombo>,
    targetIndex: Int,
    onFinished: (Int) -> Unit,
    selectedItem: Track?,
    modifier: Modifier = Modifier,
    simpleAnimation: Boolean = false
)
```

### Description
Roue de sélection à défilement vertical. Utilise `VerticalPager` (500 pages simulées pour un défilement infini).

**Deux modes d'animation :**

| Mode | `simpleAnimation` | Usage | Comportement |
|---|---|---|---|
| Simple | `true` | `SpinningTrackPhase` | 15 pages, décélération progressive (25ms → 340ms) |
| Longue | `false` | `DualSpinnerPhase` | Défilement complet, délai augmenté dans les 8 dernières pages (+80ms par page) |

**États :**
- **Avant animation :** texte "SÉLECTION EN COURS" clignotant (alpha 1→0 toutes les 250ms)
- **Pendant :** `VerticalPager` avec images des pistes (largeIcon)
- **Après :** large image fixe + nom du circuit (Minecraft Font, 35sp)

**Bouton Recommencer :** `RecommencerButton` — jaune (0xFFFFE401), clignotant (alpha 1→0.4, 1000ms)

## 3. RecommencerButton — `ui/composables/SpinWheel.kt:227`

### Paramètres
```kotlin
@Composable
fun RecommencerButton(modifier: Modifier = Modifier, onClick: () -> Unit)
```

Bouton jaune avec animation d'opacité infinie. Texte "RECOMMENCER" en Minecraft Font (35sp).

## 4. SelectionCubePhase — `ui/composables/SelectionCubePhase.kt`

### Paramètres
```kotlin
@Composable
fun SelectionCubePhase(
    totalPool: Int,
    deleteTrackAfterCompletion: Boolean,
    onPickRandomMap: () -> Unit,
    onToggleDeleteTrack: (Boolean) -> Unit,
)
```

Wrapper autour de `HomeUI` + switch « Supp. les trajets faits ».

## 5. SpinningTrackPhase — `ui/composables/SpinningTrackPhase.kt`

### Paramètres
```kotlin
@Composable
fun SpinningTrackPhase(
    selectedTracks: List<TrackCombo>,
    selectedTrackIndex: Int,
    selectedItem: TrackCombo?,
    showResultActions: Boolean,
    hasPlayers: Boolean,
    onSpinFinished: () -> Unit,
    onScoreSelection: () -> Unit,
    onRecommencer: () -> Unit,
)
```

- Centrage vertical de `SpinWheel` avec `simpleAnimation = true`
- `RaceResultActions` en bas

## 6. DualSpinnerPhase — `ui/composables/DualSpinnerPhase.kt`

### Paramètres
```kotlin
@Composable
fun DualSpinnerPhase(
    selectedTracks: List<TrackCombo>,
    selectedTrackIndex: Int,
    selectedItem: TrackCombo?,
    destinationItems: List<TrackCombo>,
    destinationTargetIndex: Int,
    isSecondSpinnerReady: Boolean,
    showResultActions: Boolean,
    hasPlayers: Boolean,
    onFirstSpinFinished: () -> Unit,
    onSecondSpinFinished: () -> Unit,
    onScoreSelection: () -> Unit,
    onRecommencer: () -> Unit,
)
```

- Premier `SpinWheel` (circuit de départ, `simpleAnimation = false`)
- Texte « vers » (Minecraft Font, 22sp, gris)
- Deuxième `SpinWheel` conditionnel : affiché seulement si `isSecondSpinnerReady == true`

## 7. RaceResultActions — `ui/composables/RaceResultActions.kt`

### Paramètres
```kotlin
@Composable
fun RaceResultActions(
    showResultActions: Boolean,
    hasPlayers: Boolean,
    onScoreSelection: () -> Unit,
    onRecommencer: () -> Unit,
)
```

Panneau de boutons visible après la fin de l'animation :
- « Saisir les scores » (jaune) — si `hasPlayers == true`
- `RecommencerButton` — toujours visible

## 8. BiasSlider — `ui/composables/BiasSlider.kt`

### Paramètres
```kotlin
@Composable
fun BiasSlider(value: Float, onValueChange: (Float) -> Unit)
```

- Material3 `Slider` avec range 0..100, steps=1 (cran à 50)
- État local `localValue` pour réactivité instantanée
- Commit au ViewModel seulement dans `onValueChangeFinished`
- Labels : « Circuits » | « 50 / 50 » | « Connections »

## 9. TitleComposable — `ui/composables/TitleComposable.kt`

### Paramètres
```kotlin
@Composable
fun TitleComposable(text: String, fontSize: TextUnit, modifier: Modifier = Modifier)
```

Effet d'ombre Minecraft : deux couches de texte superposées :
- **Dessous :** rouge, offset +3dp x/y
- **Dessus :** jaune

Les deux utilisent `MinecraftFontFamily`.

## 10. PodiumSection — `ui/composables/PodiumSection.kt`

### Paramètres
```kotlin
@Composable
fun PodiumSection(
    podium: List<PlayerProfile>,
    onClick: (PlayerProfile) -> Unit,
    onLongClick: (PlayerProfile) -> Unit
)
```

3 `PodiumBar` alignés en bas (`Arrangement.Bottom`).

| Position | Hauteur | Poids | Taille Avatar |
|---|---|---|---|
| 1er (centre) | 85% | 1.2f | 70dp |
| 2e (gauche) | 55% | 1.0f | 55dp |
| 3e (droite) | 35% | 1.0f | 55dp |

### PodiumBar — `ui/composables/PodiumSection.kt:88`

Barre verticale avec gradient chromé :
- **Or :** `#FFD700 → #FFF1A6 → #D4AF37 → #F9E498`
- **Argent :** `#C0C0C0 → #E8E8E8 → #8A8A8A → #D1D1D1`
- **Bronze :** `#CD7F32 → #E3AF84 → #8B4513 → #A0522D`

## 11. PlayerDetailsComposable — `ui/composables/PlayerDetailsComposable.kt`

### Paramètres
```kotlin
@Composable
fun PlayersDetailsComposable(
    selectedPlayerForDetails: PlayerProfile,
    onDismiss: () -> Unit
)
```

`ModalBottomSheet` avec :
- Avatar large (90dp, cercle)
- Nom
- Score
- 3 `StatTile` : Courses (🚩), Victoires (🏆), Podiums (🎖️)
- Section « Circuits favoris » : `MapSwimlane` → `LazyRow` de `MapCard`

### MapCard — `ui/composables/PlayerDetailsComposable.kt:193`

```kotlin
@Composable
fun MapCard(mapId: Top3Maps)
```

⚠️ **Bug connu :** `correctTrack!!` force-unwrap peut causer NPE.

Recherche `TrackRepository.trackItems.find { it.start.text == mapId.mapId }` → crash si introuvable.

## 12. TrackSelectionConnectionTile — `ui/composables/TrackSelectionConnectionTile.kt`

### Paramètres
```kotlin
@Composable
fun TrackSelectionConnectionTile(
    title: String,
    isActive: Boolean,
    themeColor: Color,
    onClick: () -> Unit
)
```

Carte horizontale avec bordure colorée, icône ✔ si actif. Hauteur 50dp.

## 13. StatTile — `ui/composables/PlayerDetailsComposable.kt:138`

### Paramètres
```kotlin
@Composable
fun StatTile(modifier, icon: ImageVector, value: String, label: String, color: Color)
```

Colonne centrée avec icône, valeur (titleLarge), label (labelSmall).

## 14. MapSwimlane — `ui/composables/PlayerDetailsComposable.kt:174`

### Paramètres
```kotlin
@Composable
fun MapSwimlane(maps: List<Top3Maps>)
```

`LazyRow` de `MapCard`. Affiche « Aucun podium pour le moment » si vide.
