package com.example.ktis.domain.engine

import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows

class GameResultTest {

    @Test
    fun resultRequiresFinishedGame() {
        val state = GameState(
            players = listOf(
                Player(0, "A"),
                Player(1, "B")
            ),
            gameOver = false
        )

        assertThrows(IllegalStateException::class.java) {
            GameResult.calculate(state)
        }
    }

    @Test
    fun resultReturnsHighestScore() {
        val playerA = Player(0, "A")
        val playerB = Player(1, "B")

        playerA.collectedCards.addAll(
            listOf(
                com.example.ktis.domain.model.Card(
                    com.example.ktis.domain.model.Suit.HEARTS,
                    com.example.ktis.domain.model.Rank.ACE
                ),
                com.example.ktis.domain.model.Card(
                    com.example.ktis.domain.model.Suit.SPADES,
                    com.example.ktis.domain.model.Rank.KING
                )
            )
        )

        playerB.collectedCards.add(
            com.example.ktis.domain.model.Card(
                com.example.ktis.domain.model.Suit.CLUBS,
                com.example.ktis.domain.model.Rank.TWO
            )
        )

        val state = GameState(
            players = listOf(playerA, playerB),
            gameOver = true
        )

        val result = GameResult.calculate(state)

        assertEquals(0, result.winnerId)
        assertEquals("A", result.winnerName)
        assertEquals(2, result.scores[0])
        assertEquals(1, result.scores[1])
        assertEquals(false, result.isTieBroken)
    }
}