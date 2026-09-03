package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Rank
import com.example.ktis.domain.model.Suit
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class GameRulesTest {

    @Test
    fun higherRankWins() {
        val low = Card(Suit.HEARTS, Rank.SEVEN)
        val high = Card(Suit.SPADES, Rank.KING)

        assertTrue(
            GameRules.compareCards(high, low) > 0
        )
    }

    @Test
    fun sameRankCreatesTie() {
        val first = Card(Suit.HEARTS, Rank.EIGHT)
        val second = Card(Suit.SPADES, Rank.EIGHT)

        assertEquals(
            0,
            GameRules.compareCards(first, second)
        )
    }

    @Test
    fun highestCardsReturnsAllHighestCards() {
        val cards = listOf(
            0 to Card(Suit.HEARTS, Rank.KING),
            1 to Card(Suit.SPADES, Rank.ACE),
            2 to Card(Suit.CLUBS, Rank.ACE),
            3 to Card(Suit.DIAMONDS, Rank.SEVEN)
        )

        val highest = GameRules.highestCards(cards)

        assertEquals(2, highest.size)

        assertTrue(
            highest.all {
                it.second.rank == Rank.ACE
            }
        )
    }
}