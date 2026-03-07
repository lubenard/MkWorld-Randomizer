package com.escatrag.mkworldrandomiser

object TrackRepository {

    val tracks = listOf(
        TrackItem("Circuit Mario Bros.", listOf(R.drawable.circuit_mario_bros)),
        TrackItem("Trophéopolis", listOf(R.drawable.tropheopolis)),
        TrackItem("Mont Tchou Tchou",listOf(R.drawable.mont_tchou_tchou)),
        TrackItem("Spatioport DK",listOf(R.drawable.spatioport_dk)),

        TrackItem("Désert du Soleil",listOf(R.drawable.desert_du_soleil)),
        TrackItem("Souk Maskass",listOf(R.drawable.souk_maskass)),
        TrackItem("Stade Wario",listOf(R.drawable.stade_warrio)),
        TrackItem("Bateau Volant",listOf(R.drawable.bateau_volant)),

        TrackItem("Alpes DK",listOf(R.drawable.tropheopolis)),
        TrackItem("Pic de l’Observatoire",listOf(R.drawable.tropheopolis)),
        TrackItem("Cité Sorbet",listOf(R.drawable.tropheopolis)),
        TrackItem("Galion de Wario",listOf(R.drawable.tropheopolis)),

        TrackItem("Plage Koopa",listOf(R.drawable.tropheopolis)),
        TrackItem("Savane Sauvage",listOf(R.drawable.tropheopolis)),

        TrackItem("Plage Peach",listOf(R.drawable.tropheopolis)),
        TrackItem("Cité Fleur-de-sel",listOf(R.drawable.tropheopolis)),
        TrackItem("Jungle Dino Dino",listOf(R.drawable.tropheopolis)),
        TrackItem("Bloc ? Antique",listOf(R.drawable.tropheopolis)),

        TrackItem("Chutes Cheep Cheep",listOf(R.drawable.tropheopolis)),
        TrackItem("Gouffre Pissenlit",listOf(R.drawable.tropheopolis)),
        TrackItem("Cinéma Boo",listOf(R.drawable.tropheopolis)),
        TrackItem("Fournaise Osseuse",listOf(R.drawable.tropheopolis)),

        TrackItem("Prairie Meuh Meuh",listOf(R.drawable.tropheopolis)),
        TrackItem("Montagne Choco",listOf(R.drawable.tropheopolis)),
        TrackItem("Usine Toad",listOf(R.drawable.tropheopolis)),
        TrackItem("Château de Bowser",listOf(R.drawable.tropheopolis)),

        TrackItem("Chemin du Chêne",listOf(R.drawable.tropheopolis)),
        TrackItem("Circuit Mario", listOf(R.drawable.tropheopolis)),
        TrackItem("Stade Peach",listOf(R.drawable.tropheopolis)),
        TrackItem("Route Arc-en-ciel", listOf(R.drawable.tropheopolis)),
    )

    // Graph des connexions
    val connections = mapOf(
        "Circuit Mario Bros." to listOf("Stade Wario"),
        "Trophéopolis" to listOf("Montagne Choco"),
        "Mont Tchou Tchou" to listOf("Désert du Soleil"),
        "Spatioport DK" to listOf("Mont Tchou Tchou"),

        "Désert du Soleil" to listOf("Souk Maskass"),
        "Souk Maskass" to listOf("Désert du Soleil", "Bateau Volant"),
        "Stade Wario" to listOf("Château de Bowser"),
        "Bateau Volant" to listOf("Château de Bowser"),

        "Alpes DK" to listOf("Cité Sorbet"),
        "Pic de l’Observatoire" to listOf("Cité Sorbet"),
        "Cité Sorbet" to listOf("Galion de Wario", "Cité Fleur-de-sel", "Alpes DK", "Chutes Cheep Cheep", "Gouffre Pissenlit", "Pic de l’Observatoire"), // VERIFIED
        "Galion de Wario" to listOf("Plage Peach"),

        "Plage Koopa" to listOf("Spatioport DK"),
        "Savane Sauvage" to listOf("Chutes Cheep Cheep"),

        "Plage Peach" to listOf("Bloc ? Antique"),
        "Cité Fleur-de-sel" to listOf("Alpes DK", "Galion de Wario", "Plage Peach", "Bloc ? Antique", "Jungle Dino Dino", "Savane Sauvage", "Chutes Cheep Cheep"), // VERIFIED
        "Jungle Dino Dino" to listOf("Plage Koopa", "Savane Sauvage", "Cité Fleur-de-sel", "Plage Peach", "Bloc ? Antique"), // VERIFIED
        "Bloc ? Antique" to listOf("Jungle Dino Dino"),

        "Chutes Cheep Cheep" to listOf("Gouffre Pissenlit"),
        "Gouffre Pissenlit" to listOf("Cinéma Boo"),
        "Cinéma Boo" to listOf("Pic de l’Observatoire"),
        "Fournaise Osseuse" to listOf("Chemin du Chêne"),

        "Prairie Meuh Meuh" to listOf("Circuit Mario"),
        "Montagne Choco" to listOf("Usine Toad"),
        "Usine Toad" to listOf("Fournaise Osseuse"),
        "Château de Bowser" to listOf("Fournaise Osseuse"),

        "Chemin du Chêne" to listOf("Cinéma Boo"),
        "Circuit Mario" to listOf("Chemin du Chêne"),
        "Stade Peach" to listOf("Route Arc-en-ciel")
    )
}
