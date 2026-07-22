# SPEC 04 — Backend des Circuits (Tracks)

## 1. TrackItems — Les 29 Pistes

Enum complet dans `backend/TrackItems.kt`:

| # | Nom Enum | String Resource | Drawable (small/large) |
|---|---|---|---|
| 1 | CIRCUIT_MARIO_BROS | `track_mario_bros` | `circuit_mario_bros` / `large_circuit_mario_bros` |
| 2 | TROPHEOPOLIS | `track_tropheopolis` | `tropheopolis` / `large_tropheopolis` |
| 3 | MONT_TCHOU_TCHOU | `track_mont_tchou_tchou` | `mont_tchou_tchou` / `large_mont_tchou_tchou` |
| 4 | SPATIOPORT_DK | `track_spatioport_dk` | `spatioport_dk` / `large_spatioport_dk` |
| 5 | DESERT_SOLEIL | `track_desert_soleil` | `desert_du_soleil` / `large_desert_du_soleil` |
| 6 | SOUK_MASKASS | `track_souk_maskass` | `souk_maskass` / `large_souk_maskass` |
| 7 | STADE_WARIO | `track_stade_wario` | `stade_warrio` / `large_stade_warrio` |
| 8 | BATEAU_VOLANT | `track_bateau_volant` | `bateau_volant` / `large_bateau_volant` |
| 9 | ALPES_DK | `track_alpes_dk` | `alpes_dk` / `large_alpes_dk` |
| 10 | PIC_OBSERVATOIRE | `track_pic_observatoire` | `pic_observatoire` / `large_pic_observatoire` |
| 11 | CITE_SORBET | `track_cite_sorbet` | `cite_sorbet` / `large_cite_sorbet` |
| 12 | GALION_WARIO | `track_galion_wario` | `galion_warion` / `large_galion_warion` |
| 13 | PLAGE_KOOPA | `track_plage_koopa` | `plage_koopa` / `large_plage_koopa` |
| 14 | SAVANE_SAUVAGE | `track_savane_sauvage` | `savane_sauvage` / `large_savane_sauvage` |
| 15 | PLAGE_PEACH | `track_plage_peach` | `plage_peach` / `large_plage_peach` |
| 16 | CITE_FLEUR_SEL | `track_cite_fleur_sel` | `cite_fleur_sel` / `large_cite_fleur_sel` |
| 17 | JUNGLE_DINO_DINO | `track_jungle_dino_dino` | `jungle_dino_dino` / `large_jungle_dino_dino` |
| 18 | BLOC_ANTIQUE | `track_bloc_antique` | `bloc_antique` / `large_bloc_antique` |
| 19 | CHUTES_CHEEP_CHEEP | `track_chutes_cheep_cheep` | `chutes_cheep_cheep` / `large_chutes_cheep_cheep` |
| 20 | GOUFFRE_PISSENLIT | `track_gouffre_pissenlit` | `gouffre_pissenlit` / `large_gouffre_pissenlit` |
| 21 | CINEMA_BOO | `track_cinema_boo` | `cinema_boo` / `large_cinema_boo` |
| 22 | FOURNAISE_OSSEUSE | `track_fournaise_osseuse` | `fournaise_osseuse` / `large_fournaise_osseuse` |
| 23 | PRAIRIE_MEUH_MEUH | `track_prairie_meuh_meuh` | `circuit_meuh_meuh` / `large_circuit_meuh_meuh` |
| 24 | MONTAGNE_CHOCO | `track_montagne_choco` | `montagne_choco` / `large_montagne_choco` |
| 25 | USINE_TOAD | `track_usine_toad` | `usine_toad` / `large_usine_toad` |
| 26 | CHATEAU_BOWSER | `track_chateau_bowser` | `chateau_bowser` / `large_chateau_bowser` |
| 27 | CHEMIN_CHENE | `track_chemin_chene` | `chemin_du_chene` / `large_chemin_du_chene` |
| 28 | CIRCUIT_MARIO | `track_circuit_mario` | `circuit_mario` / `large_circuit_mario` |
| 29 | STADE_PEACH | `track_stade_peach` | `stade_peach` / `large_stade_peach` |
| 30 | ROUTE_ARC_EN_CIEL | `track_route_arc_en_ciel` | `route_arcenciel` / `large_route_arcenciel` |

**Remarque :** Il y a 30 entrées dans le code mais les specs du jeu Mario Kart World en contiennent 29. La 30e est le `ROUTE_ARC_EN_CIEL` classique.

## 2. Mappers — Extensions Shadow

**Fichier :** `backend/Mappers.kt`

```kotlin
fun TrackItems.map(): Track
// Convertit TrackItems → Track(text=nameRes, icon=imgRes, largeIcon=largeImgRes)

fun List<TrackItems>.map(): List<TrackCombo>
// Convertit List<TrackItems> → List<TrackCombo> avec type=TRACK
```

**⚠️ Attention :** Ces fonctions *shadowent* la fonction standard `map{...}` de Kotlin.  
`items.map` appelle ces extensions, pas `Iterable.map{}`. Les utiliser dans un contexte où `map{ it → ... }` était attendu peut causer des bugs silencieux.

## 3. TrackRepository — Graphe de Connexions

**Fichier :** `backend/TrackRepository.kt`

```kotlin
object TrackRepository {
    val trackItems: List<TrackCombo>
    val connections: Map<TrackItems, List<TrackItems>>
}
```

### `trackItems`

`get() = TrackItems.entries.toList().map()` — les 29 pistes converties en `List<TrackCombo>` avec `type = TRACK`.

### `connections`

Graphe dirigé statique. Chaque clé `TrackItems` a une liste de destinations (environ 5-10 par piste).  
Exemple extrait :

```
CIRCUIT_MARIO_BROS → [STADE_WARIO, MONTAGNE_CHOCO, TROPHEOPOLIS, MONT_TCHOU_TCHOU, DESERT_SOLEIL, SOUK_MASKASS]
TROPHEOPOLIS → [MONTAGNE_CHOCO, PRAIRIE_MEUH_MEUH, STADE_PEACH, SAVANE_SAUVAGE, PLAGE_KOOPA, ...]
```

**Total des arêtes :** ~200 connexions.  
**Notes :** Certaines listes contiennent des doublons (ex: `SPATIOPORT_DK` apparaît 2× pour `PLAGE_KOOPA`).

## 4. Concept de « Trajet » (Route/Connection)

Un **trajet** est une course de type « point A → point B » :

1. **Départ :** Circuit A (le point de départ du trajet)
2. **Arrivée :** Circuit B (la piste effectivement jouée — 1 tour)
3. Le joueur ne fait PAS de tour de A, uniquement de B.

Dans le code, un trajet est représenté par `TrackCombo(type=CONNECTION, start=Track(A), end=Track(B))`.

### Génération d'un trajet

1. `pickRandomMap()` sélectionne un circuit A aléatoire parmi `selectedTracks`
2. Si le biais le permet et que A a des connexions activées, on entre en `DUAL_SPINNER`
3. `pickRandomDestination()` choisi aléatoirement une destination B parmi les connexions activées de A
4. Le `TrackCombo` final a `type=CONNECTION`, `start=A`, `end=B`
5. À la fin de la course, le trajet est retiré du pool (`completeRace`/`deleteCircuit`)
