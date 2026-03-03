package com.escatrag.mkworldrandomiser

object TrackRepository {

    val tracks = listOf(
        "Circuit Mario Bros.",
        "Trophéopolis",
        "Mont Tchou Tchou",
        "Spatioport DK",

        "Désert du Soleil",
        "Souk Maskass",
        "Stade Wario",
        "Bateau Volant",

        "Alpes DK",
        "Pic de l’Observatoire",
        "Cité Sorbet",
        "Galion de Wario",

        "Plage Koopa",
        "Savane Sauvage",

        "Plage Peach",
        "Cité Fleur-de-sel",
        "Jungle Dino Dino",
        "Bloc Antique",

        "Chutes Cheep Cheep",
        "Gouffre Pissenlit",
        "Cinéma Boo",
        "Fournaise Osseuse",

        "Prairie Meuh Meuh",
        "Montagne Choco",
        "Usine Toad",
        "Château de Bowser",

        "Chemin du Chêne",
        "Circuit Mario",
        "Stade Peach",
        "Route Arc-en-ciel"
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

        "Plage Peach" to listOf("Bloc Antique"),
        "Cité Fleur-de-sel" to listOf("Alpes DK", "Galion de Wario", "Plage Peach", "Bloc ? Antique", "Jungle Dino Dino", "Savane Sauvage", "Chutes Cheep Cheep"), // VERIFIED
        "Jungle Dino Dino" to listOf("Plage Koopa", "Savane Sauvage", "Cité Fleur-de-sel", "Plage Peach", "Bloc ? Antique"), // VERIFIED
        "Bloc Antique" to listOf("Jungle Dino Dino"),

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
