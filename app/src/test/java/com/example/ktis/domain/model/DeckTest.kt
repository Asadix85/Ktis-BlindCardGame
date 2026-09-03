package com.example.ktis.domain.model

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

class DeckTest {

    @Test
    fun defaultDeckHas52Cards() {
        assertEquals(52, Deck().remainingCards())
    }

    @Test
    fun twoDecksHave104Cards() {
        assertEquals(104, Deck(2).remainingCards())
    }

    @Test
    fun drawRemovesCard() {
        val deck = Deck()

        val card = deck.draw()

        assertNotNull(card)
        assertEquals(51, deck.remainingCards())
    }
}