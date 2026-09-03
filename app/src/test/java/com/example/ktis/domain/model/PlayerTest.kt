package com.example.ktis.domain.model

import org.junit.Test
import org.junit.Assert.assertEquals

class PlayerTest {

    @Test
    fun scoreEqualsCollectedCards() {
        val player = Player(1, "Player 1")

        player.collectedCards.add(
            Card(Suit.HEARTS, Rank.ACE)
        )

        player.collectedCards.add(
            Card(Suit.SPADES, Rank.KING)
        )

        assertEquals(2, player.score)
    }

    @Test
    fun remainingCardsEqualsDrawPileSize() {
        val player = Player(1, "Player 1")

        player.drawPile.add(
            Card(Suit.HEARTS, Rank.ACE)
        )

        assertEquals(1, player.remainingCards)
    }
}