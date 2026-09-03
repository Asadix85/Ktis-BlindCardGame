package com.example.ktis.domain.model

data class Player(
    val id: Int,
    val name: String,
    val drawPile: MutableList<Card> = mutableListOf(),
    val collectedCards: MutableList<Card> = mutableListOf()
) {
    val score: Int
        get() = collectedCards.size

    val remainingCards: Int
        get() = drawPile.size
}