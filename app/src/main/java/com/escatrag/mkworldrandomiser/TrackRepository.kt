package com.escatrag.mkworldrandomiser

object TrackRepository {

    val tracks
        get() = Track.entries.toList()

    // Graph des connexions
    val connections = mapOf(
        Track.MARIO_BROS to listOf(Track.STADE_WARIO),
        Track.TROPHEOPOLIS to listOf(Track.MONTAGNE_CHOCO),
        Track.MONT_TCHOU_TCHOU to listOf(Track.DESERT_SOLEIL),
        Track.SPATIOPORT_DK to listOf(Track.MONT_TCHOU_TCHOU),

        Track.DESERT_SOLEIL to listOf(Track.SOUK_MASKASS),
        Track.SOUK_MASKASS to listOf(Track.DESERT_SOLEIL, Track.BATEAU_VOLANT),
        Track.STADE_WARIO to listOf(Track.CHATEAU_BOWSER),
        Track.BATEAU_VOLANT to listOf(Track.CHATEAU_BOWSER),

        Track.ALPES_DK to listOf(Track.CITE_SORBET),
        Track.PIC_OBSERVATOIRE to listOf(Track.CITE_SORBET),
        Track.CITE_SORBET to listOf(Track.GALION_WARIO, Track.CITE_FLEUR_SEL, Track.ALPES_DK, Track.CHUTES_CHEEP_CHEEP, Track.GOUFFRE_PISSENLIT, Track.PIC_OBSERVATOIRE), // VERIFIED
        Track.GALION_WARIO to listOf(Track.PLAGE_PEACH),

        Track.PLAGE_KOOPA to listOf(Track.SPATIOPORT_DK),
        Track.SAVANE_SAUVAGE to listOf(Track.CHUTES_CHEEP_CHEEP),

        Track.PLAGE_PEACH to listOf(Track.BLOC_ANTIQUE),
        Track.CITE_FLEUR_SEL to listOf(Track.ALPES_DK, Track.GALION_WARIO, Track.PLAGE_PEACH, Track.BLOC_ANTIQUE, Track.JUNGLE_DINO_DINO, Track.SAVANE_SAUVAGE, Track.CHUTES_CHEEP_CHEEP), // VERIFIED
        Track.JUNGLE_DINO_DINO to listOf(Track.PLAGE_KOOPA, Track.SAVANE_SAUVAGE, Track.CITE_FLEUR_SEL, Track.PLAGE_PEACH, Track.BLOC_ANTIQUE), // VERIFIED
        Track.BLOC_ANTIQUE to listOf(Track.JUNGLE_DINO_DINO),

        Track.CHUTES_CHEEP_CHEEP to listOf(Track.GOUFFRE_PISSENLIT),
        Track.GOUFFRE_PISSENLIT to listOf(Track.CINEMA_BOO),
        Track.CINEMA_BOO to listOf(Track.PIC_OBSERVATOIRE),
        Track.FOURNAISE_OSSEUSE to listOf(Track.CHEMIN_CHENE),

        Track.PRAIRIE_MEUH_MEUH to listOf(Track.CIRCUIT_MARIO),
        Track.MONTAGNE_CHOCO to listOf(Track.USINE_TOAD),
        Track.USINE_TOAD to listOf(Track.FOURNAISE_OSSEUSE),
        Track.CHATEAU_BOWSER to listOf(Track.FOURNAISE_OSSEUSE),

        Track.CHEMIN_CHENE to listOf(Track.CINEMA_BOO),
        Track.CIRCUIT_MARIO to listOf(Track.CHEMIN_CHENE),
        Track.STADE_PEACH to listOf(Track.ROUTE_ARC_EN_CIEL) // VERIFIED
    )
}
