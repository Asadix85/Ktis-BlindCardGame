package com.example.ktis.domain.engine

import com.example.ktis.domain.model.FinalResult
import com.example.ktis.domain.model.GameState
import kotlin.random.Random

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

        val highestScore =
            scores.values.maxOrNull()
                ?: error("No players.")

        val tiedPlayers =
            state.players.filter {
                it.score == highestScore
            }

        if (tiedPlayers.size == 1) {
            val winner = tiedPlayers.first()

            return FinalResult(
                winnerId = winner.id,
                winnerName = winner.name,
                scores = scores,
                isTieBroken = false
            )
        }

        val pool =
            tiedPlayers.flatMap {
                it.collectedCards
            }.shuffled(Random)

        var position = 0

        while (true) {
            val drawn =
                tiedPlayers.map { player ->
                    player.id to pool[position++]
                }

            val highest =
                drawn.maxOf {
                    it.second.rank.value
                }

            val winners =
                drawn.filter {
                    it.second.rank.value == highest
                }

            if (winners.size == 1) {
                val winnerId = winners.first().first
                val winner =
                    tiedPlayers.first {
                        it.id == winnerId
                    }

                return FinalResult(
                    winnerId = winner.id,
                    winnerName = winner.name,
                    scores = scores,
                    isTieBroken = true
                )
            }
        }
    }
}