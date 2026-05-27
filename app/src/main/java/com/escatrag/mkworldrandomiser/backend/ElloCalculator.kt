package com.escatrag.mkworldrandomiser.backend

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Représente un joueur à la fin de la course.
 * @param id Identifiant unique du joueur
 * @param currentElo L'Elo du joueur AVANT la course
 * @param position La position d'arrivée (1 pour le 1er, 2 pour le 2e, etc.)
 */
data class RacingPlayer(
    val id: String,
    val currentElo: Int,
    val position: Int
)

object LobbyEloCalculator {

    /**
     * Calcule les nouveaux scores Elo de tous les joueurs de la course.
     * Applique la dictature de la moyenne du salon via la décomposition par paires.
     *
     * @param players Liste des joueurs ayant participé (2 à 4 joueurs)
     * @param baseKFactor Le facteur de volatilité (32 par défaut).
     * @return Une Map contenant l'ID du joueur et son NOUVEL Elo.
     */
    fun processRaceResults(players: List<RacingPlayer>, baseKFactor: Int = 32): Map<String, Int> {
        val n = players.size
        // S'il n'y a pas assez de joueurs, on ne change rien
        if (n < 2) return players.associate { it.id to it.currentElo }

        // Division du facteur K par le nombre d'adversaires (N-1) pour éviter l'inflation
        val scaledK = baseKFactor.toDouble() / (n - 1)

        // On initialise une table pour stocker la variation de points (en Double pour rester précis)
        val eloVariations = players.associate { it.id to 0.0 }.toMutableMap()

        // Boucle de décomposition par paires (chaque joueur affronte individuellement tous les autres)
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                val playerA = players[i]
                val playerB = players[j]

                // 1. Calcul des probabilités de victoire mutuelles
                val expectedA = 1.0 / (1.0 + 10.0.pow((playerB.currentElo - playerA.currentElo) / 400.0))
                val expectedB = 1.0 - expectedA

                // 2. Résultat réel basé sur la position d'arrivée
                val (actualA, actualB) = when {
                    playerA.position < playerB.position -> Pair(1.0, 0.0) // A a battu B
                    playerA.position > playerB.position -> Pair(0.0, 1.0) // B a battu A
                    else -> Pair(0.5, 0.5) // Égalité parfaite (si même chrono au millième)
                }

                // 3. Calcul du transfert de points pour ce duel spécifique
                val changeA = scaledK * (actualA - expectedA)
                val changeB = scaledK * (actualB - expectedB)

                // 4. Accumulation dans le profil de chaque joueur
                eloVariations[playerA.id] = eloVariations[playerA.id]!! + changeA
                eloVariations[playerB.id] = eloVariations[playerB.id]!! + changeB
            }
        }

        // Appliquer les variations aux anciens Elos et arrondir proprement
        return players.associate { player ->
            val totalChange = eloVariations[player.id]!!.roundToInt()
            val newElo = player.currentElo + totalChange

            // Pas de tolérance bas niveau, mais on bloque à 0 pour éviter un Elo négatif.
            player.id to maxOf(0, newElo)
        }
    }
}