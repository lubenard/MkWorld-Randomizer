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

/** Comportement de suppression appliqué après une course SNES (slider des réglages). */
enum class SnesRemovalMode {
    /** Seul le circuit tiré est retiré du pool. */
    OFF,

    /** Toute la série du circuit tiré est retirée (ex. Mario 1 retire Mario 2 et 3). */
    SERIES,

    /** Toutes les autres maps SNES du tirage sont retirées. */
    ALL_SNES
}

/**
 * Circuits à retirer du pool après avoir joué [this].
 *
 * - [SnesRemovalMode.OFF] : uniquement le circuit joué.
 * - [SnesRemovalMode.SERIES] et circuit SNES : toute la série (ex. « Circuit Mario 1 » retire
 *   « Circuit Mario 2 » et « Circuit Mario 3 »), sans toucher aux autres séries SNES.
 * - [SnesRemovalMode.ALL_SNES] et circuit SNES : toutes les maps SNES du tirage.
 *
 * Les circuits MKW (`STANDALONE`) sont toujours seuls dans leur série : ils ne sont jamais
 * concernés par les modes SNES.
 */
fun TrackItems.removalTargets(mode: SnesRemovalMode): List<TrackItems> = when {
    !isSnes || mode == SnesRemovalMode.OFF -> listOf(this)
    mode == SnesRemovalMode.SERIES -> seriesMembers()
    else -> TrackItems.entries.filter { it.isSnes }
}

/** Version null-safe pour les circuits du pool : un Track inconnu est traité comme MKTW. */
fun Track.isSnesOrDefault(): Boolean = toTrackItem()?.isSnes ?: false