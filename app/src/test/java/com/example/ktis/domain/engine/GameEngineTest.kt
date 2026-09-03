package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Rank
import com.example.ktis.domain.model.Suit
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue

class GameEngineTest {

    @Test
    fun gameStartsWithEvenCards() {
        val engine = GameEngine()

        val state = engine.startGame(
            listOf("A", "B", "C")
        )

        assertEquals(3, state.players.size)

        assertEquals(
            17,
            state.players[0].remainingCards
        )

        assertEquals(
            17,
            state.players[1].remainingCards
        )

        assertEquals(
            17,
            state.players[2].remainingCards
        )
    }

    @Test
    fun gameRequiresAtLeastTwoPlayers() {
        val engine = GameEngine()

        assertThrows(IllegalArgumentException::class.java){
            engine.startGame(listOf("A"))
        }
    }

    @Test
    fun gameRejectsDuplicateNames() {
        val engine = GameEngine()

        assertThrows(IllegalArgumentException::class.java) {
            engine.startGame(
                listOf("A", "A")
            )
        }
    }

    @Test
    fun playCardChangesTurn() {
        val engine = GameEngine()

        engine.startGame(
            listOf("A", "B")
        )

        assertEquals(
            0,
            engine.getState().currentPlayerIndex
        )

        engine.playCard()

        assertEquals(
            1,
            engine.getState().currentPlayerIndex
        )
    }

    @Test
    fun playedCardKeepsPlayerId() {
        val engine = GameEngine()

        engine.startGame(
            listOf("A", "B")
        )

        engine.playCard()

        assertEquals(
            0,
            engine.getState().centerPile.first().playerId
        )
    }

    @Test
    fun roundCannotResolveEarly() {
        val engine = GameEngine()

        engine.startGame(
            listOf("A", "B")
        )

        engine.playCard()

        assertEquals(
            null,
            engine.resolveRound()
        )
    }

    @Test
    fun higherCardWinsRound() {
        val engine = GameEngine()

        val state = engine.startGame(
            listOf("A", "B")
        )

        state.players.forEach {
            it.drawPile.clear()
        }

        state.players[0].drawPile.add(
            Card(Suit.HEARTS, Rank.ACE)
        )

        state.players[1].drawPile.add(
            Card(Suit.SPADES, Rank.KING)
        )

        engine.playCard()
        engine.playCard()

        assertEquals(
            0,
            engine.resolveRound()
        )

        val result = engine.getState()

        assertEquals(
            2,
            result.players[0].score
        )

        assertTrue(
            result.centerPile.isEmpty()
        )
    }

    @Test
    fun tieKeepsCenterCards() {
        val engine = GameEngine()

        val state = engine.startGame(
            listOf("A", "B")
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

        engine.playCard()
        engine.playCard()
        engine.resolveRound()

        val result = engine.getState()

        assertEquals(
            listOf(0, 1),
            result.tiedPlayerIds
        )

        assertEquals(
            2,
            result.centerPile.size
        )
    }

    @Test
    fun requestCardRequiresTwoCardDifference() {
        val engine = GameEngine()

        engine.startGame(
            listOf("A", "B")
        )

        assertFalse(
            engine.canRequestCard(0)
        )
    }

    @Test
    fun requestCardDoesNotChangeTurn() {
        val engine = GameEngine()

        val state = engine.startGame(
            listOf("A", "B")
        )

        repeat(2) {
            state.players[0].drawPile.removeAt(
                state.players[0].drawPile.lastIndex
            )
        }

        assertTrue(
            engine.canRequestCard(0)
        )

        engine.requestCard(0)

        assertEquals(
            0,
            engine.getState().currentPlayerIndex
        )

        assertEquals(
            25,
            state.players[0].remainingCards
        )
    }
}