package com.example.ktis.domain.engine

import com.example.ktis.domain.model.FinalResult
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayerStats

object GameResult {

    fun calculate(
        state: GameState
    ): FinalResult {

        check(state.gameOver) {
            "Game is not over."
        }

        val scores =
            state.players.associate {
                it.id to it.score
            }

        val playerStats =
            state.players.associate { player ->

                player.id to
                        (
                                state.playerStats[player.id]
                                    ?: PlayerStats()
                                )
            }

        val highestScore =
            scores.values.maxOrNull()
                ?: error("No players.")

        val tiedPlayers =
            state.players.filter {
                it.score == highestScore
            }

        if (tiedPlayers.size == 1) {

            val winner =
                tiedPlayers.first()

            return FinalResult(
                winnerId =
                    winner.id,

                winnerName =
                    winner.name,

                scores =
                    scores,

                playerStats =
                    playerStats,

                isTieBroken =
                    false
            )
        }

        return breakFinalTie(
            tiedPlayers = tiedPlayers,
            scores = scores,
            playerStats = playerStats
        )
    }

    private fun breakFinalTie(
        tiedPlayers: List<Player>,
        scores: Map<Int, Int>,
        playerStats: Map<Int, PlayerStats>
    ): FinalResult {

        val pool =
            tiedPlayers
                .flatMap {
                    it.collectedCards
                }
                .shuffled()

        if (pool.isEmpty()) {

            val winner =
                tiedPlayers.first()

            return FinalResult(
                winnerId =
                    winner.id,

                winnerName =
                    winner.name,

                scores =
                    scores,

                playerStats =
                    playerStats,

                isTieBroken =
                    true
            )
        }

        var availableCards =
            pool.toMutableList()

        while (availableCards.isNotEmpty()) {

            if (
                availableCards.size <
                tiedPlayers.size
            ) {
                availableCards =
                    pool.toMutableList()
            }

            availableCards.shuffle()

            val drawn =
                tiedPlayers.map { player ->

                    val card =
                        availableCards.removeAt(
                            availableCards.lastIndex
                        )

                    player.id to card
                }

            val highestRank =
                drawn.maxOf {
                    it.second.rank.value
                }

            val winners =
                drawn.filter {
                    it.second.rank.value ==
                            highestRank
                }

            if (winners.size == 1) {

                val winnerId =
                    winners.first().first

                val winner =
                    tiedPlayers.first {
                        it.id == winnerId
                    }

                return FinalResult(
                    winnerId =
                        winner.id,

                    winnerName =
                        winner.name,

                    scores =
                        scores,

                    playerStats =
                        playerStats,

                    isTieBroken =
                        true
                )
            }
        }

        val winner =
            tiedPlayers.first()

        return FinalResult(
            winnerId =
                winner.id,

            winnerName =
                winner.name,

            scores =
                scores,

            playerStats =
                playerStats,

            isTieBroken =
                true
        )
    }
}