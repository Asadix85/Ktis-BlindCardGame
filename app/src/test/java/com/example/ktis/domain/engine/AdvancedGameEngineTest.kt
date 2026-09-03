package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Rank
import com.example.ktis.domain.model.Suit
import org.junit.Test
import org.junit.Assert.assertEquals

class AdvancedGameEngineTest {

    @Test
    fun onlyTiedPlayersContinue() {
        val engine = GameEngine()

        val state = engine.startGame(
            listOf("A", "B", "C")
        )

        state.players.forEach {
            it.drawPile.clear()
        }

        state.players[0].drawPile.add(
            Card(Suit.HEARTS, Rank.ACE)
        )

        state.players[1].drawPile.add(
            Card(Suit.SPADES, Rank.ACE)
        )

        state.players[2].drawPile.add(
            Card(Suit.CLUBS, Rank.KING)
        )

        engine.playCard()
        engine.playCard()
        engine.playCard()
        engine.resolveRound()

        val result = engine.getState()

        assertEquals(
            listOf(0, 1),
            result.tiedPlayerIds
        )

        assertEquals(
            listOf(0, 1),
            result.roundPlayerIds
        )
    }

    @Test
    fun finalResultReturnsHighestScore() {
        val engine = GameEngine()

        val state = engine.startGame(
            listOf("A", "B")
        )

        state.players.forEach {
            it.drawPile.clear()
        }

        state.players[0].collectedCards.addAll(
            listOf(
                Card(Suit.HEARTS, Rank.ACE),
                Card(Suit.SPADES, Rank.KING)
            )
        )

        state.players[1].collectedCards.add(
            Card(Suit.CLUBS, Rank.TWO)
        )

        state.players[0].drawPile.add(
            Card(Suit.HEARTS, Rank.ACE)
        )

        state.players[1].drawPile.add(
            Card(Suit.SPADES, Rank.KING)
        )

        engine.playCard()
        engine.playCard()
        engine.resolveRound()

        val result = GameResult.calculate(
            engine.getState()
        )

        assertEquals(0, result.winnerId)
        assertEquals("A", result.winnerName)
        assertEquals(false, result.isTieBroken)
    }
}