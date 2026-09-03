package com.example.ktis.domain.engine

import org.junit.Test
import org.junit.Assert.assertEquals

class DeckCountTest {

    @Test
    fun recommendedDeckCountForSmallGames() {
        assertEquals(
            1,
            GameEngine.recommendedDeckCount(2)
        )

        assertEquals(
            1,
            GameEngine.recommendedDeckCount(4)
        )
    }

    @Test
    fun recommendedDeckCountForMediumGames() {
        assertEquals(
            2,
            GameEngine.recommendedDeckCount(5)
        )

        assertEquals(
            2,
            GameEngine.recommendedDeckCount(8)
        )
    }

    @Test
    fun recommendedDeckCountForLargeGames() {
        assertEquals(
            3,
            GameEngine.recommendedDeckCount(9)
        )
    }
}