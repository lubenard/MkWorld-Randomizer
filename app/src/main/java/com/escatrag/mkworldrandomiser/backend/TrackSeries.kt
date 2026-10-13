package com.escatrag.mkworldrandomiser.backend

/**
 * Regroupement des variantes d'un même circuit.
 *
 * [STANDALONE] marque un circuit sans variante : c'est le cas des 30 circuits Mario Kart
 * World, chacun est indépendant et forme sa propre série d'un seul élément.
 *
 * Les circuits SNES sont des variantes d'un même circuit d'origine : tirer
 * « Circuit Mario 2 (SNES) » retire de la rotation toute la série (Circuit Mario 1, 2 et 3).
 */
enum class TrackSeries {
    STANDALONE,
    SNES_MARIO_CIRCUIT,
    SNES_GHOST_VALLEY,
    SNES_CHOCO_ISLAND,
    SNES_VANILLA_LAKE,
    SNES_KOOPA_BEACH
}

/**
 * Tous les circuits de la même série que celui-ci.
 * Un circuit sans variante est seul dans sa série : la suppression d'un circuit classique
 * ne change donc rien à son comportement.
 */
fun TrackItems.seriesMembers(): List<TrackItems> =
    if (series == TrackSeries.STANDALONE) listOf(this)
    else TrackItems.entries.filter { it.series == series }

/** Vrai pour les circuits issus des cartouches SNES. */
val TrackItems.isSnes: Boolean
    get() = series != TrackSeries.STANDALONE

/**
 * Circuits à retirer du pool après avoir joué [this].
 *
 * - [deleteInBlock] vrai ET circuit SNES : toute la série (ex. « Circuit Mario 1 » retire
 *   « Circuit Mario 2 » et « Circuit Mario 3 »), sans toucher aux autres séries SNES.
 * - Sinon : uniquement le circuit joué (les circuits MKW sont seuls dans leur série).
 */
fun TrackItems.removalTargets(deleteInBlock: Boolean): List<TrackItems> =
    if (deleteInBlock && isSnes) seriesMembers() else listOf(this)

/** Version null-safe pour les circuits du pool : un Track inconnu est traité comme MKTW. */
fun Track.isSnesOrDefault(): Boolean = toTrackItem()?.isSnes ?: false