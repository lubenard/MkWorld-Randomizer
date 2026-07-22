# SPEC 02 — Data Models

## 1. Track — `backend/Model.kt:3`

```kotlin
data class Track(
    val text: Int,      // @StringRes resource ID (ex: R.string.track_mario_bros)
    val icon: Int,      // @DrawableRes small icon (ex: R.drawable.circuit_mario_bros)
    val largeIcon: Int  // @DrawableRes large icon (ex: R.drawable.large_circuit_mario_bros)
)
```

Simple conteneur de ressources pour une piste. Ne porte pas d'identifiant métier propre.

## 2. TrackCombo — `backend/Model.kt:9`

```kotlin
data class TrackCombo(
    val start: Track,
    val end: Track? = null,
    val type: TrackComboType
)
```

Représente soit un circuit seul (`type = TRACK`, `end = null`), soit un trajet (`type = CONNECTION`, `end = Track`).

**Règles :**
- `type = TRACK` → utilisé dans `selectedTracks` (pool de circuits)
- `type = CONNECTION` → utilisé dans `selectedConnections` (pool de trajets)
- L'`equals` utilisera `start`, `end`, `type` — deux instances avec les mêmes ressources sont égales.

## 3. TrackComboType — `backend/Model.kt:15`

```kotlin
enum class TrackComboType {
    TRACK,       // Circuit simple (1 tour de start)
    CONNECTION   // Trajet (départ start → 1 tour de end)
}
```

## 4. TrackItems — `backend/TrackItems.kt:6`

```kotlin
enum class TrackItems(@StringRes val nameRes: Int, val imgRes: Int, val largeImgRes: Int) {
    CIRCUIT_MARIO_BROS(R.string.track_mario_bros, ...),
    TROPHEOPOLIS(...),
    // ... 29 entries total
    ROUTE_ARC_EN_CIEL(R.string.track_route_arc_en_ciel, ...)
}
```

Les 29 pistes de Mario Kart World (Switch 2). Chaque entrée référence :
- `nameRes` : `R.string.track_xxx` (nom français)
- `imgRes` : `R.drawable.xxx` (icône 60×40dp)
- `largeImgRes` : `R.drawable.large_xxx` (icône large pour le spinner)

### Reverse lookup (`TrackItems.kt:39`)

```kotlin
fun Track.toTrackItem(): TrackItems?
```

Cherche `this.text` (R.string) parmi `TrackItems.entries.firstOrNull { it.nameRes == this.text }`. Retourne `null` si aucune correspondance.

**⚠️ Fragilité :** cette fonction est le maillon faible — si un `Track.text` ne correspond à aucun `TrackItems.nameRes`, le retour est `null` et peut causer des NPE (ex: `TrackSelectionScreen.kt:231` avec `!!`).

## 5. Phase — `viewmodels/TrackViewModel.kt:20`

```kotlin
enum class Phase { SELECTION_CUBE, SPINNING_TRACK, DUAL_SPINNER }
```

- `SELECTION_CUBE` : écran d'accueil / sélection (affiche `HomeUI`)
- `SPINNING_TRACK` : roue simple (circuit seul)
- `DUAL_SPINNER` : double roue (circuit + destination)

## 6. PlayerProfile — `viewmodels/ScoreViewModel.kt:29`

```kotlin
data class PlayerProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val avatarRes: Int? = null,
    val profileColor: Int = Color.Gray.toArgb(),
    val currentMonthScore: Int = 3000,
    val runNumbers: Int = 0,
    val victoryNumbers: Int = 0,
    val timesInPodium: Int = 0,
    val top3Maps: List<Top3Maps> = emptyList()
) {
    val composeColor: Color get() = Color(profileColor)
    val initials: String get() = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
}
```

| Champ | Type | Défaut | Description |
|---|---|---|---|
| `id` | String | UUID | Identifiant unique persistant |
| `name` | String | "" | Prénom du joueur |
| `avatarRes` | Int? | null | Drawable resource (avatar) |
| `profileColor` | Int | Gray | Couleur d'accent ARGB |
| `currentMonthScore` | Int | 3000 | Score mensuel (ELO rating ou points cumulés) |
| `runNumbers` | Int | 0 | Nombre total de courses |
| `victoryNumbers` | Int | 0 | Nombre de 1ères places |
| `timesInPodium` | Int | 0 | Nombre de top 3 |
| `top3Maps` | List<Top3Maps> | [] | Maps où le joueur a été top 3 |

## 7. Top3Maps — `viewmodels/ScoreViewModel.kt:44`

```kotlin
data class Top3Maps(
    val mapId: Int,     // R.string resource ID (⚠️ fragile)
    val timeInTop3: Int  // Nombre de fois dans le top 3 sur cette map
)
```

**⚠️ Attention :** `mapId` stocke la valeur entière de `R.string.xxx`. Si la ressource string est renommée, les données persistées deviennent orphelines.

## 8. ThemeMode — `viewmodels/SettingsViewModel.kt:16`

```kotlin
enum class ThemeMode { SYSTEM, LIGHT, DARK }
```

## 9. Relations entre Modèles

```
TrackRepository.trackItems : List<TrackCombo>
  → chaque TrackCombo a start = Track, type = TRACK, end = null
  → 29 items (toutes les pistes)

TrackRepository.connections : Map<TrackItems, List<TrackItems>>
  → graphe dirigé : depuis un TrackItems, vers les destinations possibles

TrackViewModel._selectedTracks : List<TrackCombo>
  → sous-ensemble de trackItems (pistes activées, type = TRACK)

TrackViewModel._selectedConnections : List<TrackCombo>
  → trajets activés (type = CONNECTION, start = parent, end = destination)
```
