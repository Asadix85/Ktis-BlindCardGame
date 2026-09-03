package com.example.ktis.domain.model

class Deck(
    deckCount: Int = 1
) {

    private val cards = mutableListOf<Card>()

    init {
        require(deckCount >= 1)
        createDecks(deckCount)
    }

    private fun createDecks(deckCount: Int) {
        repeat(deckCount) {
            for (suit in Suit.entries) {
                for (rank in Rank.entries) {
                    cards.add(
                        Card(
                            suit = suit,
                            rank = rank
                        )
                    )
                }
            }
        }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun draw(): Card? {
        if (cards.isEmpty()) {
            return null
        }

        return cards.removeAt(cards.lastIndex)
    }

    fun remainingCards(): Int {
        return cards.size
    }
}