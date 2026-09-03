package com.example.ktis.domain.model

import org.junit.Test
import org.junit.Assert.assertEquals

class PlayedCardTest {

    @Test
    fun storesPlayerAndCard() {
        val card = Card(
            Suit.HEARTS,
            Rank.ACE
        )

        val played = PlayedCard(
            playerId = 3,
            card = card
        )

        assertEquals(3, played.playerId)
        assertEquals(card, played.card)
    }
}