package com.escatrag.mkworldrandomiser.backend

import androidx.annotation.StringRes
import com.escatrag.mkworldrandomiser.R

enum class TrackItems(@StringRes val nameRes: Int, val imgRes: Int, val largeImgRes: Int) {
    CIRCUIT_MARIO_BROS(R.string.track_mario_bros, R.drawable.circuit_mario_bros, R.drawable.large_circuit_mario_bros),
    TROPHEOPOLIS(R.string.track_tropheopolis, R.drawable.tropheopolis, R.drawable.large_tropheopolis),
    MONT_TCHOU_TCHOU(R.string.track_mont_tchou_tchou, R.drawable.mont_tchou_tchou, R.drawable.large_mont_tchou_tchou),
    SPATIOPORT_DK(R.string.track_spatioport_dk, R.drawable.spatioport_dk, R.drawable.large_spatioport_dk),
    DESERT_SOLEIL(R.string.track_desert_soleil, R.drawable.desert_du_soleil, R.drawable.large_desert_du_soleil),
    SOUK_MASKASS(R.string.track_souk_maskass, R.drawable.souk_maskass, R.drawable.large_souk_maskass),
    STADE_WARIO(R.string.track_stade_wario, R.drawable.stade_warrio, R.drawable.large_stade_warrio),
    BATEAU_VOLANT(R.string.track_bateau_volant, R.drawable.bateau_volant, R.drawable.large_bateau_volant),
    ALPES_DK(R.string.track_alpes_dk , R.drawable.alpes_dk, R.drawable.large_alpes_dk),
    PIC_OBSERVATOIRE(R.string.track_pic_observatoire, R.drawable.pic_observatoire, R.drawable.large_pic_observatoire),
    CITE_SORBET(R.string.track_cite_sorbet, R.drawable.cite_sorbet, R.drawable.large_cite_sorbet),
    GALION_WARIO(R.string.track_galion_wario, R.drawable.galion_warion, R.drawable.large_galion_warion),
    PLAGE_KOOPA(R.string.track_plage_koopa, R.drawable.plage_koopa, R.drawable.large_plage_koopa),
    SAVANE_SAUVAGE(R.string.track_savane_sauvage, R.drawable.savane_sauvage, R.drawable.large_savane_sauvage),
    PLAGE_PEACH(R.string.track_plage_peach, R.drawable.plage_peach, R.drawable.large_plage_peach),
    CITE_FLEUR_SEL(R.string.track_cite_fleur_sel, R.drawable.cite_fleur_sel, R.drawable.large_cite_fleur_sel),
    JUNGLE_DINO_DINO(R.string.track_jungle_dino_dino, R.drawable.jungle_dino_dino, R.drawable.large_jungle_dino_dino),
    BLOC_ANTIQUE(R.string.track_bloc_antique, R.drawable.bloc_antique, R.drawable.large_bloc_antique),
    CHUTES_CHEEP_CHEEP(R.string.track_chutes_cheep_cheep, R.drawable.chutes_cheep_cheep, R.drawable.large_chutes_cheep_cheep),
    GOUFFRE_PISSENLIT(R.string.track_gouffre_pissenlit, R.drawable.gouffre_pissenlit, R.drawable.large_gouffre_pissenlit),
    CINEMA_BOO(R.string.track_cinema_boo, R.drawable.cinema_boo, R.drawable.large_cinema_boo),
    FOURNAISE_OSSEUSE(R.string.track_fournaise_osseuse, R.drawable.fournaise_osseuse, R.drawable.large_fournaise_osseuse),
    PRAIRIE_MEUH_MEUH(R.string.track_prairie_meuh_meuh, R.drawable.circuit_meuh_meuh, R.drawable.large_circuit_meuh_meuh),
    MONTAGNE_CHOCO(R.string.track_montagne_choco, R.drawable.montagne_choco, R.drawable.large_montagne_choco),
    USINE_TOAD(R.string.track_usine_toad, R.drawable.usine_toad, R.drawable.large_usine_toad),
    CHATEAU_BOWSER(R.string.track_chateau_bowser, R.drawable.chateau_bowser, R.drawable.large_chateau_bowser),
    CHEMIN_CHENE(R.string.track_chemin_chene, R.drawable.chemin_du_chene, R.drawable.large_chemin_du_chene),
    CIRCUIT_MARIO(R.string.track_circuit_mario, R.drawable.circuit_mario, R.drawable.large_circuit_mario),
    STADE_PEACH(R.string.track_stade_peach, R.drawable.stade_peach, R.drawable.large_stade_peach),
    ROUTE_ARC_EN_CIEL(R.string.track_route_arc_en_ciel, R.drawable.route_arcenciel, R.drawable.large_route_arcenciel),
    SNES_MARIO_CIRCUIT_1(R.string.track_snes_mario_circuit_1, R.drawable.snes_mario_circuit_1, R.drawable.snes_mario_circuit_1),
    SNES_MARIO_CIRCUIT_2(R.string.track_snes_mario_circuit_2, R.drawable.snes_mario_circuit_2, R.drawable.snes_mario_circuit_2),
    SNES_MARIO_CIRCUIT_3(R.string.track_snes_mario_circuit_3, R.drawable.snes_mario_circuit_3, R.drawable.snes_mario_circuit_3),
    SNES_GHOST_VALLEY_1(R.string.track_snes_ghost_valley_1, R.drawable.snes_ghost_valley_1, R.drawable.snes_ghost_valley_1),
    SNES_GHOST_VALLEY_2(R.string.track_snes_ghost_valley_2, R.drawable.snes_ghost_valley_2, R.drawable.snes_ghost_valley_2),
    SNES_GHOST_VALLEY_3(R.string.track_snes_ghost_valley_3, R.drawable.snes_ghost_valley_3, R.drawable.snes_ghost_valley_3),
    SNES_CHOCO_ISLAND_1(R.string.track_snes_choco_island_1, R.drawable.snes_choco_island_1, R.drawable.snes_choco_island_1),
    SNES_CHOCO_ISLAND_2(R.string.track_snes_choco_island_2, R.drawable.snes_choco_island_2, R.drawable.snes_choco_island_2),
    SNES_VANILLA_LAKE_1(R.string.track_snes_vanilla_lake_1, R.drawable.snes_vanilla_lake_1, R.drawable.snes_vanilla_lake_1),
    SNES_KOOPA_BEACH_1(R.string.track_snes_koopa_beach_1, R.drawable.snes_koopa_beach_1, R.drawable.snes_koopa_beach_1)
}

fun Track.toTrackItem(): TrackItems? {
    // On cherche dans toutes les valeurs de l'Enum TrackItems
    // celle dont le nom correspond (ou l'id si tu en as un)
    return TrackItems.entries.firstOrNull { it.nameRes == this.text } ?: null
}