package com.escatrag.mkworldrandomiser.backend

object TrackRepository {

    val trackItems
        get() = TrackItems.entries.toList().map()

    // Graph des connexions
    val connections = mapOf(
        TrackItems.CIRCUIT_MARIO_BROS to listOf(TrackItems.STADE_WARIO, TrackItems.MONTAGNE_CHOCO, TrackItems.TROPHEOPOLIS, TrackItems.MONT_TCHOU_TCHOU, TrackItems.DESERT_SOLEIL, TrackItems.SOUK_MASKASS), // VERIFIED
        TrackItems.TROPHEOPOLIS to listOf(TrackItems.MONTAGNE_CHOCO, TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.STADE_PEACH, TrackItems.SAVANE_SAUVAGE, TrackItems.PLAGE_KOOPA, TrackItems.SPATIOPORT_DK, TrackItems.MONT_TCHOU_TCHOU, TrackItems.DESERT_SOLEIL, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.STADE_WARIO), // VERIFIED
        TrackItems.MONT_TCHOU_TCHOU to listOf(TrackItems.DESERT_SOLEIL, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.MONTAGNE_CHOCO, TrackItems.TROPHEOPOLIS, TrackItems.PLAGE_KOOPA, TrackItems.SPATIOPORT_DK), // VERIFIED
        TrackItems.SPATIOPORT_DK to listOf(TrackItems.MONT_TCHOU_TCHOU, TrackItems.DESERT_SOLEIL, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.TROPHEOPOLIS, TrackItems.STADE_PEACH, TrackItems.PLAGE_KOOPA), // VERIFIED

        TrackItems.DESERT_SOLEIL to listOf(TrackItems.SOUK_MASKASS, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.TROPHEOPOLIS, TrackItems.PLAGE_KOOPA, TrackItems.MONT_TCHOU_TCHOU), // VERIFIED
        TrackItems.SOUK_MASKASS to listOf(TrackItems.BATEAU_VOLANT, TrackItems.STADE_WARIO, TrackItems.MONTAGNE_CHOCO, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.DESERT_SOLEIL), // VERIFIED
        TrackItems.STADE_WARIO to listOf(TrackItems.CHATEAU_BOWSER, TrackItems.FOURNAISE_OSSEUSE, TrackItems.USINE_TOAD, TrackItems.MONTAGNE_CHOCO, TrackItems.TROPHEOPOLIS, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.SOUK_MASKASS, TrackItems.BATEAU_VOLANT), // VERIFIED
        TrackItems.BATEAU_VOLANT to listOf(TrackItems.CHATEAU_BOWSER, TrackItems.CHATEAU_BOWSER, TrackItems.FOURNAISE_OSSEUSE, TrackItems.USINE_TOAD, TrackItems.STADE_WARIO, TrackItems.SOUK_MASKASS), // VERIFIED

        TrackItems.ALPES_DK to listOf(TrackItems.CITE_SORBET, TrackItems.PIC_OBSERVATOIRE, TrackItems.CITE_SORBET, TrackItems.GALION_WARIO, TrackItems.CITE_FLEUR_SEL, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.GOUFFRE_PISSENLIT), // VERIFIED
        TrackItems.PIC_OBSERVATOIRE to listOf(TrackItems.CITE_SORBET, TrackItems.GALION_WARIO, TrackItems.ALPES_DK, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.GOUFFRE_PISSENLIT, TrackItems.CIRCUIT_MARIO, TrackItems.CINEMA_BOO), // VERIFIED
        TrackItems.CITE_SORBET to listOf(TrackItems.GALION_WARIO, TrackItems.CITE_FLEUR_SEL, TrackItems.ALPES_DK, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.GOUFFRE_PISSENLIT, TrackItems.PIC_OBSERVATOIRE), // VERIFIED
        TrackItems.GALION_WARIO to listOf(TrackItems.PLAGE_PEACH, TrackItems.CITE_FLEUR_SEL, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.ALPES_DK, TrackItems.PIC_OBSERVATOIRE, TrackItems.CITE_SORBET), // VERIFIED

        TrackItems.PLAGE_KOOPA to listOf(TrackItems.SPATIOPORT_DK, TrackItems.SPATIOPORT_DK, TrackItems.TROPHEOPOLIS, TrackItems.STADE_PEACH, TrackItems.SAVANE_SAUVAGE, TrackItems.JUNGLE_DINO_DINO), // VERIFIED
        TrackItems.SAVANE_SAUVAGE to listOf(TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.CITE_FLEUR_SEL, TrackItems.PLAGE_PEACH, TrackItems.BLOC_ANTIQUE, TrackItems.JUNGLE_DINO_DINO, TrackItems.PLAGE_KOOPA, TrackItems.TROPHEOPOLIS, TrackItems.STADE_PEACH), // VERIFIED

        TrackItems.PLAGE_PEACH to listOf(TrackItems.BLOC_ANTIQUE, TrackItems.JUNGLE_DINO_DINO, TrackItems.SAVANE_SAUVAGE, TrackItems.CITE_FLEUR_SEL, TrackItems.GALION_WARIO), // VERIFIED
        TrackItems.CITE_FLEUR_SEL to listOf(TrackItems.ALPES_DK, TrackItems.GALION_WARIO, TrackItems.PLAGE_PEACH, TrackItems.BLOC_ANTIQUE, TrackItems.JUNGLE_DINO_DINO, TrackItems.SAVANE_SAUVAGE, TrackItems.CHUTES_CHEEP_CHEEP), // VERIFIED
        TrackItems.JUNGLE_DINO_DINO to listOf(TrackItems.PLAGE_KOOPA, TrackItems.SAVANE_SAUVAGE, TrackItems.CITE_FLEUR_SEL, TrackItems.PLAGE_PEACH, TrackItems.BLOC_ANTIQUE), // VERIFIED
        TrackItems.BLOC_ANTIQUE to listOf(TrackItems.JUNGLE_DINO_DINO, TrackItems.JUNGLE_DINO_DINO, TrackItems.PLAGE_KOOPA, TrackItems.SAVANE_SAUVAGE, TrackItems.CITE_FLEUR_SEL, TrackItems.PLAGE_PEACH), // VERIFIED

        TrackItems.CHUTES_CHEEP_CHEEP to listOf(TrackItems.GOUFFRE_PISSENLIT, TrackItems.PIC_OBSERVATOIRE, TrackItems.ALPES_DK, TrackItems.GALION_WARIO, TrackItems.CITE_FLEUR_SEL, TrackItems.SAVANE_SAUVAGE, TrackItems.STADE_PEACH, TrackItems.MONTAGNE_CHOCO, TrackItems.PRAIRIE_MEUH_MEUH), // VERIFIED
        TrackItems.GOUFFRE_PISSENLIT to listOf(TrackItems.CINEMA_BOO, TrackItems.PIC_OBSERVATOIRE, TrackItems.CITE_SORBET, TrackItems.ALPES_DK, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.USINE_TOAD, TrackItems.CIRCUIT_MARIO, TrackItems.CHEMIN_CHENE), // VERIFIED
        TrackItems.CINEMA_BOO to listOf(TrackItems.PIC_OBSERVATOIRE, TrackItems.GOUFFRE_PISSENLIT, TrackItems.CIRCUIT_MARIO, TrackItems.FOURNAISE_OSSEUSE, TrackItems.CHEMIN_CHENE), // VERIFIED
        TrackItems.FOURNAISE_OSSEUSE to listOf(TrackItems.CHEMIN_CHENE, TrackItems.CINEMA_BOO, TrackItems.CIRCUIT_MARIO, TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.USINE_TOAD, TrackItems.STADE_WARIO, TrackItems.BATEAU_VOLANT, TrackItems.CHATEAU_BOWSER), // VERIFIED

        TrackItems.PRAIRIE_MEUH_MEUH to listOf(TrackItems.CIRCUIT_MARIO, TrackItems.CIRCUIT_MARIO, TrackItems.GOUFFRE_PISSENLIT, TrackItems.ALPES_DK, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.STADE_PEACH, TrackItems.TROPHEOPOLIS, TrackItems.MONTAGNE_CHOCO, TrackItems.USINE_TOAD, TrackItems.FOURNAISE_OSSEUSE), // VERIFIED
        TrackItems.MONTAGNE_CHOCO to listOf(TrackItems.USINE_TOAD, TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.STADE_PEACH, TrackItems.TROPHEOPOLIS, TrackItems.MONT_TCHOU_TCHOU, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.SOUK_MASKASS, TrackItems.STADE_WARIO, TrackItems.CHATEAU_BOWSER), // VERIFIED
        TrackItems.USINE_TOAD to listOf(TrackItems.FOURNAISE_OSSEUSE, TrackItems.CHEMIN_CHENE, TrackItems.CIRCUIT_MARIO, TrackItems.GOUFFRE_PISSENLIT, TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.STADE_PEACH, TrackItems.MONTAGNE_CHOCO, TrackItems.CIRCUIT_MARIO_BROS, TrackItems.STADE_WARIO, TrackItems.BATEAU_VOLANT, TrackItems.CHATEAU_BOWSER), // VERIFIED
        TrackItems.CHATEAU_BOWSER to listOf(TrackItems.FOURNAISE_OSSEUSE, TrackItems.CIRCUIT_MARIO, TrackItems.USINE_TOAD, TrackItems.MONTAGNE_CHOCO, TrackItems.STADE_WARIO, TrackItems.BATEAU_VOLANT), // VERIFIED

        TrackItems.CHEMIN_CHENE to listOf(TrackItems.CINEMA_BOO, TrackItems.GOUFFRE_PISSENLIT, TrackItems.CIRCUIT_MARIO, TrackItems.USINE_TOAD, TrackItems.FOURNAISE_OSSEUSE), // VERIFIED
        TrackItems.CIRCUIT_MARIO to listOf(TrackItems.CHEMIN_CHENE, TrackItems.CINEMA_BOO, TrackItems.PIC_OBSERVATOIRE, TrackItems.GOUFFRE_PISSENLIT, TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.STADE_PEACH, TrackItems.USINE_TOAD, TrackItems.CHATEAU_BOWSER, TrackItems.FOURNAISE_OSSEUSE), // VERIFIED
        TrackItems.STADE_PEACH to listOf(TrackItems.PRAIRIE_MEUH_MEUH, TrackItems.CHUTES_CHEEP_CHEEP, TrackItems.SAVANE_SAUVAGE, TrackItems.PLAGE_KOOPA, TrackItems.TROPHEOPOLIS, TrackItems.MONTAGNE_CHOCO, TrackItems.USINE_TOAD, TrackItems.ROUTE_ARC_EN_CIEL) // VERIFIED
    )
}