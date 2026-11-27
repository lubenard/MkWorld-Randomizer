# 2026-11-27 — Joueurs illimités + circuits 1.9.0

## Objectif
Supprimer la limite de 4 participants à la saisie des résultats et ajouter les 8 circuits « 2 » de la mise à jour Mario Kart World 1.9.0.

## Décisions

| Décision | Raison | Date |
|---|---|---|
| Participants illimités, calcul ELO inchangé | `submitRaceResults()` gère déjà n joueurs avec `K = 32/(n-1)` ; demande utilisateur | 2026-11-27 |
| 8 variantes `2` en `STANDALONE`, sans connexions | Demande utilisateur : circuits seuls, pas de trajets | 2026-11-27 |
| Visuels MarioWiki redimensionnés aux formats existants | Pas d'assets dédiés dans le projet ; sources trouvées sur mariowiki.com | 2026-11-27 |

## État
- Terminé : cap de 4 retiré, 8 variantes `2` ajoutées (40 → 48 pistes), 16 drawables MarioWiki redimensionnés, strings et specs 02/04/07 mises à jour.
- Validé : `testDeveloppDebugUnitTest` + `assembleDeveloppDebug` → BUILD SUCCESSFUL.
- Bloqué (préexistant, non modifié) : `assembleDevelop` échoue sur les variantes release car `snes_mario_circuit_1/2/3.png` contiennent en réalité du JPEG (AAPT2 `file failed to compile`). `gradle/gradle-daemon-jvm.properties` déjà stagé par l'utilisateur : laissé tel quel.

## Fichiers
- `app/src/main/java/com/escatrag/mkworldrandomiser/ui/screens/RaceResultScreen.kt` — retrait du cap de 4 joueurs.
- `app/src/main/res/values/strings.xml` — libellé participants + 8 noms de circuits.
- `app/src/main/java/com/escatrag/mkworldrandomiser/backend/TrackItems.kt` — 8 entrées `..._2` (40 → 48).
- `app/src/main/res/drawable/` — 16 images (8 petites + 8 grandes).
- `specs/SPEC_04_BACKEND_TRACKS.md`, `specs/SPEC_07_UI_SCREENS.md` — specs mises à jour.

## Prochaine action
Si besoin, corriger séparément les 3 PNG SNES mal encodés pour réparer le build release.
