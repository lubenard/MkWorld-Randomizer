# SPEC 04 — Backend des Circuits (Tracks)

## 1. TrackItems — Les 48 Pistes

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
| 31 | SNES_MARIO_CIRCUIT_1 | `track_snes_mario_circuit_1` | `snes_mario_circuit_1` / `snes_mario_circuit_1` |
| 32 | SNES_MARIO_CIRCUIT_2 | `track_snes_mario_circuit_2` | `snes_mario_circuit_2` / `snes_mario_circuit_2` |
| 33 | SNES_MARIO_CIRCUIT_3 | `track_snes_mario_circuit_3` | `snes_mario_circuit_3` / `snes_mario_circuit_3` |
| 34 | SNES_GHOST_VALLEY_1 | `track_snes_ghost_valley_1` | `snes_ghost_valley_1` / `snes_ghost_valley_1` |
| 35 | SNES_GHOST_VALLEY_2 | `track_snes_ghost_valley_2` | `snes_ghost_valley_2` / `snes_ghost_valley_2` |
| 36 | SNES_GHOST_VALLEY_3 | `track_snes_ghost_valley_3` | `snes_ghost_valley_3` / `snes_ghost_valley_3` |
| 37 | SNES_CHOCO_ISLAND_1 | `track_snes_choco_island_1` | `snes_choco_island_1` / `snes_choco_island_1` |
| 38 | SNES_CHOCO_ISLAND_2 | `track_snes_choco_island_2` | `snes_choco_island_2` / `snes_choco_island_2` |
| 39 | SNES_VANILLA_LAKE_1 | `track_snes_vanilla_lake_1` | `snes_vanilla_lake_1` / `snes_vanilla_lake_1` |
| 40 | SNES_KOOPA_BEACH_1 | `track_snes_koopa_beach_1` | `snes_koopa_beach_1` / `snes_koopa_beach_1` |
| 41 | CIRCUIT_MARIO_BROS_2 | `track_mario_bros_2` | `circuit_mario_bros_2` / `large_circuit_mario_bros_2` |
| 42 | DESERT_SOLEIL_2 | `track_desert_soleil_2` | `desert_du_soleil_2` / `large_desert_du_soleil_2` |
| 43 | SOUK_MASKASS_2 | `track_souk_maskass_2` | `souk_maskass_2` / `large_souk_maskass_2` |
| 44 | STADE_WARIO_2 | `track_stade_wario_2` | `stade_warrio_2` / `large_stade_warrio_2` |
| 45 | BATEAU_VOLANT_2 | `track_bateau_volant_2` | `bateau_volant_2` / `large_bateau_volant_2` |
| 46 | ALPES_DK_2 | `track_alpes_dk_2` | `alpes_dk_2` / `large_alpes_dk_2` |
| 47 | PLAGE_KOOPA_2 | `track_plage_koopa_2` | `plage_koopa_2` / `large_plage_koopa_2` |
| 48 | CITE_FLEUR_SEL_2 | `track_cite_fleur_sel_2` | `cite_fleur_sel_2` / `large_cite_fleur_sel_2` |

**Remarque :** 48 entrées au total, réparties en **30 circuits Mario Kart World** (#1–#30), **10 circuits SNES** (#31–#40) et **8 variantes de la mise à jour 1.9.0** (#41–#48). Parmi les 30 MKW, 29 correspondent aux circuits officiels du jeu ; le 30e, `ROUTE_ARC_EN_CIEL`, est ajouté car absent de la liste officielle. Les 10 SNES ne font pas partie du circuit officiel Mario Kart World : ce sont des variantes d'anciennes cartouches, regroupées en 5 séries (cf. « Séries de variantes » ci-dessous). Les 8 variantes `2` sont des circuits seuls, sans connexion dans le graphe.

**Le champ `series`** — Signature de l'enum : `TrackItems(nameRes, imgRes, largeImgRes, series = TrackSeries.STANDALONE)`. Les 38 circuits MKW (30 de base + 8 variantes 1.9.0) n'annotent rien et restent donc `STANDALONE`.

### Séries de variantes

`backend/TrackSeries.kt` regroupe les variantes d'un même circuit d'origine.

| Série | Membres | Taille |
|---|---|---|
| `STANDALONE` | les 38 circuits MKW (dont les 8 variantes 1.9.0), chacun seul dans sa série | 1 |
| `SNES_MARIO_CIRCUIT` | `SNES_MARIO_CIRCUIT_1/2/3` | 3 |
| `SNES_GHOST_VALLEY` | `SNES_GHOST_VALLEY_1/2/3` | 3 |
| `SNES_CHOCO_ISLAND` | `SNES_CHOCO_ISLAND_1/2` | 2 |
| `SNES_VANILLA_LAKE` | `SNES_VANILLA_LAKE_1` | 1 |
| `SNES_KOOPA_BEACH` | `SNES_KOOPA_BEACH_1` | 1 |

Extensions fournies :

| Fonction | Rôle |
|---|---|
| `TrackItems.seriesMembers()` | Tous les circuits de la même série. `STANDALONE` → `listOf(this)`. |
| `val TrackItems.isSnes` | `series != TrackSeries.STANDALONE`. |
| `Track.isSnesOrDefault()` | Variante null-safe pour le pool : un `Track` inconnu du `TrackItems` enum est traité comme MKTW. |
| `TrackItems.removalTargets(SnesRemovalMode)` | Circuits à retirer après une course : le circuit seul (`OFF`), sa série (`SERIES`) ou toutes les SNES du tirage (`ALL_SNES`). |

`SnesRemovalMode` (enum du même fichier) : `OFF`, `SERIES`, `ALL_SNES`. Les deux derniers modes
n'ont d'effet que sur les circuits SNES ; un circuit `STANDALONE` (MKW) reste toujours seul dans
sa série.

**Consommateur** : `TrackViewModel.removeSeriesFromPool()`, qui retire du pool les circuits
retournés par `removalTargets()` (cf. `SPEC_05` §5). Un `STANDALONE` n'appartenant qu'à lui-même,
la suppression de série ne change rien au comportement des circuits MKW.

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

`get() = TrackItems.entries.toList().map()` — les 48 pistes converties en `List<TrackCombo>` avec `type = TRACK`.

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
